package mctmods.immersivefabrication.core.util;

import mctmods.immersivefabrication.ImmersiveFabrication;
import mctmods.immersivefabrication.common.blocks.metal.storage.StorageType;
import mctmods.immersivefabrication.core.registration.IFabBlocks;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public class IFabCreativeTab extends CreativeTabs {

    public IFabCreativeTab() { super(ImmersiveFabrication.MODID); }

    @Override @Nonnull public ItemStack createIcon() { return IFabBlocks.storage == null ? new ItemStack(Items.IRON_INGOT) : new ItemStack(IFabBlocks.storage, 1, StorageType.STEEL.getMeta()); }
}
