package mctmods.immersivefabrication.common.multiblocks.stone.cokeoven;

import net.minecraft.util.EnumFacing;

public final class CokeOvenFacing {

    private CokeOvenFacing() {}

    public static float yRotation(EnumFacing facing) {
        switch (facing) {
            case SOUTH: return 180.0F;
            case WEST: return 90.0F;
            case EAST: return -90.0F;
            default: return 0.0F;
        }
    }
}
