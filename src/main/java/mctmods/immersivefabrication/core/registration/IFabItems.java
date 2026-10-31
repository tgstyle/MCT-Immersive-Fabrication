package mctmods.immersivefabrication.core.registration;

import com.immersiveconvergence.api.block.ModBlockItem;
import com.immersiveconvergence.api.wires.ICWireType;
import com.immersiveconvergence.api.wires.fittings.ICWireCoilItem;

import mctmods.immersivefabrication.ImmersiveFabrication;
import mctmods.immersivefabrication.common.blocks.metal.ore.OreType;
import mctmods.immersivefabrication.common.items.CuttersItem;
import mctmods.immersivefabrication.common.items.FlaxSeedItem;
import mctmods.immersivefabrication.common.items.HammerItem;
import mctmods.immersivefabrication.common.items.ScrewdriverItem;
import mctmods.immersivefabrication.common.util.IFabMetals;
import mctmods.immersivefabrication.core.IFabCommonConfig;

import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class IFabItems {
    public static final DeferredRegister.Items REGISTER = DeferredRegister.createItems(ImmersiveFabrication.MODID);
    public static final Map<String, DeferredItem<Item>> METAL_ITEMS = new LinkedHashMap<>();
    public static final Map<OreType, DeferredItem<Item>> RAW_ITEMS = new EnumMap<>(OreType.class);
    public static final Map<String, DeferredItem<Item>> COILS = new LinkedHashMap<>();

    public static final List<String> MATERIAL_IDS = List.of("graphite_ingot", "graphite_dust", "coke_dust", "niter_dust", "sulfur_dust", "flax_fiber", "linen", "coal_coke", "slag", "assembly_iron", "assembly_steel", "vacuum_tube", "circuit_board");
    public static final DeferredItem<Item> HAMMER = REGISTER.register("hammer", () -> new HammerItem(new Item.Properties().durability(HammerItem.DURABILITY)));

    public static final DeferredItem<Item> SEED_FLAX = REGISTER.register("seed_flax", () -> new FlaxSeedItem(IFabBlocks.FLAX.get()));

    static {
        REGISTER.register("cutters", () -> new CuttersItem(new Item.Properties().durability(CuttersItem.DURABILITY)));
        REGISTER.register("screwdriver", () -> new ScrewdriverItem(new Item.Properties().stacksTo(1)));
        for (String id : MATERIAL_IDS) { REGISTER.registerSimpleItem(id); }
        for (OreType type : OreType.values()) { RAW_ITEMS.put(type, REGISTER.registerSimpleItem(type.rawId())); }
        for (String form : IFabMetals.FORMS) {
            for (String metal : IFabMetals.names(form)) {
                if (!IFabMetals.inVanilla(metal, form)) {
                    String id = IFabMetals.id(metal, form);
                    METAL_ITEMS.put(id, REGISTER.registerSimpleItem(id));
                }
            }
        }
    }

    private IFabItems() {}

    public static DeferredItem<Item> coil(String type) { return COILS.get(type); }

    private static String spoolId(String wire) { return "spool_" + (wire.equals("steel_cable") ? "structural" : wire); }

    public static void register(IEventBus bus) {
        if (IFabCommonConfig.wires()) {
            for (ICWireType type : IFabWireTypes.TYPES) { COILS.put(type.getUniqueName(), REGISTER.register(spoolId(type.getUniqueName()), () -> new ICWireCoilItem(type))); }
            IFabBlocks.WIRES.forEach((id, block) -> REGISTER.register(id, () -> new ModBlockItem(block.get())));
        }
        REGISTER.register(bus);
    }
}
