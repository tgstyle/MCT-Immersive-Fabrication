package mctmods.immersivefabrication;

import com.immersiveconvergence.api.fluid.FluidEntry;

import mctmods.immersivefabrication.core.IFabCommonConfig;
import mctmods.immersivefabrication.core.IFabWiresCondition;
import mctmods.immersivefabrication.core.registration.IFabBlockEntities;
import mctmods.immersivefabrication.core.registration.IFabBlocks;
import mctmods.immersivefabrication.core.registration.IFabCreativeTabs;
import mctmods.immersivefabrication.core.registration.IFabDataComponents;
import mctmods.immersivefabrication.core.registration.IFabFluids;
import mctmods.immersivefabrication.core.registration.IFabItems;
import mctmods.immersivefabrication.core.registration.IFabMenuTypes;
import mctmods.immersivefabrication.core.registration.IFabLootModifiers;
import mctmods.immersivefabrication.core.registration.IFabMultiblocks;
import mctmods.immersivefabrication.core.registration.IFabWireTypes;

import com.immersiveconvergence.api.multiblock.MultiblockBuilder;
import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import org.slf4j.Logger;

@Mod(ImmersiveFabrication.MODID)
public class ImmersiveFabrication {
    public static final String MODID = "immersivefabrication";
    public static final Logger LOGGER = LogUtils.getLogger();

    public ImmersiveFabrication(IEventBus modEventBus, ModContainer container) {
        container.registerConfig(ModConfig.Type.COMMON, IFabCommonConfig.SPEC);
        MultiblockBuilder.modBus = modEventBus;
        IFabWiresCondition.init(modEventBus);
        if (IFabCommonConfig.wires()) { IFabWireTypes.register(); }
        IFabDataComponents.register(modEventBus);
        IFabMultiblocks.forceClassLoad();
        NeoForge.EVENT_BUS.addListener(ServerStartedEvent.class, event -> IFabMultiblocks.register());
        IFabBlocks.register(modEventBus);
        IFabBlockEntities.register(modEventBus);
        IFabItems.register(modEventBus);
        IFabCreativeTabs.register(modEventBus);
        IFabFluids.register(modEventBus);
        IFabMenuTypes.register(modEventBus);
        IFabLootModifiers.register(modEventBus);
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::registerCapabilities);
        LOGGER.info("Immersive Fabrication starting");
    }

    private void commonSetup(FMLCommonSetupEvent event) { event.enqueueWork(() -> FluidEntry.registerDispenserBehavior(IFabFluids.ENTRIES)); }

    private void registerCapabilities(RegisterCapabilitiesEvent event) {
        FluidEntry.registerBucketCapabilities(event, IFabFluids.ENTRIES);
        IFabBlockEntities.registerCapabilities(event);
    }
}
