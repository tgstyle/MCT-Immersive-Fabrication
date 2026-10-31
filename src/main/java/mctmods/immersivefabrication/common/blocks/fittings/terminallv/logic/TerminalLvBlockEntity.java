package mctmods.immersivefabrication.common.blocks.fittings.terminallv.logic;

import com.immersiveconvergence.api.wires.fittings.ICEnergyFittingBlockEntity;
import com.immersiveconvergence.api.wires.fittings.ICFittingTier;

import mctmods.immersivefabrication.core.registration.IFabBlockEntities;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class TerminalLvBlockEntity extends ICEnergyFittingBlockEntity {
    public TerminalLvBlockEntity(BlockPos pos, BlockState state) { super(IFabBlockEntities.TERMINAL_LV.get(), pos, state, ICFittingTier.LV); }
}
