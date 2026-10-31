package mctmods.immersivefabrication.common.blocks.metal.decoration;

import mctmods.immersivefabrication.common.util.IFabBlockType;

import java.util.Locale;

public enum ScaffoldingType implements IFabBlockType {
    STEEL_STANDARD,
    STEEL_GRATE_TOP,
    STEEL_WOODEN_TOP,
    ALUMINUM_STANDARD,
    ALUMINUM_GRATE_TOP,
    ALUMINUM_WOODEN_TOP;

    @Override public String id() {
        String lower = name().toLowerCase(Locale.ROOT);
        int split = lower.indexOf('_');
        return lower.substring(0, split) + "_scaffolding_" + lower.substring(split + 1);
    }
}
