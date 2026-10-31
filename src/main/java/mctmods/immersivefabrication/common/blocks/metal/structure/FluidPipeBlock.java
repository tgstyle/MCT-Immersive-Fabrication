package mctmods.immersivefabrication.common.blocks.metal.structure;

import com.immersiveconvergence.api.multiblock.util.SafeChunkUtils;
import com.immersiveconvergence.api.util.ICUtils;

import mctmods.immersivefabrication.common.blocks.metal.structure.logic.FluidPipeBlockEntity;
import mctmods.immersivefabrication.common.util.PipeArms;
import mctmods.immersivefabrication.core.registration.IFabDataComponents;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class FluidPipeBlock extends Block implements EntityBlock {
    private static final VoxelShape SHAPE = Shapes.box(0.25, 0, 0.25, 0.75, 1, 0.75);
    private static final VoxelShape[] SHAPES = shapes();

    public FluidPipeBlock(Properties properties) { super(properties); }

    private static VoxelShape[] shapes() {
        VoxelShape[] shapes = new VoxelShape[256];
        for (int key = 0; key < 256; key++) { shapes[key] = shape(key); }
        return shapes;
    }

    private static VoxelShape shape(int key) {
        int mask = key & 63;
        int flanged = mask == 3 || mask == 12 || mask == 48 ? (key >> 6 & 3) << Integer.numberOfTrailingZeros(mask) : mask;
        double[] lo = {0.25, 0.25, 0.25};
        double[] hi = {0.75, 0.75, 0.75};
        for (int f = 0; f < 6; f++) {
            int axis = f >> 1;
            if ((mask >> f & 1) != 0) {
                if ((f & 1) == 0) { lo[axis] = 0; }
                else { hi[axis] = 1; }
            }
            if ((flanged >> f & 1) == 0) { continue; }
            for (int a = 0; a < 3; a++) {
                if (a == axis) { continue; }
                lo[a] = Math.min(lo[a], 0.125);
                hi[a] = Math.max(hi[a], 0.875);
            }
        }
        return Shapes.box(lo[2], lo[0], lo[1], hi[2], hi[0], hi[1]);
    }

    @Nullable private static FluidPipeBlockEntity pipe(BlockGetter level, BlockPos pos) {
        BlockEntity be = level instanceof LevelAccessor accessor ? SafeChunkUtils.getExistingBlockEntity(accessor, pos) : level.getBlockEntity(pos);
        return be instanceof FluidPipeBlockEntity pipe ? pipe : null;
    }

    @Nullable private static DyeColor dyeColor(ItemStack stack) {
        for (DyeColor color : DyeColor.values()) { if (stack.is(color.getTag())) { return color; } }
        return null;
    }

    @Override @Nonnull public BlockEntity newBlockEntity(@Nonnull BlockPos pos, @Nonnull BlockState state) { return new FluidPipeBlockEntity(pos, state); }

    @Override @Nonnull protected VoxelShape getShape(@Nonnull BlockState state, @Nonnull BlockGetter level, @Nonnull BlockPos pos, @Nonnull CollisionContext context) {
        FluidPipeBlockEntity pipe = pipe(level, pos);
        return pipe == null ? SHAPE : SHAPES[pipe.getShape() & 0xFF];
    }

    @Override protected void neighborChanged(@Nonnull BlockState state, @Nonnull Level level, @Nonnull BlockPos pos, @Nonnull Block block, @Nonnull BlockPos fromPos, boolean moving) {
        if (level.isClientSide) { return; }
        FluidPipeBlockEntity pipe = pipe(level, pos);
        if (pipe != null) { pipe.refresh(); }
    }

    @Override @Nonnull protected ItemInteractionResult useItemOn(@Nonnull ItemStack held, @Nonnull BlockState state, @Nonnull Level level, @Nonnull BlockPos pos, @Nonnull Player player, @Nonnull InteractionHand hand, @Nonnull BlockHitResult hit) {
        if (ICUtils.isToggleTool(held)) {
            FluidPipeBlockEntity toggled = level.isClientSide ? null : pipe(level, pos);
            if (toggled != null) { toggled.toggleSide(PipeArms.at(hit.getDirection(), hit.getLocation().subtract(Vec3.atLowerCornerOf(pos)))); }
            return ItemInteractionResult.sidedSuccess(level.isClientSide);
        }
        DyeColor dye = player.isShiftKeyDown() ? null : dyeColor(held);
        if (dye == null) { return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION; }
        FluidPipeBlockEntity pipe = level.isClientSide ? null : pipe(level, pos);
        if (pipe != null && pipe.dye(dye) && !player.getAbilities().instabuild) { held.shrink(1); }
        return ItemInteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override public void setPlacedBy(@Nonnull Level level, @Nonnull BlockPos pos, @Nonnull BlockState state, @Nullable LivingEntity placer, @Nonnull ItemStack stack) {
        if (level.isClientSide) { return; }
        FluidPipeBlockEntity pipe = pipe(level, pos);
        if (pipe != null) { pipe.placed(); }
    }

    @Override protected void onRemove(@Nonnull BlockState state, @Nonnull Level level, @Nonnull BlockPos pos, @Nonnull BlockState newState, boolean moving) {
        if (!state.is(newState.getBlock())) {
            FluidPipeBlockEntity pipe = pipe(level, pos);
            if (pipe != null) { pipe.removed(); }
        }
        super.onRemove(state, level, pos, newState, moving);
    }

    @SuppressWarnings("deprecation") @Override @Nonnull public ItemStack getCloneItemStack(@Nonnull LevelReader level, @Nonnull BlockPos pos, @Nonnull BlockState state) {
        ItemStack stack = super.getCloneItemStack(level, pos, state);
        FluidPipeBlockEntity pipe = pipe(level, pos);
        if (pipe != null && pipe.getColor() >= 0) { stack.set(IFabDataComponents.COLOR, pipe.getColor()); }
        return stack;
    }
}
