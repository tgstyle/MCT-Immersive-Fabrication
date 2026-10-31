package mctmods.immersivefabrication.core.registration;

import com.immersiveconvergence.api.block.ICBlockBase;

import mctmods.immersivefabrication.common.blocks.metal.decoration.MetalFenceType;
import mctmods.immersivefabrication.common.blocks.metal.decoration.ScaffoldingType;
import mctmods.immersivefabrication.common.blocks.stone.ConcreteType;
import mctmods.immersivefabrication.common.blocks.stone.StoneDecorationType;
import mctmods.immersivefabrication.common.blocks.wooden.TreatedFenceType;
import mctmods.immersivefabrication.common.util.IFabItemNames;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import java.util.Arrays;
import java.util.Locale;

public final class IFabOreDicts {

    private IFabOreDicts() {}

    public static void register() {
        registerForm(IFabItems.ingot, "ingot", IFabItemNames.INGOTS);
        registerForm(IFabItems.nugget, "nugget", IFabItemNames.NUGGETS);
        registerForm(IFabItems.dust, "dust", IFabItemNames.DUSTS);
        registerForm(IFabItems.plate, "plate", IFabItemNames.PLATES);
        registerForm(IFabItems.wire, "wire", IFabItemNames.WIRES);
        registerForm(IFabItems.rod, "stick", IFabItemNames.RODS);
        registerAliases();
        registerBlocks(IFabBlocks.storage, "block");
        registerBlocks(IFabBlocks.ore, "ore");
        registerBlocks(IFabBlocks.cladding, "blockCladding");
        registerBlocks(IFabBlocks.cladding, "blockSheetmetal");
        registerBlocks(IFabBlocks.claddingSlab.bottom, "slabCladding");
        registerBlocks(IFabBlocks.claddingSlab.bottom, "slabSheetmetal");
        registerDecoration();
        OreDictionary.registerOre("seedFlax", new ItemStack(IFabItems.seed));
        for (int meta = 0; meta < IFabItemNames.MATERIALS.length; meta++) {
            String name = materialName(IFabItemNames.MATERIALS[meta]);
            if (name != null) { OreDictionary.registerOre(name, new ItemStack(IFabItems.material, 1, meta)); }
        }
    }

    private static void registerAliases() {
        alias(IFabItems.ingot, IFabItemNames.INGOTS, "graphite", "ingotHOPGraphite");
        alias(IFabItems.dust, IFabItemNames.DUSTS, "graphite", "dustHOPGraphite");
        alias(IFabItems.dust, IFabItemNames.DUSTS, "niter", "dustSaltpeter");
        alias(IFabItems.wire, IFabItemNames.WIRES, "acsr", "wireSteel");
    }

    private static void alias(Item item, String[] subs, String sub, String name) { OreDictionary.registerOre(name, new ItemStack(item, 1, Arrays.asList(subs).indexOf(sub))); }

    private static void registerDecoration() {
        OreDictionary.registerOre("plankTreatedWood", new ItemStack(IFabBlocks.treatedWood, 1, OreDictionary.WILDCARD_VALUE));
        OreDictionary.registerOre("slabTreatedWood", new ItemStack(IFabBlocks.treatedWoodSlab.bottom, 1, OreDictionary.WILDCARD_VALUE));
        OreDictionary.registerOre("fenceTreatedWood", new ItemStack(IFabBlocks.treatedWoodFence, 1, TreatedFenceType.TREATED_WOOD.getMeta()));
        OreDictionary.registerOre("fenceSteel", new ItemStack(IFabBlocks.metalFence, 1, MetalFenceType.STEEL.getMeta()));
        OreDictionary.registerOre("fenceAluminum", new ItemStack(IFabBlocks.metalFence, 1, MetalFenceType.ALUMINUM.getMeta()));
        for (ScaffoldingType type : ScaffoldingType.values()) { OreDictionary.registerOre(type.name().startsWith("STEEL") ? "scaffoldingSteel" : "scaffoldingAluminum", new ItemStack(IFabBlocks.scaffolding, 1, type.getMeta())); }
        OreDictionary.registerOre("blockFuelCoke", new ItemStack(IFabBlocks.stoneDecoration, 1, StoneDecorationType.COAL_COKE.getMeta()));
        OreDictionary.registerOre("concrete", new ItemStack(IFabBlocks.concrete, 1, ConcreteType.CONCRETE.getMeta()));
    }

    private static String materialName(String sub) {
        switch (sub) {
            case "coal_coke": return "fuelCoke";
            case "slag": return "itemSlag";
            case "vacuum_tube": return "electronTube";
            default: return null;
        }
    }

    private static void registerBlocks(ICBlockBase<?> block, String prefix) {
        for (Enum<?> type : block.enumValues) { OreDictionary.registerOre(prefix + capitalize(type.name().toLowerCase(Locale.ROOT)), new ItemStack(block, 1, type.ordinal())); }
    }

    private static void registerForm(Item item, String prefix, String[] subs) {
        for (int meta = 0; meta < subs.length; meta++) {
            String suffix = subs[meta].equals("treated") ? "TreatedWood" : capitalize(subs[meta]);
            OreDictionary.registerOre(prefix + suffix, new ItemStack(item, 1, meta));
        }
    }

    private static String capitalize(String name) {
        StringBuilder result = new StringBuilder();
        for (String part : name.split("_")) { result.append(Character.toUpperCase(part.charAt(0))).append(part.substring(1)); }
        return result.toString();
    }
}
