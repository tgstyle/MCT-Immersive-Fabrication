package mctmods.immersivefabrication;

import com.mojang.logging.LogUtils;
import net.neoforged.fml.common.Mod;
import org.slf4j.Logger;

@Mod(ImmersiveFabrication.MODID)
public class ImmersiveFabrication {
    public static final String MODID = "immersivefabrication";
    public static final Logger LOGGER = LogUtils.getLogger();

    public ImmersiveFabrication() { LOGGER.info("Immersive Fabrication starting"); }
}
