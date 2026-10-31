package mctmods.immersivefabrication.core.util;

import com.immersiveconvergence.api.block.BlockContext;
import com.immersiveconvergence.api.multiblock.ICBlockInterfaces.IGuiTile;

import mctmods.immersivefabrication.ImmersiveFabrication;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;

import java.util.ArrayList;
import java.util.List;

public final class IFabContext {
    public static final List<Block> BLOCKS = new ArrayList<>();
    public static final List<Item> ITEMS = new ArrayList<>();
    public static final BlockContext CONTEXT = new BlockContext(ImmersiveFabrication.MODID, new IFabCreativeTab(), BLOCKS, ITEMS, ImmersiveFabrication.LOGGER, IFabContext::noGui);

    private IFabContext() {}

    private static <T extends TileEntity & IGuiTile> void noGui(EntityPlayer player, T tile) { throw new UnsupportedOperationException("Immersive Fabrication has no machine GUIs"); }
}
