package mctmods.immersivefabrication.common.blocks.metal.ore;

import mctmods.immersivefabrication.common.util.IFabBlockType;

public enum OreType implements IFabBlockType {
    ALUMINUM(1),
    LEAD(2),
    SILVER(2),
    NICKEL(2),
    URANIUM(2);

    public final int tier;

    OreType(int tier) { this.tier = tier; }

    @Override public String id() { return IFabBlockType.super.id() + "_ore"; }

    public String deepslateId() { return "deepslate_" + id(); }

    public String rawId() { return "raw_" + IFabBlockType.super.id(); }

    public String rawBlockId() { return rawId() + "_block"; }
}
