package mctmods.immersivefabrication.common.blocks.fittings.transformerhv.logic;

import com.immersiveconvergence.api.wires.fittings.ICFittingTier;
import com.immersiveconvergence.api.wires.fittings.ICTransformerBlockEntity;
import com.immersiveconvergence.api.wires.fittings.ICTransformerLayout;

import mctmods.immersivefabrication.core.registration.IFabBlockEntities;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class TransformerHvBlockEntity extends ICTransformerBlockEntity {
    public static final ICTransformerLayout LAYOUT = new ICTransformerLayout(2, 0.125F, 0.875F, ICTransformerLayout.terminal(0.25, 2.75), ICTransformerLayout.terminal(-0.25, 2.875));

    public TransformerHvBlockEntity(BlockPos pos, BlockState state) { super(IFabBlockEntities.TRANSFORMER_HV.get(), pos, state, LAYOUT, ICFittingTier.MV, ICFittingTier.HV); }
}
