package mctmods.immersivefabrication;

import mctmods.immersivefabrication.core.IFabCommonConfig;
import mctmods.immersivefabrication.core.proxy.CommonProxy;
import mctmods.immersivefabrication.core.proxy.IFabGuiHandler;
import mctmods.immersivefabrication.core.registration.IFabBlocks;
import mctmods.immersivefabrication.core.registration.IFabFluids;
import mctmods.immersivefabrication.core.registration.IFabItems;
import mctmods.immersivefabrication.core.registration.IFabMultiblocks;
import mctmods.immersivefabrication.core.registration.IFabSmelting;
import mctmods.immersivefabrication.core.registration.IFabWireTypes;
import mctmods.immersivefabrication.core.registration.IFabWorldGen;

import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(modid = ImmersiveFabrication.MODID, name = ImmersiveFabrication.NAME, version = "1.0", acceptedMinecraftVersions = "[1.12.2,1.13)", dependencies = "required-after:immersiveconvergence@[1.0.185,);" + "required-after:forge@[14.23.3.2655,);")
public class ImmersiveFabrication {

    public static final String MODID = "immersivefabrication";
    public static final String NAME = "Immersive Fabrication";
    public static final Logger LOGGER = LogManager.getLogger(MODID);

    @Mod.Instance public static ImmersiveFabrication instance;

    @SidedProxy(clientSide = "mctmods.immersivefabrication.core.proxy.ClientProxy", serverSide = "mctmods.immersivefabrication.core.proxy.CommonProxy")
    public static CommonProxy proxy;

    static { FluidRegistry.enableUniversalBucket(); }

    @EventHandler public void preInit(FMLPreInitializationEvent event) {
        IFabFluids.register();
        IFabBlocks.register();
        if (IFabCommonConfig.wires()) { IFabWireTypes.register(); }
        IFabItems.register();
        IFabWorldGen.register();
        NetworkRegistry.INSTANCE.registerGuiHandler(instance, new IFabGuiHandler());
        proxy.preInit();
    }

    @EventHandler public void init(FMLInitializationEvent event) {
        IFabMultiblocks.register();
        IFabSmelting.register();
        proxy.init();
    }

    @EventHandler public void postInit(FMLPostInitializationEvent event) { proxy.postInit(); }
}
