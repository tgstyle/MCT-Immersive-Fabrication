package mctmods.immersivefabrication.core.registration;

import com.immersiveconvergence.api.block.ModBlockItem;
import com.immersiveconvergence.api.wires.ICWireType;
import com.immersiveconvergence.api.wires.fittings.ICWireCoilItem;

import mctmods.immersivefabrication.ImmersiveFabrication;
import mctmods.immersivefabrication.common.items.CuttersItem;
import mctmods.immersivefabrication.common.items.HammerItem;
import mctmods.immersivefabrication.common.items.FlaxSeedItem;
import mctmods.immersivefabrication.common.items.ScrewdriverItem;
import mctmods.immersivefabrication.common.util.IFabItemNames;
import mctmods.immersivefabrication.core.IFabCommonConfig;

import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.LinkedHashMap;
import java.util.Map;

public final class IFabItems {
    public static final DeferredRegister<Item> REGISTER = DeferredRegister.create(ForgeRegistries.ITEMS, ImmersiveFabrication.MODID);
    public static final Map<String, RegistryObject<Item>> ENTRIES = new LinkedHashMap<>();
    public static final RegistryObject<Item> HAMMER;
    public static final RegistryObject<Item> SCREWDRIVER;
    public static final RegistryObject<Item> SEEDS;
    public static final RegistryObject<Item> CUTTERS;
    private static final String SPOOL = "spool_";

    static {
        addAll(IFabItemNames.INGOTS, "_ingot");
        addAll(IFabItemNames.NUGGETS, "_nugget");
        addAll(IFabItemNames.DUSTS, "_dust");
        addAll(IFabItemNames.PLATES, "_plate");
        addAll(IFabItemNames.RODS, "_rod");
        addAll(IFabItemNames.WIRES, "_wire");
        addAll(IFabItemNames.RAW_ORES, "");
        addAll(IFabItemNames.MATERIALS, "");
        HAMMER = REGISTER.register("hammer", HammerItem::new);
        CUTTERS = REGISTER.register("cutters", CuttersItem::new);
        SCREWDRIVER = REGISTER.register("screwdriver", ScrewdriverItem::new);
        SEEDS = REGISTER.register("seed_flax", () -> new FlaxSeedItem(IFabBlocks.FLAX.get()));
    }

    private IFabItems() {}

    private static void addAll(String[] names, String suffix) {
        for (String name : names) {
            String id = name + suffix;
            ENTRIES.put(id, REGISTER.register(id, () -> new Item(new Item.Properties())));
        }
    }

    public static void addBlockItem(String id, RegistryObject<? extends Block> block) { ENTRIES.put(id, REGISTER.register(id, () -> new ModBlockItem(block.get()))); }

    public static RegistryObject<Item> get(String id) { return ENTRIES.get(id); }

    public static RegistryObject<Item> coil(String type) { return ENTRIES.get(spoolId(type)); }

    private static String spoolId(String type) { return SPOOL + (type.equals("steel_cable") ? "structural" : type); }

    public static void register(IEventBus bus) {
        if (IFabCommonConfig.wires()) {
            for (ICWireType type : IFabWireTypes.TYPES) { ENTRIES.put(spoolId(type.getUniqueName()), REGISTER.register(spoolId(type.getUniqueName()), () -> new ICWireCoilItem(type))); }
            IFabBlocks.WIRES.forEach(IFabItems::addBlockItem);
        }
        REGISTER.register(bus);
    }
}
