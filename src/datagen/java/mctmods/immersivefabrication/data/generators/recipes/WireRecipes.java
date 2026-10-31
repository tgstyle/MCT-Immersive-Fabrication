package mctmods.immersivefabrication.data.generators.recipes;

import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.world.item.Items;
import java.util.function.Consumer;

public final class WireRecipes extends RecipeBase {
    private static final String FITTINGS = "fittings";
    private static final String SPOOLS = "spools";
    private static final String SPOOL_PATTERN = " I /ISI/ I ";
    private static final String[] TIERS = {"lv", "mv", "hv"};
    private static final String[] TIER_METALS = {"copper", "iron", "aluminum"};

    public WireRecipes(Consumer<FinishedRecipe> out) { super(out); }

    public void build() {
        fittings();
        spools();
    }

    private void fittings() {
        shaped(FITTINGS, "anchor", "anchor", 8, "ISI/I I", 'I', tag("ingots/steel"), 'S', in("nickel_rod"));
        for (int tier = 0; tier < TIERS.length; tier++) {
            shaped(FITTINGS, "insulator_" + TIERS[tier], "insulator_" + TIERS[tier], 8, " I /BIB", 'I', tag("ingots/" + TIER_METALS[tier]), 'B', vanilla(Items.TERRACOTTA));
            shaped(FITTINGS, "terminal_" + TIERS[tier], "terminal_" + TIERS[tier], 4, " I /BIB/BIB", 'I', tag("ingots/" + TIER_METALS[tier]), 'B', vanilla(Items.TERRACOTTA));
        }
        shaped(FITTINGS, "transformer", "transformer", 1, "L M/IBI/III", 'L', in("terminal_lv"), 'M', in("terminal_mv"), 'I', tag("ingots/iron"), 'B', in("spool_aluminum"));
        shaped(FITTINGS, "transformer_hv", "transformer_hv", 1, "M H/IBI/III", 'M', in("terminal_mv"), 'H', in("terminal_hv"), 'I', tag("ingots/iron"), 'B', in("coil_acsr"));
    }

    private void spools() {
        shaped(SPOOLS, "spool_copper", "spool_copper", 4, SPOOL_PATTERN, 'I', tag("wires/copper"), 'S', in("copper_rod"));
        shaped(SPOOLS, "spool_aluminum", "spool_aluminum", 4, SPOOL_PATTERN, 'I', tag("wires/aluminum"), 'S', tag("rods/aluminum"));
        shaped(SPOOLS, "spool_rope", "spool_rope", 4, SPOOL_PATTERN, 'I', in("flax_fiber"), 'S', tag("rods/wooden"));
        shaped(SPOOLS, "spool_structural", "spool_structural", 4, SPOOL_PATTERN, 'I', tag("wires/acsr"), 'S', in("nickel_rod"));
        shapedIn("immersivefabrication:spool_acsr", SPOOLS, "spool_acsr", "spool_acsr", 4, " I /ASA/ I ", 'I', tag("wires/acsr"), 'A', tag("wires/aluminum"), 'S', in("nickel_rod"));
        shapedIn("immersivefabrication:spool_acsr", SPOOLS, "spool_acsr_2", "spool_acsr", 4, " A /ISI/ A ", 'I', tag("wires/acsr"), 'A', tag("wires/aluminum"), 'S', in("nickel_rod"));
        for (String metal : new String[]{"copper", "aluminum"}) { shaped(SPOOLS, "spool_" + metal + "_insulated", "spool_" + metal + "_insulated", 4, "FCF/CFC/FCF", 'F', in("linen"), 'C', in("spool_" + metal)); }
    }
}
