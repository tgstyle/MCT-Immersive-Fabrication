package mctmods.immersivefabrication.common.multiblocks.stone.cokeoven;

import mctmods.immersivefabrication.core.registration.IFabFluids;

import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

import javax.annotation.Nullable;

public final class CokeOvenFluids {
    private CokeOvenFluids() {}

    @Nullable public static Fluid flueGas() { return FluidRegistry.getFluid("fluegas"); }

    public static Fluid rawGas() { return IFabFluids.cokeOvenGas; }
}
