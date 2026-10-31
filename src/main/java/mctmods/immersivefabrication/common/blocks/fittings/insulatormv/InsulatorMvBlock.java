package mctmods.immersivefabrication.common.blocks.fittings.insulatormv;

import com.immersiveconvergence.api.wires.fittings.ICPlatedFittingBlock;

import mctmods.immersivefabrication.common.blocks.fittings.insulatormv.logic.InsulatorMvBlockEntity;

public class InsulatorMvBlock extends ICPlatedFittingBlock<InsulatorMvBlockEntity> {
    public InsulatorMvBlock() { super(InsulatorMvBlockEntity::new); }
}
