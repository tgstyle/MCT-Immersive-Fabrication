package mctmods.immersivefabrication.common.multiblocks.stone.cokeoven;

import mctmods.immersivefabrication.common.multiblocks.stone.cokeoven.logic.CokeOvenLogic;

import com.immersiveconvergence.api.multiblock.util.CapabilityReference;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

public final class CokeOvenOutput {
    private CokeOvenOutput() {}

    public static boolean pushCoke(CokeOvenLogic.State state) {
        if (!CokeOvenStacks.isCokeBlock(state.getChamber())) { return false; }
        ItemStack one = ItemHandlerHelper.copyStackWithSize(state.getChamber(), 1);
        for (CapabilityReference<IItemHandler> target : state.getCokeTargets()) {
            IItemHandler handler = target.getNullable();
            if (handler != null && ItemHandlerHelper.insertItemStacked(handler, one, false).isEmpty()) {
                state.setChamber(ItemStack.EMPTY);
                return true;
            }
        }
        return false;
    }

    public static void pushFluid(CapabilityReference<IFluidHandler> target, IFluidTank tank) {
        if (tank.getFluidAmount() <= 0) { return; }
        IFluidHandler handler = target.getNullable();
        if (handler == null) { return; }
        FluidStack offer = tank.drain(tank.getFluidAmount(), FluidAction.SIMULATE);
        if (offer.isEmpty()) { return; }
        int accepted = handler.fill(offer, FluidAction.SIMULATE);
        if (accepted <= 0) { return; }
        FluidStack taken = tank.drain(accepted, FluidAction.EXECUTE);
        if (!taken.isEmpty()) { handler.fill(taken, FluidAction.EXECUTE); }
    }
}
