package mctmods.immersivefabrication.data.generators;

import mctmods.immersivefabrication.ImmersiveFabrication;
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
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredHolder;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ModItemTags extends ItemTagsProvider {
    private static final String[] WIRES = {"aluminum", "copper", "acsr"};
    private static final String[] FENCE_METALS = {"aluminum", "steel"};
    private static final Map<String, String> ALIASES = Map.of("hop_graphite", "graphite", "saltpeter", "niter");

    private final Map<String, Item> registered = IFabItems.REGISTER.getEntries().stream().collect(Collectors.toMap(holder -> holder.getId().getPath(), DeferredHolder::get));

    public ModItemTags(PackOutput output, CompletableFuture<Provider> lookupProvider, CompletableFuture<TagLookup<Block>> blockTags, ExistingFileHelper existingFileHelper) { super(output, lookupProvider, blockTags, ImmersiveFabrication.MODID, existingFileHelper); }

    private static TagKey<Item> key(String namespace, String path) { return TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(namespace, path)); }

    private static TagKey<Block> blockKey(String path) { return TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("c", path)); }

    private static String[] join(String[] first, String... more) { return Stream.concat(Arrays.stream(first), Arrays.stream(more)).toArray(String[]::new); }

    private static String[] sorted(String[] ids) { return Arrays.stream(ids).sorted().toArray(String[]::new); }

    private static String[] dustMetals() { return Arrays.stream(TagNames.DUSTS).filter(id -> !id.equals("coke")).toArray(String[]::new); }

    private Item item(String id) { return Objects.requireNonNull(registered.get(id), id); }

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
        IntrinsicTagAppender<Item> parent = tag(key("c", path));
        for (String child : children) {
            items(key("c", path + "/" + child), itemOf.apply(child));
            parent.addTag(key("c", path + "/" + child));
        }
    }

    private static Function<String, String> suffixed(String suffix) { return name -> ALIASES.getOrDefault(name, name) + suffix; }

    private void addMaterials() {
        group("ingots", sorted(join(TagNames.INGOTS, "hop_graphite")), suffixed("_ingot"));
        group("nuggets", sorted(TagNames.NUGGETS), suffixed("_nugget"));
        group("dusts", sorted(join(dustMetals(), "hop_graphite", "saltpeter")), suffixed("_dust"));
        group("plates", sorted(TagNames.PLATES), suffixed("_plate"));
        group("rods", new String[]{"aluminum", "iron", "steel", "treated_wood"}, name -> name.equals("treated_wood") ? "treated_rod" : name + "_rod");
        group("raw_materials", Arrays.stream(TagNames.RAW_ORES).map(id -> id.substring(4)).toArray(String[]::new), name -> "raw_" + name);
        tag(key("c", "seeds/flax")).add(IFabItems.SEED_FLAX.get());
        tag(key("c", "seeds")).addTag(key("c", "seeds/flax"));
        items(key("c", "dusts/coal_coke"), "coke_dust");
        items(key("c", "coal_coke"), "coal_coke");
        items(key("c", "slag"), "slag");
    }

    private void addCopies() {
        copyGroup("fences", FENCE_METALS);
        copyGroup("ores", TagNames.ORE_METALS);
        copyGroup("sheetmetals", TagNames.CLADDING_METALS);
        String[] storage = join(TagNames.STORAGE_METALS, Arrays.stream(TagNames.ORE_METALS).map(metal -> "raw_" + metal).toArray(String[]::new));
        copyGroup("storage_blocks", join(storage, "coal_coke"));
    }

    private void copyGroup(String path, String[] children) {
        IntrinsicTagAppender<Item> parent = tag(key("c", path));
        for (String child : children) {
            copy(blockKey(path + "/" + child), key("c", path + "/" + child));
            parent.addTag(key("c", path + "/" + child));
        }
    }

    private void addMisc() {
        for (String wire : WIRES) {
            tag(key("c", "wires/" + wire)).add(ResourceKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(ImmersiveFabrication.MODID, wire + "_wire")));
            tag(key("c", "wires")).addTag(key("c", "wires/" + wire));
        }
    }

    private void addTools() {
        for (String namespace : new String[]{"immersiveengineering", ImmersiveFabrication.MODID}) {
            tag(key(namespace, "tools/hammers")).add(IFabItems.HAMMER.get());
            items(key(namespace, "tools/screwdrivers"), "screwdriver");
            items(key(namespace, "tools/cutters"), "cutters");
        }
    }

    private void addMinecraft() {
        copy(BlockTags.FENCES, ItemTags.FENCES);
        copy(BlockTags.LOGS, ItemTags.LOGS);
        copy(BlockTags.SLABS, ItemTags.SLABS);
        copy(BlockTags.STAIRS, ItemTags.STAIRS);
        copy(BlockTags.WOODEN_FENCES, ItemTags.WOODEN_FENCES);
        copy(BlockTags.WOODEN_SLABS, ItemTags.WOODEN_SLABS);
        copy(BlockTags.WOODEN_STAIRS, ItemTags.WOODEN_STAIRS);
    }
}
