package mctmods.immersivefabrication;

import com.mojang.logging.LogUtils;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(ImmersiveFabrication.MODID)
public class ImmersiveFabrication {
    public static final String MODID = "immersivefabrication";
    public static final Logger LOGGER = LogUtils.getLogger();

    public ImmersiveFabrication(FMLJavaModLoadingContext context) {
        IEventBus modEventBus = context.getModEventBus();
        modEventBus.addListener(this::commonSetup);
    }

    private void commonSetup(FMLCommonSetupEvent event) { LOGGER.info("Immersive Fabrication common setup"); }
}
