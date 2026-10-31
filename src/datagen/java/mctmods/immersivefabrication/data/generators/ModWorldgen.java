package mctmods.immersivefabrication.data.generators;

import mctmods.immersivefabrication.ImmersiveFabrication;
import mctmods.immersivefabrication.core.registration.IFabBlocks;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.worldgen.BootstapContext;
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
import net.minecraftforge.common.data.DatapackBuiltinEntriesProvider;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.common.world.ForgeBiomeModifiers;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class ModWorldgen extends DatapackBuiltinEntriesProvider {
    private static final String[] ORDER = {"aluminum_ore", "lead_ore", "silver_ore", "nickel_ore", "nickel_ore_deep", "uranium_ore"};

    public static final RegistrySetBuilder BUILDER = new RegistrySetBuilder()
            .add(Registries.CONFIGURED_FEATURE, ModWorldgen::configured)
            .add(Registries.PLACED_FEATURE, ModWorldgen::placed)
            .add(ForgeRegistries.Keys.BIOME_MODIFIERS, ModWorldgen::modifiers);

    public ModWorldgen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, BUILDER, Set.of(ImmersiveFabrication.MODID));
    }

    private static ResourceKey<ConfiguredFeature<?, ?>> configuredKey(String name) { return ResourceKey.create(Registries.CONFIGURED_FEATURE, ResourceLocation.fromNamespaceAndPath(ImmersiveFabrication.MODID, name)); }

    private static ResourceKey<PlacedFeature> placedKey(String name) { return ResourceKey.create(Registries.PLACED_FEATURE, ResourceLocation.fromNamespaceAndPath(ImmersiveFabrication.MODID, name)); }

    private static void ore(BootstapContext<ConfiguredFeature<?, ?>> context, String name, String metal, int size, float discard) {
        List<OreConfiguration.TargetBlockState> targets = List.of(
                OreConfiguration.target(new TagMatchTest(BlockTags.STONE_ORE_REPLACEABLES), IFabBlocks.ENTRIES.get(metal + "_ore").get().defaultBlockState()),
                OreConfiguration.target(new TagMatchTest(BlockTags.DEEPSLATE_ORE_REPLACEABLES), IFabBlocks.ENTRIES.get("deepslate_" + metal + "_ore").get().defaultBlockState()));
        context.register(configuredKey(name), new ConfiguredFeature<>(Feature.ORE, new OreConfiguration(targets, size, discard)));
    }

    private static void configured(BootstapContext<ConfiguredFeature<?, ?>> context) {
        ore(context, "aluminum_ore", "aluminum", 6, 0.0F);
        ore(context, "lead_ore", "lead", 8, 0.0F);
        ore(context, "nickel_ore", "nickel", 5, 0.0F);
        ore(context, "nickel_ore_deep", "nickel", 6, 0.0F);
        ore(context, "silver_ore", "silver", 9, 0.25F);
        ore(context, "uranium_ore", "uranium", 4, 0.5F);
    }

    private static HeightProvider trapezoid(int min, int max) { return TrapezoidHeight.of(VerticalAnchor.absolute(min), VerticalAnchor.absolute(max)); }

    private static void place(BootstapContext<PlacedFeature> context, String name, int count, HeightProvider height) {
        HolderGetter<ConfiguredFeature<?, ?>> features = context.lookup(Registries.CONFIGURED_FEATURE);
        context.register(placedKey(name), new PlacedFeature(features.getOrThrow(configuredKey(name)), List.of(CountPlacement.of(count), InSquarePlacement.spread(), HeightRangePlacement.of(height), BiomeFilter.biome())));
    }

    private static void placed(BootstapContext<PlacedFeature> context) {
        place(context, "aluminum_ore", 16, trapezoid(32, 112));
        place(context, "lead_ore", 12, trapezoid(-32, 80));
        place(context, "nickel_ore", 7, UniformHeight.of(VerticalAnchor.absolute(-64), VerticalAnchor.absolute(24)));
        place(context, "nickel_ore_deep", 11, trapezoid(-120, -8));
        place(context, "silver_ore", 10, trapezoid(-48, 32));
        place(context, "uranium_ore", 9, trapezoid(-64, -16));
    }

    private static void modifiers(BootstapContext<BiomeModifier> context) {
        HolderGetter<Biome> biomes = context.lookup(Registries.BIOME);
        HolderGetter<PlacedFeature> features = context.lookup(Registries.PLACED_FEATURE);
        List<Holder<PlacedFeature>> holders = Arrays.stream(ORDER).<Holder<PlacedFeature>>map(name -> features.getOrThrow(placedKey(name))).toList();
        ResourceKey<BiomeModifier> key = ResourceKey.create(ForgeRegistries.Keys.BIOME_MODIFIERS, ResourceLocation.fromNamespaceAndPath(ImmersiveFabrication.MODID, "ores"));
        context.register(key, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(biomes.getOrThrow(BiomeTags.IS_OVERWORLD), HolderSet.direct(holders), GenerationStep.Decoration.UNDERGROUND_ORES));
    }
}
