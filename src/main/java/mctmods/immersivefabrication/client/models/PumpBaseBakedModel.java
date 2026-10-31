package mctmods.immersivefabrication.client.models;

import mctmods.immersivefabrication.ImmersiveFabrication;
import mctmods.immersivefabrication.common.blocks.metal.structure.StructureBlock;
import mctmods.immersivefabrication.common.util.PumpFaces;

import com.google.common.collect.ImmutableList;
import com.immersiveconvergence.api.block.ICSideConfig;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BakedQuadRetextured;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.BakedModelWrapper;
import net.minecraftforge.common.property.IExtendedBlockState;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

final class PumpBaseBakedModel extends BakedModelWrapper<IBakedModel> {
    private static final int SIDE = 0;
    private static final int BOTTOM = 1;
    private final List<BakedQuad> quads;
    private final TextureAtlasSprite[][] sprites = new TextureAtlasSprite[2][ICSideConfig.values().length];
    private final Map<Integer, List<BakedQuad>> cache = new ConcurrentHashMap<>();

    PumpBaseBakedModel(IBakedModel base, Function<ResourceLocation, TextureAtlasSprite> getter) {
        super(base);
        quads = base.getQuads(null, null, 0);
        for (ICSideConfig config : ICSideConfig.values()) {
            sprites[SIDE][config.ordinal()] = getter.apply(texture("side", config));
            sprites[BOTTOM][config.ordinal()] = getter.apply(texture("bottom", config));
        }
    }

    private static ResourceLocation texture(String kind, ICSideConfig config) { return new ResourceLocation(ImmersiveFabrication.MODID, "blocks/industrial_pump_" + kind + "_" + config.getTextureName()); }

    @Override @Nonnull public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand) {
        if (side != null) { return Collections.emptyList(); }
        Integer shape = state instanceof IExtendedBlockState ? ((IExtendedBlockState)state).getValue(StructureBlock.SHAPE) : null;
        return cache.computeIfAbsent(shape == null ? PumpFaces.DEFAULT_CODE : shape, this::build);
    }

    private List<BakedQuad> build(int code) {
        ImmutableList.Builder<BakedQuad> out = ImmutableList.builder();
        for (BakedQuad quad : quads) {
            EnumFacing face = quad.getFace();
            if (face == EnumFacing.UP) { out.add(quad); }
            else { out.add(new BakedQuadRetextured(quad, sprites[face == EnumFacing.DOWN ? BOTTOM : SIDE][PumpFaces.state(code, face)])); }
        }
        return out.build();
    }
}
