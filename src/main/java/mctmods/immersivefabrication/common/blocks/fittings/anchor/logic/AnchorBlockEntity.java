package mctmods.immersivefabrication.common.blocks.fittings.anchor.logic;

import com.immersiveconvergence.api.wires.ICConnectionPoint;
import com.immersiveconvergence.api.wires.ICWireRole;
import com.immersiveconvergence.api.wires.ICWireType;
import com.immersiveconvergence.api.wires.fittings.ICFittingBlockEntity;
import com.immersiveconvergence.api.wires.fittings.ICFittingTier;

import mctmods.immersivefabrication.core.registration.IFabBlockEntities;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class AnchorBlockEntity extends ICFittingBlockEntity {
    private static final double REACH = -0.125 - 0.03125;

    public AnchorBlockEntity(BlockPos pos, BlockState state) { super(IFabBlockEntities.ANCHOR.get(), pos, state, ICFittingTier.STRUCTURE, false); }

    @Override public boolean accepts(@Nonnull ICConnectionPoint point, @Nonnull ICWireType type) {
        if (type.getCategory() == null || type.getRole() != ICWireRole.STRUCTURE) { return false; }
        return limitType == null || limitType == type;
    }

    @Override @Nonnull public Vec3 offset(@Nonnull ICConnectionPoint point, @Nonnull ICWireType type) { return offset(REACH); }

    @Override @Nonnull public VoxelShape getBlockBounds(@Nullable CollisionContext context) { return bounds(0.5F, 0.25F, 0.75F); }
}
