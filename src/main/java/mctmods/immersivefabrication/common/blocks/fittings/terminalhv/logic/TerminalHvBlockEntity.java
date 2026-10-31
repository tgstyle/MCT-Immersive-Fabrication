package mctmods.immersivefabrication.common.blocks.fittings.terminalhv.logic;

import com.immersiveconvergence.api.wires.fittings.ICEnergyFittingBlockEntity;
import com.immersiveconvergence.api.wires.fittings.ICFittingTier;

import mctmods.immersivefabrication.core.registration.IFabBlockEntities;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class TerminalHvBlockEntity extends ICEnergyFittingBlockEntity {
    public TerminalHvBlockEntity(BlockPos pos, BlockState state) { super(IFabBlockEntities.TERMINAL_HV.get(), pos, state, ICFittingTier.HV); }
}
