package mctmods.immersivefabrication.common.blocks.fittings.terminalhv;

import mctmods.immersivefabrication.common.blocks.fittings.terminalhv.logic.TileEntityTerminalHv;
import mctmods.immersivefabrication.core.util.IFabContext;

import com.immersiveconvergence.api.wires.fittings.ICBlockPlatedFitting;

public class TerminalHvBlock extends ICBlockPlatedFitting {
    public TerminalHvBlock() { super(IFabContext.CONTEXT, "terminal_hv", TileEntityTerminalHv::new); }
}
