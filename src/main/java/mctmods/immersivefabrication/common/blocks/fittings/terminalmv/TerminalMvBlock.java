package mctmods.immersivefabrication.common.blocks.fittings.terminalmv;

import com.immersiveconvergence.api.wires.fittings.ICPlatedFittingBlock;

import mctmods.immersivefabrication.common.blocks.fittings.terminalmv.logic.TerminalMvBlockEntity;

public class TerminalMvBlock extends ICPlatedFittingBlock<TerminalMvBlockEntity> {
    public TerminalMvBlock() { super(TerminalMvBlockEntity::new); }
}
