package mctmods.immersivefabrication.data.generators.recipes;

import com.google.gson.JsonObject;

import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class BareResultFinishedRecipe implements FinishedRecipe {
    private final FinishedRecipe recipe;
    private final Integer color;
    private final boolean strip;

    public BareResultFinishedRecipe(FinishedRecipe recipe, @Nullable Integer color, boolean strip) {
        this.recipe = recipe;
        this.color = color;
        this.strip = strip;
    }

    @Override public void serializeRecipeData(@Nonnull JsonObject json) {
        recipe.serializeRecipeData(json);
        if (strip) {
            json.remove("category");
            json.remove("show_notification");
        }
        if (color == null) { return; }
        JsonObject nbt = new JsonObject();
        nbt.addProperty("color", color);
        json.getAsJsonObject("result").add("nbt", nbt);
    }

    @Override @Nonnull public ResourceLocation getId() { return recipe.getId(); }

    @Override @Nonnull public RecipeSerializer<?> getType() { return recipe.getType(); }

    @Override @Nullable public JsonObject serializeAdvancement() { return null; }

    @Override @Nullable public ResourceLocation getAdvancementId() { return null; }
}
