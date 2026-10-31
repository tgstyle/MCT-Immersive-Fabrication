package mctmods.immersivefabrication.data.generators;

import mctmods.immersivefabrication.core.IFabWiresCondition;
import mctmods.immersivefabrication.data.generators.recipes.BlockRecipes;
import mctmods.immersivefabrication.data.generators.recipes.EngineeringRecipes;
import mctmods.immersivefabrication.data.generators.recipes.JsonRecipeOutput;
import mctmods.immersivefabrication.data.generators.recipes.MaterialRecipes;
import mctmods.immersivefabrication.data.generators.recipes.WireRecipes;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;

import javax.annotation.Nonnull;
import java.util.concurrent.CompletableFuture;

public class Recipes extends RecipeProvider {
    public Recipes(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) { super(output, registries); }

    @Override @Nonnull protected CompletableFuture<?> run(@Nonnull CachedOutput output, @Nonnull HolderLookup.Provider registries) {
        JsonRecipeOutput json = new JsonRecipeOutput(output, registries, recipePathProvider);
        new BlockRecipes(json, json).build();
        new MaterialRecipes(json, json).build();
        new EngineeringRecipes(json, json).build();
        new WireRecipes(json, json.withConditions(IFabWiresCondition.INSTANCE)).build();
        return json.finish();
    }

    @Override protected void buildRecipes(@Nonnull RecipeOutput output) {}
}
