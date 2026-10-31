package mctmods.immersivefabrication.common.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.BlockHitResult;

public final class PipeArms {
    private static final double LOW = 0.25D;
    private static final double HIGH = 0.75D;
    private static final double EDGE = 0.01D;

    private PipeArms() {}

    public static Direction at(BlockPos pos, BlockHitResult hit) {
        Direction side = hit.getDirection();
        double x = hit.getLocation().x - pos.getX();
        double y = hit.getLocation().y - pos.getY();
        double z = hit.getLocation().z - pos.getZ();
        double along = side.getAxis().choose(x, y, z);
        if (along < EDGE || along > 1.0D - EDGE) { return side; }
        Direction best = side;
        double far = 0.0D;
        for (Direction f : Direction.values()) {
            if (f.getAxis() == side.getAxis()) { continue; }
            double v = f.getAxis().choose(x, y, z);
            double out = f.getAxisDirection() == Direction.AxisDirection.POSITIVE ? v - HIGH : LOW - v;
            if (out > far) {
                far = out;
                best = f;
            }
        }
        return best;
    }
}
