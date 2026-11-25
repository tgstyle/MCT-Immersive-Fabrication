package mctmods.immersivefabrication.common.multiblocks.stone.cokeoven;

import mctmods.immersivefabrication.core.registration.IFabFluids;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluid;

public final class CokeOvenFluids {
    private static final ResourceLocation FLUE_GAS = ResourceLocation.fromNamespaceAndPath("immersivetechnology", "flue_gas");

    private CokeOvenFluids() {}

    public static Fluid flueGas() { return BuiltInRegistries.FLUID.get(FLUE_GAS); }

    public static Fluid rawGas() { return IFabFluids.RAW_COKE_OVEN_GAS.getStill(); }
}
