package mctmods.immersivefabrication.core.registration;

import com.immersiveconvergence.api.block.ICBlockBase;
import com.immersiveconvergence.api.block.ICItemBase;
import com.immersiveconvergence.api.wires.fittings.ICItemWireCoil;

import mctmods.immersivefabrication.ImmersiveFabrication;
import mctmods.immersivefabrication.common.items.FlaxSeedItem;
import mctmods.immersivefabrication.common.items.CuttersItem;
import mctmods.immersivefabrication.common.items.HammerItem;
import mctmods.immersivefabrication.common.items.ScrewdriverItem;
import mctmods.immersivefabrication.common.util.IFabItemNames;
import mctmods.immersivefabrication.core.IFabCommonConfig;
import mctmods.immersivefabrication.core.util.IFabContext;

import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Objects;

@EventBusSubscriber(modid = ImmersiveFabrication.MODID)
public final class IFabItems {
    public static ICItemBase ingot;
    public static ICItemBase nugget;
    public static ICItemBase dust;
    public static ICItemBase plate;
    public static ICItemBase rod;
    public static ICItemBase wire;
    public static ICItemBase material;
    public static HammerItem hammer;
    public static CuttersItem cutters;
    public static ScrewdriverItem screwdriver;
    public static FlaxSeedItem seed;
    public static ICItemWireCoil spool;
    private static final int GRASS_SEED_WEIGHT = 5;

    private IFabItems() {}

    public static void register() {
        ingot = new ICItemBase(IFabContext.CONTEXT, "ingot", 64, IFabItemNames.INGOTS);
        nugget = new ICItemBase(IFabContext.CONTEXT, "nugget", 64, IFabItemNames.NUGGETS);
        dust = new ICItemBase(IFabContext.CONTEXT, "dust", 64, IFabItemNames.DUSTS);
        plate = new ICItemBase(IFabContext.CONTEXT, "plate", 64, IFabItemNames.PLATES);
        rod = new ICItemBase(IFabContext.CONTEXT, "rod", 64, IFabItemNames.RODS);
        wire = new ICItemBase(IFabContext.CONTEXT, "wire", 64, IFabItemNames.WIRES);
        material = new ICItemBase(IFabContext.CONTEXT, "material", 64, IFabItemNames.MATERIALS);
        hammer = new HammerItem(IFabContext.CONTEXT);
        cutters = new CuttersItem(IFabContext.CONTEXT);
        screwdriver = new ScrewdriverItem(IFabContext.CONTEXT);
        seed = new FlaxSeedItem(IFabContext.CONTEXT, IFabBlocks.flax);
        if (IFabCommonConfig.wires()) { spool = new ICItemWireCoil(IFabContext.CONTEXT, "spool", IFabWireTypes.TYPES); }
        MinecraftForge.addGrassSeed(new ItemStack(seed), GRASS_SEED_WEIGHT);
    }

    @SubscribeEvent public static void onRegisterItems(RegistryEvent.Register<Item> event) {
        for (Item item : IFabContext.ITEMS) {
            if (item instanceof ItemBlock) { item.setRegistryName(new ResourceLocation(ImmersiveFabrication.MODID, ((ICBlockBase<?>)((ItemBlock)item).getBlock()).name)); }
            else { item.setRegistryName(new ResourceLocation(ImmersiveFabrication.MODID, ((ICItemBase)item).itemName)); }
            event.getRegistry().register(item);
        }
        event.getRegistry().register(new ItemBlock(IFabBlocks.treatedLog).setRegistryName(Objects.requireNonNull(IFabBlocks.treatedLog.getRegistryName())));
        IFabOreDicts.register();
    }
}
