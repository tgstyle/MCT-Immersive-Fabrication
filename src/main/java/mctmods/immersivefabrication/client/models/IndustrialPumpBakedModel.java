package mctmods.immersivefabrication.client.models;

import mctmods.immersivefabrication.client.util.QuadRetexture;
import mctmods.immersivefabrication.common.blocks.metal.structure.logic.IndustrialPumpBlockEntity;
import mctmods.immersivefabrication.common.util.PumpFaces;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.BakedModelWrapper;
import net.neoforged.neoforge.client.model.data.ModelData;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

final class IndustrialPumpBakedModel extends BakedModelWrapper<BakedModel> {
    private static final int SIDE = 0;
    private static final int BOTTOM = 1;
    private final List<BakedQuad> quads;
    private final TextureAtlasSprite[][] sprites;
    private final Map<Integer, List<BakedQuad>> cache = new ConcurrentHashMap<>();

    IndustrialPumpBakedModel(BakedModel base, List<BakedQuad> quads, TextureAtlasSprite[][] sprites) {
        super(base);
        this.quads = quads;
        this.sprites = sprites;
    }

    @Override @Nonnull public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @Nonnull RandomSource rand) { return lookup(PumpFaces.DEFAULT_CODE, side); }

    @Override @Nonnull public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @Nonnull RandomSource rand, @Nonnull ModelData data, @Nullable RenderType renderType) {
        Integer code = data.get(IndustrialPumpBlockEntity.FACES);
        return lookup(code == null ? PumpFaces.DEFAULT_CODE : code, side);
    }

    private List<BakedQuad> lookup(int code, @Nullable Direction side) { return side == null ? cache.computeIfAbsent(code, this::build) : List.of(); }

    private List<BakedQuad> build(int code) {
        List<BakedQuad> out = new ArrayList<>(quads.size());
        for (BakedQuad quad : quads) {
            Direction face = quad.getDirection();
            out.add(face == Direction.UP ? quad : QuadRetexture.apply(quad, sprites[face == Direction.DOWN ? BOTTOM : SIDE][PumpFaces.state(code, face)]));
        }
        return List.copyOf(out);
    }
}
