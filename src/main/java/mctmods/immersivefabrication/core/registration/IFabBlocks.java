package mctmods.immersivefabrication.core.registration;

import com.immersiveconvergence.api.block.BaseBlock;
import com.immersiveconvergence.api.block.StairsBlock;

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
import mctmods.immersivefabrication.common.blocks.metal.MetalProperties;
import mctmods.immersivefabrication.common.blocks.metal.structure.FluidPipeBlock;
import mctmods.immersivefabrication.common.blocks.metal.structure.HeatingElementBlock;
import mctmods.immersivefabrication.common.blocks.metal.structure.IndustrialPumpBlock;
import mctmods.immersivefabrication.common.blocks.plant.flax.FlaxBlock;
import mctmods.immersivefabrication.common.blocks.stone.StoneProperties;
import mctmods.immersivefabrication.common.blocks.wooden.TreatedLogBlock;
import mctmods.immersivefabrication.common.blocks.wooden.WoodenProperties;
import mctmods.immersivefabrication.common.util.IFabBlockNames;
import mctmods.immersivefabrication.core.IFabCommonConfig;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.material.PushReaction;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;

public final class IFabBlocks {
    public static final DeferredRegister<Block> REGISTER = DeferredRegister.create(ForgeRegistries.BLOCKS, ImmersiveFabrication.MODID);
    public static final Map<String, RegistryObject<? extends Block>> ENTRIES = new LinkedHashMap<>();
    public static final RegistryObject<FlaxBlock> FLAX = REGISTER.register("flax", FlaxBlock::new);
    public static final Map<String, RegistryObject<? extends Block>> WIRES = new LinkedHashMap<>();
    private static final DeferredRegister<Block> WIRE_REGISTER = DeferredRegister.create(ForgeRegistries.BLOCKS, ImmersiveFabrication.MODID);
    public static final RegistryObject<TerminalLvBlock> TERMINAL_LV = wire("terminal_lv", TerminalLvBlock::new);
    public static final RegistryObject<InsulatorLvBlock> INSULATOR_LV = wire("insulator_lv", InsulatorLvBlock::new);
    public static final RegistryObject<TerminalMvBlock> TERMINAL_MV = wire("terminal_mv", TerminalMvBlock::new);
    public static final RegistryObject<InsulatorMvBlock> INSULATOR_MV = wire("insulator_mv", InsulatorMvBlock::new);
    public static final RegistryObject<TerminalHvBlock> TERMINAL_HV = wire("terminal_hv", TerminalHvBlock::new);
    public static final RegistryObject<InsulatorHvBlock> INSULATOR_HV = wire("insulator_hv", InsulatorHvBlock::new);
    public static final RegistryObject<AnchorBlock> ANCHOR = wire("anchor", AnchorBlock::new);
    public static final RegistryObject<TransformerBlock> TRANSFORMER = wire("transformer", TransformerBlock::new);
    public static final RegistryObject<TransformerHvBlock> TRANSFORMER_HV = wire("transformer_hv", TransformerHvBlock::new);
    public static final RegistryObject<FluidPipeBlock> FLUID_PIPE;
    public static final RegistryObject<HeatingElementBlock> HEATING_ELEMENT;
    public static final RegistryObject<IndustrialPumpBlock> INDUSTRIAL_PUMP;

    static {
        registerMetals();
        registerEngineering();
        registerFrames();
        registerStone();
        registerTreatedWood();
        for (String structure : IFabBlockNames.STRUCTURES) { full(structure, MetalProperties::engineering); }
        FLUID_PIPE = add("fluid_pipe", () -> new FluidPipeBlock(MetalProperties.engineering().noOcclusion().dynamicShape()));
        HEATING_ELEMENT = add("heating_element", () -> new HeatingElementBlock(MetalProperties.engineering()));
        INDUSTRIAL_PUMP = add("industrial_pump", () -> new IndustrialPumpBlock(MetalProperties.engineering().noOcclusion().pushReaction(PushReaction.BLOCK)));
    }

    private IFabBlocks() {}

    private static void registerMetals() {
        for (String metal : IFabBlockNames.STORAGE_METALS) { full(metal + "_block", () -> MetalProperties.storage(metal.equals("steel"))); }
        for (String metal : IFabBlockNames.ORE_METALS) {
            full(metal + "_ore", MetalProperties::ore);
            full("deepslate_" + metal + "_ore", MetalProperties::deepslateOre);
            full("raw_" + metal + "_block", MetalProperties::rawBlock);
        }
        for (String metal : IFabBlockNames.CLADDING_METALS) {
            String id = metal + "_cladding";
            full(id, MetalProperties::cladding);
            slab(id, MetalProperties::cladding);
        }
    }

    private static void registerEngineering() {
        for (String name : IFabBlockNames.ENGINEERING) { full(name, MetalProperties::engineering); }
    }

    private static void registerFrames() {
        for (String metal : IFabBlockNames.FRAME_METALS) {
            add(metal + "_fence", () -> new FenceBlock(MetalProperties.fence()));
            for (String type : IFabBlockNames.SCAFFOLDING_TYPES) { withSlabAndStairs(metal + "_scaffolding_" + type, MetalProperties::frame); }
        }
    }

    private static void registerStone() {
        for (String name : IFabBlockNames.CONCRETES) { withSlabAndStairs(name, StoneProperties::concrete); }
        fullWithSlab("coke_brick", StoneProperties::cokeBrick);
        fullWithSlab("blast_brick", StoneProperties::blastBrick);
        fullWithSlab("blast_brick_reinforced", StoneProperties::reinforcedBlastBrick);
        fullWithSlab("alloy_brick", StoneProperties::alloyBrick);
        full("coal_coke_block", StoneProperties::coalCokeBlock);
    }

    private static void registerTreatedWood() {
        for (String style : IFabBlockNames.TREATED_WOOD) { withSlabAndStairs("treated_wood_" + style, WoodenProperties::treated); }
        add("treated_log", TreatedLogBlock::new);
        add("treated_wood_fence", () -> new FenceBlock(WoodenProperties.treatedFence()));
    }

    private static <B extends Block> RegistryObject<B> add(String id, Supplier<B> factory) {
        RegistryObject<B> block = REGISTER.register(id, factory);
        ENTRIES.put(id, block);
        IFabItems.addBlockItem(id, block);
        return block;
    }

    private static <B extends Block> RegistryObject<B> wire(String id, Supplier<B> factory) {
        RegistryObject<B> block = WIRE_REGISTER.register(id, factory);
        WIRES.put(id, block);
        return block;
    }

    private static RegistryObject<BaseBlock> full(String id, Supplier<Properties> properties) { return add(id, () -> new BaseBlock(properties.get(), false)); }

    private static void slab(String id, Supplier<Properties> properties) { add(id + "_slab", () -> new SlabBlock(properties.get())); }

    private static void fullWithSlab(String id, Supplier<Properties> properties) {
        full(id, properties);
        slab(id, () -> properties.get().strength(2.0F, 10.0F));
    }

    private static void withSlabAndStairs(String id, Supplier<Properties> properties) {
        RegistryObject<BaseBlock> base = full(id, properties);
        slab(id, properties);
        add(id + "_stairs", () -> new StairsBlock(properties.get(), base));
    }

    public static void register(IEventBus bus) {
        REGISTER.register(bus);
        if (IFabCommonConfig.wires()) {
            WIRE_REGISTER.register(bus);
            IFabWiresPack.register(bus);
        }
    }
}
