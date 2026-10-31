package mctmods.immersivefabrication.common.blocks.fittings.anchor;

import com.immersiveconvergence.api.wires.fittings.ICFittingBlock;

import mctmods.immersivefabrication.common.blocks.fittings.anchor.logic.AnchorBlockEntity;

public class AnchorBlock extends ICFittingBlock<AnchorBlockEntity> {
    public AnchorBlock() { super(AnchorBlockEntity::new); }
}
