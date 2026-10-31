package mctmods.immersivefabrication.common.blocks.fittings.transformer;

import com.immersiveconvergence.api.wires.fittings.ICTransformerBlock;

import mctmods.immersivefabrication.common.blocks.fittings.transformer.logic.TransformerBlockEntity;

public class TransformerBlock extends ICTransformerBlock<TransformerBlockEntity> {
    public TransformerBlock() { super(TransformerBlockEntity::new, TransformerBlockEntity.LAYOUT); }
}
