package mctmods.immersivefabrication.common.blocks.fittings.insulatorhv;

import mctmods.immersivefabrication.common.blocks.fittings.insulatorhv.logic.TileEntityInsulatorHv;
import mctmods.immersivefabrication.core.util.IFabContext;

import com.immersiveconvergence.api.wires.fittings.ICBlockPlatedFitting;
import com.immersiveconvergence.api.wires.fittings.ICFittingType;
import net.minecraft.util.BlockRenderLayer;

public class InsulatorHvBlock extends ICBlockPlatedFitting {
    public InsulatorHvBlock() {
        super(IFabContext.CONTEXT, "insulator_hv", TileEntityInsulatorHv::new);
        setMetaBlockLayer(ICFittingType.values()[0].getMeta(), BlockRenderLayer.SOLID, BlockRenderLayer.TRANSLUCENT);
    }
}
