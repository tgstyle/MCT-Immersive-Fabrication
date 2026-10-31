package mctmods.immersivefabrication.core.registration;

import com.immersiveconvergence.api.block.ICBlockBase;
import com.immersiveconvergence.api.block.ICBlockBase.IBlockEnum;
import com.immersiveconvergence.api.block.ICItemBlockBase;

import mctmods.immersivefabrication.ImmersiveFabrication;
import mctmods.immersivefabrication.common.blocks.fittings.anchor.AnchorBlock;
import mctmods.immersivefabrication.common.blocks.fittings.anchor.logic.TileEntityAnchor;
import mctmods.immersivefabrication.common.blocks.fittings.insulatorhv.InsulatorHvBlock;
import mctmods.immersivefabrication.common.blocks.fittings.insulatorhv.logic.TileEntityInsulatorHv;
import mctmods.immersivefabrication.common.blocks.fittings.insulatorlv.InsulatorLvBlock;
import mctmods.immersivefabrication.common.blocks.fittings.insulatorlv.logic.TileEntityInsulatorLv;
import mctmods.immersivefabrication.common.blocks.fittings.insulatormv.InsulatorMvBlock;
import mctmods.immersivefabrication.common.blocks.fittings.insulatormv.logic.TileEntityInsulatorMv;
import mctmods.immersivefabrication.common.blocks.fittings.terminalhv.TerminalHvBlock;
import mctmods.immersivefabrication.common.blocks.fittings.terminalhv.logic.TileEntityTerminalHv;
import mctmods.immersivefabrication.common.blocks.fittings.terminallv.TerminalLvBlock;
import mctmods.immersivefabrication.common.blocks.fittings.terminallv.logic.TileEntityTerminalLv;
import mctmods.immersivefabrication.common.blocks.fittings.terminalmv.TerminalMvBlock;
import mctmods.immersivefabrication.common.blocks.fittings.terminalmv.logic.TileEntityTerminalMv;
import mctmods.immersivefabrication.common.blocks.fittings.transformer.TransformerBlock;
import mctmods.immersivefabrication.common.blocks.fittings.transformer.logic.TileEntityTransformer;
import mctmods.immersivefabrication.common.blocks.fittings.transformerhv.TransformerHvBlock;
import mctmods.immersivefabrication.common.blocks.fittings.transformerhv.logic.TileEntityTransformerHv;
import mctmods.immersivefabrication.common.blocks.metal.cladding.CladdingType;
import mctmods.immersivefabrication.common.blocks.metal.decoration.MetalDecorationType;
import mctmods.immersivefabrication.common.blocks.metal.decoration.MetalFenceType;
import mctmods.immersivefabrication.common.blocks.metal.decoration.ScaffoldingBlock;
import mctmods.immersivefabrication.common.blocks.metal.decoration.ScaffoldingType;
import mctmods.immersivefabrication.common.blocks.metal.ore.OreType;
import mctmods.immersivefabrication.common.blocks.metal.storage.StorageType;
import mctmods.immersivefabrication.common.blocks.metal.structure.StructureBlock;
import mctmods.immersivefabrication.common.blocks.metal.structure.StructureType;
import mctmods.immersivefabrication.common.blocks.metal.structure.logic.TileEntityFluidPipe;
import mctmods.immersivefabrication.common.blocks.metal.structure.logic.TileEntityHeatingElement;
import mctmods.immersivefabrication.common.blocks.metal.structure.logic.TileEntityIndustrialPump;
import mctmods.immersivefabrication.common.blocks.plant.flax.FlaxBlock;
import mctmods.immersivefabrication.common.blocks.stone.ConcreteType;
import mctmods.immersivefabrication.common.blocks.stone.StoneDecorationType;
import mctmods.immersivefabrication.common.blocks.wooden.TreatedFenceType;
import mctmods.immersivefabrication.common.blocks.wooden.TreatedLogBlock;
import mctmods.immersivefabrication.common.blocks.wooden.TreatedWoodType;
import mctmods.immersivefabrication.common.util.FenceBlock;
import mctmods.immersivefabrication.common.util.SlabFamily;
import mctmods.immersivefabrication.common.util.StairsBlock;
import mctmods.immersivefabrication.core.IFabCommonConfig;
import mctmods.immersivefabrication.core.util.IFabContext;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

import java.util.ArrayList;
import java.util.List;

