package mctmods.immersivefabrication.core.registration;

import com.immersiveconvergence.api.wires.fittings.ICFittingBlockEntity;

import mctmods.immersivefabrication.ImmersiveFabrication;
import mctmods.immersivefabrication.common.blocks.fittings.anchor.logic.AnchorBlockEntity;
import mctmods.immersivefabrication.common.blocks.fittings.insulatorhv.logic.InsulatorHvBlockEntity;
import mctmods.immersivefabrication.common.blocks.fittings.insulatorlv.logic.InsulatorLvBlockEntity;
import mctmods.immersivefabrication.common.blocks.fittings.insulatormv.logic.InsulatorMvBlockEntity;
import mctmods.immersivefabrication.common.blocks.fittings.terminalhv.logic.TerminalHvBlockEntity;
import mctmods.immersivefabrication.common.blocks.fittings.terminallv.logic.TerminalLvBlockEntity;
import mctmods.immersivefabrication.common.blocks.fittings.terminalmv.logic.TerminalMvBlockEntity;
import mctmods.immersivefabrication.common.blocks.fittings.transformer.logic.TransformerBlockEntity;
import mctmods.immersivefabrication.common.blocks.fittings.transformerhv.logic.TransformerHvBlockEntity;
import mctmods.immersivefabrication.common.blocks.metal.structure.StructureType;
import mctmods.immersivefabrication.common.blocks.metal.structure.logic.FluidPipeBlockEntity;
import mctmods.immersivefabrication.common.blocks.metal.structure.logic.HeatingElementBlockEntity;
import mctmods.immersivefabrication.common.blocks.metal.structure.logic.IndustrialPumpBlockEntity;
import mctmods.immersivefabrication.core.IFabCommonConfig;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

@SuppressWarnings("DataFlowIssue") public final class IFabBlockEntities {
    private static final DeferredRegister<BlockEntityType<?>> REGISTER = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, ImmersiveFabrication.MODID);
    private static final DeferredRegister<BlockEntityType<?>> WIRE_REGISTER = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, ImmersiveFabrication.MODID);
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<FluidPipeBlockEntity>> FLUID_PIPE = REGISTER.register("fluid_pipe", () -> BlockEntityType.Builder.of(FluidPipeBlockEntity::new, IFabBlocks.STRUCTURE.get(StructureType.FLUID_PIPE).get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<HeatingElementBlockEntity>> HEATING_ELEMENT = REGISTER.register("heating_element", () -> BlockEntityType.Builder.of(HeatingElementBlockEntity::new, IFabBlocks.STRUCTURE.get(StructureType.HEATING_ELEMENT).get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<IndustrialPumpBlockEntity>> INDUSTRIAL_PUMP = REGISTER.register("industrial_pump", () -> BlockEntityType.Builder.of(IndustrialPumpBlockEntity::new, IFabBlocks.STRUCTURE.get(StructureType.INDUSTRIAL_PUMP).get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TerminalLvBlockEntity>> TERMINAL_LV = WIRE_REGISTER.register("terminal_lv", () -> BlockEntityType.Builder.of(TerminalLvBlockEntity::new, IFabBlocks.TERMINAL_LV.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<InsulatorLvBlockEntity>> INSULATOR_LV = WIRE_REGISTER.register("insulator_lv", () -> BlockEntityType.Builder.of(InsulatorLvBlockEntity::new, IFabBlocks.INSULATOR_LV.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TerminalMvBlockEntity>> TERMINAL_MV = WIRE_REGISTER.register("terminal_mv", () -> BlockEntityType.Builder.of(TerminalMvBlockEntity::new, IFabBlocks.TERMINAL_MV.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<InsulatorMvBlockEntity>> INSULATOR_MV = WIRE_REGISTER.register("insulator_mv", () -> BlockEntityType.Builder.of(InsulatorMvBlockEntity::new, IFabBlocks.INSULATOR_MV.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TerminalHvBlockEntity>> TERMINAL_HV = WIRE_REGISTER.register("terminal_hv", () -> BlockEntityType.Builder.of(TerminalHvBlockEntity::new, IFabBlocks.TERMINAL_HV.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<InsulatorHvBlockEntity>> INSULATOR_HV = WIRE_REGISTER.register("insulator_hv", () -> BlockEntityType.Builder.of(InsulatorHvBlockEntity::new, IFabBlocks.INSULATOR_HV.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<AnchorBlockEntity>> ANCHOR = WIRE_REGISTER.register("anchor", () -> BlockEntityType.Builder.of(AnchorBlockEntity::new, IFabBlocks.ANCHOR.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TransformerBlockEntity>> TRANSFORMER = WIRE_REGISTER.register("transformer", () -> BlockEntityType.Builder.of(TransformerBlockEntity::new, IFabBlocks.TRANSFORMER.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TransformerHvBlockEntity>> TRANSFORMER_HV = WIRE_REGISTER.register("transformer_hv", () -> BlockEntityType.Builder.of(TransformerHvBlockEntity::new, IFabBlocks.TRANSFORMER_HV.get()).build(null));

    private IFabBlockEntities() {}

    public static void register(IEventBus bus) {
        REGISTER.register(bus);
        if (IFabCommonConfig.wires()) { WIRE_REGISTER.register(bus); }
    }

    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, FLUID_PIPE.get(), FluidPipeBlockEntity::fluidHandler);
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, HEATING_ELEMENT.get(), HeatingElementBlockEntity::energyHandler);
        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, INDUSTRIAL_PUMP.get(), IndustrialPumpBlockEntity::fluidHandler);
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, INDUSTRIAL_PUMP.get(), IndustrialPumpBlockEntity::energyHandler);
        if (IFabCommonConfig.wires()) {
            event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, TERMINAL_LV.get(), ICFittingBlockEntity::getEnergyHandler);
            event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, TERMINAL_MV.get(), ICFittingBlockEntity::getEnergyHandler);
            event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, TERMINAL_HV.get(), ICFittingBlockEntity::getEnergyHandler);
        }
    }
}
