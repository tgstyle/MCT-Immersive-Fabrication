package mctmods.immersivefabrication.common.util;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidBlock;

import javax.annotation.Nullable;

public final class FluidBlocks {
    private FluidBlocks() {}

    @Nullable public static Fluid fluidAt(World world, BlockPos pos) { return world.isBlockLoaded(pos) ? FluidRegistry.lookupFluidForBlock(world.getBlockState(pos).getBlock()) : null; }

    @Nullable public static FluidStack source(World world, BlockPos pos) {
        if (!world.isBlockLoaded(pos)) { return null; }
        IBlockState state = world.getBlockState(pos);
        Block block = state.getBlock();
        Fluid fluid = FluidRegistry.lookupFluidForBlock(block);
        if (fluid == null) { return null; }
        if (block instanceof IFluidBlock) { return ((IFluidBlock)block).canDrain(world, pos) ? ((IFluidBlock)block).drain(world, pos, false) : null; }
        return block.getMetaFromState(state) == 0 ? new FluidStack(fluid, Fluid.BUCKET_VOLUME) : null;
    }

    public static void take(World world, BlockPos pos, IBlockState replacement) {
        Block block = world.getBlockState(pos).getBlock();
        if (block instanceof IFluidBlock) { ((IFluidBlock)block).drain(world, pos, true); }
        world.setBlockState(pos, replacement);
    }

    public static int adjacentSources(World world, BlockPos pos, Fluid fluid) {
        int count = 0;
        for (EnumFacing side : EnumFacing.HORIZONTALS) {
            FluidStack neighbor = source(world, pos.offset(side));
            if (neighbor != null && neighbor.getFluid() == fluid) { count++; }
        }
        return count;
    }
}
