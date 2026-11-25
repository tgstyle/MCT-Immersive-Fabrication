package mctmods.immersivefabrication.core.registration;

import mctmods.immersivefabrication.ImmersiveFabrication;
import mctmods.immersivefabrication.common.blocks.fittings.anchor.AnchorBlock;
import mctmods.immersivefabrication.common.blocks.fittings.insulatorhv.InsulatorHvBlock;
import mctmods.immersivefabrication.common.blocks.fittings.insulatorlv.InsulatorLvBlock;
import mctmods.immersivefabrication.common.blocks.fittings.insulatormv.InsulatorMvBlock;
import mctmods.immersivefabrication.common.blocks.fittings.terminalhv.TerminalHvBlock;
import mctmods.immersivefabrication.common.blocks.fittings.terminallv.TerminalLvBlock;
import mctmods.immersivefabrication.common.blocks.fittings.terminalmv.TerminalMvBlock;
import mctmods.immersivefabrication.common.blocks.fittings.transformer.TransformerBlock;
import mctmods.immersivefabrication.common.blocks.fittings.transformerhv.TransformerHvBlock;
import mctmods.immersivefabrication.common.blocks.metal.cladding.CladdingType;
import mctmods.immersivefabrication.common.blocks.metal.decoration.MetalDecorationType;
import mctmods.immersivefabrication.common.blocks.metal.decoration.MetalFenceType;
import mctmods.immersivefabrication.common.blocks.metal.decoration.ScaffoldingBlock;
import mctmods.immersivefabrication.common.blocks.metal.decoration.ScaffoldingType;
import mctmods.immersivefabrication.common.blocks.metal.ore.OreType;
import mctmods.immersivefabrication.common.blocks.metal.storage.StorageType;
import mctmods.immersivefabrication.common.blocks.metal.structure.FluidPipeBlock;
import mctmods.immersivefabrication.common.blocks.metal.structure.HeatingElementBlock;
import mctmods.immersivefabrication.common.blocks.metal.structure.IndustrialPumpBlock;
import mctmods.immersivefabrication.common.blocks.metal.structure.StructureType;
import mctmods.immersivefabrication.common.blocks.plant.flax.FlaxBlock;
import mctmods.immersivefabrication.common.blocks.stone.ConcreteType;
import mctmods.immersivefabrication.common.blocks.stone.StoneDecorationType;
import mctmods.immersivefabrication.common.blocks.wooden.TreatedLogBlock;
import mctmods.immersivefabrication.common.blocks.wooden.TreatedWoodType;
import mctmods.immersivefabrication.common.util.IFabBlockProps;
import mctmods.immersivefabrication.core.IFabCommonConfig;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

