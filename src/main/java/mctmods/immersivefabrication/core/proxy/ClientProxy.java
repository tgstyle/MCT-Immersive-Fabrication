package mctmods.immersivefabrication.core.proxy;

import mctmods.immersivefabrication.ImmersiveFabrication;
import mctmods.immersivefabrication.client.gui.GuiCokeOven;
import mctmods.immersivefabrication.client.renderer.CokeOvenRenderer;
import mctmods.immersivefabrication.client.renderer.IndustrialPumpRenderer;
import mctmods.immersivefabrication.common.blocks.metal.structure.logic.TileEntityIndustrialPump;
import mctmods.immersivefabrication.common.gui.IFabGuiIds;
import mctmods.immersivefabrication.common.multiblocks.stone.cokeoven.CokeOvenMultiblock;
import mctmods.immersivefabrication.common.multiblocks.stone.cokeoven.logic.TileEntityCokeOvenMaster;

import com.immersiveconvergence.api.client.split.SplitModelHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.registry.ClientRegistry;

@SuppressWarnings("unused")
public class ClientProxy extends CommonProxy {

    @Override public void preInit() {
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityIndustrialPump.class, new IndustrialPumpRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityCokeOvenMaster.class, new CokeOvenRenderer());
        SplitModelHandler.register(ImmersiveFabrication.MODID, "stone_multiblock_coke_oven", () -> CokeOvenMultiblock.instance);
    }

    @Override public Object guiClient(int id, EntityPlayer player, World world, int x, int y, int z) {
        TileEntity tile = world.getTileEntity(new BlockPos(x, y, z));
        if (id == IFabGuiIds.COKE_OVEN && tile instanceof TileEntityCokeOvenMaster) { return new GuiCokeOven(player.inventory, (TileEntityCokeOvenMaster) tile); }
        return null;
    }
}
