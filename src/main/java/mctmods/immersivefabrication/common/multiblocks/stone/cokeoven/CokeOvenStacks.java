package mctmods.immersivefabrication.common.multiblocks.stone.cokeoven;

import mctmods.immersivefabrication.common.blocks.stone.StoneDecorationType;
import mctmods.immersivefabrication.core.registration.IFabBlocks;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.common.Tags;

public final class CokeOvenStacks {
    private static final TagKey<Item> COKE_BLOCKS = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "storage_blocks/coal_coke"));
    private static final ResourceLocation IE_COKE_BLOCK = ResourceLocation.fromNamespaceAndPath("immersiveengineering", "coke");

    private CokeOvenStacks() {}

    public static ItemStack cokeBlock() {
        if (ModList.get().isLoaded("immersiveengineering")) {
            Block block = BuiltInRegistries.BLOCK.get(IE_COKE_BLOCK);
            if (block != Blocks.AIR) { return new ItemStack(block); }
        }
        return new ItemStack(IFabBlocks.STONE_DECORATION.get(StoneDecorationType.COAL_COKE_BLOCK).get());
    }

    public static boolean isCoalBlock(ItemStack stack) { return stack.is(Tags.Items.STORAGE_BLOCKS_COAL); }

    public static boolean isCokeBlock(ItemStack stack) { return stack.is(COKE_BLOCKS); }

    public static int burnTime(ItemStack stack) { return stack.getBurnTime(null); }

    public static boolean isFuel(ItemStack stack) { return !stack.isEmpty() && burnTime(stack) > 0; }
}
