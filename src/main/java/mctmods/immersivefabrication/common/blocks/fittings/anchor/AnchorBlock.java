package mctmods.immersivefabrication.common.blocks.fittings.anchor;

import mctmods.immersivefabrication.common.blocks.fittings.anchor.logic.TileEntityAnchor;
import mctmods.immersivefabrication.core.util.IFabContext;

import com.immersiveconvergence.api.wires.fittings.ICBlockFitting;

public class AnchorBlock extends ICBlockFitting {
    public AnchorBlock() { super(IFabContext.CONTEXT, "anchor", TileEntityAnchor::new); }
}
