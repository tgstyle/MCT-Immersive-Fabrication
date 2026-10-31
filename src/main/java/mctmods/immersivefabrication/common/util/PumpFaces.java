package mctmods.immersivefabrication.common.util;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.MathHelper;

public final class PumpFaces {
    public static final int NONE = -1;
    public static final int INPUT = 0;
    public static final int OUTPUT = 1;
    public static final EnumFacing[] FACES = {EnumFacing.DOWN, EnumFacing.NORTH, EnumFacing.SOUTH, EnumFacing.WEST, EnumFacing.EAST};
    private static final int[] POWERS = {1, 3, 9, 27, 81};
    public static final int DEFAULT_CODE = encode(defaults());

    private PumpFaces() {}

    public static int[] defaults() { return new int[] {INPUT, NONE, NONE, NONE, NONE, NONE}; }

    public static int encode(int[] sideConfig) {
        int code = 0;
        for (int i = FACES.length - 1; i >= 0; i--) { code = code * 3 + MathHelper.clamp(sideConfig[FACES[i].getIndex()] + 1, 0, 2); }
        return code;
    }

    public static int state(int code, EnumFacing face) {
        for (int i = 0; i < FACES.length; i++) { if (FACES[i] == face) { return code / POWERS[i] % 3; } }
        return 0;
    }

    public static int next(EnumFacing face, int config) {
        if (face == EnumFacing.DOWN) { return config == INPUT ? OUTPUT : config == OUTPUT ? NONE : INPUT; }
        return config == NONE ? OUTPUT : config == OUTPUT ? INPUT : NONE;
    }
}
