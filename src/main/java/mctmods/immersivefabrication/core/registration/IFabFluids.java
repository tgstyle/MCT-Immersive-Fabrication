package mctmods.immersivefabrication.core.registration;

import com.immersiveconvergence.api.fluid.BaseFluid;
import com.immersiveconvergence.api.fluid.FluidEntry;
import com.immersiveconvergence.api.fluid.FluidRegisters;
import com.immersiveconvergence.api.registration.BlockEntry;

import mctmods.immersivefabrication.ImmersiveFabrication;
import mctmods.immersivefabrication.common.fluids.IFabFluidBlock;

import com.google.common.collect.ImmutableList;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public final class IFabFluids implements FluidRegisters {
    private static final IFabFluids INSTANCE = new IFabFluids();
    public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(Registries.FLUID, ImmersiveFabrication.MODID);
    public static final DeferredRegister<FluidType> FLUID_TYPES = DeferredRegister.create(NeoForgeRegistries.Keys.FLUID_TYPES, ImmersiveFabrication.MODID);
    public static final FluidEntry CREOSOTE = make("creosote", 1100, 3000, 40, 400);
    public static final FluidEntry BIODIESEL = make("biodiesel", 789, 1000, 60, 200);
    public static final FluidEntry RAW_COKE_OVEN_GAS = make("raw_coke_oven_gas", -700, 800, 100, 300);
    public static final List<FluidEntry> ENTRIES = List.of(CREOSOTE, BIODIESEL, RAW_COKE_OVEN_GAS);

    private IFabFluids() {}

    private static FluidEntry make(String name, int density, int viscosity, int flammability, int fireSpread) {
        return FluidEntry.make(INSTANCE, name, 0, texture(name + "_still"), texture(name + "_flow"), (entry, props) -> new IFabFluidBlock(entry, props, flammability, fireSpread), BaseFluid::new, BaseFluid.Flowing::new, type -> type.density(density).viscosity(viscosity), ImmutableList.of(), -1);
    }

    private static ResourceLocation texture(String name) { return ResourceLocation.fromNamespaceAndPath(ImmersiveFabrication.MODID, "block/fluid/" + name); }

    public static void register(IEventBus bus) {
        FLUIDS.register(bus);
        FLUID_TYPES.register(bus);
    }

    @Override public DeferredRegister<Fluid> fluids() { return FLUIDS; }

    @Override public DeferredRegister<FluidType> fluidTypes() { return FLUID_TYPES; }

    @Override public DeferredRegister.Items items() { return IFabItems.REGISTER; }

    @Override public <T extends Block> BlockEntry<T> block(String name, Supplier<BlockBehaviour.Properties> properties, Function<BlockBehaviour.Properties, T> make) { return new BlockEntry<>(IFabBlocks.REGISTER, name, properties, make); }
}
