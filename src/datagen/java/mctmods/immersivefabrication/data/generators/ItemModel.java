package mctmods.immersivefabrication.data.generators;

import mctmods.immersivefabrication.ImmersiveFabrication;
import mctmods.immersivefabrication.common.util.IFabItemNames;
import mctmods.immersivefabrication.core.registration.IFabBlocks;
import mctmods.immersivefabrication.core.registration.IFabFluids;

import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.client.model.generators.loaders.DynamicFluidContainerModelBuilder;
import net.minecraftforge.common.data.ExistingFileHelper;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class ItemModel extends ItemModelProvider {
    private static final Set<String> OBJ_ITEMS = Set.of("anchor", "industrial_pump", "insulator_lv", "insulator_mv", "insulator_hv", "terminal_lv", "terminal_mv", "terminal_hv", "transformer", "transformer_hv");
    private static final Map<String, String> BLOCK_PARENTS = Map.of("aluminum_fence", "aluminum_fence_inventory", "steel_fence", "steel_fence_inventory", "treated_wood_fence", "treated_wood_fence_inventory");
    private static final String[] SPOOLS = {"acsr", "aluminum", "aluminum_insulated", "copper", "copper_insulated", "rope", "structural"};
    private static final String[] ASSEMBLIES = {"iron", "steel"};
    private static final String[] HANDHELD = {"cutters", "hammer", "screwdriver"};
    private static final String[] FLAT = {"flax_fiber", "linen", "coal_coke", "slag", "vacuum_tube", "circuit_board", "treated_rod", "seed_flax"};
    private static final String[] BUCKETS = {"biodiesel", "creosote"};

    public ItemModel(PackOutput output, ExistingFileHelper helper) { super(output, ImmersiveFabrication.MODID, helper); }

    private ResourceLocation tex(String path) { return modLoc("item/" + path); }

    private void generated(String id, String path) { withExistingParent(id, mcLoc("item/generated")).texture("layer0", tex(path)); }

    private void each(String[] names, String suffix, String folder) {
        for (String name : names) {
            if (!name.equals("treated")) { generated(name + suffix, "metal/" + folder + "/" + name); }
        }
    }

    private void blockItem(String id) { withExistingParent(id, modLoc("block/" + BLOCK_PARENTS.getOrDefault(id, id))); }

    private ResourceLocation forgeLoc() { return ResourceLocation.fromNamespaceAndPath("forge", "item/bucket"); }

    private void cokeOven() {
        ItemModelBuilder.TransformsBuilder transforms = withExistingParent("coke_oven", modLoc("block/coke_oven")).transforms();
        transforms.transform(ItemDisplayContext.GUI).rotation(30, 225, 0).scale(0.1591F);
        transforms.transform(ItemDisplayContext.GROUND).scale(0.0625F);
        transforms.transform(ItemDisplayContext.FIXED).scale(0.0625F);
    }

    @Override protected void registerModels() {
        each(IFabItemNames.INGOTS, "_ingot", "ingots");
        each(IFabItemNames.NUGGETS, "_nugget", "nuggets");
        each(IFabItemNames.DUSTS, "_dust", "dusts");
        each(IFabItemNames.PLATES, "_plate", "plates");
        each(IFabItemNames.RODS, "_rod", "rods");
        each(IFabItemNames.WIRES, "_wire", "wires");
        for (String raw : IFabItemNames.RAW_ORES) { generated(raw, "metal/raw/" + raw.substring("raw_".length())); }
        for (String assembly : ASSEMBLIES) { generated("assembly_" + assembly, "metal/assemblies/" + assembly); }
        for (String spool : SPOOLS) { generated("spool_" + spool, "spools/" + spool); }
        for (String flat : FLAT) { generated(flat, flat); }
        for (String bucket : BUCKETS) { generated(bucket + "_bucket", "fluid_containers_bucket_" + bucket); }
        withExistingParent("raw_coke_oven_gas_bucket", forgeLoc()).customLoader(DynamicFluidContainerModelBuilder::begin).fluid(IFabFluids.RAW_COKE_OVEN_GAS.getStill()).flipGas(true);
        cokeOven();
        for (String tool : HANDHELD) { withExistingParent(tool, mcLoc("item/handheld")).texture("layer0", tex(tool)); }
        Set<String> blocks = new LinkedHashSet<>(IFabBlocks.ENTRIES.keySet());
        blocks.addAll(IFabBlocks.WIRES.keySet());
        blocks.removeAll(OBJ_ITEMS);
        blocks.forEach(this::blockItem);
    }
}
