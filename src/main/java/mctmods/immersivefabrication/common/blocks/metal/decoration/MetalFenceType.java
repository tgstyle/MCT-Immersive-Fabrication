package mctmods.immersivefabrication.common.blocks.metal.decoration;

import mctmods.immersivefabrication.common.util.IFabBlockType;

public enum MetalFenceType implements IFabBlockType {
    STEEL,
    ALUMINUM;

    @Override public String id() { return IFabBlockType.super.id() + "_fence"; }
}
