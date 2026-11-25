package mctmods.immersivefabrication.common.multiblocks.stone.cokeoven;

import mctmods.immersivefabrication.common.blocks.stone.StoneDecorationType;
import mctmods.immersivefabrication.core.registration.IFabBlocks;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.oredict.OreDictionary;

public final class CokeOvenStacks {
    private CokeOvenStacks() {}

    public static ItemStack cokeBlock() {
        if (Loader.isModLoaded("immersiveengineering")) {
            Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation("immersiveengineering", "stone_decoration"));
            if (block != null && block != Blocks.AIR) { return new ItemStack(block, 1, 3); }
        }
        return new ItemStack(IFabBlocks.stoneDecoration, 1, StoneDecorationType.COAL_COKE.getMeta());
    }

    public static boolean isCoalBlock(ItemStack stack) { return hasOre(stack, "blockCoal"); }

    public static boolean isCokeBlock(ItemStack stack) { return hasOre(stack, "blockFuelCoke"); }

    public static int burnTime(ItemStack stack) { return TileEntityFurnace.getItemBurnTime(stack); }

    public static boolean isFuel(ItemStack stack) { return !stack.isEmpty() && burnTime(stack) > 0; }

    private static boolean hasOre(ItemStack stack, String name) {
        if (stack.isEmpty()) { return false; }
        for (int id : OreDictionary.getOreIDs(stack)) {
            if (name.equals(OreDictionary.getOreName(id))) { return true; }
        }
        return false;
    }
}