public final class IFabBlocks {
    public static final DeferredRegister.Blocks REGISTER = DeferredRegister.createBlocks(ImmersiveFabrication.MODID);
    public static final Map<String, DeferredBlock<? extends Block>> ALL = new LinkedHashMap<>();
    public static final Map<StorageType, DeferredBlock<Block>> STORAGE = new EnumMap<>(StorageType.class);
    public static final Map<CladdingType, DeferredBlock<Block>> CLADDING = new EnumMap<>(CladdingType.class);
    public static final Map<CladdingType, DeferredBlock<SlabBlock>> CLADDING_SLABS = new EnumMap<>(CladdingType.class);
    public static final Map<OreType, DeferredBlock<Block>> ORES = new EnumMap<>(OreType.class);
    public static final Map<OreType, DeferredBlock<Block>> DEEPSLATE_ORES = new EnumMap<>(OreType.class);
    public static final Map<OreType, DeferredBlock<Block>> RAW_BLOCKS = new EnumMap<>(OreType.class);
    public static final Map<MetalDecorationType, DeferredBlock<Block>> METAL_DECORATION = new EnumMap<>(MetalDecorationType.class);
    public static final Map<MetalFenceType, DeferredBlock<FenceBlock>> METAL_FENCES = new EnumMap<>(MetalFenceType.class);
    public static final Map<ScaffoldingType, DeferredBlock<ScaffoldingBlock>> SCAFFOLDING = new EnumMap<>(ScaffoldingType.class);
    public static final Map<ScaffoldingType, DeferredBlock<SlabBlock>> SCAFFOLDING_SLABS = new EnumMap<>(ScaffoldingType.class);
    public static final Map<ScaffoldingType, DeferredBlock<StairBlock>> SCAFFOLDING_STAIRS = new EnumMap<>(ScaffoldingType.class);
    public static final Map<StoneDecorationType, DeferredBlock<Block>> STONE_DECORATION = new EnumMap<>(StoneDecorationType.class);
    public static final Map<ConcreteType, DeferredBlock<Block>> CONCRETE = new EnumMap<>(ConcreteType.class);
    public static final Map<ConcreteType, DeferredBlock<SlabBlock>> CONCRETE_SLABS = new EnumMap<>(ConcreteType.class);
    public static final Map<ConcreteType, DeferredBlock<StairBlock>> CONCRETE_STAIRS = new EnumMap<>(ConcreteType.class);
    public static final Map<TreatedWoodType, DeferredBlock<Block>> TREATED_WOOD = new EnumMap<>(TreatedWoodType.class);
    public static final Map<TreatedWoodType, DeferredBlock<SlabBlock>> TREATED_WOOD_SLABS = new EnumMap<>(TreatedWoodType.class);
    public static final Map<TreatedWoodType, DeferredBlock<StairBlock>> TREATED_WOOD_STAIRS = new EnumMap<>(TreatedWoodType.class);
    public static final Map<StructureType, DeferredBlock<? extends Block>> STRUCTURE = new EnumMap<>(StructureType.class);
    public static final DeferredBlock<FenceBlock> TREATED_WOOD_FENCE;
    public static final DeferredBlock<FlaxBlock> FLAX = REGISTER.registerBlock("flax", FlaxBlock::new, IFabBlockProps.crop());
    public static final DeferredBlock<TreatedLogBlock> TREATED_LOG;
    public static final Map<String, DeferredBlock<? extends Block>> WIRES = new LinkedHashMap<>();
    private static final DeferredRegister.Blocks WIRE_REGISTER = DeferredRegister.createBlocks(ImmersiveFabrication.MODID);
    public static final DeferredBlock<TerminalLvBlock> TERMINAL_LV = wire("terminal_lv", TerminalLvBlock::new);
    public static final DeferredBlock<InsulatorLvBlock> INSULATOR_LV = wire("insulator_lv", InsulatorLvBlock::new);
    public static final DeferredBlock<TerminalMvBlock> TERMINAL_MV = wire("terminal_mv", TerminalMvBlock::new);
    public static final DeferredBlock<InsulatorMvBlock> INSULATOR_MV = wire("insulator_mv", InsulatorMvBlock::new);
    public static final DeferredBlock<TerminalHvBlock> TERMINAL_HV = wire("terminal_hv", TerminalHvBlock::new);
    public static final DeferredBlock<InsulatorHvBlock> INSULATOR_HV = wire("insulator_hv", InsulatorHvBlock::new);
    public static final DeferredBlock<AnchorBlock> ANCHOR = wire("anchor", AnchorBlock::new);
    public static final DeferredBlock<TransformerBlock> TRANSFORMER = wire("transformer", TransformerBlock::new);
    public static final DeferredBlock<TransformerHvBlock> TRANSFORMER_HV = wire("transformer_hv", TransformerHvBlock::new);

    static {
        registerMetals();
        registerOres();
        registerMetalDecoration();
        registerStone();
        TREATED_WOOD_FENCE = block("treated_wood_fence", FenceBlock::new, IFabBlockProps.wood(2.0F, 5.0F).isViewBlocking((state, level, pos) -> false));
        for (TreatedWoodType type : TreatedWoodType.values()) {
            DeferredBlock<Block> full = block(type.id(), Block::new, IFabBlockProps.wood(2.0F, 5.0F));
            TREATED_WOOD.put(type, full);
            TREATED_WOOD_SLABS.put(type, block(type.id() + "_slab", SlabBlock::new, IFabBlockProps.wood(2.0F, 5.0F)));
            TREATED_WOOD_STAIRS.put(type, stairs(type.id() + "_stairs", full, IFabBlockProps.wood(2.0F, 5.0F)));
        }
        TREATED_LOG = block("treated_log", TreatedLogBlock::new, IFabBlockProps.wood(2.0F, 5.0F));
        STRUCTURE.put(StructureType.FLUID_PIPE, block(StructureType.FLUID_PIPE.id(), FluidPipeBlock::new, IFabBlockProps.metal(3.0F, 15.0F).noOcclusion().dynamicShape()));
        STRUCTURE.put(StructureType.FLUID_PLACER, block(StructureType.FLUID_PLACER.id(), Block::new, IFabBlockProps.metal(3.0F, 15.0F)));
        STRUCTURE.put(StructureType.HEATING_ELEMENT, block(StructureType.HEATING_ELEMENT.id(), HeatingElementBlock::new, IFabBlockProps.metal(3.0F, 15.0F)));
        STRUCTURE.put(StructureType.METAL_BARREL, block(StructureType.METAL_BARREL.id(), Block::new, IFabBlockProps.metal(3.0F, 15.0F)));
        STRUCTURE.put(StructureType.INDUSTRIAL_PUMP, block(StructureType.INDUSTRIAL_PUMP.id(), IndustrialPumpBlock::new, IFabBlockProps.metal(3.0F, 15.0F).noOcclusion().pushReaction(PushReaction.BLOCK)));
    }

