package mctmods.immersivefabrication.common.util;

import com.immersiveconvergence.api.block.BlockContext;
import com.immersiveconvergence.api.block.ICBlockBase;
import com.immersiveconvergence.api.block.ICItemBlockBase;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFence;
import net.minecraft.block.BlockFenceGate;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class FenceBlock<E extends Enum<E> & ICBlockBase.IBlockEnum> extends ICBlockBase<E> {

    public FenceBlock(BlockContext context, String name, Material material, Class<E> types) {
        super(context, name, material, PropertyEnum.create("type", types), ICItemBlockBase.class, BlockFence.NORTH, BlockFence.EAST, BlockFence.SOUTH, BlockFence.WEST);
        setAllNotNormalBlock();
        this.lightOpacity = 0;
    }

    @Override public boolean useCustomStateMapper() { return true; }

    @Override @Nonnull public String getCustomStateMapping(int meta, boolean itemBlock) { return enumValues[meta].getName(); }

    @SuppressWarnings("deprecation")
    @Override @Nonnull public IBlockState getActualState(@Nonnull IBlockState state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos) {
        return state.withProperty(BlockFence.NORTH, connects(world, pos, EnumFacing.NORTH)).withProperty(BlockFence.EAST, connects(world, pos, EnumFacing.EAST)).withProperty(BlockFence.SOUTH, connects(world, pos, EnumFacing.SOUTH)).withProperty(BlockFence.WEST, connects(world, pos, EnumFacing.WEST));
    }

    @SuppressWarnings("deprecation")
    @Override public void addCollisionBoxToList(@Nonnull IBlockState state, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull AxisAlignedBB entityBox, @Nonnull List<AxisAlignedBB> collidingBoxes, @Nullable Entity entity, boolean isActualState) {
        IBlockState actual = isActualState ? state : state.getActualState(world, pos);
        addCollisionBoxToList(pos, entityBox, collidingBoxes, BlockFence.PILLAR_AABB);
        if (actual.getValue(BlockFence.NORTH)) { addCollisionBoxToList(pos, entityBox, collidingBoxes, BlockFence.NORTH_AABB); }
        if (actual.getValue(BlockFence.EAST)) { addCollisionBoxToList(pos, entityBox, collidingBoxes, BlockFence.EAST_AABB); }
        if (actual.getValue(BlockFence.SOUTH)) { addCollisionBoxToList(pos, entityBox, collidingBoxes, BlockFence.SOUTH_AABB); }
        if (actual.getValue(BlockFence.WEST)) { addCollisionBoxToList(pos, entityBox, collidingBoxes, BlockFence.WEST_AABB); }
    }

    @SuppressWarnings("deprecation")
    @Override @Nonnull public AxisAlignedBB getBoundingBox(@Nonnull IBlockState state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos) {
        IBlockState actual = state.getActualState(world, pos);
        return new AxisAlignedBB(actual.getValue(BlockFence.WEST) ? 0.0D : 0.375D, 0.0D, actual.getValue(BlockFence.NORTH) ? 0.0D : 0.375D, actual.getValue(BlockFence.EAST) ? 1.0D : 0.625D, 1.0D, actual.getValue(BlockFence.SOUTH) ? 1.0D : 0.625D);
    }

    @SuppressWarnings("deprecation")
    @Override @Nonnull public BlockFaceShape getBlockFaceShape(@Nonnull IBlockAccess world, @Nonnull IBlockState state, @Nonnull BlockPos pos, @Nonnull EnumFacing face) { return face.getAxis() == EnumFacing.Axis.Y ? BlockFaceShape.CENTER : BlockFaceShape.MIDDLE_POLE; }

    @Override @Nonnull public PathNodeType getAiPathNodeType(@Nonnull IBlockState state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos, @Nullable EntityLiving entity) { return PathNodeType.FENCE; }

    private boolean connects(IBlockAccess world, BlockPos pos, EnumFacing side) {
        BlockPos other = pos.offset(side);
        IBlockState state = world.getBlockState(other);
        Block block = state.getBlock();
        if (block instanceof FenceBlock) { return state.getMaterial() == material; }
        if (block instanceof BlockFenceGate) { return state.getValue(BlockFenceGate.FACING).getAxis() == side.rotateY().getAxis(); }
        return !Block.isExceptBlockForAttachWithPiston(block) && state.getBlockFaceShape(world, other, side.getOpposite()) == BlockFaceShape.SOLID;
    }
}
