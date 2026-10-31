package mctmods.immersivefabrication.common.blocks.fittings.insulatormv;

import mctmods.immersivefabrication.common.blocks.fittings.insulatormv.logic.TileEntityInsulatorMv;
import mctmods.immersivefabrication.core.util.IFabContext;

import com.immersiveconvergence.api.wires.fittings.ICBlockPlatedFitting;

public class InsulatorMvBlock extends ICBlockPlatedFitting {
    public InsulatorMvBlock() { super(IFabContext.CONTEXT, "insulator_mv", TileEntityInsulatorMv::new); }
}
