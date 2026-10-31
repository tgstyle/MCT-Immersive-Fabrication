package mctmods.immersivefabrication.common.util;

import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;

public final class PipeArms {
    private static final double LOW = 0.25D;
    private static final double HIGH = 0.75D;
    private static final double EDGE = 0.01D;

    private PipeArms() {}

    public static Direction at(Direction side, Vec3 local) {
        double along = side.getAxis().choose(local.x, local.y, local.z);
        if (along < EDGE || along > 1.0D - EDGE) { return side; }
        Direction best = side;
        double far = 0.0D;
        for (Direction f : Direction.values()) {
            if (f.getAxis() == side.getAxis()) { continue; }
            double v = f.getAxis().choose(local.x, local.y, local.z);
            double out = f.getAxisDirection() == Direction.AxisDirection.POSITIVE ? v - HIGH : LOW - v;
            if (out > far) {
                far = out;
                best = f;
            }
        }
        return best;
    }
}
