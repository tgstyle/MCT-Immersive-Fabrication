package mctmods.immersivefabrication.common.blocks.fittings.transformerhv;

import com.immersiveconvergence.api.wires.fittings.ICTransformerBlock;

import mctmods.immersivefabrication.common.blocks.fittings.transformerhv.logic.TransformerHvBlockEntity;

public class TransformerHvBlock extends ICTransformerBlock<TransformerHvBlockEntity> {
    public TransformerHvBlock() { super(TransformerHvBlockEntity::new, TransformerHvBlockEntity.LAYOUT); }
}
