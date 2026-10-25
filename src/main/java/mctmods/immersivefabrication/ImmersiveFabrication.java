package mctmods.immersivefabrication;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(modid = ImmersiveFabrication.MODID, name = ImmersiveFabrication.NAME, version = "1.0", acceptedMinecraftVersions = "[1.12.2,1.13)", dependencies = "required-after:immersiveconvergence@[1.0.177,);" + "required-after:forge@[14.23.3.2655,);")
public class ImmersiveFabrication {

    public static final String MODID = "immersivefabrication";
    public static final String NAME = "Immersive Fabrication";
    public static final Logger LOGGER = LogManager.getLogger(MODID);

    @EventHandler public void preInit(FMLPreInitializationEvent event) { LOGGER.info("Pre-initializing {}", NAME); }

    @EventHandler public void init(FMLInitializationEvent event) { }

    @EventHandler public void postInit(FMLPostInitializationEvent event) { }
}
