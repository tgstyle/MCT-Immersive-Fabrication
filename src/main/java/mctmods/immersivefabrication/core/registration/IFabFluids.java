package mctmods.immersivefabrication.core.registration;

import mctmods.immersivefabrication.ImmersiveFabrication;
import mctmods.immersivefabrication.common.fluids.IFabFluidBlock;

import net.minecraft.block.Block;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;
import java.util.List;

@EventBusSubscriber(modid = ImmersiveFabrication.MODID)
public final class IFabFluids {
    public static final List<IFabFluidBlock> BLOCKS = new ArrayList<>();
    public static Fluid creosote;
    public static Fluid biodiesel;

    private IFabFluids() {}

    public static void register() {
        creosote = fluid("creosote", 1100, 3000, 40, 400);
        biodiesel = fluid("biodiesel", 789, 1000, 60, 200);
    }

    private static Fluid fluid(String name, int density, int viscosity, int flammability, int fireSpread) {
        if (FluidRegistry.isFluidRegistered(name)) { return FluidRegistry.getFluid(name); }
        ResourceLocation still = new ResourceLocation(ImmersiveFabrication.MODID, "blocks/fluid/" + name + "_still");
        ResourceLocation flowing = new ResourceLocation(ImmersiveFabrication.MODID, "blocks/fluid/" + name + "_flow");
        Fluid fluid = new Fluid(name, still, flowing).setDensity(density).setViscosity(viscosity);
        FluidRegistry.registerFluid(fluid);
        FluidRegistry.addBucketForFluid(fluid);
        BLOCKS.add(new IFabFluidBlock(fluid, "fluid_" + name, flammability, fireSpread));
        return fluid;
    }

    @SubscribeEvent public static void onRegisterBlocks(RegistryEvent.Register<Block> event) {
        for (IFabFluidBlock block : BLOCKS) { event.getRegistry().register(block); }
    }
}
