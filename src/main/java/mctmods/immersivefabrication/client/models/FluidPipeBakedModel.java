package mctmods.immersivefabrication.client.models;

import mctmods.immersivefabrication.common.blocks.metal.structure.logic.FluidPipeBlockEntity;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.BakedModelWrapper;
import net.neoforged.neoforge.client.model.data.ModelData;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

final class FluidPipeBakedModel extends BakedModelWrapper<BakedModel> {
    private final List<List<BakedQuad>> quads;
    private final int[] bake;

    FluidPipeBakedModel(BakedModel item, List<List<BakedQuad>> quads, int[] bake) {
        super(item);
        this.quads = quads;
        this.bake = bake;
    }

    @Override @Nonnull public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @Nonnull RandomSource rand) { return lookup(FluidPipeBlockEntity.DEFAULT_SHAPE, side); }

    @Override @Nonnull public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @Nonnull RandomSource rand, @Nonnull ModelData data, @Nullable RenderType renderType) {
        Integer shape = data.get(FluidPipeBlockEntity.SHAPE);
        return lookup(shape == null ? FluidPipeBlockEntity.DEFAULT_SHAPE : shape, side);
    }

    private List<BakedQuad> lookup(int shape, @Nullable Direction side) { return quads.get(bake[shape] * 7 + (side == null ? 6 : side.get3DDataValue())); }
}
