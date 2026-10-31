package mctmods.immersivefabrication.common.blocks.fittings.terminallv;

import mctmods.immersivefabrication.common.blocks.fittings.terminallv.logic.TileEntityTerminalLv;
import mctmods.immersivefabrication.core.util.IFabContext;

import com.immersiveconvergence.api.wires.fittings.ICBlockPlatedFitting;

public class TerminalLvBlock extends ICBlockPlatedFitting {
    public TerminalLvBlock() { super(IFabContext.CONTEXT, "terminal_lv", TileEntityTerminalLv::new); }
}
