package mctmods.immersivefabrication.common.blocks.fittings.terminallv;

import com.immersiveconvergence.api.wires.fittings.ICPlatedFittingBlock;

import mctmods.immersivefabrication.common.blocks.fittings.terminallv.logic.TerminalLvBlockEntity;

public class TerminalLvBlock extends ICPlatedFittingBlock<TerminalLvBlockEntity> {
    public TerminalLvBlock() { super(TerminalLvBlockEntity::new); }
}
