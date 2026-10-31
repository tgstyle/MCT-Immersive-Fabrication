package mctmods.immersivefabrication.common.blocks.fittings.insulatorlv;

import mctmods.immersivefabrication.common.blocks.fittings.insulatorlv.logic.TileEntityInsulatorLv;
import mctmods.immersivefabrication.core.util.IFabContext;

import com.immersiveconvergence.api.wires.fittings.ICBlockPlatedFitting;

public class InsulatorLvBlock extends ICBlockPlatedFitting {
    public InsulatorLvBlock() { super(IFabContext.CONTEXT, "insulator_lv", TileEntityInsulatorLv::new); }
}
