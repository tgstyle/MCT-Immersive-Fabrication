package mctmods.immersivefabrication.common.blocks.fittings.insulatorlv;

import com.immersiveconvergence.api.wires.fittings.ICPlatedFittingBlock;

import mctmods.immersivefabrication.common.blocks.fittings.insulatorlv.logic.InsulatorLvBlockEntity;

public class InsulatorLvBlock extends ICPlatedFittingBlock<InsulatorLvBlockEntity> {
    public InsulatorLvBlock() { super(InsulatorLvBlockEntity::new); }
}
