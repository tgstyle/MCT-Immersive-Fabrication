package mctmods.immersivefabrication.common.blocks.metal.structure.logic;

import net.minecraft.core.Direction;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.capability.IFluidHandler;
import javax.annotation.Nonnull;

final class FluidPipeHandler implements IFluidHandler {
    private final FluidPipeBlockEntity pipe;
    private final long origin;

    FluidPipeHandler(FluidPipeBlockEntity pipe, Direction side) {
        this.pipe = pipe;
        this.origin = pipe.getBlockPos().relative(side).asLong();
    }

    @Override public int getTanks() { return 1; }

    @Override @Nonnull public FluidStack getFluidInTank(int tank) { return FluidStack.EMPTY; }

    @Override public int getTankCapacity(int tank) { return FluidType.BUCKET_VOLUME; }

    @Override public boolean isFluidValid(int tank, @Nonnull FluidStack stack) { return true; }

    @Override public int fill(FluidStack resource, FluidAction action) { return pipe.fillFrom(resource, origin, action.execute()); }

    @Override @Nonnull public FluidStack drain(FluidStack resource, FluidAction action) { return FluidStack.EMPTY; }

    @Override @Nonnull public FluidStack drain(int maxDrain, FluidAction action) { return FluidStack.EMPTY; }
}
