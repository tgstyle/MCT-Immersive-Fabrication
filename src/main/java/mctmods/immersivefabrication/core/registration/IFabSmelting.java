package mctmods.immersivefabrication.core.registration;

import mctmods.immersivefabrication.common.blocks.metal.ore.OreType;
import mctmods.immersivefabrication.common.util.IFabItemNames;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;

import java.util.Arrays;

public final class IFabSmelting {
    private static final float ORE_EXPERIENCE = 1.0F;
    private static final float DUST_EXPERIENCE = 0.5F;
    private static final int IRON_DUST = Arrays.asList(IFabItemNames.DUSTS).indexOf("iron");
    private static final int GOLD_DUST = Arrays.asList(IFabItemNames.DUSTS).indexOf("gold");

    private IFabSmelting() {}

    public static void register() {
        for (OreType type : OreType.values()) { GameRegistry.addSmelting(new ItemStack(IFabBlocks.ore, 1, type.getMeta()), new ItemStack(IFabItems.ingot, 1, type.getMeta()), ORE_EXPERIENCE); }
        for (int meta = 0; meta < IFabItemNames.DUSTS.length; meta++) {
            ItemStack ingot = ingotOf(meta);
            if (!ingot.isEmpty()) { GameRegistry.addSmelting(new ItemStack(IFabItems.dust, 1, meta), ingot, DUST_EXPERIENCE); }
        }
    }

    private static ItemStack ingotOf(int dustMeta) {
        if (dustMeta == IRON_DUST) { return new ItemStack(Items.IRON_INGOT); }
        if (dustMeta == GOLD_DUST) { return new ItemStack(Items.GOLD_INGOT); }
        return dustMeta < IFabItemNames.NUGGETS.length ? new ItemStack(IFabItems.ingot, 1, dustMeta) : ItemStack.EMPTY;
    }
}
