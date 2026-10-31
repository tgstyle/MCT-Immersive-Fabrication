package mctmods.immersivefabrication.common.util;

import net.minecraft.util.EnumFacing;

public final class PipeArms {
    private static final double LOW = 0.25D;
    private static final double HIGH = 0.75D;
    private static final double EDGE = 0.01D;

    private PipeArms() {}

    public static EnumFacing at(EnumFacing side, float hitX, float hitY, float hitZ) {
        double[] hit = {hitY, hitZ, hitX};
        double along = hit[side.getIndex() >> 1];
        if (along < EDGE || along > 1.0D - EDGE) { return side; }
        EnumFacing best = side;
        double far = 0.0D;
        for (EnumFacing f : EnumFacing.VALUES) {
            if (f.getAxis() == side.getAxis()) { continue; }
            double v = hit[f.getIndex() >> 1];
            double out = f.getAxisDirection() == EnumFacing.AxisDirection.POSITIVE ? v - HIGH : LOW - v;
            if (out > far) {
                far = out;
                best = f;
            }
        }
        return best;
    }
}
