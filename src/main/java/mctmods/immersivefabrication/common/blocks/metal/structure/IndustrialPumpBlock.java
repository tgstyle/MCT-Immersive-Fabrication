package mctmods.immersivefabrication.common.blocks.metal.structure;

import mctmods.immersivefabrication.common.blocks.metal.structure.logic.IndustrialPumpBlockEntity;
import mctmods.immersivefabrication.core.registration.IFabBlockEntities;

import com.immersiveconvergence.api.block.IServerTickableBE;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class IndustrialPumpBlock extends Block implements EntityBlock {
    public static final EnumProperty<DoubleBlockHalf> HALF = BlockStateProperties.DOUBLE_BLOCK_HALF;
    private static final VoxelShape UPPER_SHAPE = Shapes.box(0.125, 0, 0.125, 0.875, 1, 0.9375);

    public IndustrialPumpBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(HALF, DoubleBlockHalf.LOWER));
    }

    @Override protected void createBlockStateDefinition(@Nonnull StateDefinition.Builder<Block, BlockState> builder) { builder.add(HALF); }

    @Override @Nonnull protected VoxelShape getShape(@Nonnull BlockState state, @Nonnull BlockGetter level, @Nonnull BlockPos pos, @Nonnull CollisionContext context) { return state.getValue(HALF) == DoubleBlockHalf.UPPER ? UPPER_SHAPE : Shapes.block(); }

    @Override @Nullable public BlockState getStateForPlacement(@Nonnull BlockPlaceContext context) {
        BlockPos above = context.getClickedPos().above();
        return above.getY() < context.getLevel().getMaxBuildHeight() && context.getLevel().getBlockState(above).canBeReplaced(context) ? defaultBlockState() : null;
    }

    @Override public void setPlacedBy(@Nonnull Level level, @Nonnull BlockPos pos, @Nonnull BlockState state, @Nullable LivingEntity placer, @Nonnull ItemStack stack) { level.setBlock(pos.above(), state.setValue(HALF, DoubleBlockHalf.UPPER), Block.UPDATE_ALL); }

    @Override @Nonnull protected BlockState updateShape(@Nonnull BlockState state, @Nonnull Direction direction, @Nonnull BlockState neighbor, @Nonnull LevelAccessor level, @Nonnull BlockPos pos, @Nonnull BlockPos neighborPos) {
        boolean orphan = state.getValue(HALF) == DoubleBlockHalf.UPPER && direction == Direction.DOWN && !(neighbor.is(this) && neighbor.getValue(HALF) == DoubleBlockHalf.LOWER);
        return orphan ? Blocks.AIR.defaultBlockState() : state;
    }

    @Override @Nonnull public BlockState playerWillDestroy(@Nonnull Level level, @Nonnull BlockPos pos, @Nonnull BlockState state, @Nonnull Player player) {
        BlockPos below = pos.below();
        BlockState lower = level.getBlockState(below);
        if (!level.isClientSide && state.getValue(HALF) == DoubleBlockHalf.UPPER && lower.is(this) && lower.getValue(HALF) == DoubleBlockHalf.LOWER) { level.destroyBlock(below, !player.isCreative(), player); }
        return super.playerWillDestroy(level, pos, state, player);
    }

    @Override protected void neighborChanged(@Nonnull BlockState state, @Nonnull Level level, @Nonnull BlockPos pos, @Nonnull Block block, @Nonnull BlockPos fromPos, boolean moving) {
        super.neighborChanged(state, level, pos, block, fromPos, moving);
        if (!level.isClientSide) { level.getBlockEntity(pos); }
    }

    @Override @Nonnull public BlockEntity newBlockEntity(@Nonnull BlockPos pos, @Nonnull BlockState state) { return new IndustrialPumpBlockEntity(pos, state); }

    @Override @Nullable public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@Nonnull Level level, @Nonnull BlockState state, @Nonnull BlockEntityType<T> type) { return level.isClientSide || type != IFabBlockEntities.INDUSTRIAL_PUMP.get() || state.getValue(HALF) == DoubleBlockHalf.UPPER ? null : IServerTickableBE.makeTicker(); }
}
