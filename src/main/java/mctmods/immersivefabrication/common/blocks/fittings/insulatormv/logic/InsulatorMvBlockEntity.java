package mctmods.immersivefabrication.common.blocks.fittings.insulatormv.logic;

import com.immersiveconvergence.api.wires.fittings.ICFittingBlockEntity;
import com.immersiveconvergence.api.wires.fittings.ICFittingTier;

import mctmods.immersivefabrication.core.registration.IFabBlockEntities;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class InsulatorMvBlockEntity extends ICFittingBlockEntity {
    public InsulatorMvBlockEntity(BlockPos pos, BlockState state) { super(IFabBlockEntities.INSULATOR_MV.get(), pos, state, ICFittingTier.MV, true); }
}
