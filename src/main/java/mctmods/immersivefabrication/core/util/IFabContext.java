package mctmods.immersivefabrication.core.util;

import com.immersiveconvergence.api.block.BlockContext;

import mctmods.immersivefabrication.ImmersiveFabrication;
import mctmods.immersivefabrication.core.proxy.CommonProxy;

import net.minecraft.block.Block;
import net.minecraft.item.Item;

import java.util.ArrayList;
import java.util.List;

public final class IFabContext {
    public static final List<Block> BLOCKS = new ArrayList<>();
    public static final List<Item> ITEMS = new ArrayList<>();
    public static final BlockContext CONTEXT = new BlockContext(ImmersiveFabrication.MODID, new IFabCreativeTab(), BLOCKS, ITEMS, ImmersiveFabrication.LOGGER, CommonProxy::openGuiForTile);

    private IFabContext() {}
}
