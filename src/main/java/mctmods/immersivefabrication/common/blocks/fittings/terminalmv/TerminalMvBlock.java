package mctmods.immersivefabrication.common.blocks.fittings.terminalmv;

import mctmods.immersivefabrication.common.blocks.fittings.terminalmv.logic.TileEntityTerminalMv;
import mctmods.immersivefabrication.core.util.IFabContext;

import com.immersiveconvergence.api.wires.fittings.ICBlockPlatedFitting;

public class TerminalMvBlock extends ICBlockPlatedFitting {
    public TerminalMvBlock() { super(IFabContext.CONTEXT, "terminal_mv", TileEntityTerminalMv::new); }
}