@EventBusSubscriber(modid = ImmersiveFabrication.MODID)
public final class IFabBlocks {
    public static ICBlockBase<StorageType> storage;
    public static ICBlockBase<CladdingType> cladding;
    public static SlabFamily<CladdingType> claddingSlab;
    public static ICBlockBase<OreType> ore;
    public static ICBlockBase<MetalDecorationType> metalDecoration;
    public static FenceBlock<MetalFenceType> metalFence;
    public static ScaffoldingBlock scaffolding;
    public static SlabFamily<ScaffoldingType> scaffoldingSlab;
    public static final List<StairsBlock> scaffoldingStairs = new ArrayList<>();
    public static ICBlockBase<StoneDecorationType> stoneDecoration;
    public static ICBlockBase<ConcreteType> concrete;
    public static SlabFamily<ConcreteType> concreteSlab;
    public static final List<StairsBlock> concreteStairs = new ArrayList<>();
    public static ICBlockBase<TreatedWoodType> treatedWood;
    public static SlabFamily<TreatedWoodType> treatedWoodSlab;
    public static final List<StairsBlock> treatedWoodStairs = new ArrayList<>();
    public static FenceBlock<TreatedFenceType> treatedWoodFence;
    public static TreatedLogBlock treatedLog;
    public static ICBlockBase<StructureType> structure;
    public static FlaxBlock flax;
    public static TerminalLvBlock terminalLv;
    public static InsulatorLvBlock insulatorLv;
    public static TerminalMvBlock terminalMv;
    public static InsulatorMvBlock insulatorMv;
    public static TerminalHvBlock terminalHv;
    public static InsulatorHvBlock insulatorHv;
    public static AnchorBlock anchor;
    public static TransformerBlock transformer;
    public static TransformerHvBlock transformerHv;

    private IFabBlocks() {}

    public static void register() {
        storage = new ICBlockBase<>(IFabContext.CONTEXT, "storage", Material.IRON, PropertyEnum.create("type", StorageType.class), ICItemBlockBase.class);
        storage.setHardness(5.0F);
        storage.setResistance(10.0F);
        for (StorageType type : StorageType.values()) { storage.setHarvestLevel("pickaxe", type.harvestLevel, storage.getStateFromMeta(type.getMeta())); }
        cladding = new ICBlockBase<>(IFabContext.CONTEXT, "cladding", Material.IRON, PropertyEnum.create("type", CladdingType.class), ICItemBlockBase.class);
        claddingSlab = new SlabFamily<>(cladding, "cladding_slab");
        for (Block block : new Block[] {cladding, claddingSlab.bottom, claddingSlab.top}) { strength(block, 3.0F, 10.0F); }
        ore = new ICBlockBase<>(IFabContext.CONTEXT, "ore", Material.ROCK, PropertyEnum.create("type", OreType.class), ICItemBlockBase.class);
        ore.setHardness(3.0F);
        ore.setResistance(5.0F);
        for (OreType type : OreType.values()) { ore.setHarvestLevel("pickaxe", type.harvestLevel, ore.getStateFromMeta(type.getMeta())); }
        registerMetalDecoration();
        registerStoneDecoration();
        registerTreatedWood();
        structure = new StructureBlock();
        strength(structure, 3.0F, 15.0F);
        tile(TileEntityFluidPipe.class, "fluid_pipe");
        tile(TileEntityIndustrialPump.class, "industrial_pump");
        tile(TileEntityHeatingElement.class, "heating_element");
        flax = new FlaxBlock();
        if (IFabCommonConfig.wires()) { registerWireBlocks(); }
    }

    private static void registerWireBlocks() {
        terminalLv = new TerminalLvBlock();
        insulatorLv = new InsulatorLvBlock();
        terminalMv = new TerminalMvBlock();
        insulatorMv = new InsulatorMvBlock();
        terminalHv = new TerminalHvBlock();
        insulatorHv = new InsulatorHvBlock();
        anchor = new AnchorBlock();
        transformer = new TransformerBlock();
        transformerHv = new TransformerHvBlock();
    }

    private static void registerWireTiles() {
        tile(TileEntityTerminalLv.class, terminalLv);
        tile(TileEntityInsulatorLv.class, insulatorLv);
        tile(TileEntityTerminalMv.class, terminalMv);
        tile(TileEntityInsulatorMv.class, insulatorMv);
        tile(TileEntityTerminalHv.class, terminalHv);
        tile(TileEntityInsulatorHv.class, insulatorHv);
        tile(TileEntityAnchor.class, anchor);
        tile(TileEntityTransformer.class, transformer);
        tile(TileEntityTransformerHv.class, transformerHv);
    }

