package mctmods.immersivefabrication.common.items;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;

public abstract class CraftingToolItem extends Item {
    protected CraftingToolItem(Properties properties) { super(properties); }

    @Override public boolean hasCraftingRemainingItem(@Nonnull ItemStack stack) { return true; }

    @Override @Nonnull public ItemStack getCraftingRemainingItem(@Nonnull ItemStack stack) {
        int damage = stack.getDamageValue() + 1;
        if (damage >= stack.getMaxDamage()) { return ItemStack.EMPTY; }
        ItemStack worn = stack.copy();
        worn.setCount(1);
        worn.setDamageValue(damage);
        return worn;
    }
}
