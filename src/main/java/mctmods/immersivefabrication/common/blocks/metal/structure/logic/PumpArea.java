package mctmods.immersivefabrication.common.blocks.metal.structure.logic;

import mctmods.immersivefabrication.common.util.FluidBlocks;
import mctmods.immersivefabrication.common.util.PumpFaces;
import mctmods.immersivefabrication.common.util.PumpSources;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;

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

    @Nullable BlockPos target() { return closed.isEmpty() ? null : closed.get(closed.size() - 1); }

    void dropTarget() { closed.remove(closed.size() - 1); }

    void clear() {
        open.clear();
        closed.clear();
        seen.clear();
        fluid = null;
        scanning = false;
    }

    void restart(BlockPos origin, int[] sideConfig) {
        clear();
        for (EnumFacing side : EnumFacing.VALUES) {
            if (sideConfig[side.getIndex()] != PumpFaces.INPUT) { continue; }
            BlockPos start = origin.offset(side);
            if (seen.add(start)) { open.addLast(start); }
        }
        scanning = !open.isEmpty();
    }

    void scan(World world) {
        if (!scanning) { return; }
        int steps = 0;
        while (steps++ < STEPS && closed.size() < CLOSED_MAX && !open.isEmpty()) {
            BlockPos next = open.pollFirst();
            Fluid here = FluidBlocks.fluidAt(world, next);
            if (!accepts(here)) { continue; }
            fluid = here;
            if (FluidBlocks.source(world, next) != null) { closed.add(next); }
            for (EnumFacing side : EnumFacing.VALUES) {
                BlockPos around = next.offset(side);
                if (accepts(FluidBlocks.fluidAt(world, around)) && seen.add(around)) { open.addLast(around); }
            }
        }
        scanning = closed.size() < CLOSED_MAX && !open.isEmpty();
    }

    private boolean accepts(@Nullable Fluid candidate) { return candidate != null && PumpSources.pumps(candidate) && (fluid == null || candidate == fluid); }
}
