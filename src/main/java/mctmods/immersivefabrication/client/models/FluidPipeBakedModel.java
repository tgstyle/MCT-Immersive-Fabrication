package mctmods.immersivefabrication.client.models;

import mctmods.immersivefabrication.common.blocks.metal.structure.StructureBlock;
import mctmods.immersivefabrication.common.blocks.metal.structure.logic.TileEntityFluidPipe;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.model.BakedModelWrapper;
import net.minecraftforge.common.property.IExtendedBlockState;

import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.vecmath.Matrix4f;
import java.util.List;

final class FluidPipeBakedModel extends BakedModelWrapper<IBakedModel> {
    private final List<List<BakedQuad>> quads;
    private final int[] bake;

    FluidPipeBakedModel(IBakedModel item, List<List<BakedQuad>> quads, int[] bake) {
        super(item);
        this.quads = quads;
        this.bake = bake;
    }

    @Override @Nonnull public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand) {
        Integer shape = state instanceof IExtendedBlockState ? ((IExtendedBlockState)state).getValue(StructureBlock.SHAPE) : null;
        int key = shape == null ? TileEntityFluidPipe.DEFAULT_SHAPE : shape;
        return quads.get(bake[key] * 7 + (side == null ? 6 : side.getIndex()));
    }

    @Override @Nonnull public Pair<? extends IBakedModel, Matrix4f> handlePerspective(@Nonnull ItemCameraTransforms.TransformType type) { return Pair.of(this, originalModel.handlePerspective(type).getRight()); }
}
