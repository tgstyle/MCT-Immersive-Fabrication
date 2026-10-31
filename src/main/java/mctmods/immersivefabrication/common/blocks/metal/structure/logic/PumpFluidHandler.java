package mctmods.immersivefabrication.common.blocks.metal.structure.logic;

import mctmods.immersivefabrication.common.util.PumpFaces;

import net.minecraft.core.Direction;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import javax.annotation.Nonnull;

final class PumpFluidHandler implements IFluidHandler {
    private final IndustrialPumpBlockEntity pump;
    private final Direction side;

    PumpFluidHandler(IndustrialPumpBlockEntity pump, Direction side) {
        this.pump = pump;
        this.side = side;
    }

    @Override public int getTanks() { return 1; }

    @Override @Nonnull public FluidStack getFluidInTank(int tank) { return pump.tank.getFluid(); }

    @Override public int getTankCapacity(int tank) { return pump.tank.getCapacity(); }

    @Override public boolean isFluidValid(int tank, @Nonnull FluidStack stack) { return pump.tank.isFluidValid(tank, stack); }

    @Override public int fill(FluidStack resource, FluidAction action) { return pump.config(side) != PumpFaces.INPUT ? 0 : pump.tank.fill(resource, action); }

    @Override @Nonnull public FluidStack drain(FluidStack resource, FluidAction action) { return pump.config(side) != PumpFaces.OUTPUT ? FluidStack.EMPTY : pump.tank.drain(resource, action); }

    @Override @Nonnull public FluidStack drain(int maxDrain, FluidAction action) { return pump.config(side) != PumpFaces.OUTPUT ? FluidStack.EMPTY : pump.tank.drain(maxDrain, action); }
}
