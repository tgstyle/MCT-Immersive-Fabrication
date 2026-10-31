package mctmods.immersivefabrication.common.util;

import net.minecraft.core.Direction;
import net.minecraft.util.Mth;

public final class PumpFaces {
    public static final int NONE = -1;
    public static final int INPUT = 0;
    public static final int OUTPUT = 1;
    public static final Direction[] FACES = {Direction.DOWN, Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST};
    private static final int[] POWERS = {1, 3, 9, 27, 81};
    public static final int DEFAULT_CODE = encode(defaults());

    private PumpFaces() {}

    public static int[] defaults() { return new int[] {INPUT, NONE, NONE, NONE, NONE, NONE}; }

    public static int encode(int[] sideConfig) {
        int code = 0;
        for (int i = FACES.length - 1; i >= 0; i--) { code = code * 3 + Mth.clamp(sideConfig[FACES[i].get3DDataValue()] + 1, 0, 2); }
        return code;
    }

    public static int state(int code, Direction face) {
        for (int i = 0; i < FACES.length; i++) { if (FACES[i] == face) { return code / POWERS[i] % 3; } }
        return 0;
    }

    public static int next(Direction face, int config) {
        if (face == Direction.DOWN) { return config == INPUT ? OUTPUT : config == OUTPUT ? NONE : INPUT; }
        return config == NONE ? OUTPUT : config == OUTPUT ? INPUT : NONE;
    }
}
