package mctmods.immersivefabrication.common.blocks.metal.cladding;

import mctmods.immersivefabrication.common.util.IFabBlockType;

public enum CladdingType implements IFabBlockType {
    COPPER,
    ALUMINUM,
    LEAD,
    SILVER,
    NICKEL,
    URANIUM,
    CONSTANTAN,
    STEEL,
    IRON,
    GOLD;

    @Override public String id() { return IFabBlockType.super.id() + "_cladding"; }
}
