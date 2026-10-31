package mctmods.immersivefabrication.data.generators.recipes;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Set;
import java.util.TreeSet;

public final class ConditionalFinishedRecipe implements FinishedRecipe {
    private static final Set<String> OWN_NAMESPACES = Set.of("minecraft", "forge", "immersivefabrication");
    private static final String[] REFERENCES = {"item", "tag", "fluid", "result"};
    private final FinishedRecipe recipe;
    private final Set<String> mods = new TreeSet<>();
    private final String[] types;

    private ConditionalFinishedRecipe(FinishedRecipe recipe, String[] types) {
        this.recipe = recipe;
        this.types = types;
        collect(recipe.serializeRecipe());
    }

    public static FinishedRecipe wrap(FinishedRecipe recipe, String... types) { return new ConditionalFinishedRecipe(recipe, types); }

    private void collect(JsonElement element) {
        if (element.isJsonArray()) {
            for (JsonElement child : element.getAsJsonArray()) { collect(child); }
        } else if (element.isJsonObject()) {
            JsonObject object = element.getAsJsonObject();
            for (String key : REFERENCES) {
                if (object.has(key) && object.get(key).isJsonPrimitive()) { addNamespace(object.get(key).getAsString()); }
            }
            for (String key : object.keySet()) { collect(object.get(key)); }
        }
    }

    private void addNamespace(String reference) {
        int colon = reference.indexOf(':');
        if (colon > 0 && !OWN_NAMESPACES.contains(reference.substring(0, colon))) { mods.add(reference.substring(0, colon)); }
    }

    private static void normalize(JsonObject json) {
        json.remove("category");
        json.remove("show_notification");
        if (json.has("result") && json.get("result").isJsonObject() && !json.getAsJsonObject("result").has("count")) { json.getAsJsonObject("result").addProperty("count", 1); }
    }

    @Override public void serializeRecipeData(@Nonnull JsonObject json) {
        recipe.serializeRecipeData(json);
        if (!(recipe instanceof BareResultFinishedRecipe)) { normalize(json); }
        if (mods.isEmpty() && types.length == 0) { return; }
        JsonArray conditions = new JsonArray();
        for (String type : types) {
            JsonObject condition = new JsonObject();
            condition.addProperty("type", type);
            conditions.add(condition);
        }
        for (String mod : mods) {
            JsonObject condition = new JsonObject();
            condition.addProperty("type", "forge:mod_loaded");
            condition.addProperty("modid", mod);
            conditions.add(condition);
        }
        json.add("conditions", conditions);
    }

    @Override @Nonnull public ResourceLocation getId() { return recipe.getId(); }

    @Override @Nonnull public RecipeSerializer<?> getType() { return recipe.getType(); }

    @Override @Nullable public JsonObject serializeAdvancement() { return null; }

    @Override @Nullable public ResourceLocation getAdvancementId() { return null; }
}
