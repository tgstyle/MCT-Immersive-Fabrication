package mctmods.immersivefabrication.common.blocks.fittings.transformer.logic;

import com.immersiveconvergence.api.wires.fittings.ICFittingTier;
import com.immersiveconvergence.api.wires.fittings.ICTileEntityTransformer;
import com.immersiveconvergence.api.wires.fittings.ICTransformerLayout;

public class TileEntityTransformer extends ICTileEntityTransformer {
    public static final ICTransformerLayout LAYOUT = new ICTransformerLayout(1, 0.1875F, 0.8125F, ICTransformerLayout.terminal(0.25, 1.5), ICTransformerLayout.terminal(-0.25, 1.75));

    public TileEntityTransformer() { super(LAYOUT, ICFittingTier.LV, ICFittingTier.MV); }
}
