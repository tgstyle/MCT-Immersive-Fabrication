package mctmods.immersivefabrication.common.blocks.metal.decoration;

import com.immersiveconvergence.api.block.BlockContext;
import com.immersiveconvergence.api.block.ICBlockBase;
import com.immersiveconvergence.api.block.ICItemBlockBase;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ScaffoldingBlock extends ICBlockBase<ScaffoldingType> {
    private static final AxisAlignedBB COLLISION_BOX = new AxisAlignedBB(0.0625D, 0.0D, 0.0625D, 0.9375D, 1.0D, 0.9375D);

    public ScaffoldingBlock(BlockContext context, String name) {
        super(context, name, Material.IRON, PropertyEnum.create("type", ScaffoldingType.class), ICItemBlockBase.class);
        setAllNotNormalBlock();
        this.lightOpacity = 0;
    }

    @Override public boolean isLadder(@Nonnull IBlockState state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos, @Nonnull EntityLivingBase entity) { return true; }

    @SuppressWarnings("deprecation")
    @Override @Nullable public AxisAlignedBB getCollisionBoundingBox(@Nonnull IBlockState state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos) { return COLLISION_BOX; }

    @SuppressWarnings("deprecation")
    @Override public boolean isSideSolid(@Nonnull IBlockState state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos, @Nonnull EnumFacing side) { return true; }

    @SuppressWarnings("deprecation")
    @Override @Nonnull public BlockFaceShape getBlockFaceShape(@Nonnull IBlockAccess world, @Nonnull IBlockState state, @Nonnull BlockPos pos, @Nonnull EnumFacing face) { return BlockFaceShape.SOLID; }

    @SuppressWarnings("deprecation")
    @Override public boolean shouldSideBeRendered(@Nonnull IBlockState state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos, @Nonnull EnumFacing side) {
        IBlockState other = world.getBlockState(pos.offset(side));
        return other.getBlock() != this || getMetaFromState(other) != getMetaFromState(state);
    }
}
