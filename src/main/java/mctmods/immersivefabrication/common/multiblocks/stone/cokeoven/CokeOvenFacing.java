package mctmods.immersivefabrication.common.multiblocks.stone.cokeoven;

import net.minecraft.core.Direction;

public final class CokeOvenFacing {

    private CokeOvenFacing() {}

    public static float yRotation(Direction facing) {
        return switch (facing) {
            case SOUTH -> 180.0F;
            case WEST -> 90.0F;
            case EAST -> -90.0F;
            default -> 0.0F;
        };
    }
}
