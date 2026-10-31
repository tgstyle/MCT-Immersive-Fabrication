package mctmods.immersivefabrication.common.blocks.wooden;

import mctmods.immersivefabrication.common.util.IFabBlockType;

public enum TreatedWoodType implements IFabBlockType {
    HORIZONTAL,
    VERTICAL,
    PACKAGED;

    @Override public String id() { return "treated_wood_" + IFabBlockType.super.id(); }
}
