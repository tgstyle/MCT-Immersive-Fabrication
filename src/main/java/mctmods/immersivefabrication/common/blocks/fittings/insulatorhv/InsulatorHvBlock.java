package mctmods.immersivefabrication.common.blocks.fittings.insulatorhv;

import com.immersiveconvergence.api.wires.fittings.ICPlatedFittingBlock;

import mctmods.immersivefabrication.common.blocks.fittings.insulatorhv.logic.InsulatorHvBlockEntity;

public class InsulatorHvBlock extends ICPlatedFittingBlock<InsulatorHvBlockEntity> {
    public InsulatorHvBlock() { super(InsulatorHvBlockEntity::new); }
}