    private static void registerMetalDecoration() {
        metalDecoration = block("metal_decoration", Material.IRON, MetalDecorationType.class, 3.0F, 15.0F);
        metalFence = new FenceBlock<>(IFabContext.CONTEXT, "metal_fence", Material.IRON, MetalFenceType.class);
        strength(metalFence, 3.0F, 15.0F);
        scaffolding = new ScaffoldingBlock(IFabContext.CONTEXT, "scaffolding");
        strength(scaffolding, 3.0F, 15.0F);
        cutout(scaffolding);
        scaffoldingSlab = slabs(scaffolding, "scaffolding_slab", 3.0F, 15.0F);
        cutout(scaffoldingSlab.bottom);
        cutout(scaffoldingSlab.top);
        stairs(scaffoldingStairs, scaffolding, "scaffolding_stairs", BlockRenderLayer.CUTOUT_MIPPED, 3.0F, 15.0F);
    }

    private static void registerStoneDecoration() {
        stoneDecoration = block("stone_decoration", Material.ROCK, StoneDecorationType.class, 2.0F, 10.0F);
        concrete = block("concrete", Material.ROCK, ConcreteType.class, 2.0F, 10.0F);
        concreteSlab = slabs(concrete, "concrete_slab", 2.0F, 10.0F);
        stairs(concreteStairs, concrete, "concrete_stairs", BlockRenderLayer.SOLID, 2.0F, 10.0F);
    }

    private static void registerTreatedWood() {
        treatedWood = block("treated_wood", Material.WOOD, TreatedWoodType.class, 2.0F, 5.0F);
        treatedWoodSlab = slabs(treatedWood, "treated_wood_slab", 2.0F, 5.0F);
        stairs(treatedWoodStairs, treatedWood, "treated_wood_stairs", BlockRenderLayer.SOLID, 2.0F, 5.0F);
        treatedWoodFence = new FenceBlock<>(IFabContext.CONTEXT, "treated_wood_fence", Material.WOOD, TreatedFenceType.class);
        strength(treatedWoodFence, 2.0F, 5.0F);
        treatedLog = new TreatedLogBlock();
        strength(treatedLog, 2.0F, 5.0F);
    }

    private static <E extends Enum<E> & IBlockEnum> ICBlockBase<E> block(String name, Material material, Class<E> types, float hardness, float resistance) {
        ICBlockBase<E> block = new ICBlockBase<>(IFabContext.CONTEXT, name, material, PropertyEnum.create("type", types), ICItemBlockBase.class);
        strength(block, hardness, resistance);
        return block;
    }

    private static <E extends Enum<E> & IBlockEnum> SlabFamily<E> slabs(ICBlockBase<E> full, String name, float hardness, float resistance) {
        SlabFamily<E> family = new SlabFamily<>(full, name);
        strength(family.bottom, hardness, resistance);
        strength(family.top, hardness, resistance);
        return family;
    }

    private static <E extends Enum<E> & IBlockEnum> void stairs(List<StairsBlock> out, ICBlockBase<E> full, String prefix, BlockRenderLayer layer, float hardness, float resistance) {
        Material material = full.getDefaultState().getMaterial();
        for (E type : full.enumValues) {
            StairsBlock stairs = new StairsBlock(IFabContext.CONTEXT, prefix + "_" + type.getName(), material, layer);
            strength(stairs, hardness, resistance);
            out.add(stairs);
        }
    }

    private static void strength(Block block, float hardness, float resistance) {
        block.setHardness(hardness);
        block.setResistance(resistance);
        if (block.getDefaultState().getMaterial() != Material.WOOD) { block.setHarvestLevel("pickaxe", 0); }
    }

    private static void cutout(ICBlockBase<?> block) {
        for (int meta = 0; meta < block.enumValues.length; meta++) { block.setMetaBlockLayer(meta, BlockRenderLayer.CUTOUT_MIPPED); }
    }

    @SubscribeEvent public static void onRegisterBlocks(RegistryEvent.Register<Block> event) {
        for (Block block : IFabContext.BLOCKS) { event.getRegistry().register(block.setRegistryName(new ResourceLocation(ImmersiveFabrication.MODID, ((ICBlockBase<?>)block).name))); }
        event.getRegistry().registerAll(flax, treatedLog);
        if (IFabCommonConfig.wires()) { registerWireTiles(); }
    }

    private static void tile(Class<? extends TileEntity> type, ICBlockBase<?> block) { tile(type, block.name); }

    private static void tile(Class<? extends TileEntity> type, String name) { GameRegistry.registerTileEntity(type, new ResourceLocation(ImmersiveFabrication.MODID, name)); }
}
