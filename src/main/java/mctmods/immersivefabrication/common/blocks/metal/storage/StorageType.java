package mctmods.immersivefabrication.common.blocks.metal.storage;

import mctmods.immersivefabrication.common.util.IFabBlockType;

public enum StorageType implements IFabBlockType {
    ALUMINUM(1),
    LEAD(2),
    SILVER(2),
    NICKEL(2),
    URANIUM(2),
    CONSTANTAN(2),
    STEEL(2);

    public final int tier;

    StorageType(int tier) { this.tier = tier; }

    @Override public String id() { return IFabBlockType.super.id() + "_block"; }
}
