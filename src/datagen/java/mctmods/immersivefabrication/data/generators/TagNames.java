package mctmods.immersivefabrication.data.generators;

import mctmods.immersivefabrication.common.blocks.metal.cladding.CladdingType;
import mctmods.immersivefabrication.common.blocks.metal.ore.OreType;
import mctmods.immersivefabrication.common.blocks.metal.storage.StorageType;
import mctmods.immersivefabrication.common.blocks.wooden.TreatedWoodType;
import mctmods.immersivefabrication.common.util.IFabBlockType;
import mctmods.immersivefabrication.common.util.IFabMetals;

import java.util.Arrays;
import java.util.Locale;

public final class TagNames {
    public static final String[] STORAGE_METALS = names(StorageType.values());
    public static final String[] ORE_METALS = names(OreType.values());
    public static final String[] CLADDING_METALS = names(CladdingType.values());
    public static final String[] FRAME_METALS = {"steel", "aluminum"};
    public static final String[] SCAFFOLDING_TYPES = {"standard", "grate_top", "wooden_top"};
    public static final String[] CONCRETES = {"concrete", "flaxcrete"};
    public static final String[] TREATED_WOOD = Arrays.stream(TreatedWoodType.values()).map(type -> type.name().toLowerCase(Locale.ROOT)).toArray(String[]::new);
    public static final String[] ENGINEERING = {"coil_acsr", "generator", "radiator", "heavy_fabrication", "light_fabrication", "redstone_fabrication"};
    public static final String[] INGOTS = {"aluminum", "lead", "silver", "nickel", "uranium", "constantan", "steel", "graphite"};
    public static final String[] NUGGETS = {"copper", "aluminum", "lead", "silver", "nickel", "uranium", "constantan", "steel"};
    public static final String[] DUSTS = {"copper", "aluminum", "lead", "silver", "nickel", "uranium", "constantan", "steel", "iron", "gold", "coke", "graphite", "niter", "sulfur"};
    public static final String[] PLATES = IFabMetals.PLATES.toArray(String[]::new);
    public static final String[] RAW_ORES = Arrays.stream(ORE_METALS).map(metal -> "raw_" + metal).toArray(String[]::new);

    private TagNames() {}

    private static String[] names(IFabBlockType[] types) { return Arrays.stream(types).map(type -> type.name().toLowerCase(Locale.ROOT)).toArray(String[]::new); }
}
