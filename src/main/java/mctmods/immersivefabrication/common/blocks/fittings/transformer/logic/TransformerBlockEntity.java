package mctmods.immersivefabrication.common.blocks.fittings.transformer.logic;

import com.immersiveconvergence.api.wires.fittings.ICFittingTier;
import com.immersiveconvergence.api.wires.fittings.ICTransformerBlockEntity;
import com.immersiveconvergence.api.wires.fittings.ICTransformerLayout;

import mctmods.immersivefabrication.core.registration.IFabBlockEntities;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class TransformerBlockEntity extends ICTransformerBlockEntity {
    public static final ICTransformerLayout LAYOUT = new ICTransformerLayout(1, 0.1875F, 0.8125F, ICTransformerLayout.terminal(0.25, 1.5), ICTransformerLayout.terminal(-0.25, 1.75));

    public TransformerBlockEntity(BlockPos pos, BlockState state) { super(IFabBlockEntities.TRANSFORMER.get(), pos, state, LAYOUT, ICFittingTier.LV, ICFittingTier.MV); }
}
