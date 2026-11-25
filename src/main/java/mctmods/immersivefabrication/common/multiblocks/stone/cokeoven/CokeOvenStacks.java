package mctmods.immersivefabrication.common.multiblocks.stone.cokeoven;

import mctmods.immersivefabrication.core.registration.IFabBlocks;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.ForgeRegistries;

public final class CokeOvenStacks {
    private static final TagKey<Item> COAL_BLOCK = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("forge", "storage_blocks/coal"));
    private static final TagKey<Item> COKE_BLOCK = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("forge", "storage_blocks/coal_coke"));

    private CokeOvenStacks() {}

    public static ItemStack cokeBlock() {
        if (ModList.get().isLoaded("immersiveengineering")) {
            Block block = ForgeRegistries.BLOCKS.getValue(ResourceLocation.fromNamespaceAndPath("immersiveengineering", "coke"));
            if (block != null && block != Blocks.AIR) { return new ItemStack(block); }
        }
        return new ItemStack(IFabBlocks.ENTRIES.get("coal_coke_block").get());
    }

    public static boolean isCoalBlock(ItemStack stack) { return stack.is(COAL_BLOCK); }

    public static boolean isCokeBlock(ItemStack stack) { return stack.is(COKE_BLOCK); }

    public static int burnTime(ItemStack stack) { return ForgeHooks.getBurnTime(stack, null); }

    public static boolean isFuel(ItemStack stack) { return !stack.isEmpty() && burnTime(stack) > 0; }
}
