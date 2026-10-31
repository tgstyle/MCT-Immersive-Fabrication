package mctmods.immersivefabrication.client.models;

import com.google.common.collect.ImmutableSet;
import com.mojang.math.Transformation;

import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

final class FluidPipeShapes {
    static final int KEYS = 256;
    private static final int[] CANONICAL = {1, 5, 13, 37, 15, 45, 47};
    private static final String[][] GROUPS = {{"cube_stopper", "cube_con_yMin"}, {"cube_curve", "cube_con_yMin", "cube_con_zMin"}, {"cube_tcross", "cube_con_yMin", "cube_con_zMin", "cube_con_zMax"}, {"cube_tcurve", "cube_con_yMin", "cube_con_zMin", "cube_con_xMax"}, {"cube_cross", "cube_con_yMin", "cube_con_yMax", "cube_con_zMin", "cube_con_zMax"}, {"cube_tcross2", "cube_con_yMin", "cube_con_zMin", "cube_con_zMax", "cube_con_xMax"}, {"cube_tcross3", "cube_con_yMin", "cube_con_yMax", "cube_con_zMin", "cube_con_zMax", "cube_con_xMax"}};
    private static final String[] CON = {"cube_con_yMin", "cube_con_yMax", "cube_con_zMin", "cube_con_zMax", "cube_con_xMin", "cube_con_xMax"};
    private static final String[] STRAIGHT = {"cube_pipe_y", "cube_pipe_z", "cube_pipe_x"};
    private static final String[] NAMES = {"cube", "cube_con_yMin", "cube_con_yMax", "cube_con_zMin", "cube_con_zMax", "cube_con_xMin", "cube_con_xMax", "cube_pipe_x", "cube_pipe_y", "cube_pipe_z", "cube_cross", "cube_curve", "cube_tcross", "cube_tcross2", "cube_tcross3", "cube_tcurve", "cube_stopper"};
    final List<Object2BooleanMap<String>> visibility = new ArrayList<>();
    final List<Transformation> transforms = new ArrayList<>();
    final int[] bake = new int[KEYS];
    private final List<int[]> faces = new ArrayList<>();
    private final List<Transformation> rotations = new ArrayList<>();

    FluidPipeShapes() {
        collectRotations();
        for (int key = 0; key < KEYS; key++) {
            int mask = key & 63;
            int axis = mask == 3 ? 0 : mask == 12 ? 1 : mask == 48 ? 2 : -1;
            if (key >= 64 && axis < 0) {
                bake[key] = bake[mask];
                continue;
            }
            bake[key] = visibility.size();
            if (axis >= 0) { add(straight(axis, key), 0); }
            else if (mask == 0) { add(ImmutableSet.of("cube"), 0); }
            else if (mask == 63) { add(ImmutableSet.copyOf(CON), 0); }
            else { addRotated(mask); }
        }
    }

    private static Set<String> straight(int axis, int key) {
        ImmutableSet.Builder<String> set = ImmutableSet.builder();
        set.add(STRAIGHT[axis]);
        if ((key & 64) != 0) { set.add(CON[axis * 2]); }
        if ((key & 128) != 0) { set.add(CON[axis * 2 + 1]); }
        return set.build();
    }

    private void addRotated(int mask) {
        for (int entry = 0; entry < CANONICAL.length; entry++) {
            if (Integer.bitCount(CANONICAL[entry]) != Integer.bitCount(mask)) { continue; }
            for (int r = 0; r < faces.size(); r++) {
                if (map(faces.get(r), CANONICAL[entry]) == mask) {
                    add(ImmutableSet.copyOf(GROUPS[entry]), r);
                    return;
                }
            }
        }
        throw new IllegalStateException("no pipe shape for mask " + mask);
    }

    private void add(Set<String> visible, int rotation) {
        Object2BooleanMap<String> parts = new Object2BooleanOpenHashMap<>();
        for (String name : NAMES) { parts.put(name, visible.contains(name)); }
        visibility.add(parts);
        transforms.add(rotations.get(rotation));
    }

    private static int map(int[] perm, int mask) {
        int out = 0;
        for (int f = 0; f < 6; f++) { if ((mask >> f & 1) != 0) { out |= 1 << perm[f]; } }
        return out;
    }

    private void collectRotations() {
        Set<Integer> seen = new HashSet<>();
        for (int x = 0; x < 4; x++) {
            for (int y = 0; y < 4; y++) {
                for (int z = 0; z < 4; z++) {
                    Transformation turn = new Transformation(null, new Quaternionf().rotationXYZ(x * Mth.HALF_PI, y * Mth.HALF_PI, z * Mth.HALF_PI), null, null);
                    Matrix4f matrix = turn.getMatrix();
                    int[] perm = new int[6];
                    int code = 0;
                    for (Direction f : Direction.values()) {
                        perm[f.get3DDataValue()] = Direction.rotate(matrix, f).get3DDataValue();
                        code = code * 6 + perm[f.get3DDataValue()];
                    }
                    if (!seen.add(code)) { continue; }
                    faces.add(perm);
                    rotations.add(turn);
                }
            }
        }
    }
}
