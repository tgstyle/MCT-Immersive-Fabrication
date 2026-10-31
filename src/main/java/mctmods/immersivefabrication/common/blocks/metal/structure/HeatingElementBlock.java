package mctmods.immersivefabrication.common.blocks.metal.structure;

import mctmods.immersivefabrication.common.blocks.metal.structure.logic.HeatingElementBlockEntity;
import mctmods.immersivefabrication.core.registration.IFabBlockEntities;

import com.immersiveconvergence.api.block.IServerTickableBE;
import com.immersiveconvergence.api.block.ModProperties;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class HeatingElementBlock extends Block implements EntityBlock {
    public static final DirectionProperty FACING = BlockStateProperties.FACING;
    public static final BooleanProperty ACTIVE = ModProperties.ACTIVE;

    public HeatingElementBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(ACTIVE, false));
    }

    @Override protected void createBlockStateDefinition(@Nonnull StateDefinition.Builder<Block, BlockState> builder) { builder.add(FACING, ACTIVE); }

    @Override public BlockState getStateForPlacement(@Nonnull BlockPlaceContext context) {
        Direction look = context.getNearestLookingDirection();
        Player player = context.getPlayer();
        return defaultBlockState().setValue(FACING, player != null && player.isShiftKeyDown() ? look : look.getOpposite());
    }

    @Override @Nonnull public BlockEntity newBlockEntity(@Nonnull BlockPos pos, @Nonnull BlockState state) { return new HeatingElementBlockEntity(pos, state); }

    @Override @Nullable public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@Nonnull Level level, @Nonnull BlockState state, @Nonnull BlockEntityType<T> type) { return level.isClientSide || type != IFabBlockEntities.HEATING_ELEMENT.get() ? null : IServerTickableBE.makeTicker(); }
}
