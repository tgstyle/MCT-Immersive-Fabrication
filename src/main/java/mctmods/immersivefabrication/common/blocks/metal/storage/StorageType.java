package mctmods.immersivefabrication.common.blocks.metal.storage;

import mctmods.immersivefabrication.common.util.IFabBlockEnum;

public enum StorageType implements IFabBlockEnum {
    COPPER(1),
    ALUMINUM(1),
    LEAD(2),
    SILVER(2),
    NICKEL(2),
    URANIUM(2),
    CONSTANTAN(2),
    STEEL(2);

    public final int harvestLevel;

    StorageType(int harvestLevel) { this.harvestLevel = harvestLevel; }
}
