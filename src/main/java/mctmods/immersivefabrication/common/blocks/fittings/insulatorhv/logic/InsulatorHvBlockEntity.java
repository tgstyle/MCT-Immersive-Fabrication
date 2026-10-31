package mctmods.immersivefabrication.common.blocks.fittings.insulatorhv.logic;

import com.immersiveconvergence.api.wires.fittings.ICFittingBlockEntity;
import com.immersiveconvergence.api.wires.fittings.ICFittingTier;

import mctmods.immersivefabrication.core.registration.IFabBlockEntities;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class InsulatorHvBlockEntity extends ICFittingBlockEntity {
    public InsulatorHvBlockEntity(BlockPos pos, BlockState state) { super(IFabBlockEntities.INSULATOR_HV.get(), pos, state, ICFittingTier.HV, true); }
}
