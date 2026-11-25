package mctmods.immersivefabrication.core.proxy;

import mctmods.immersivefabrication.ImmersiveFabrication;

import com.immersiveconvergence.api.multiblock.ICBlockInterfaces.IGuiTile;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class CommonProxy {

    public void preInit() {}

    public void init() {}

    public void postInit() {}

    public Object guiClient(int id, EntityPlayer player, World world, int x, int y, int z) { return null; }

    public static <T extends TileEntity & IGuiTile> void openGuiForTile(EntityPlayer player, T tile) { player.openGui(ImmersiveFabrication.instance, tile.getGuiID(), tile.getWorld(), tile.getPos().getX(), tile.getPos().getY(), tile.getPos().getZ()); }
}
