package mctmods.immersivefabrication.common.blocks.metal.structure.logic;

import net.minecraft.util.EnumFacing;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.FluidTankProperties;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;

import javax.annotation.Nullable;

final class FluidPipeHandler implements IFluidHandler {
    private static final IFluidTankProperties[] PROPERTIES = {new FluidTankProperties(null, Fluid.BUCKET_VOLUME, true, false)};
    private final TileEntityFluidPipe pipe;
    private final EnumFacing side;
    private long origin;
    private boolean located;

    FluidPipeHandler(TileEntityFluidPipe pipe, EnumFacing side) {
        this.pipe = pipe;
        this.side = side;
    }

    @Override public IFluidTankProperties[] getTankProperties() { return PROPERTIES; }

    @Override public int fill(FluidStack resource, boolean doFill) {
        if (!located) {
            origin = pipe.getPos().offset(side).toLong();
            located = true;
        }
        return pipe.fillFrom(resource, origin, doFill);
    }

    @Override @Nullable public FluidStack drain(FluidStack resource, boolean doDrain) { return null; }

    @Override @Nullable public FluidStack drain(int maxDrain, boolean doDrain) { return null; }
}
