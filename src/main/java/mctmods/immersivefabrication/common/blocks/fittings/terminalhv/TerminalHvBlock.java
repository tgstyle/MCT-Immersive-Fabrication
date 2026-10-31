package mctmods.immersivefabrication.common.blocks.fittings.terminalhv;

import com.immersiveconvergence.api.wires.fittings.ICPlatedFittingBlock;

import mctmods.immersivefabrication.common.blocks.fittings.terminalhv.logic.TerminalHvBlockEntity;

public class TerminalHvBlock extends ICPlatedFittingBlock<TerminalHvBlockEntity> {
    public TerminalHvBlock() { super(TerminalHvBlockEntity::new); }
}
