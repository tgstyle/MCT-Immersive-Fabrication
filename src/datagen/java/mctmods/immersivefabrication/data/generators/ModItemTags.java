package mctmods.immersivefabrication.data.generators;

import mctmods.immersivefabrication.ImmersiveFabrication;
import mctmods.immersivefabrication.common.util.IFabBlockNames;
import mctmods.immersivefabrication.common.util.IFabItemNames;
import mctmods.immersivefabrication.core.registration.IFabBlocks;
import mctmods.immersivefabrication.core.registration.IFabItems;

import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.ExistingFileHelper;
import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Stream;

public class ModItemTags extends ItemTagsProvider {
    private static final String[] WIRES = {"aluminum", "copper", "acsr"};
    private static final String[] FENCE_METALS = {"aluminum", "steel"};
    private static final String[] TREATED = {"treated_wood", "treated_wood_slab"};
    private static final Map<String, String> ALIASES = Map.of("hop_graphite", "graphite", "saltpeter", "niter");

    public ModItemTags(PackOutput output, CompletableFuture<Provider> lookupProvider, CompletableFuture<TagLookup<Block>> blockTags, ExistingFileHelper existingFileHelper) { super(output, lookupProvider, blockTags, ImmersiveFabrication.MODID, existingFileHelper); }

    private static TagKey<Item> key(String namespace, String path) { return TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(namespace, path)); }

    private static TagKey<Block> blockKey(String path) { return TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("forge", path)); }

    private static String[] join(String[] first, String... more) { return Stream.concat(Arrays.stream(first), Arrays.stream(more)).toArray(String[]::new); }

    private static String[] sorted(String[] ids) { return Arrays.stream(ids).sorted().toArray(String[]::new); }

    private static String[] dustMetals() { return Arrays.stream(IFabItemNames.DUSTS).filter(id -> !id.equals("coke")).toArray(String[]::new); }

    private static Item item(String id) { return Objects.requireNonNull(IFabItems.ENTRIES.get(id), id).get(); }

    private void items(TagKey<Item> tag, String... ids) {
        IntrinsicTagAppender<Item> appender = tag(tag);
        for (String id : ids) { appender.add(item(id)); }
    }

    @Override protected void addTags(@Nonnull Provider provider) {
        addMaterials();
        addCopies();
        addMisc();
        addTools();
        addMinecraft();
    }

    private void group(String path, String[] children, Function<String, String> itemOf) {
        IntrinsicTagAppender<Item> parent = tag(key("forge", path));
        for (String child : children) {
            items(key("forge", path + "/" + child), itemOf.apply(child));
            parent.addTag(key("forge", path + "/" + child));
        }
    }

    private static Function<String, String> suffixed(String suffix) { return name -> ALIASES.getOrDefault(name, name) + suffix; }

    private void addMaterials() {
        group("ingots", sorted(join(IFabItemNames.INGOTS, "hop_graphite")), suffixed("_ingot"));
        group("nuggets", sorted(IFabItemNames.NUGGETS), suffixed("_nugget"));
        group("dusts", sorted(join(dustMetals(), "hop_graphite", "saltpeter")), suffixed("_dust"));
        group("plates", sorted(IFabItemNames.PLATES), suffixed("_plate"));
        group("rods", new String[]{"aluminum", "iron", "steel", "treated_wood"}, name -> name.equals("treated_wood") ? "treated_rod" : name + "_rod");
        group("raw_materials", Arrays.stream(IFabItemNames.RAW_ORES).map(id -> id.substring(4)).toArray(String[]::new), name -> "raw_" + name);
        tag(key("forge", "seeds/flax")).add(IFabItems.SEEDS.get());
        tag(key("forge", "seeds")).addTag(key("forge", "seeds/flax"));
        items(key("forge", "dusts/coal_coke"), "coke_dust");
        items(key("forge", "coal_coke"), "coal_coke");
        items(key("forge", "slag"), "slag");
    }

    private void addCopies() {
        copyGroup("fences", FENCE_METALS);
        copyGroup("ores", IFabBlockNames.ORE_METALS);
        copyGroup("sheetmetals", IFabBlockNames.CLADDING_METALS);
        String[] storage = join(IFabBlockNames.STORAGE_METALS, Arrays.stream(IFabBlockNames.ORE_METALS).map(metal -> "raw_" + metal).toArray(String[]::new));
        copyGroup("storage_blocks", join(storage, "coal_coke"));
    }

    private void copyGroup(String path, String[] children) {
        IntrinsicTagAppender<Item> parent = tag(key("forge", path));
        for (String child : children) {
            copy(blockKey(path + "/" + child), key("forge", path + "/" + child));
            parent.addTag(key("forge", path + "/" + child));
        }
    }

    private void addMisc() {
        for (String wire : WIRES) {
            tag(key("forge", "wires/" + wire)).add(ResourceKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(ImmersiveFabrication.MODID, wire + "_wire")));
            tag(key("forge", "wires")).addTag(key("forge", "wires/" + wire));
        }
        IntrinsicTagAppender<Item> wood = tag(key("forge", TREATED[0]));
        IntrinsicTagAppender<Item> slabs = tag(key("forge", TREATED[1]));
        for (String style : IFabBlockNames.TREATED_WOOD) {
            wood.add(Objects.requireNonNull(IFabBlocks.ENTRIES.get("treated_wood_" + style), style).get().asItem());
            slabs.add(Objects.requireNonNull(IFabBlocks.ENTRIES.get("treated_wood_" + style + "_slab"), style).get().asItem());
        }
    }

    private void addTools() {
        for (String namespace : new String[]{"immersiveengineering", ImmersiveFabrication.MODID}) {
            tag(key(namespace, "tools/hammers")).add(IFabItems.HAMMER.get());
            tag(key(namespace, "tools/screwdrivers")).add(IFabItems.SCREWDRIVER.get());
            tag(key(namespace, "tools/wirecutters")).add(IFabItems.CUTTERS.get());
        }
    }

    private void addMinecraft() {
        copy(BlockTags.FENCES, ItemTags.FENCES);
        copy(BlockTags.SLABS, ItemTags.SLABS);
        copy(BlockTags.STAIRS, ItemTags.STAIRS);
        copy(BlockTags.WOODEN_FENCES, ItemTags.WOODEN_FENCES);
        copy(BlockTags.WOODEN_SLABS, ItemTags.WOODEN_SLABS);
        copy(BlockTags.WOODEN_STAIRS, ItemTags.WOODEN_STAIRS);
    }
}
