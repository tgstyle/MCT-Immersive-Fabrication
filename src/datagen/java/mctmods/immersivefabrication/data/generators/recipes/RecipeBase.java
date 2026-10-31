package mctmods.immersivefabrication.data.generators.recipes;

import mctmods.immersivefabrication.ImmersiveFabrication;

import net.minecraft.advancements.critereon.PlayerTrigger;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.registries.ForgeRegistries;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;

public abstract class RecipeBase {
    private final Consumer<FinishedRecipe> out;
    private UnaryOperator<FinishedRecipe> decorator = UnaryOperator.identity();

    protected RecipeBase(Consumer<FinishedRecipe> out) { this.out = out; }

    protected void decorated(UnaryOperator<FinishedRecipe> operator, Runnable block) {
        decorator = operator;
        block.run();
        decorator = UnaryOperator.identity();
    }

    protected Consumer<FinishedRecipe> sink() { return recipe -> out.accept(decorator.apply(recipe)); }

    protected static Item item(String id) {
        ResourceLocation key = ResourceLocation.fromNamespaceAndPath(ImmersiveFabrication.MODID, id);
        if (!ForgeRegistries.ITEMS.containsKey(key)) { throw new IllegalArgumentException(id); }
        return ForgeRegistries.ITEMS.getValue(key);
    }

    protected static Ingredient in(String id) { return Ingredient.of(item(id)); }

    protected static Ingredient tag(String path) { return Ingredient.of(TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("forge", path))); }

    protected static Ingredient vanilla(Item item) { return Ingredient.of(item); }

    protected static ResourceLocation id(String folder, String name) { return ResourceLocation.fromNamespaceAndPath(ImmersiveFabrication.MODID, folder + "/" + name); }

    protected void shaped(String folder, String name, String result, int count, String pattern, Object... keys) { shapedIn(null, folder, name, result, count, pattern, keys); }

    protected void shapedIn(String group, String folder, String name, String result, int count, String pattern, Object... keys) {
        ShapedRecipeBuilder builder = ShapedRecipeBuilder.shaped(RecipeCategory.MISC, item(result), count);
        for (String row : pattern.split("/")) { builder.pattern(row); }
        for (int i = 0; i < keys.length; i += 2) { builder.define((Character) keys[i], (Ingredient) keys[i + 1]); }
        if (group != null) { builder.group(group); }
        builder.unlockedBy("tick", PlayerTrigger.TriggerInstance.tick()).save(sink(), id(folder, name));
    }

    protected void shapeless(String folder, String name, Ingredient ingredient, String result, int count) { mix(folder, name, result, count, ingredient); }

    protected void mix(String folder, String name, String result, int count, Ingredient... ingredients) { mixIn(null, folder, name, result, count, ingredients); }

    protected void mixIn(String group, String folder, String name, String result, int count, Ingredient... ingredients) {
        ShapelessRecipeBuilder builder = ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, item(result), count);
        for (Ingredient ingredient : ingredients) { builder.requires(ingredient); }
        if (group != null) { builder.group(group); }
        builder.unlockedBy("tick", PlayerTrigger.TriggerInstance.tick()).save(sink(), id(folder, name));
    }
}
