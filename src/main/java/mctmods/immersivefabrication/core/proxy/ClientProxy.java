package mctmods.immersivefabrication.core.proxy;

import mctmods.immersivefabrication.client.renderer.IndustrialPumpRenderer;
import mctmods.immersivefabrication.common.blocks.metal.structure.logic.TileEntityIndustrialPump;

import net.minecraftforge.fml.client.registry.ClientRegistry;

@SuppressWarnings("unused")
public class ClientProxy extends CommonProxy {

    @Override public void preInit() { ClientRegistry.bindTileEntitySpecialRenderer(TileEntityIndustrialPump.class, new IndustrialPumpRenderer()); }
}
