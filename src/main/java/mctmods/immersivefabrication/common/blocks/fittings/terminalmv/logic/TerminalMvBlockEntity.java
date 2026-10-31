package mctmods.immersivefabrication.common.blocks.fittings.terminalmv.logic;

import com.immersiveconvergence.api.wires.fittings.ICEnergyFittingBlockEntity;
import com.immersiveconvergence.api.wires.fittings.ICFittingTier;

import mctmods.immersivefabrication.core.registration.IFabBlockEntities;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class TerminalMvBlockEntity extends ICEnergyFittingBlockEntity {
    public TerminalMvBlockEntity(BlockPos pos, BlockState state) { super(IFabBlockEntities.TERMINAL_MV.get(), pos, state, ICFittingTier.MV); }
}
