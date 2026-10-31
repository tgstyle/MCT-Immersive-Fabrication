package mctmods.immersivefabrication.common.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.IFluidBlock;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import javax.annotation.Nullable;

public final class FluidBlocks {
    private FluidBlocks() {}

    @Nullable public static Fluid fluidAt(Level level, BlockPos pos) {
        if (!level.isLoaded(pos)) { return null; }
        Block block = level.getBlockState(pos).getBlock();
        if (block instanceof LiquidBlock liquid) { return liquid.getFluid(); }
        return block instanceof IFluidBlock fluidBlock ? fluidBlock.getFluid() : null;
    }

    public static FluidStack source(Level level, BlockPos pos) {
        if (!level.isLoaded(pos)) { return FluidStack.EMPTY; }
        BlockState state = level.getBlockState(pos);
        if (state.getBlock() instanceof IFluidBlock fluidBlock) { return fluidBlock.canDrain(level, pos) ? fluidBlock.drain(level, pos, FluidAction.SIMULATE) : FluidStack.EMPTY; }
        if (!(state.getBlock() instanceof LiquidBlock liquid)) { return FluidStack.EMPTY; }
        return state.getFluidState().isSource() ? new FluidStack(liquid.getFluid(), FluidType.BUCKET_VOLUME) : FluidStack.EMPTY;
    }

    public static void take(Level level, BlockPos pos, BlockState replacement) {
        if (level.getBlockState(pos).getBlock() instanceof IFluidBlock fluidBlock) { fluidBlock.drain(level, pos, FluidAction.EXECUTE); }
        level.setBlock(pos, replacement, Block.UPDATE_ALL);
    }

    public static int adjacentSources(Level level, BlockPos pos, Fluid fluid) {
        int count = 0;
        for (Direction side : Direction.Plane.HORIZONTAL) {
            FluidStack neighbor = source(level, pos.relative(side));
            if (!neighbor.isEmpty() && neighbor.getFluid() == fluid) { count++; }
        }
        return count;
    }
}
