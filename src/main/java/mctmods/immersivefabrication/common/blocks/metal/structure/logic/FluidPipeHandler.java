package mctmods.immersivefabrication.common.blocks.metal.structure.logic;

import net.minecraft.core.Direction;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

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

    @Override public int fill(@Nonnull FluidStack resource, @Nonnull FluidAction action) { return pipe.fillFrom(resource, origin, action.execute()); }

    @Override @Nonnull public FluidStack drain(@Nonnull FluidStack resource, @Nonnull FluidAction action) { return FluidStack.EMPTY; }

    @Override @Nonnull public FluidStack drain(int maxDrain, @Nonnull FluidAction action) { return FluidStack.EMPTY; }
}
