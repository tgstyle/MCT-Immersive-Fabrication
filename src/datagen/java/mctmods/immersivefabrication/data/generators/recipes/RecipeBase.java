package mctmods.immersivefabrication.data.generators.recipes;

import mctmods.immersivefabrication.ImmersiveFabrication;
import mctmods.immersivefabrication.core.registration.IFabItems;

import net.minecraft.advancements.critereon.PlayerTrigger;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public abstract class RecipeBase {
    private static final Map<String, Item> REGISTERED = IFabItems.REGISTER.getEntries().stream().collect(Collectors.toMap(holder -> holder.getId().getPath(), DeferredHolder::get));
    private final JsonRecipeOutput json;
    protected final RecipeOutput out;

    protected RecipeBase(JsonRecipeOutput json, RecipeOutput out) {
        this.json = json;
        this.out = out;
    }

    protected void withShape(RecipeShape shape, Runnable block) {
        json.shape(shape);
        block.run();
        json.shape(RecipeShape.STANDARD);
    }

    protected static Item item(String id) { return Objects.requireNonNull(REGISTERED.get(id), id); }

    protected static Ingredient in(String id) { return Ingredient.of(item(id)); }

    protected static Ingredient tag(String path) { return Ingredient.of(TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", path))); }

    protected static Ingredient vanilla(Item item) { return Ingredient.of(item); }

    protected static ResourceLocation id(String folder, String name) { return ResourceLocation.fromNamespaceAndPath(ImmersiveFabrication.MODID, folder + "/" + name); }

    protected void shaped(String folder, String name, String result, int count, String pattern, Object... keys) { shapedIn(null, folder, name, result, count, pattern, keys); }

    protected void shapedIn(String group, String folder, String name, String result, int count, String pattern, Object... keys) {
        ShapedRecipeBuilder builder = ShapedRecipeBuilder.shaped(RecipeCategory.MISC, item(result), count);
        for (String row : pattern.split("/")) { builder.pattern(row); }
        for (int i = 0; i < keys.length; i += 2) { builder.define((Character) keys[i], (Ingredient) keys[i + 1]); }
        if (group != null) { builder.group(group); }
        builder.unlockedBy("tick", PlayerTrigger.TriggerInstance.tick()).save(out, id(folder, name));
    }

    protected void shapeless(String folder, String name, Ingredient ingredient, String result, int count) { mix(folder, name, result, count, ingredient); }

    protected void mix(String folder, String name, String result, int count, Ingredient... ingredients) { mixIn(null, folder, name, new ItemStack(item(result), count), ingredients); }

    protected void mixIn(String group, String folder, String name, ItemStack result, Ingredient... ingredients) {
        ShapelessRecipeBuilder builder = ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, result);
        for (Ingredient ingredient : ingredients) { builder.requires(ingredient); }
        if (group != null) { builder.group(group); }
        builder.unlockedBy("tick", PlayerTrigger.TriggerInstance.tick()).save(out, id(folder, name));
    }
}
