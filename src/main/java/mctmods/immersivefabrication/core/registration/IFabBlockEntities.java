package mctmods.immersivefabrication.core.registration;

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
import mctmods.immersivefabrication.common.blocks.metal.structure.logic.FluidPipeBlockEntity;
import mctmods.immersivefabrication.common.blocks.metal.structure.logic.HeatingElementBlockEntity;
import mctmods.immersivefabrication.common.blocks.metal.structure.logic.IndustrialPumpBlockEntity;
import mctmods.immersivefabrication.core.IFabCommonConfig;

import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@SuppressWarnings("ConstantConditions") public final class IFabBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> REGISTER = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, ImmersiveFabrication.MODID);
    private static final DeferredRegister<BlockEntityType<?>> WIRE_REGISTER = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, ImmersiveFabrication.MODID);
    public static final RegistryObject<BlockEntityType<FluidPipeBlockEntity>> FLUID_PIPE = REGISTER.register("fluid_pipe", () -> BlockEntityType.Builder.of(FluidPipeBlockEntity::new, IFabBlocks.FLUID_PIPE.get()).build(null));
    public static final RegistryObject<BlockEntityType<HeatingElementBlockEntity>> HEATING_ELEMENT = REGISTER.register("heating_element", () -> BlockEntityType.Builder.of(HeatingElementBlockEntity::new, IFabBlocks.HEATING_ELEMENT.get()).build(null));
    public static final RegistryObject<BlockEntityType<IndustrialPumpBlockEntity>> INDUSTRIAL_PUMP = REGISTER.register("industrial_pump", () -> BlockEntityType.Builder.of(IndustrialPumpBlockEntity::new, IFabBlocks.INDUSTRIAL_PUMP.get()).build(null));
    public static final RegistryObject<BlockEntityType<TerminalLvBlockEntity>> TERMINAL_LV = WIRE_REGISTER.register("terminal_lv", () -> BlockEntityType.Builder.of(TerminalLvBlockEntity::new, IFabBlocks.TERMINAL_LV.get()).build(null));
    public static final RegistryObject<BlockEntityType<InsulatorLvBlockEntity>> INSULATOR_LV = WIRE_REGISTER.register("insulator_lv", () -> BlockEntityType.Builder.of(InsulatorLvBlockEntity::new, IFabBlocks.INSULATOR_LV.get()).build(null));
    public static final RegistryObject<BlockEntityType<TerminalMvBlockEntity>> TERMINAL_MV = WIRE_REGISTER.register("terminal_mv", () -> BlockEntityType.Builder.of(TerminalMvBlockEntity::new, IFabBlocks.TERMINAL_MV.get()).build(null));
    public static final RegistryObject<BlockEntityType<InsulatorMvBlockEntity>> INSULATOR_MV = WIRE_REGISTER.register("insulator_mv", () -> BlockEntityType.Builder.of(InsulatorMvBlockEntity::new, IFabBlocks.INSULATOR_MV.get()).build(null));
    public static final RegistryObject<BlockEntityType<TerminalHvBlockEntity>> TERMINAL_HV = WIRE_REGISTER.register("terminal_hv", () -> BlockEntityType.Builder.of(TerminalHvBlockEntity::new, IFabBlocks.TERMINAL_HV.get()).build(null));
    public static final RegistryObject<BlockEntityType<InsulatorHvBlockEntity>> INSULATOR_HV = WIRE_REGISTER.register("insulator_hv", () -> BlockEntityType.Builder.of(InsulatorHvBlockEntity::new, IFabBlocks.INSULATOR_HV.get()).build(null));
    public static final RegistryObject<BlockEntityType<AnchorBlockEntity>> ANCHOR = WIRE_REGISTER.register("anchor", () -> BlockEntityType.Builder.of(AnchorBlockEntity::new, IFabBlocks.ANCHOR.get()).build(null));
    public static final RegistryObject<BlockEntityType<TransformerBlockEntity>> TRANSFORMER = WIRE_REGISTER.register("transformer", () -> BlockEntityType.Builder.of(TransformerBlockEntity::new, IFabBlocks.TRANSFORMER.get()).build(null));
    public static final RegistryObject<BlockEntityType<TransformerHvBlockEntity>> TRANSFORMER_HV = WIRE_REGISTER.register("transformer_hv", () -> BlockEntityType.Builder.of(TransformerHvBlockEntity::new, IFabBlocks.TRANSFORMER_HV.get()).build(null));

    private IFabBlockEntities() {}

    public static void register(IEventBus bus) {
        REGISTER.register(bus);
        if (IFabCommonConfig.wires()) { WIRE_REGISTER.register(bus); }
    }
}
