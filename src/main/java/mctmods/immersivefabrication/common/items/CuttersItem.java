package mctmods.immersivefabrication.common.items;

import com.immersiveconvergence.api.block.BlockContext;

public class CuttersItem extends CraftingToolItem {
    public static final int DURABILITY = 250;

    public CuttersItem(BlockContext context) { super(context, "cutters", DURABILITY); }
}
