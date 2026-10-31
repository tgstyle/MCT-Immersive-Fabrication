package mctmods.immersivefabrication.client.models;

import mctmods.immersivefabrication.ImmersiveFabrication;
import mctmods.immersivefabrication.common.blocks.metal.structure.logic.FluidPipeBlockEntity;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.neoforged.neoforge.client.model.SimpleModelState;
import net.neoforged.neoforge.client.model.geometry.IGeometryBakingContext;
import net.neoforged.neoforge.client.model.geometry.IUnbakedGeometry;
import net.neoforged.neoforge.client.model.geometry.StandaloneGeometryBakingContext;
import net.neoforged.neoforge.client.model.obj.ObjModel;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

public final class FluidPipeGeometry implements IUnbakedGeometry<FluidPipeGeometry> {
    private static final ResourceLocation NAME = ResourceLocation.fromNamespaceAndPath(ImmersiveFabrication.MODID, "fluid_pipe");
    private final ObjModel model;

    FluidPipeGeometry(ObjModel model) { this.model = model; }

    @Override @Nonnull @SuppressWarnings("deprecation") public BakedModel bake(@Nonnull IGeometryBakingContext context, @Nonnull ModelBaker baker, @Nonnull Function<Material, TextureAtlasSprite> spriteGetter, @Nonnull ModelState modelState, @Nonnull ItemOverrides overrides) {
        FluidPipeShapes shapes = new FluidPipeShapes();
        List<List<BakedQuad>> quads = new ArrayList<>();
        BakedModel item = null;
        for (int i = 0; i < shapes.visibility.size(); i++) {
            IGeometryBakingContext part = StandaloneGeometryBakingContext.builder(context).withVisibleComponents(shapes.visibility.get(i)).build(NAME);
            BakedModel baked = model.bake(part, baker, spriteGetter, new SimpleModelState(shapes.transforms.get(i)), overrides);
            if (i == shapes.bake[FluidPipeBlockEntity.DEFAULT_SHAPE]) { item = baked; }
            for (Direction side : Direction.values()) { quads.add(tinted(baked.getQuads(null, side, RandomSource.create()))); }
            quads.add(tinted(baked.getQuads(null, null, RandomSource.create())));
        }
        return new FluidPipeBakedModel(Objects.requireNonNull(item), quads, shapes.bake);
    }

    private static List<BakedQuad> tinted(List<BakedQuad> source) {
        List<BakedQuad> out = new ArrayList<>(source.size());
        for (BakedQuad quad : source) { out.add(new BakedQuad(quad.getVertices(), 0, quad.getDirection(), quad.getSprite(), quad.isShade(), quad.hasAmbientOcclusion())); }
        return out;
    }
}
