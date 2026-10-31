package mctmods.immersivefabrication.common.blocks.fittings.transformerhv;

import mctmods.immersivefabrication.common.blocks.fittings.transformerhv.logic.TileEntityTransformerHv;
import mctmods.immersivefabrication.core.util.IFabContext;

import com.immersiveconvergence.api.wires.fittings.ICBlockTransformer;

public class TransformerHvBlock extends ICBlockTransformer {
    public TransformerHvBlock() { super(IFabContext.CONTEXT, "transformer_hv", TileEntityTransformerHv::new, TileEntityTransformerHv.LAYOUT); }
}
