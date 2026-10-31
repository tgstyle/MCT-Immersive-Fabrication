package mctmods.immersivefabrication.data.generators.recipes;

import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Items;
import java.util.function.Consumer;

public final class EngineeringRecipes extends RecipeBase {
    private static final String FOLDER = "engineering";
    private static final String DYE_GROUP = "immersivefabrication:fluid_pipe_dye";

    public EngineeringRecipes(Consumer<FinishedRecipe> out) { super(out); }

    public void build() {
        shaped(FOLDER, "coil_acsr", "coil_acsr", 1, "WWW/WIW/WWW", 'I', tag("ingots/iron"), 'W', tag("wires/acsr"));
        shaped(FOLDER, "fluid_pipe", "fluid_pipe", 8, "PPP/   /PPP", 'P', tag("plates/iron"));
        shaped(FOLDER, "fluid_placer", "fluid_placer", 1, "IBI/B B/IBI", 'I', tag("plates/iron"), 'B', vanilla(Items.IRON_BARS));
        shaped(FOLDER, "industrial_pump", "industrial_pump", 1, " I /ICI/PPP", 'I', tag("plates/iron"), 'C', in("assembly_iron"), 'P', in("fluid_pipe"));
        shaped(FOLDER, "generator", "generator", 2, "III/EDE/III", 'I', tag("ingots/steel"), 'E', tag("ingots/aluminum"), 'D', tag("storage_blocks/copper"));
        shaped(FOLDER, "heating_element", "heating_element", 1, "ICI/CBC/IRI", 'I', tag("ingots/iron"), 'R', tag("dusts/redstone"), 'C', tag("ingots/copper"), 'B', tag("wires/copper"));
        shaped(FOLDER, "heavy_fabrication", "heavy_fabrication", 2, "IGI/PEP/IGI", 'I', tag("ingots/steel"), 'E', tag("ingots/aluminum"), 'G', in("assembly_steel"), 'P', vanilla(Items.PISTON));
        shaped(FOLDER, "light_fabrication", "light_fabrication", 2, "IGI/CCC/IGI", 'I', tag("ingots/iron"), 'C', tag("ingots/copper"), 'G', in("assembly_iron"));
        shaped(FOLDER, "metal_barrel", "metal_barrel", 1, "SSS/B B/BBB", 'B', tag("sheetmetals/iron"), 'S', in("iron_cladding_slab"));
        shaped(FOLDER, "radiator", "radiator", 2, "ICI/CBC/ICI", 'I', tag("ingots/steel"), 'C', tag("ingots/copper"), 'B', vanilla(Items.WATER_BUCKET));
        shaped(FOLDER, "redstone_fabrication", "redstone_fabrication", 2, "IRI/RCR/IRI", 'I', tag("ingots/iron"), 'C', tag("ingots/copper"), 'R', tag("dusts/redstone"));
        decorated(recipe -> new BareResultFinishedRecipe(recipe, null, true), () -> mixIn(DYE_GROUP, FOLDER, "fluid_pipe_undye", "fluid_pipe", 1, in("fluid_pipe")));
        for (DyeColor color : DyeColor.values()) { decorated(recipe -> new BareResultFinishedRecipe(recipe, color == DyeColor.LIGHT_GRAY ? null : color.getId(), true), () -> dye(color)); }
    }

    private void dye(DyeColor color) { mixIn(DYE_GROUP, FOLDER, "fluid_pipe_dye_" + color.getName(), "fluid_pipe", 1, in("fluid_pipe"), tag("dyes/" + color.getName())); }
}
