package mctmods.immersivefabrication.data.generators.recipes;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.common.conditions.ModLoadedCondition;
import net.neoforged.neoforge.common.conditions.WithConditions;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.CompletableFuture;

public final class JsonRecipeOutput implements RecipeOutput {
    private static final Set<String> OWN_NAMESPACES = Set.of("minecraft", "c", "neoforge", "immersivefabrication");
    private static final String[] REFERENCES = {"item", "tag", "fluid", "id"};
    private final CachedOutput output;
    private final RegistryOps<JsonElement> ops;
    private final PackOutput.PathProvider paths;
    private final Set<ResourceLocation> seen = new HashSet<>();
    private final List<CompletableFuture<?>> pending = new ArrayList<>();
    private RecipeShape shape = RecipeShape.STANDARD;

    public JsonRecipeOutput(CachedOutput output, HolderLookup.Provider registries, PackOutput.PathProvider paths) {
        this.output = output;
        this.ops = registries.createSerializationContext(JsonOps.INSTANCE);
        this.paths = paths;
    }

    public void shape(RecipeShape shape) { this.shape = shape; }

    public CompletableFuture<?> finish() { return CompletableFuture.allOf(pending.toArray(CompletableFuture[]::new)); }

    @Override public void accept(@Nonnull ResourceLocation id, @Nonnull Recipe<?> recipe, @Nullable AdvancementHolder advancement, @Nonnull ICondition... conditions) {
        if (!seen.add(id)) { throw new IllegalStateException("Duplicate recipe " + id); }
        List<ICondition> all = new ArrayList<>(List.of(conditions));
        for (String mod : mods(Recipe.CODEC.encodeStart(ops, recipe).getOrThrow())) { all.add(new ModLoadedCondition(mod)); }
        JsonElement json = Recipe.CONDITIONAL_CODEC.encodeStart(ops, Optional.of(new WithConditions<>(recipe, all.toArray(ICondition[]::new)))).getOrThrow();
        normalize(json.getAsJsonObject());
        pending.add(DataProvider.saveStable(output, json, paths.json(id)));
    }

    @Override @Nonnull public Advancement.Builder advancement() { return Advancement.Builder.recipeAdvancement(); }

    private void normalize(JsonObject json) {
        json.remove("show_notification");
        if (!shape.keepCategory()) { json.remove("category"); }
        if (shape.dropCount() && json.has("result") && json.get("result").isJsonObject()) { json.getAsJsonObject("result").remove("count"); }
    }

    private static Set<String> mods(JsonElement element) {
        Set<String> mods = new TreeSet<>();
        collect(element, mods);
        return mods;
    }

    private static void collect(JsonElement element, Set<String> mods) {
        if (element.isJsonArray()) {
            element.getAsJsonArray().forEach(child -> collect(child, mods));
        } else if (element.isJsonObject()) {
            JsonObject object = element.getAsJsonObject();
            for (String key : REFERENCES) {
                if (object.has(key) && object.get(key).isJsonPrimitive()) { addNamespace(object.get(key).getAsString(), mods); }
            }
            object.keySet().forEach(key -> collect(object.get(key), mods));
        }
    }

    private static void addNamespace(String reference, Set<String> mods) {
        int colon = reference.indexOf(':');
        if (colon > 0 && !OWN_NAMESPACES.contains(reference.substring(0, colon))) { mods.add(reference.substring(0, colon)); }
    }
}
