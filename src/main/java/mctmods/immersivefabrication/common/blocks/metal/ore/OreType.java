package mctmods.immersivefabrication.common.blocks.metal.ore;

import mctmods.immersivefabrication.common.util.IFabBlockEnum;

public enum OreType implements IFabBlockEnum {
    COPPER(1, 8, 40, 72, 8, 100),
    ALUMINUM(1, 4, 40, 85, 8, 100),
    LEAD(2, 6, 8, 36, 4, 100),
    SILVER(2, 8, 8, 40, 4, 80),
    NICKEL(2, 6, 8, 24, 2, 100),
    URANIUM(2, 4, 8, 24, 2, 60);

    public final int harvestLevel;
    public final int veinSize;
    public final int minY;
    public final int maxY;
    public final int veinsPerChunk;
    public final int chance;

    OreType(int harvestLevel, int veinSize, int minY, int maxY, int veinsPerChunk, int chance) {
        this.harvestLevel = harvestLevel;
        this.veinSize = veinSize;
        this.minY = minY;
        this.maxY = maxY;
        this.veinsPerChunk = veinsPerChunk;
        this.chance = chance;
    }
}
