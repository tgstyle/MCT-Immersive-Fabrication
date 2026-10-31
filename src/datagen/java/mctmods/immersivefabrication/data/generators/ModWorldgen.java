package mctmods.immersivefabrication.data.generators;

import mctmods.immersivefabrication.ImmersiveFabrication;
import mctmods.immersivefabrication.core.registration.IFabBlocks;

import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.heightproviders.HeightProvider;
import net.minecraft.world.level.levelgen.heightproviders.TrapezoidHeight;
import net.minecraft.world.level.levelgen.heightproviders.UniformHeight;
import net.minecraft.world.level.levelgen.placement.BiomeFilter;
import net.minecraft.world.level.levelgen.placement.CountPlacement;
import net.minecraft.world.level.levelgen.placement.HeightRangePlacement;
import net.minecraft.world.level.levelgen.placement.InSquarePlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.structure.templatesystem.TagMatchTest;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.common.world.BiomeModifiers;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class ModWorldgen extends DatapackBuiltinEntriesProvider {
    private static final String[] NAMES = {"aluminum", "lead", "silver", "nickel", "deep_nickel", "uranium"};

    public static final RegistrySetBuilder BUILDER = new RegistrySetBuilder()
            .add(Registries.CONFIGURED_FEATURE, ModWorldgen::configured)
            .add(Registries.PLACED_FEATURE, ModWorldgen::placed)
            .add(NeoForgeRegistries.Keys.BIOME_MODIFIERS, ModWorldgen::modifiers);

    public ModWorldgen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, BUILDER, Set.of(ImmersiveFabrication.MODID));
    }

    private static ResourceKey<ConfiguredFeature<?, ?>> configuredKey(String name) { return ResourceKey.create(Registries.CONFIGURED_FEATURE, ResourceLocation.fromNamespaceAndPath(ImmersiveFabrication.MODID, name)); }

    private static ResourceKey<PlacedFeature> placedKey(String name) { return ResourceKey.create(Registries.PLACED_FEATURE, ResourceLocation.fromNamespaceAndPath(ImmersiveFabrication.MODID, name)); }

    private static void ore(BootstrapContext<ConfiguredFeature<?, ?>> context, String name, String metal, int size, float discard) {
        List<OreConfiguration.TargetBlockState> targets = List.of(
                OreConfiguration.target(new TagMatchTest(BlockTags.STONE_ORE_REPLACEABLES), IFabBlocks.ALL.get(metal + "_ore").get().defaultBlockState()),
                OreConfiguration.target(new TagMatchTest(BlockTags.DEEPSLATE_ORE_REPLACEABLES), IFabBlocks.ALL.get("deepslate_" + metal + "_ore").get().defaultBlockState()));
        context.register(configuredKey(name), new ConfiguredFeature<>(Feature.ORE, new OreConfiguration(targets, size, discard)));
    }

    private static void configured(BootstrapContext<ConfiguredFeature<?, ?>> context) {
        ore(context, "aluminum", "aluminum", 6, 0.0F);
        ore(context, "lead", "lead", 8, 0.0F);
        ore(context, "nickel", "nickel", 5, 0.0F);
        ore(context, "deep_nickel", "nickel", 6, 0.0F);
        ore(context, "silver", "silver", 9, 0.25F);
        ore(context, "uranium", "uranium", 4, 0.5F);
    }

    private static HeightProvider trapezoid(int min, int max) { return TrapezoidHeight.of(VerticalAnchor.absolute(min), VerticalAnchor.absolute(max)); }

    private static void place(BootstrapContext<PlacedFeature> context, String name, int count, HeightProvider height) {
        HolderGetter<ConfiguredFeature<?, ?>> features = context.lookup(Registries.CONFIGURED_FEATURE);
        context.register(placedKey(name), new PlacedFeature(features.getOrThrow(configuredKey(name)), List.of(CountPlacement.of(count), InSquarePlacement.spread(), HeightRangePlacement.of(height), BiomeFilter.biome())));
    }

    private static void placed(BootstrapContext<PlacedFeature> context) {
        place(context, "aluminum", 16, trapezoid(32, 112));
        place(context, "lead", 12, trapezoid(-32, 80));
        place(context, "nickel", 7, UniformHeight.of(VerticalAnchor.absolute(-64), VerticalAnchor.absolute(24)));
        place(context, "deep_nickel", 11, trapezoid(-120, -8));
        place(context, "silver", 10, trapezoid(-48, 32));
        place(context, "uranium", 9, trapezoid(-64, -16));
    }

    private static void modifiers(BootstrapContext<BiomeModifier> context) {
        HolderGetter<Biome> biomes = context.lookup(Registries.BIOME);
        HolderGetter<PlacedFeature> features = context.lookup(Registries.PLACED_FEATURE);
        for (String name : NAMES) {
            ResourceKey<BiomeModifier> key = ResourceKey.create(NeoForgeRegistries.Keys.BIOME_MODIFIERS, ResourceLocation.fromNamespaceAndPath(ImmersiveFabrication.MODID, name));
            context.register(key, new BiomeModifiers.AddFeaturesBiomeModifier(biomes.getOrThrow(BiomeTags.IS_OVERWORLD), HolderSet.direct(features.getOrThrow(placedKey(name))), GenerationStep.Decoration.UNDERGROUND_ORES));
        }
    }
}