    private IFabBlocks() {}

    public static void register(IEventBus bus) {
        REGISTER.register(bus);
        if (IFabCommonConfig.wires()) { WIRE_REGISTER.register(bus); }
    }

    private static void registerMetals() {
        for (StorageType type : StorageType.values()) { STORAGE.put(type, block(type.id(), Block::new, IFabBlockProps.metal(5.0F, 10.0F))); }
        for (CladdingType type : CladdingType.values()) {
            CLADDING.put(type, block(type.id(), Block::new, IFabBlockProps.metal(3.0F, 10.0F)));
            CLADDING_SLABS.put(type, block(type.id() + "_slab", SlabBlock::new, IFabBlockProps.metal(3.0F, 10.0F)));
        }
    }

    private static void registerOres() {
        for (OreType type : OreType.values()) {
            ORES.put(type, block(type.id(), Block::new, IFabBlockProps.stone(3.0F, 5.0F)));
            DEEPSLATE_ORES.put(type, block(type.deepslateId(), Block::new, IFabBlockProps.deepslate(4.5F, 5.0F)));
            RAW_BLOCKS.put(type, block(type.rawBlockId(), Block::new, IFabBlockProps.stone(5.0F, 6.0F)));
        }
    }

    private static void registerMetalDecoration() {
        for (MetalDecorationType type : MetalDecorationType.values()) { METAL_DECORATION.put(type, block(type.id(), Block::new, IFabBlockProps.metal(3.0F, 15.0F))); }
        for (MetalFenceType type : MetalFenceType.values()) { METAL_FENCES.put(type, block(type.id(), FenceBlock::new, IFabBlockProps.metal(3.0F, 15.0F).isViewBlocking((state, level, pos) -> false))); }
        for (ScaffoldingType type : ScaffoldingType.values()) {
            DeferredBlock<ScaffoldingBlock> full = block(type.id(), ScaffoldingBlock::new, IFabBlockProps.metal(3.0F, 15.0F).noOcclusion());
            SCAFFOLDING.put(type, full);
            SCAFFOLDING_SLABS.put(type, block(type.id() + "_slab", SlabBlock::new, IFabBlockProps.metal(3.0F, 15.0F).noOcclusion()));
            SCAFFOLDING_STAIRS.put(type, stairs(type.id() + "_stairs", full, IFabBlockProps.metal(3.0F, 15.0F).noOcclusion()));
        }
    }

    private static void registerStone() {
        for (StoneDecorationType type : StoneDecorationType.values()) { STONE_DECORATION.put(type, block(type.id(), Block::new, IFabBlockProps.stone(2.0F, 10.0F))); }
        for (ConcreteType type : ConcreteType.values()) {
            DeferredBlock<Block> full = block(type.id(), Block::new, IFabBlockProps.stone(2.0F, 10.0F));
            CONCRETE.put(type, full);
            CONCRETE_SLABS.put(type, block(type.id() + "_slab", SlabBlock::new, IFabBlockProps.stone(2.0F, 10.0F)));
            CONCRETE_STAIRS.put(type, stairs(type.id() + "_stairs", full, IFabBlockProps.stone(2.0F, 10.0F)));
        }
    }

    private static <T extends Block> DeferredBlock<T> block(String id, Function<Properties, T> factory, Properties properties) {
        DeferredBlock<T> block = REGISTER.registerBlock(id, factory, properties);
        ALL.put(id, block);
        IFabItems.REGISTER.registerSimpleBlockItem(block);
        return block;
    }

    private static <B extends Block> DeferredBlock<B> wire(String id, Supplier<B> factory) {
        DeferredBlock<B> block = WIRE_REGISTER.register(id, factory);
        WIRES.put(id, block);
        return block;
    }

    private static DeferredBlock<StairBlock> stairs(String id, DeferredBlock<? extends Block> base, Properties properties) { return block(id, p -> new StairBlock(base.get().defaultBlockState(), p), properties); }
}
