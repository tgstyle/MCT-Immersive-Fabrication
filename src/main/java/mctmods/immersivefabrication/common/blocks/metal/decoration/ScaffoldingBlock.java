package mctmods.immersivefabrication.common.blocks.metal.decoration;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nonnull;

public class ScaffoldingBlock extends Block {
    private static final VoxelShape COLLISION = Block.box(1.0D, 0.0D, 1.0D, 15.0D, 16.0D, 15.0D);

    public ScaffoldingBlock(Properties properties) { super(properties); }

    @Override @Nonnull protected VoxelShape getCollisionShape(@Nonnull BlockState state, @Nonnull BlockGetter level, @Nonnull BlockPos pos, @Nonnull CollisionContext context) { return COLLISION; }

    @Override protected boolean propagatesSkylightDown(@Nonnull BlockState state, @Nonnull BlockGetter level, @Nonnull BlockPos pos) { return true; }

    @Override protected boolean skipRendering(@Nonnull BlockState state, @Nonnull BlockState adjacent, @Nonnull Direction direction) { return adjacent.is(this); }
}
