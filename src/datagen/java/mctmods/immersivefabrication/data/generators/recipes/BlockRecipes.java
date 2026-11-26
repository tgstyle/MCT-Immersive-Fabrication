package mctmods.immersivefabrication.data.generators.recipes;

import mctmods.immersivefabrication.common.util.IFabBlockNames;
import mctmods.immersivefabrication.core.registration.IFabFluids;

import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import java.util.function.Consumer;

public final class BlockRecipes extends RecipeBase {
    private static final String[] TYPES = IFabBlockNames.SCAFFOLDING_TYPES;
    private static final String[] STYLES = IFabBlockNames.TREATED_WOOD;

    public BlockRecipes(Consumer<FinishedRecipe> out) { super(out); }

    public void build() {
        cladding();
        decoration();
        stone();
        wood();
        storage();
    }

    private void cycle(String folder, String name, String from, String to) { shapeless(folder, name, in(from), to, 1); }

    private void slab(String folder, String name, String base, char letter) { shaped(folder, name, base + "_slab", 6, "" + letter + letter + letter, letter, in(base)); }

    private void back(String folder, String name, String base, char letter) { shaped(folder, name, base, 1, letter + "/" + letter, letter, in(base + "_slab")); }

    private void stairs(String folder, String name, String base, char letter) { shaped(folder, name, base + "_stairs", 4, String.format("%1$s  /%1$s%1$s /%1$s%1$s%1$s", letter), letter, in(base)); }

    private void family(String folder, String prefix, String stairsSuffix, int index, String base, String next, char letter) {
        cycle(folder, prefix + "_cycle_" + index, base, next);
        slab(folder, prefix + "_slab_" + index, base, letter);
        back(folder, prefix + "_slab_back_" + index, base, letter);
        cycle(folder, prefix + "_slab_cycle_" + index, base + "_slab", next + "_slab");
        stairs(folder, prefix + "_stairs_" + stairsSuffix, base, letter);
        cycle(folder, prefix + "_stairs_cycle_" + stairsSuffix, base + "_stairs", next + "_stairs");
    }

    private void cladding() {
        for (String metal : IFabBlockNames.CLADDING_METALS) {
            String block = metal + "_cladding";
            shaped("cladding", block, block, 4, "PP/PP", 'P', tag("plates/" + metal));
            shaped("cladding", block + "_slab", block + "_slab", 6, "BBB", 'B', tag("sheetmetals/" + metal));
            back("cladding", block + "_slab_back", block, 'S');
        }
    }

    private void decoration() {
        int index = 0;
        for (String metal : IFabBlockNames.FRAME_METALS) {
            shaped("decoration", metal + "_fence", metal + "_fence", 3, "IRI/IRI", 'I', tag("ingots/" + metal), 'R', tag("rods/" + metal));
            shaped("decoration", metal + "_scaffolding", metal + "_scaffolding_" + TYPES[0], 6, "III/ R /R R", 'I', tag("ingots/" + metal), 'R', tag("rods/" + metal));
            for (int type = 0; type < TYPES.length; type++) {
                String base = metal + "_scaffolding_" + TYPES[type];
                String next = metal + "_scaffolding_" + TYPES[(type + 1) % TYPES.length];
                family("decoration", "scaffolding", metal + "_" + TYPES[type], index++, base, next, 'S');
            }
        }
    }

    private void stone() {
        shaped("stone", "alloy_brick", "alloy_brick", 2, "SB/BS", 'S', tag("sandstone"), 'B', vanilla(Items.BRICK));
        shaped("stone", "blast_brick", "blast_brick", 3, "NBN/BDB/NBN", 'D', vanilla(Items.BLAZE_POWDER), 'N', vanilla(Items.NETHER_BRICK), 'B', vanilla(Items.BRICK));
        shaped("stone", "blast_brick_reinforced", "blast_brick_reinforced", 1, "P/B", 'P', tag("plates/steel"), 'B', in("blast_brick"));
        shaped("stone", "coke_block", "coal_coke_block", 1, "CCC/CCC/CCC", 'C', tag("coal_coke"));
        shapeless("stone", "coke_block_uncraft", tag("storage_blocks/coal_coke"), "coal_coke", 9);
        shaped("stone", "coke_brick", "coke_brick", 3, "CBC/BSB/CBC", 'S', tag("sandstone"), 'C', vanilla(Items.CLAY_BALL), 'B', vanilla(Items.BRICK));
        shaped("stone", "concrete", "concrete", 8, "SCS/GBG/SCS", 'C', vanilla(Items.CLAY_BALL), 'S', tag("sand"), 'G', vanilla(Items.GRAVEL), 'B', vanilla(Items.WATER_BUCKET));
        shaped("stone", "concrete_slag", "concrete", 12, "SCS/GBG/SCS", 'C', vanilla(Items.CLAY_BALL), 'S', tag("slag"), 'G', vanilla(Items.GRAVEL), 'B', vanilla(Items.WATER_BUCKET));
        shaped("stone", "flaxcrete", "flaxcrete", 6, "CCC/HHH/CCC", 'C', vanilla(Items.CLAY_BALL), 'H', in("flax_fiber"));
        for (String name : IFabBlockNames.BRICKS) {
            slab("stone", name + "_slab", name, 'B');
            back("stone", name + "_slab_back", name, 'S');
        }
        for (String name : IFabBlockNames.CONCRETES) {
            slab("stone", name + "_slab", name, 'B');
            back("stone", name + "_slab_back", name, 'S');
            stairs("stone", name + "_stairs", name, 'C');
        }
    }

    private void wood() {
        String horizontal = "treated_wood_" + STYLES[0];
        shaped("wood", "treated_wood", horizontal, 8, "WWW/WCW/WWW", 'W', Ingredient.of(ItemTags.PLANKS), 'C', Ingredient.of(IFabFluids.CREOSOTE.getBucket()));
        shaped("wood", "treated_wood_fence", "treated_wood_fence", 3, "WSW/WSW", 'W', Ingredient.of(item(horizontal), item("treated_wood_" + STYLES[1]), item("treated_wood_" + STYLES[2])), 'S', tag("rods/treated_wood"));
        for (int style = 0; style < STYLES.length; style++) { family("wood", "treated_wood", STYLES[style], style, "treated_wood_" + STYLES[style], "treated_wood_" + STYLES[(style + 1) % STYLES.length], 'W'); }
    }

    private void storage() {
        for (String metal : IFabBlockNames.STORAGE_METALS) {
            shaped("storage", metal + "_block", metal + "_block", 1, "III/III/III", 'I', tag("ingots/" + metal));
            shapeless("storage", metal + "_block_uncraft", tag("storage_blocks/" + metal), metal + "_ingot", 9);
            shaped("storage", metal + "_ingot", metal + "_ingot", 1, "NNN/NNN/NNN", 'N', tag("nuggets/" + metal));
            shapeless("storage", metal + "_nuggets", tag("ingots/" + metal), metal + "_nugget", 9);
        }
        shapeless("storage", "copper_nuggets", tag("ingots/copper"), "copper_nugget", 9);
    }
}
