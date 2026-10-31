package mctmods.immersivefabrication.common.blocks.fittings.insulatorlv.logic;

import com.immersiveconvergence.api.wires.fittings.ICFittingBlockEntity;
import com.immersiveconvergence.api.wires.fittings.ICFittingTier;

import mctmods.immersivefabrication.core.registration.IFabBlockEntities;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class InsulatorLvBlockEntity extends ICFittingBlockEntity {
    public InsulatorLvBlockEntity(BlockPos pos, BlockState state) { super(IFabBlockEntities.INSULATOR_LV.get(), pos, state, ICFittingTier.LV, true); }
}
