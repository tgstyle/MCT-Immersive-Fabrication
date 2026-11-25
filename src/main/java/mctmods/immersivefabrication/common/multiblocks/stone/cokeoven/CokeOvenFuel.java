package mctmods.immersivefabrication.common.multiblocks.stone.cokeoven;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.ItemStackHandler;
import javax.annotation.Nonnull;

public class CokeOvenFuel extends ItemStackHandler {
    private final Runnable changed;

    public CokeOvenFuel(NonNullList<ItemStack> stacks, Runnable changed) {
        super(stacks);
        this.changed = changed;
    }

    @Override public boolean isItemValid(int slot, @Nonnull ItemStack stack) { return CokeOvenStacks.isFuel(stack); }

    @Override protected void onContentsChanged(int slot) { changed.run(); }
}
