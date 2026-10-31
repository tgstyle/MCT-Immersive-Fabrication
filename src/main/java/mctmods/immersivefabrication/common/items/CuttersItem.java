package mctmods.immersivefabrication.common.items;

import net.minecraft.world.item.Item;

public class CuttersItem extends CraftingToolItem {
    public static final int DURABILITY = 250;

    public CuttersItem() { super(new Item.Properties().durability(DURABILITY)); }
}
