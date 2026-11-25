package mctmods.immersivefabrication.common.multiblocks.stone.cokeoven;

import mctmods.immersivefabrication.common.multiblocks.stone.cokeoven.logic.CokeOvenLogic;

import com.immersiveconvergence.api.multiblock.blocks.env.IMultiblockLevel;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.IFluidTank;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandler.FluidAction;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;

public final class CokeOvenOutput {
    private CokeOvenOutput() {}

    public static boolean pushCoke(IMultiblockLevel level, CokeOvenLogic.State state, List<BlockPos> cells) {
        if (!CokeOvenStacks.isCokeBlock(state.getChamber())) { return false; }
        ItemStack one = state.getChamber().copyWithCount(1);
        for (BlockPos cell : cells) {
            IItemHandler handler = level.getRawLevel().getCapability(Capabilities.ItemHandler.BLOCK, level.toAbsolute(cell).below(), Direction.UP);
            if (handler != null && ItemHandlerHelper.insertItemStacked(handler, one, false).isEmpty()) {
                state.setChamber(ItemStack.EMPTY);
                return true;
            }
        }
        return false;
    }

    public static void pushFluid(Level level, BlockPos portCell, IFluidTank tank) {
        if (tank.getFluidAmount() <= 0) { return; }
        IFluidHandler handler = level.getCapability(Capabilities.FluidHandler.BLOCK, portCell.above(), Direction.DOWN);
        if (handler == null) { return; }
        FluidStack offer = tank.drain(tank.getFluidAmount(), FluidAction.SIMULATE);
        if (offer.isEmpty()) { return; }
        int accepted = handler.fill(offer, FluidAction.SIMULATE);
        if (accepted <= 0) { return; }
        FluidStack taken = tank.drain(accepted, FluidAction.EXECUTE);
        if (!taken.isEmpty()) { handler.fill(taken, FluidAction.EXECUTE); }
    }
}
