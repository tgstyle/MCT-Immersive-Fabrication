package mctmods.immersivefabrication.data.generators.recipes;

import mctmods.immersivefabrication.core.registration.IFabDataComponents;

import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public final class EngineeringRecipes extends RecipeBase {
    private static final String FOLDER = "engineering";
    private static final String DYE_GROUP = "immersivefabrication:fluid_pipe_dye";

    public EngineeringRecipes(JsonRecipeOutput json, RecipeOutput out) { super(json, out); }

    public void build() {
        shaped(FOLDER, "coil_acsr", "coil_acsr", 1, "WWW/WIW/WWW", 'I', tag("ingots/iron"), 'W', tag("wires/acsr"));
        shaped(FOLDER, "fluid_pipe", "fluid_pipe", 8, "PPP/   /PPP", 'P', tag("plates/iron"));
        shaped(FOLDER, "fluid_placer", "fluid_placer", 1, "IBI/B B/IBI", 'I', tag("plates/iron"), 'B', vanilla(Items.IRON_BARS));
        shaped(FOLDER, "industrial_pump", "industrial_pump", 1, " I /ICI/PPP", 'I', tag("plates/iron"), 'C', in("assembly_iron"), 'P', in("fluid_pipe"));
        shaped(FOLDER, "generator", "generator", 2, "III/EDE/III", 'I', tag("ingots/steel"), 'E', tag("ingots/aluminum"), 'D', tag("storage_blocks/copper"));
        shaped(FOLDER, "heating_element", "heating_element", 1, "ICI/CBC/IRI", 'I', tag("ingots/iron"), 'R', vanilla(Items.REDSTONE), 'C', tag("ingots/copper"), 'B', tag("wires/copper"));
        shaped(FOLDER, "heavy_fabrication", "heavy_fabrication", 2, "IGI/PEP/IGI", 'I', tag("ingots/steel"), 'E', tag("ingots/aluminum"), 'G', in("assembly_steel"), 'P', vanilla(Items.PISTON));
        shaped(FOLDER, "light_fabrication", "light_fabrication", 2, "IGI/CCC/IGI", 'I', tag("ingots/iron"), 'C', tag("ingots/copper"), 'G', in("assembly_iron"));
        shaped(FOLDER, "metal_barrel", "metal_barrel", 1, "SSS/B B/BBB", 'B', tag("sheetmetals/iron"), 'S', in("iron_cladding_slab"));
        shaped(FOLDER, "radiator", "radiator", 2, "ICI/CBC/ICI", 'I', tag("ingots/steel"), 'C', tag("ingots/copper"), 'B', vanilla(Items.WATER_BUCKET));
        shaped(FOLDER, "redstone_fabrication", "redstone_fabrication", 2, "IRI/RCR/IRI", 'I', tag("ingots/iron"), 'C', tag("ingots/copper"), 'R', vanilla(Items.REDSTONE));
        withShape(RecipeShape.BARE, () -> mixIn(DYE_GROUP, FOLDER, "fluid_pipe_undye", new ItemStack(item("fluid_pipe")), in("fluid_pipe")));
        for (DyeColor color : DyeColor.values()) { withShape(RecipeShape.BARE, () -> dye(color)); }
    }

    private void dye(DyeColor color) {
        ItemStack result = new ItemStack(item("fluid_pipe"));
        if (color != DyeColor.LIGHT_GRAY) { result.set(IFabDataComponents.COLOR.get(), color.getId()); }
        mixIn(DYE_GROUP, FOLDER, "fluid_pipe_dye_" + color.getName(), result, in("fluid_pipe"), tag("dyes/" + color.getName()));
    }
}
