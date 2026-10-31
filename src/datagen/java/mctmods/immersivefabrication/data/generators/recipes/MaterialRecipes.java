package mctmods.immersivefabrication.data.generators.recipes;

import mctmods.immersivefabrication.common.util.IFabMetals;
import mctmods.immersivefabrication.data.generators.TagNames;

import net.minecraft.advancements.critereon.PlayerTrigger;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.Map;

public final class MaterialRecipes extends RecipeBase {
    private static final String MATERIAL = "material";
    private static final String SMELTING = "smelting";
    private static final String[] SMELTED = {"aluminum", "constantan", "copper", "gold", "iron", "lead", "nickel", "silver", "steel", "uranium"};
    private static final Map<String, Item> VANILLA_INGOTS = Map.of("copper", Items.COPPER_INGOT, "gold", Items.GOLD_INGOT, "iron", Items.IRON_INGOT);

    public MaterialRecipes(JsonRecipeOutput json, RecipeOutput out) { super(json, out); }

    public void build() {
        plates();
        rods();
        wires();
        mix(MATERIAL, "dust_constantan", "constantan_dust", 2, tag("dusts/copper"), tag("dusts/nickel"));
        shaped(MATERIAL, "linen", "linen", 1, "HHH/HSH/HHH", 'H', in("flax_fiber"), 'S', vanilla(Items.STICK));
        for (String tier : new String[]{"iron", "steel"}) { withShape(RecipeShape.KEEP_CATEGORY, () -> assembly(tier)); }
        tools();
        smelting();
    }

    private void assembly(String tier) { shaped(MATERIAL, "assembly_" + tier, "assembly_" + tier, 1, "i i/ c /i i", 'c', tag("ingots/copper"), 'i', tag("plates/" + tier)); }

    private void plates() {
        for (String metal : IFabMetals.PLATES) {
            Ingredient source = metal.equals("graphite") ? tag("dusts/graphite") : tag("ingots/" + metal);
            mix(MATERIAL, "plate_" + metal, metal + "_plate", 1, source, in("hammer"));
        }
    }

    private void rods() {
        for (String metal : IFabMetals.RODS) {
            if (!metal.equals("treated")) { shaped(MATERIAL, "rod_" + metal, metal + "_rod", 4, "I/I", 'I', tag("ingots/" + metal)); }
        }
        Ingredient wood = Ingredient.of(item("treated_wood_" + TagNames.TREATED_WOOD[0]), item("treated_wood_" + TagNames.TREATED_WOOD[1]), item("treated_wood_" + TagNames.TREATED_WOOD[2]));
        shaped(MATERIAL, "rod_treated", "treated_rod", 4, "W/W", 'W', wood);
    }

    private void wires() {
        mix(MATERIAL, "wire_acsr", "acsr_wire", 1, tag("plates/steel"), in("cutters"));
        for (String metal : new String[]{"aluminum", "copper"}) { mix(MATERIAL, "wire_" + metal, metal + "_wire", 1, tag("plates/" + metal), in("cutters")); }
    }

    private void tools() {
        shaped("tools", "cutters", "cutters", 1, "SI/ S", 'I', tag("ingots/iron"), 'S', vanilla(Items.STICK));
        shaped("tools", "hammer", "hammer", 1, " IF/ SI/S  ", 'I', tag("ingots/iron"), 'S', vanilla(Items.STICK), 'F', vanilla(Items.STRING));
    }

    private void smelt(String name, String source, Item result, float experience) {
        SimpleCookingRecipeBuilder.smelting(in(source), RecipeCategory.MISC, result, experience, 200).unlockedBy("tick", PlayerTrigger.TriggerInstance.tick()).save(out, id(SMELTING, name));
    }

    private void smelting() {
        for (String metal : SMELTED) { smelt(metal + "_dust", metal + "_dust", VANILLA_INGOTS.containsKey(metal) ? VANILLA_INGOTS.get(metal) : item(metal + "_ingot"), 0.5F); }
        for (String metal : TagNames.ORE_METALS) { smelt(metal + "_ore", metal + "_ore", item(metal + "_ingot"), 1.0F); }
    }
}
