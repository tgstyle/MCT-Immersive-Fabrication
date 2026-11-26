package mctmods.immersivefabrication.client;

import com.immersiveconvergence.api.client.StandaloneModel;
import com.immersiveconvergence.api.client.split.SplitModelHandler;
import com.immersiveconvergence.api.fluid.FluidEntry;

import mctmods.immersivefabrication.ImmersiveFabrication;
import mctmods.immersivefabrication.client.gui.CokeOvenScreen;
import mctmods.immersivefabrication.client.models.CokeOvenModels;
import mctmods.immersivefabrication.client.models.FluidPipeGeometryLoader;
import mctmods.immersivefabrication.client.models.FluidPipeTint;
import mctmods.immersivefabrication.client.models.IndustrialPumpGeometryLoader;
import mctmods.immersivefabrication.client.models.IndustrialPumpModels;
import mctmods.immersivefabrication.client.renderer.CokeOvenRenderer;
import mctmods.immersivefabrication.client.renderer.IndustrialPumpRenderer;
import mctmods.immersivefabrication.core.IFabCommonConfig;
import mctmods.immersivefabrication.core.registration.IFabBlockEntities;
import mctmods.immersivefabrication.core.registration.IFabBlocks;
import mctmods.immersivefabrication.core.registration.IFabFluids;
import mctmods.immersivefabrication.core.registration.IFabMultiblocks;
import mctmods.immersivefabrication.core.registration.IFabMenuTypes;

import java.util.List;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@EventBusSubscriber(modid = ImmersiveFabrication.MODID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public final class IFabClientSetup {
    private static final List<Vec3i> TRANSFORMER_HV_PARTS = List.of(new Vec3i(0, 0, 0), new Vec3i(0, 1, 0));

    private IFabClientSetup() {}

    @SubscribeEvent public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            FluidEntry.registerRenderLayers(IFabFluids.ENTRIES);
            MenuScreens.register(IFabMenuTypes.COKE_OVEN.getType(), CokeOvenScreen::new);
        });
    }

    @SubscribeEvent public static void onRegisterAdditional(ModelEvent.RegisterAdditional event) {
        if (IFabCommonConfig.wires()) { SplitModelHandler.register(IFabBlocks.TRANSFORMER_HV.get(), TRANSFORMER_HV_PARTS, Direction.SOUTH); }
    }

    @SubscribeEvent public static void onRegisterGeometryLoaders(ModelEvent.RegisterGeometryLoaders event) {
        event.register("fluid_pipe", new FluidPipeGeometryLoader());
        event.register("industrial_pump_base", new IndustrialPumpGeometryLoader());
        IndustrialPumpModels.SHAFT = new StandaloneModel(ResourceLocation.fromNamespaceAndPath(ImmersiveFabrication.MODID, "dynamic/industrial_pump_shaft"));
        CokeOvenModels.DOOR = new StandaloneModel(ResourceLocation.fromNamespaceAndPath(ImmersiveFabrication.MODID, "dynamic/coke_oven_door"));
    }

    @SubscribeEvent public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(IFabBlockEntities.INDUSTRIAL_PUMP.get(), context -> new IndustrialPumpRenderer());
        event.registerBlockEntityRenderer(IFabMultiblocks.COKE_OVEN.masterBE().get(), context -> new CokeOvenRenderer());
    }

    @SubscribeEvent public static void onRegisterBlockColors(RegisterColorHandlersEvent.Block event) { event.register(FluidPipeTint::block, IFabBlocks.ENTRIES.get("fluid_pipe").get()); }

    @SubscribeEvent public static void onRegisterItemColors(RegisterColorHandlersEvent.Item event) { event.register(FluidPipeTint::item, IFabBlocks.ENTRIES.get("fluid_pipe").get()); }
}
