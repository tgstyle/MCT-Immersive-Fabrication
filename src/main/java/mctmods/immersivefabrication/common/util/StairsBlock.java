package mctmods.immersivefabrication.common.util;

import com.immersiveconvergence.api.block.BlockContext;
import com.immersiveconvergence.api.block.ICBlockBase;
import com.immersiveconvergence.api.block.ICItemBlockBase;

import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class StairsBlock extends ICBlockBase<StairsType> {
    private static final PropertyDirection FACING = BlockHorizontal.FACING;
    private static final PropertyEnum<BlockStairs.EnumHalf> HALF = BlockStairs.HALF;
    private static final PropertyEnum<BlockStairs.EnumShape> SHAPE = BlockStairs.SHAPE;
    private static final AxisAlignedBB BOTTOM_BOX = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.5D, 1.0D);
    private static final AxisAlignedBB TOP_BOX = new AxisAlignedBB(0.0D, 0.5D, 0.0D, 1.0D, 1.0D, 1.0D);

    private final BlockRenderLayer layer;

    public StairsBlock(BlockContext context, String name, Material material, BlockRenderLayer layer) {
        super(context, name, material, PropertyEnum.create("type", StairsType.class), ICItemBlockBase.class, FACING, HALF, SHAPE);
        this.layer = layer;
        setAllNotNormalBlock();
        this.useNeighborBrightness = true;
    }

    @Override protected boolean normalBlockCheck(IBlockState state) { return false; }

    @Override public boolean canRenderInLayer(@Nonnull IBlockState state, @Nonnull BlockRenderLayer renderLayer) { return renderLayer == layer; }

    @Override @Nonnull public IBlockState getStateFromMeta(int meta) { return getDefaultState().withProperty(FACING, EnumFacing.byHorizontalIndex(meta & 3)).withProperty(HALF, (meta & 4) != 0 ? BlockStairs.EnumHalf.TOP : BlockStairs.EnumHalf.BOTTOM); }

    @Override public int getMetaFromState(@Nonnull IBlockState state) { return state.getValue(FACING).getHorizontalIndex() | (state.getValue(HALF) == BlockStairs.EnumHalf.TOP ? 4 : 0); }

    @Override public int damageDropped(@Nonnull IBlockState state) { return 0; }

    @SuppressWarnings("deprecation")
    @Override @Nonnull public IBlockState getStateForPlacement(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull EnumFacing facing, float hitX, float hitY, float hitZ, int meta, @Nonnull EntityLivingBase placer) {
        boolean upper = facing == EnumFacing.DOWN || facing != EnumFacing.UP && hitY > 0.5F;
        return getDefaultState().withProperty(FACING, placer.getHorizontalFacing()).withProperty(HALF, upper ? BlockStairs.EnumHalf.TOP : BlockStairs.EnumHalf.BOTTOM);
    }

    @SuppressWarnings("deprecation")
    @Override @Nonnull public IBlockState getActualState(@Nonnull IBlockState state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos) { return state.withProperty(SHAPE, shapeAt(state, world, pos)); }

    @SuppressWarnings("deprecation")
    @Override public void addCollisionBoxToList(@Nonnull IBlockState state, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull AxisAlignedBB entityBox, @Nonnull List<AxisAlignedBB> collidingBoxes, @Nullable Entity entity, boolean isActualState) {
        IBlockState actual = isActualState ? state : state.getActualState(world, pos);
        for (AxisAlignedBB box : boxes(actual)) { addCollisionBoxToList(pos, entityBox, collidingBoxes, box); }
    }

    @SuppressWarnings("deprecation")
    @Override @Nullable public RayTraceResult collisionRayTrace(@Nonnull IBlockState state, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull Vec3d start, @Nonnull Vec3d end) {
        RayTraceResult closest = null;
        double closestDistance = Double.MAX_VALUE;
        for (AxisAlignedBB box : boxes(state.getActualState(world, pos))) {
            RayTraceResult hit = rayTrace(pos, start, end, box);
            if (hit != null && hit.hitVec.squareDistanceTo(start) < closestDistance) {
                closest = hit;
                closestDistance = hit.hitVec.squareDistanceTo(start);
            }
        }
        return closest;
    }

    @SuppressWarnings("deprecation")
    @Override @Nonnull public BlockFaceShape getBlockFaceShape(@Nonnull IBlockAccess world, @Nonnull IBlockState state, @Nonnull BlockPos pos, @Nonnull EnumFacing face) {
        IBlockState actual = state.getActualState(world, pos);
        boolean top = actual.getValue(HALF) == BlockStairs.EnumHalf.TOP;
        if (face.getAxis() == EnumFacing.Axis.Y) { return (face == EnumFacing.UP) == top ? BlockFaceShape.SOLID : BlockFaceShape.UNDEFINED; }
        BlockStairs.EnumShape shape = actual.getValue(SHAPE);
        boolean wall = shape == BlockStairs.EnumShape.STRAIGHT || shape == BlockStairs.EnumShape.INNER_LEFT || shape == BlockStairs.EnumShape.INNER_RIGHT;
        return face == actual.getValue(FACING) && wall ? BlockFaceShape.SOLID : BlockFaceShape.UNDEFINED;
    }

    private static BlockStairs.EnumShape shapeAt(IBlockState state, IBlockAccess world, BlockPos pos) {
        EnumFacing facing = state.getValue(FACING);
        IBlockState ahead = world.getBlockState(pos.offset(facing));
        if (sameHalf(state, ahead)) {
            EnumFacing aheadFacing = ahead.getValue(FACING);
            if (aheadFacing.getAxis() != facing.getAxis() && differs(state, world, pos, aheadFacing.getOpposite())) { return aheadFacing == facing.rotateYCCW() ? BlockStairs.EnumShape.OUTER_LEFT : BlockStairs.EnumShape.OUTER_RIGHT; }
        }
        IBlockState behind = world.getBlockState(pos.offset(facing.getOpposite()));
        if (sameHalf(state, behind)) {
            EnumFacing behindFacing = behind.getValue(FACING);
            if (behindFacing.getAxis() != facing.getAxis() && differs(state, world, pos, behindFacing)) { return behindFacing == facing.rotateYCCW() ? BlockStairs.EnumShape.INNER_LEFT : BlockStairs.EnumShape.INNER_RIGHT; }
        }
        return BlockStairs.EnumShape.STRAIGHT;
    }

    private static boolean sameHalf(IBlockState state, IBlockState other) { return other.getBlock() instanceof StairsBlock && other.getValue(HALF) == state.getValue(HALF); }

    private static boolean differs(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
        IBlockState other = world.getBlockState(pos.offset(side));
        return !sameHalf(state, other) || other.getValue(FACING) != state.getValue(FACING);
    }

    private static List<AxisAlignedBB> boxes(IBlockState state) {
        boolean top = state.getValue(HALF) == BlockStairs.EnumHalf.TOP;
        EnumFacing back = state.getValue(FACING);
        EnumFacing right = back.rotateY();
        EnumFacing left = right.getOpposite();
        List<AxisAlignedBB> boxes = new ArrayList<>(4);
        boxes.add(top ? TOP_BOX : BOTTOM_BOX);
        switch (state.getValue(SHAPE)) {
            case OUTER_LEFT: boxes.add(quadrant(back, left, top)); break;
            case OUTER_RIGHT: boxes.add(quadrant(back, right, top)); break;
            case INNER_LEFT: boxes.add(quadrant(back.getOpposite(), left, top)); addBack(boxes, back, left, right, top); break;
            case INNER_RIGHT: boxes.add(quadrant(back.getOpposite(), right, top)); addBack(boxes, back, left, right, top); break;
            default: addBack(boxes, back, left, right, top); break;
        }
        return boxes;
    }

    private static void addBack(List<AxisAlignedBB> boxes, EnumFacing back, EnumFacing left, EnumFacing right, boolean top) {
        boxes.add(quadrant(back, left, top));
        boxes.add(quadrant(back, right, top));
    }

    private static AxisAlignedBB quadrant(EnumFacing first, EnumFacing second, boolean top) {
        double[] range = {0.0D, 1.0D, 0.0D, 1.0D};
        for (EnumFacing side : new EnumFacing[] {first, second}) {
            int base = side.getAxis() == EnumFacing.Axis.X ? 0 : 2;
            range[base + (side.getAxisDirection() == EnumFacing.AxisDirection.POSITIVE ? 0 : 1)] = 0.5D;
        }
        return new AxisAlignedBB(range[0], top ? 0.0D : 0.5D, range[2], range[1], top ? 0.5D : 1.0D, range[3]);
    }
}
