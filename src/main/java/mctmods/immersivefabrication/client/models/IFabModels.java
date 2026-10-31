package mctmods.immersivefabrication.client.models;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.UnmodifiableIterator;
import com.immersiveconvergence.api.block.ICItemBase;
import com.immersiveconvergence.api.client.ICCustomStateMapper;
import com.immersiveconvergence.api.client.ICModels;
import com.immersiveconvergence.api.client.ICObjParts;
import com.immersiveconvergence.api.wires.fittings.ICBlockFitting;
import com.immersiveconvergence.client.ICModelSupport;

import mctmods.immersivefabrication.ImmersiveFabrication;
import mctmods.immersivefabrication.common.blocks.metal.structure.StructureBlock;
import mctmods.immersivefabrication.common.blocks.metal.structure.logic.TileEntityFluidPipe;
import mctmods.immersivefabrication.common.fluids.IFabFluidBlock;
import mctmods.immersivefabrication.common.util.FenceBlock;
import mctmods.immersivefabrication.core.registration.IFabBlocks;
import mctmods.immersivefabrication.core.registration.IFabFluids;
import mctmods.immersivefabrication.core.util.IFabContext;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ForgeBlockStateV1;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.common.model.IModelPart;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.model.Models;
import net.minecraftforge.common.model.TRSRTransformation;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.vecmath.Vector3f;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

@EventBusSubscriber(modid = ImmersiveFabrication.MODID, value = Side.CLIENT)
public final class IFabModels {

    private static final int IRON_TINT = 0xFF9EA2A6;
    private static final TRSRTransformation LIFT = new TRSRTransformation(new Vector3f(0.0F, 1.0F, 0.0F), null, null, null);

    private IFabModels() {}

