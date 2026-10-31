package mctmods.immersivefabrication.common.blocks.metal.structure.logic;

import mctmods.immersivefabrication.common.util.PumpFaces;

import net.minecraft.util.EnumFacing;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;

import javax.annotation.Nullable;

final class PumpFluidHandler implements IFluidHandler {
    private final TileEntityIndustrialPump pump;
    private final EnumFacing side;

    PumpFluidHandler(TileEntityIndustrialPump pump, EnumFacing side) {
        this.pump = pump;
        this.side = side;
    }

    @Override public IFluidTankProperties[] getTankProperties() { return pump.tank.getTankProperties(); }

    @Override public int fill(FluidStack resource, boolean doFill) { return resource == null || pump.config(side) != PumpFaces.INPUT ? 0 : pump.tank.fill(resource, doFill); }

    @Override @Nullable public FluidStack drain(FluidStack resource, boolean doDrain) { return resource == null || pump.config(side) != PumpFaces.OUTPUT ? null : pump.tank.drain(resource, doDrain); }

    @Override @Nullable public FluidStack drain(int maxDrain, boolean doDrain) { return pump.config(side) != PumpFaces.OUTPUT ? null : pump.tank.drain(maxDrain, doDrain); }
}
