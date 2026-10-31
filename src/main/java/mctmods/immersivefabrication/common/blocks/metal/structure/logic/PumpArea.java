package mctmods.immersivefabrication.common.blocks.metal.structure.logic;

import mctmods.immersivefabrication.common.util.FluidBlocks;
import mctmods.immersivefabrication.common.util.PumpFaces;
import mctmods.immersivefabrication.common.util.PumpSources;

import com.immersiveconvergence.api.multiblock.util.DirectionUtils;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import javax.annotation.Nullable;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

final class PumpArea {
    private static final int CLOSED_MAX = 2048;
    private static final int STEPS = 64;
    private final ArrayDeque<BlockPos> open = new ArrayDeque<>();
    private final List<BlockPos> closed = new ArrayList<>();
    private final Set<BlockPos> seen = new HashSet<>();
    @Nullable private Fluid fluid;
    private boolean scanning;

    boolean scanning() { return scanning; }

    @Nullable BlockPos target() { return closed.isEmpty() ? null : closed.getLast(); }

    void dropTarget() { closed.removeLast(); }

    void clear() {
        open.clear();
        closed.clear();
        seen.clear();
        fluid = null;
        scanning = false;
    }

    void restart(BlockPos origin, int[] sideConfig) {
        clear();
        for (Direction side : DirectionUtils.VALUES) {
            if (sideConfig[side.get3DDataValue()] != PumpFaces.INPUT) { continue; }
            BlockPos start = origin.relative(side);
            if (seen.add(start)) { open.addLast(start); }
        }
        scanning = !open.isEmpty();
    }

    void scan(Level level) {
        if (!scanning) { return; }
        int steps = 0;
        while (steps++ < STEPS && closed.size() < CLOSED_MAX && !open.isEmpty()) {
            BlockPos next = open.pollFirst();
            Fluid here = FluidBlocks.fluidAt(level, next);
            if (!accepts(here)) { continue; }
            fluid = here;
            if (!FluidBlocks.source(level, next).isEmpty()) { closed.add(next); }
            for (Direction side : DirectionUtils.VALUES) {
                BlockPos around = next.relative(side);
                if (accepts(FluidBlocks.fluidAt(level, around)) && seen.add(around)) { open.addLast(around); }
            }
        }
        scanning = closed.size() < CLOSED_MAX && !open.isEmpty();
    }

    private boolean accepts(@Nullable Fluid candidate) { return candidate != null && PumpSources.pumps(candidate) && (fluid == null || candidate == fluid); }
}
