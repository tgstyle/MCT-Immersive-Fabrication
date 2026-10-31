package mctmods.immersivefabrication.common.blocks.fittings.transformerhv.logic;

import com.immersiveconvergence.api.wires.fittings.ICFittingTier;
import com.immersiveconvergence.api.wires.fittings.ICTileEntityTransformer;
import com.immersiveconvergence.api.wires.fittings.ICTransformerLayout;

public class TileEntityTransformerHv extends ICTileEntityTransformer {
    public static final ICTransformerLayout LAYOUT = new ICTransformerLayout(2, 0.125F, 0.875F, ICTransformerLayout.terminal(0.25, 2.75), ICTransformerLayout.terminal(-0.25, 2.875));

    public TileEntityTransformerHv() { super(LAYOUT, ICFittingTier.MV, ICFittingTier.HV); }
}
