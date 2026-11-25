package mctmods.immersivefabrication;

import com.immersiveconvergence.api.fluid.FluidEntry;

import mctmods.immersivefabrication.core.IFabCommonConfig;
import mctmods.immersivefabrication.core.IFabWiresCondition;
import mctmods.immersivefabrication.core.registration.IFabBlockEntities;
import mctmods.immersivefabrication.core.registration.IFabBlocks;
import mctmods.immersivefabrication.core.registration.IFabCreativeTabs;
import mctmods.immersivefabrication.core.registration.IFabFluids;
import mctmods.immersivefabrication.core.registration.IFabItems;
import mctmods.immersivefabrication.core.registration.IFabLootModifiers;
import mctmods.immersivefabrication.core.registration.IFabMenuTypes;
import mctmods.immersivefabrication.core.registration.IFabMultiblocks;
import mctmods.immersivefabrication.core.registration.IFabWireTypes;

import com.mojang.logging.LogUtils;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(ImmersiveFabrication.MODID)
public class ImmersiveFabrication {
    public static final String MODID = "immersivefabrication";
    public static final Logger LOGGER = LogUtils.getLogger();

    public ImmersiveFabrication(FMLJavaModLoadingContext context) {
        IEventBus modEventBus = context.getModEventBus();
        context.registerConfig(ModConfig.Type.COMMON, IFabCommonConfig.SPEC);
        CraftingHelper.register(IFabWiresCondition.Serializer.INSTANCE);
        if (IFabCommonConfig.wires()) { IFabWireTypes.register(); }
        IFabMultiblocks.register();
        IFabBlocks.register(modEventBus);
        IFabBlockEntities.register(modEventBus);
        IFabItems.register(modEventBus);
        IFabCreativeTabs.register(modEventBus);
        IFabFluids.register(modEventBus);
        IFabLootModifiers.register(modEventBus);
        IFabMenuTypes.register(modEventBus);
        modEventBus.addListener(this::commonSetup);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> FluidEntry.registerDispenserBehavior(IFabFluids.ENTRIES));
        LOGGER.info("Immersive Fabrication common setup");
    }
}