    @SubscribeEvent public static void registerModels(ModelRegistryEvent event) {
        OBJLoader.INSTANCE.addDomain(ImmersiveFabrication.MODID);
        for (Block block : IFabContext.BLOCKS) {
            if (block instanceof FenceBlock) { registerFence((FenceBlock<?>)block); }
            else if (block instanceof ICBlockFitting) { registerFitting(block); }
            else { ICModelSupport.registerMetaBlock(block, block.getRegistryName(), Item.getItemFromBlock(block)); }
        }
        for (Item item : IFabContext.ITEMS) { if (item instanceof ICItemBase) { registerItem((ICItemBase)item); } }
        for (IFabFluidBlock fluid : IFabFluids.BLOCKS) { registerFluid(fluid); }
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(IFabBlocks.treatedLog), 0, new ModelResourceLocation(Objects.requireNonNull(IFabBlocks.treatedLog.getRegistryName()), "inventory"));
    }

    private static void registerFitting(Block block) {
        String name = Objects.requireNonNull(block.getRegistryName()).getPath();
        ICModels.registerConnectorForRender(name, new ResourceLocation(ImmersiveFabrication.MODID, "block/fitting/" + name + ".obj"));
        if (name.equals("transformer_hv")) { ICModels.registerConnectorForRender(name + "_slave", new ResourceLocation(ImmersiveFabrication.MODID, "block/empty")); }
        ICModelSupport.registerMetaBlock(block, block.getRegistryName(), Item.getItemFromBlock(block));
    }

    @SubscribeEvent public static void bakePipe(ModelBakeEvent event) {
        IModelState transforms = ForgeBlockStateV1.Transforms.get("forge:default-block").orElse(TRSRTransformation.identity());
        IModel model = ModelLoaderRegistry.getModelOrMissing(new ResourceLocation(ImmersiveFabrication.MODID, "block/metal_device/fluid_pipe.obj")).process(ImmutableMap.of("flip-v", "true"));
        FluidPipeShapes shapes = new FluidPipeShapes();
        List<List<BakedQuad>> quads = new ArrayList<>();
        IBakedModel item = null;
        for (int i = 0; i < shapes.parts.size(); i++) {
            Set<String> visible = shapes.parts.get(i);
            TRSRTransformation rotation = shapes.transforms.get(i);
            IModelState state = part -> !part.isPresent() ? Optional.of(rotation) : part.get() instanceof ItemCameraTransforms.TransformType ? transforms.apply(part) : hiddenPart(part.get(), visible);
            IBakedModel baked = model.bake(state, DefaultVertexFormats.ITEM, ModelLoader.defaultTextureGetter());
            if (i == shapes.bake[TileEntityFluidPipe.DEFAULT_SHAPE]) { item = baked; }
            for (EnumFacing side : EnumFacing.VALUES) { quads.add(tinted(baked.getQuads(null, side, 0))); }
            quads.add(tinted(baked.getQuads(null, null, 0)));
        }
        IBakedModel pipe = new FluidPipeBakedModel(Objects.requireNonNull(item), quads, shapes.bake);
        ResourceLocation name = Objects.requireNonNull(IFabBlocks.structure.getRegistryName());
        event.getModelRegistry().putObject(new ModelResourceLocation(name, "boolean0=false,facing=north,type=fluid_pipe"), pipe);
        event.getModelRegistry().putObject(new ModelResourceLocation(name, "inventory,type=fluid_pipe"), pipe);
    }

    @SubscribeEvent public static void bakePump(ModelBakeEvent event) {
        IModel model = ModelLoaderRegistry.getModelOrMissing(new ResourceLocation(ImmersiveFabrication.MODID, "block/metal_device/industrial_pump.obj")).process(ImmutableMap.of("flip-v", "true"));
        Function<ResourceLocation, TextureAtlasSprite> getter = ModelLoader.defaultTextureGetter();
        IBakedModel base = model.bake(ICObjParts.only(ImmutableSet.of(ICObjParts.BODY), part -> part.isPresent() ? Optional.empty() : Optional.of(LIFT)), DefaultVertexFormats.ITEM, getter);
        IBakedModel top = model.bake(ICObjParts.only(ImmutableSet.of(ICObjParts.part("motor")), part -> Optional.empty()), DefaultVertexFormats.ITEM, getter);
        IBakedModel shaft = model.bake(ICObjParts.only(ImmutableSet.of(ICObjParts.part("shaft")), part -> Optional.empty()), DefaultVertexFormats.ITEM, getter);
        ResourceLocation name = Objects.requireNonNull(IFabBlocks.structure.getRegistryName());
        event.getModelRegistry().putObject(pumpShaft(), shaft);
        event.getModelRegistry().putObject(new ModelResourceLocation(name, "boolean0=false,facing=north,type=industrial_pump"), new PumpBaseBakedModel(base, getter));
        event.getModelRegistry().putObject(new ModelResourceLocation(name, "boolean0=false,facing=north,type=industrial_pump_top"), top);
    }

    private static ModelResourceLocation pumpShaft() { return new ModelResourceLocation(Objects.requireNonNull(IFabBlocks.structure.getRegistryName()), "inventory,type=industrial_pump_shaft"); }

    public static List<BakedQuad> pumpShaftQuads() { return Minecraft.getMinecraft().getRenderItem().getItemModelMesher().getModelManager().getModel(pumpShaft()).getQuads(null, null, 0L); }

    private static Optional<TRSRTransformation> hiddenPart(IModelPart part, Set<String> visible) {
        UnmodifiableIterator<String> path = Models.getParts(part);
        return path.hasNext() && !visible.contains(path.next()) ? Optional.of(TRSRTransformation.identity()) : Optional.empty();
    }

    private static List<BakedQuad> tinted(List<BakedQuad> source) {
        ImmutableList.Builder<BakedQuad> out = ImmutableList.builder();
        for (BakedQuad quad : source) { out.add(new BakedQuad(quad.getVertexData(), 0, quad.getFace(), quad.getSprite(), quad.shouldApplyDiffuseLighting(), quad.getFormat())); }
        return out.build();
    }

    @SubscribeEvent public static void blockColors(ColorHandlerEvent.Block event) { event.getBlockColors().registerBlockColorHandler((state, world, pos, tint) -> blockColor(state), IFabBlocks.structure); }

    @SubscribeEvent public static void itemColors(ColorHandlerEvent.Item event) { event.getItemColors().registerItemColorHandler((stack, tint) -> itemColor(stack), IFabBlocks.structure); }

    private static int blockColor(IBlockState state) {
        if (!(state instanceof IExtendedBlockState)) { return IRON_TINT; }
        Integer meta = ((IExtendedBlockState)state).getValue(StructureBlock.COLOR);
        return tint(meta == null ? null : EnumDyeColor.byMetadata(meta));
    }

    private static int itemColor(ItemStack stack) { return tint(TileEntityFluidPipe.stackColor(stack)); }

    private static int tint(@Nullable EnumDyeColor dye) { return dye == null ? IRON_TINT : 0xFF000000 | mix(dye.getColorValue(), 16) << 16 | mix(dye.getColorValue(), 8) << 8 | mix(dye.getColorValue(), 0); }

    private static int mix(int rgb, int shift) { return ((rgb >> shift & 0xFF) + (IRON_TINT >> shift & 0xFF)) / 2; }

    private static void registerFluid(IFabFluidBlock block) {
        ModelResourceLocation location = new ModelResourceLocation(new ResourceLocation(ImmersiveFabrication.MODID, "fluid_block"), block.getFluid().getName());
        ModelLoader.setCustomStateMapper(block, new StateMapperBase() {
            @Override @Nonnull protected ModelResourceLocation getModelResourceLocation(@Nonnull IBlockState state) { return location; }
        });
    }

    private static void registerFence(FenceBlock<?> fence) {
        ModelLoader.setCustomStateMapper(fence, ICCustomStateMapper.getStateMapper(fence));
        Item item = Item.getItemFromBlock(fence);
        ResourceLocation name = Objects.requireNonNull(fence.getRegistryName());
        for (int meta = 0; meta < fence.enumValues.length; meta++) { ModelLoader.setCustomModelResourceLocation(item, meta, new ModelResourceLocation(name, "inventory,type=" + fence.enumValues[meta].getName())); }
    }

    private static void registerItem(ICItemBase item) {
        String[] subNames = item.getSubNames();
        for (int meta = 0; meta < subNames.length; meta++) { ModelLoader.setCustomModelResourceLocation(item, meta, new ModelResourceLocation(new ResourceLocation(ImmersiveFabrication.MODID, item.itemName + "/" + subNames[meta]), "inventory")); }
    }
}
