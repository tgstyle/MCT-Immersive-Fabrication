package mctmods.immersivefabrication.common.blocks.fittings.anchor.logic;

import com.immersiveconvergence.api.wires.ICConnectionPoint;
import com.immersiveconvergence.api.wires.ICWireRole;
import com.immersiveconvergence.api.wires.ICWireType;
import com.immersiveconvergence.api.wires.fittings.ICFittingTier;
import com.immersiveconvergence.api.wires.fittings.ICTileEntityFitting;

import net.minecraft.util.math.Vec3d;

import javax.annotation.Nonnull;

public class TileEntityAnchor extends ICTileEntityFitting {
    private static final double REACH = -0.125 - 0.03125;

    public TileEntityAnchor() { super(ICFittingTier.STRUCTURE, false); }

    @Override public boolean accepts(@Nonnull ICConnectionPoint point, @Nonnull ICWireType cable) {
        if (cable.getCategory() == null || cable.getRole() != ICWireRole.STRUCTURE) { return false; }
        return limitType == null || limitType == cable;
    }

    @Override @Nonnull public Vec3d offset(@Nonnull ICConnectionPoint point, @Nonnull ICWireType cable) { return offset(REACH); }

    @Override public float[] getBlockBounds() { return bounds(0.5F, 0.25F, 0.75F); }
}
