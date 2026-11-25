package mctmods.immersivefabrication.common.multiblocks.stone.cokeoven;

import mctmods.immersivefabrication.core.registration.IFabFluids;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.registries.ForgeRegistries;

public final class CokeOvenFluids {
    private static final ResourceLocation FLUE_GAS = ResourceLocation.fromNamespaceAndPath("immersivetechnology", "flue_gas");

    private CokeOvenFluids() {}

    public static Fluid flueGas() {
        Fluid fluid = ForgeRegistries.FLUIDS.getValue(FLUE_GAS);
        return fluid == null ? Fluids.EMPTY : fluid;
    }

    public static Fluid rawGas() { return IFabFluids.RAW_COKE_OVEN_GAS.getStillGetter().get(); }
}
