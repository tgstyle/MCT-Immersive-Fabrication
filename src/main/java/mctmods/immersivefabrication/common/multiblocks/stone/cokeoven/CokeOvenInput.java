package mctmods.immersivefabrication.common.multiblocks.stone.cokeoven;

import mctmods.immersivefabrication.common.multiblocks.stone.cokeoven.logic.CokeOvenLogic;

import javax.annotation.Nonnull;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;

public class CokeOvenInput implements IItemHandler {
    private final CokeOvenLogic.State state;

    public CokeOvenInput(CokeOvenLogic.State state) { this.state = state; }

    @Override public int getSlots() { return 3; }

    @Override @Nonnull public ItemStack getStackInSlot(int slot) {
        if (slot == 0) { return state.getChamber(); }
        if (slot == 1 || slot == 2) { return state.getFuel().get(slot - 1); }
        return ItemStack.EMPTY;
    }

    @Override @Nonnull public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
        if (stack.isEmpty()) { return stack; }
        if (CokeOvenStacks.isCoalBlock(stack)) { return insertChamber(stack, simulate); }
        if (!CokeOvenStacks.isFuel(stack)) { return stack; }
        return insertFuel(stack, simulate);
    }

    private ItemStack insertChamber(ItemStack stack, boolean simulate) {
        if (!state.getChamber().isEmpty()) { return stack; }
        if (!simulate) {
            state.setChamber(stack.copyWithCount(1));
            state.markChanged();
        }
        return stack.getCount() > 1 ? stack.copyWithCount(stack.getCount() - 1) : ItemStack.EMPTY;
    }

    private ItemStack insertFuel(ItemStack stack, boolean simulate) {
        NonNullList<ItemStack> fuel = state.getFuel();
        ItemStack remaining = stack.copy();
        int first = state.getFuelNext() == 0 ? 0 : 1;
        int limit = stack.getMaxStackSize();
        boolean moved = false;
        for (int i = 0; i < 2; i++) {
            int index = i == 0 ? first : 1 - first;
            ItemStack current = fuel.get(index);
            int room;
            if (current.isEmpty()) { room = limit; }
            else if (ItemStack.isSameItemSameComponents(current, remaining)) { room = limit - current.getCount(); }
            else { continue; }
            int amount = Math.min(remaining.getCount(), room);
            if (amount <= 0) { continue; }
            if (!simulate) {
                if (current.isEmpty()) { fuel.set(index, stack.copyWithCount(amount)); }
                else { current.grow(amount); }
            }
            remaining.shrink(amount);
            moved = true;
            if (remaining.isEmpty()) { break; }
        }
        if (moved && !simulate) {
            state.setFuelNext(1 - first);
            state.markChanged();
        }
        return remaining.isEmpty() ? ItemStack.EMPTY : remaining;
    }

    @Override @Nonnull public ItemStack extractItem(int slot, int amount, boolean simulate) { return ItemStack.EMPTY; }

    @Override public int getSlotLimit(int slot) { return 64; }

    @Override public boolean isItemValid(int slot, @Nonnull ItemStack stack) { return insertItem(slot, stack, true).getCount() < stack.getCount(); }
}
