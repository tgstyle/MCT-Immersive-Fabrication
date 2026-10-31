package mctmods.immersivefabrication.data.generators;

import mctmods.immersivefabrication.ImmersiveFabrication;
import mctmods.immersivefabrication.core.registration.IFabBlocks;

import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Stream;

public class ModBlockTags extends BlockTagsProvider {
    private static final String[] ALUMINUM = {"aluminum"};
    private static final String[] NEEDS_IRON_METALS = {"lead", "silver", "nickel", "uranium"};
    private static final String[] NEEDS_IRON_STORAGE = {"lead", "silver", "nickel", "uranium", "constantan", "steel"};
    private static final String[] FENCES = {"steel_fence", "aluminum_fence"};
    private static final String[] LADDER = {"steel_ladder"};
    private static final String[] MACHINES = {"fluid_pipe", "fluid_placer", "heating_element", "metal_barrel", "industrial_pump"};
    private static final String[] BRICKS = {"coke_brick", "blast_brick", "blast_brick_reinforced", "alloy_brick", "coal_coke_block"};

    public ModBlockTags(PackOutput output, CompletableFuture<Provider> lookupProvider, ExistingFileHelper existingFileHelper) { super(output, lookupProvider, ImmersiveFabrication.MODID, existingFileHelper); }

    private static TagKey<Block> key(String namespace, String path) { return TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath(namespace, path)); }

    private static String[] join(String[]... parts) { return Arrays.stream(parts).flatMap(Arrays::stream).toArray(String[]::new); }

    private static String[] map(String[] ids, Function<String, String> mapper) { return Arrays.stream(ids).map(mapper).toArray(String[]::new); }

    private static String[] flat(String[] ids, Function<String, Stream<String>> mapper) { return Arrays.stream(ids).flatMap(mapper).toArray(String[]::new); }

    private static String[] suffixed(String[] ids, String suffix) { return map(ids, id -> id + suffix); }

    private static String[] withVariants(String[] ids) { return flat(ids, id -> Stream.of(id, id + "_slab", id + "_stairs")); }

    private static String[] scaffoldings(String metal) { return map(TagNames.SCAFFOLDING_TYPES, type -> metal + "_scaffolding_" + type); }

    private static String[] scaffoldings() { return join(scaffoldings("steel"), scaffoldings("aluminum")); }

    private static String[] treatedWood() { return map(TagNames.TREATED_WOOD, style -> "treated_wood_" + style); }

    private static String[] claddings() { return suffixed(TagNames.CLADDING_METALS, "_cladding"); }

    private static String[] oreBlocks(String[] metals) { return flat(metals, metal -> Stream.of(metal + "_ore", "deepslate_" + metal + "_ore", "raw_" + metal + "_block")); }

    private IntrinsicTagAppender<Block> blocks(TagKey<Block> tag, String... ids) {
        IntrinsicTagAppender<Block> appender = tag(tag);
        for (String id : ids) { appender.add(Objects.requireNonNull(IFabBlocks.ALL.get(id), id).get()); }
        return appender;
    }

    @Override protected void addTags(@Nonnull Provider provider) {
        addCommon();
        addImmersiveEngineering();
        addMinecraft();
    }

    private void parent(String path, String[] children, Function<String, String[]> blocksOf) {
        IntrinsicTagAppender<Block> parent = tag(key("c", path));
        for (String child : children) {
            blocks(key("c", path + "/" + child), blocksOf.apply(child));
            parent.addTag(key("c", path + "/" + child));
        }
    }

    private void addCommon() {
        blocks(key("c", "fences"), "steel_fence", "aluminum_fence", "treated_wood_fence");
        for (String name : new String[]{"steel", "aluminum", "wooden"}) { blocks(key("c", "fences/" + name), (name.equals("wooden") ? "treated_wood" : name) + "_fence"); }
        parent("ores", TagNames.ORE_METALS, metal -> new String[]{metal + "_ore", "deepslate_" + metal + "_ore"});
        parent("sheetmetals", TagNames.CLADDING_METALS, metal -> new String[]{metal + "_cladding"});
        String[] storage = join(TagNames.STORAGE_METALS, map(TagNames.ORE_METALS, metal -> "raw_" + metal), new String[]{"coal_coke"});
        parent("storage_blocks", storage, name -> new String[]{name + "_block"});
    }

    private void addImmersiveEngineering() {
        for (String metal : TagNames.FRAME_METALS) { blocks(key("immersiveengineering", "scaffoldings/" + metal), scaffoldings(metal)); }
        blocks(key("immersiveengineering", "treated_wood"), treatedWood());
        blocks(key("immersiveengineering", "treated_wood_slab"), suffixed(treatedWood(), "_slab"));
    }

    private void addMinecraft() {
        blocks(BlockTags.CLIMBABLE, join(scaffoldings(), LADDER));
        blocks(BlockTags.FENCES, "steel_fence", "aluminum_fence", "treated_wood_fence");
        blocks(BlockTags.LOGS, "treated_log");
        blocks(BlockTags.WOODEN_FENCES, "treated_wood_fence");
        blocks(BlockTags.WOODEN_SLABS, suffixed(treatedWood(), "_slab"));
        blocks(BlockTags.WOODEN_STAIRS, suffixed(treatedWood(), "_stairs"));
        blocks(BlockTags.SLABS, join(suffixed(claddings(), "_slab"), suffixed(scaffoldings(), "_slab"), suffixed(TagNames.CONCRETES, "_slab"), suffixed(treatedWood(), "_slab")));
        blocks(BlockTags.STAIRS, join(suffixed(scaffoldings(), "_stairs"), suffixed(TagNames.CONCRETES, "_stairs"), suffixed(treatedWood(), "_stairs")));
        blocks(BlockTags.MINEABLE_WITH_AXE, join(withVariants(treatedWood()), new String[]{"treated_wood_fence", "treated_log"}));
        blocks(BlockTags.NEEDS_STONE_TOOL, join(new String[]{"aluminum_block"}, oreBlocks(ALUMINUM)));
        blocks(BlockTags.NEEDS_IRON_TOOL, join(suffixed(NEEDS_IRON_STORAGE, "_block"), oreBlocks(NEEDS_IRON_METALS)));
        String[] cladding = flat(claddings(), id -> Stream.of(id, id + "_slab"));
        String[] pickaxe = join(suffixed(TagNames.STORAGE_METALS, "_block"), cladding, oreBlocks(TagNames.ORE_METALS), TagNames.ENGINEERING, FENCES, withVariants(scaffoldings()), BRICKS, withVariants(TagNames.CONCRETES), LADDER, MACHINES);
        IntrinsicTagAppender<Block> appender = blocks(BlockTags.MINEABLE_WITH_PICKAXE, pickaxe);
        for (String id : IFabBlocks.WIRES.keySet()) { appender.addOptional(ResourceLocation.fromNamespaceAndPath(ImmersiveFabrication.MODID, id)); }
    }
}
