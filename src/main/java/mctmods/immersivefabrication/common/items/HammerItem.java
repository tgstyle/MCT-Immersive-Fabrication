package mctmods.immersivefabrication.common.items;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelReader;

import javax.annotation.Nonnull;

public class HammerItem extends CraftingToolItem {
    public static final int DURABILITY = 100;

    public HammerItem() { super(new Item.Properties().durability(DURABILITY)); }

    @Override public boolean doesSneakBypassUse(@Nonnull ItemStack stack, @Nonnull LevelReader level, @Nonnull BlockPos pos, @Nonnull Player player) { return true; }
}
