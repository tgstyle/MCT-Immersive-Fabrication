package mctmods.immersivefabrication.common.items;

import com.immersiveconvergence.api.block.BlockContext;
import com.immersiveconvergence.api.block.ICItemBase;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import javax.annotation.Nonnull;

public class CraftingToolItem extends ICItemBase {
    private static final String DAMAGE_KEY = "ifabDamage";

    private final int durability;

    public CraftingToolItem(BlockContext context, String name, int durability) {
        super(context, name, 1, name);
        this.durability = durability;
    }

    private static int damageOf(ItemStack stack) {
        NBTTagCompound tag = stack.getTagCompound();
        return tag == null ? 0 : tag.getInteger(DAMAGE_KEY);
    }

    @Override public boolean hasContainerItem(@Nonnull ItemStack stack) { return true; }

    @Override @Nonnull public ItemStack getContainerItem(@Nonnull ItemStack stack) {
        int damage = damageOf(stack) + 1;
        if (damage >= durability) { return ItemStack.EMPTY; }
        ItemStack worn = stack.copy();
        worn.setCount(1);
        NBTTagCompound tag = worn.getTagCompound();
        if (tag == null) {
            tag = new NBTTagCompound();
            worn.setTagCompound(tag);
        }
        tag.setInteger(DAMAGE_KEY, damage);
        return worn;
    }

    @Override public boolean showDurabilityBar(@Nonnull ItemStack stack) { return damageOf(stack) > 0; }

    @Override public double getDurabilityForDisplay(@Nonnull ItemStack stack) { return (double)damageOf(stack) / durability; }
}
