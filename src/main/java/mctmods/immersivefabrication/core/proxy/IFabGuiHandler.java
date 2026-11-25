package mctmods.immersivefabrication.core.proxy;

import mctmods.immersivefabrication.ImmersiveFabrication;
import mctmods.immersivefabrication.common.gui.ContainerCokeOven;
import mctmods.immersivefabrication.common.gui.IFabGuiIds;
import mctmods.immersivefabrication.common.multiblocks.stone.cokeoven.logic.TileEntityCokeOvenMaster;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class IFabGuiHandler implements IGuiHandler {

    @Override public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
        TileEntity tile = world.getTileEntity(new BlockPos(x, y, z));
        if (id == IFabGuiIds.COKE_OVEN && tile instanceof TileEntityCokeOvenMaster) { return new ContainerCokeOven(player.inventory, (TileEntityCokeOvenMaster) tile); }
        return null;
    }

    @Override public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) { return ImmersiveFabrication.proxy.guiClient(id, player, world, x, y, z); }
}
