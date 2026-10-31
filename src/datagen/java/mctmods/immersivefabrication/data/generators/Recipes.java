package mctmods.immersivefabrication.data.generators;

import mctmods.immersivefabrication.data.generators.recipes.BlockRecipes;
import mctmods.immersivefabrication.data.generators.recipes.ConditionalFinishedRecipe;
import mctmods.immersivefabrication.data.generators.recipes.EngineeringRecipes;
import mctmods.immersivefabrication.data.generators.recipes.MaterialRecipes;
import mctmods.immersivefabrication.data.generators.recipes.WireRecipes;

import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import javax.annotation.Nonnull;
import java.util.function.Consumer;

public class Recipes extends RecipeProvider {
    public Recipes(PackOutput output) { super(output); }

    @Override protected void buildRecipes(@Nonnull Consumer<FinishedRecipe> consumer) {
        Consumer<FinishedRecipe> wrapped = recipe -> consumer.accept(ConditionalFinishedRecipe.wrap(recipe));
        Consumer<FinishedRecipe> wired = recipe -> consumer.accept(ConditionalFinishedRecipe.wrap(recipe, "immersivefabrication:wires_enabled"));
        new BlockRecipes(wrapped).build();
        new MaterialRecipes(wrapped).build();
        new EngineeringRecipes(wrapped).build();
        new WireRecipes(wired).build();
    }
}
