package mctmods.immersivefabrication.client;

import mctmods.immersivefabrication.ImmersiveFabrication;
import mctmods.immersivefabrication.client.gui.CokeOvenScreen;
import mctmods.immersivefabrication.client.models.CokeOvenModels;
import mctmods.immersivefabrication.client.models.FluidPipeGeometryLoader;
import mctmods.immersivefabrication.client.models.FluidPipeTint;
import mctmods.immersivefabrication.client.models.IndustrialPumpGeometryLoader;
import mctmods.immersivefabrication.client.models.IndustrialPumpModels;
import mctmods.immersivefabrication.client.renderer.CokeOvenRenderer;
import mctmods.immersivefabrication.client.renderer.IndustrialPumpRenderer;
import mctmods.immersivefabrication.common.blocks.metal.structure.StructureType;
import mctmods.immersivefabrication.core.IFabCommonConfig;
import mctmods.immersivefabrication.core.registration.IFabBlockEntities;
import mctmods.immersivefabrication.core.registration.IFabBlocks;
import mctmods.immersivefabrication.core.registration.IFabMenuTypes;
import mctmods.immersivefabrication.core.registration.IFabMultiblocks;

import com.immersiveconvergence.api.client.StandaloneModel;
import com.immersiveconvergence.api.client.split.SplitModelHandler;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

import java.util.List;

@EventBusSubscriber(modid = ImmersiveFabrication.MODID, value = Dist.CLIENT)
public final class IFabClientEvents {
    private static final List<Vec3i> TRANSFORMER_PARTS = List.of(new Vec3i(0, 0, 0));
    private static final List<Vec3i> TRANSFORMER_HV_PARTS = List.of(new Vec3i(0, 0, 0), new Vec3i(0, 1, 0));

    private IFabClientEvents() {}

    @SubscribeEvent public static void onRegisterMenuScreens(RegisterMenuScreensEvent event) { event.register(IFabMenuTypes.COKE_OVEN.getType(), CokeOvenScreen::new); }

    @SubscribeEvent public static void onRegisterAdditional(ModelEvent.RegisterAdditional event) {
        if (IFabCommonConfig.wires()) {
            SplitModelHandler.register(IFabBlocks.TRANSFORMER.get(), TRANSFORMER_PARTS, Direction.SOUTH);
            SplitModelHandler.register(IFabBlocks.TRANSFORMER_HV.get(), TRANSFORMER_HV_PARTS, Direction.SOUTH);
        }
    }

    @SubscribeEvent public static void onRegisterGeometryLoaders(ModelEvent.RegisterGeometryLoaders event) {
        event.register(ResourceLocation.fromNamespaceAndPath(ImmersiveFabrication.MODID, "fluid_pipe"), new FluidPipeGeometryLoader());
        event.register(ResourceLocation.fromNamespaceAndPath(ImmersiveFabrication.MODID, "industrial_pump_base"), new IndustrialPumpGeometryLoader());
        IndustrialPumpModels.SHAFT = new StandaloneModel(ResourceLocation.fromNamespaceAndPath(ImmersiveFabrication.MODID, "dynamic/industrial_pump_shaft"));
        CokeOvenModels.DOOR = new StandaloneModel(ResourceLocation.fromNamespaceAndPath(ImmersiveFabrication.MODID, "dynamic/coke_oven_door"));
    }

    @SubscribeEvent public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(IFabBlockEntities.INDUSTRIAL_PUMP.get(), context -> new IndustrialPumpRenderer());
        event.registerBlockEntityRenderer(IFabMultiblocks.COKE_OVEN.masterBE().get(), context -> new CokeOvenRenderer());
    }

    @SubscribeEvent public static void onRegisterBlockColors(RegisterColorHandlersEvent.Block event) { event.register(FluidPipeTint::block, IFabBlocks.STRUCTURE.get(StructureType.FLUID_PIPE).get()); }

    @SubscribeEvent public static void onRegisterItemColors(RegisterColorHandlersEvent.Item event) { event.register(FluidPipeTint::item, IFabBlocks.STRUCTURE.get(StructureType.FLUID_PIPE).get()); }
}
