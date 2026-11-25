package mctmods.immersivefabrication.common.multiblocks.stone.cokeoven;

import mctmods.immersivefabrication.common.multiblocks.stone.cokeoven.logic.TileEntityCokeOvenMaster;

import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nonnull;

public class CokeOvenInput implements IItemHandler {
    private final TileEntityCokeOvenMaster master;

    public CokeOvenInput(TileEntityCokeOvenMaster master) { this.master = master; }

    @Override public int getSlots() { return 3; }

    @Override @Nonnull public ItemStack getStackInSlot(int slot) {
        if (slot == 0) { return master.getChamber(); }
        if (slot == 1 || slot == 2) { return master.getInventory().get(slot - 1); }
        return ItemStack.EMPTY;
    }

    @Override @Nonnull public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
        if (stack.isEmpty()) { return stack; }
        if (CokeOvenStacks.isCoalBlock(stack)) { return insertChamber(stack, simulate); }
        if (!CokeOvenStacks.isFuel(stack)) { return stack; }
        return insertFuel(stack, simulate);
    }

    private ItemStack insertChamber(ItemStack stack, boolean simulate) {
        if (!master.getChamber().isEmpty()) { return stack; }
        if (!simulate) {
            master.setChamber(ItemHandlerHelper.copyStackWithSize(stack, 1));
            sync(0);
        }
        return stack.getCount() > 1 ? ItemHandlerHelper.copyStackWithSize(stack, stack.getCount() - 1) : ItemStack.EMPTY;
    }

    private ItemStack insertFuel(ItemStack stack, boolean simulate) {
        NonNullList<ItemStack> fuel = master.getInventory();
        ItemStack remaining = stack.copy();
        int first = master.getFuelNext() == 0 ? 0 : 1;
        int limit = stack.getMaxStackSize();
        boolean moved = false;
        for (int i = 0; i < 2; i++) {
            int index = i == 0 ? first : 1 - first;
            ItemStack current = fuel.get(index);
            int room;
            if (current.isEmpty()) { room = limit; }
            else if (ItemHandlerHelper.canItemStacksStack(current, remaining)) { room = limit - current.getCount(); }
            else { continue; }
            int amount = Math.min(remaining.getCount(), room);
            if (amount <= 0) { continue; }
            if (!simulate) {
                if (current.isEmpty()) { fuel.set(index, ItemHandlerHelper.copyStackWithSize(stack, amount)); }
                else { current.grow(amount); }
            }
            remaining.shrink(amount);
            moved = true;
            if (remaining.isEmpty()) { break; }
        }
        if (moved && !simulate) {
            master.setFuelNext(1 - first);
            sync(1);
        }
        return remaining.isEmpty() ? ItemStack.EMPTY : remaining;
    }

    private void sync(int slot) {
        master.doGraphicalUpdates(slot);
        master.requestClientSync();
    }

    @Override @Nonnull public ItemStack extractItem(int slot, int amount, boolean simulate) { return ItemStack.EMPTY; }

    @Override public int getSlotLimit(int slot) { return 64; }

    @Override public boolean isItemValid(int slot, @Nonnull ItemStack stack) { return insertItem(slot, stack, true).getCount() < stack.getCount(); }
}
