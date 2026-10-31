package mctmods.immersivefabrication.common.blocks.fittings.transformer;

import mctmods.immersivefabrication.common.blocks.fittings.transformer.logic.TileEntityTransformer;
import mctmods.immersivefabrication.core.util.IFabContext;

import com.immersiveconvergence.api.wires.fittings.ICBlockTransformer;

public class TransformerBlock extends ICBlockTransformer {
    public TransformerBlock() { super(IFabContext.CONTEXT, "transformer", TileEntityTransformer::new, TileEntityTransformer.LAYOUT); }
}
