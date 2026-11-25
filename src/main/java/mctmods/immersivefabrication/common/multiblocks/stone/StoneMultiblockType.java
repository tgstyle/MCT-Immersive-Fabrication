package mctmods.immersivefabrication.common.multiblocks.stone;

import mctmods.immersivefabrication.common.util.IFabBlockEnum;

public enum StoneMultiblockType implements IFabBlockEnum {
    COKE_OVEN,
    COKE_OVEN_SLAVE;

    @Override public boolean listForCreative() { return false; }
}
