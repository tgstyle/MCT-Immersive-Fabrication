package mctmods.immersivefabrication.common.multiblocks.stone.cokeoven;

import mctmods.immersivefabrication.common.multiblocks.stone.cokeoven.logic.TileEntityCokeOvenMaster;

import com.immersiveconvergence.api.util.ICFluidTank;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

public final class CokeOvenOutput {
    private CokeOvenOutput() {}

    public static boolean pushCoke(World world, TileEntityCokeOvenMaster master) {
        if (!CokeOvenStacks.isCokeBlock(master.getChamber())) { return false; }
        ItemStack one = ItemHandlerHelper.copyStackWithSize(master.getChamber(), 1);
        for (int x = 0; x < 3; x++) {
            for (int z = 0; z < 3; z++) {
                TileEntity tile = world.getTileEntity(master.getBlockPosForPos(new BlockPos(x, 0, z)).down());
                if (tile == null || !tile.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.UP)) { continue; }
                IItemHandler handler = tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.UP);
                if (handler != null && ItemHandlerHelper.insertItemStacked(handler, one, false).isEmpty()) {
                    master.setChamber(ItemStack.EMPTY);
                    return true;
                }
            }
        }
        return false;
    }

    public static void pushFluid(World world, BlockPos portCell, ICFluidTank tank) {
        if (tank.getFluidAmount() <= 0) { return; }
        IFluidHandler handler = FluidUtil.getFluidHandler(world, portCell.up(), EnumFacing.DOWN);
        if (handler == null) { return; }
        FluidStack offer = tank.drain(tank.getFluidAmount(), false);
        if (offer == null) { return; }
        int accepted = handler.fill(offer, false);
        if (accepted <= 0) { return; }
        FluidStack taken = tank.drain(accepted, true);
        if (taken != null) { handler.fill(taken, true); }
    }
}
