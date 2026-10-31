package mctmods.immersivefabrication.data.generators;

import mctmods.immersivefabrication.core.IFabWiresCondition;
import mctmods.immersivefabrication.core.registration.IFabBlocks;

import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.neoforge.common.conditions.ConditionalOps;
import net.neoforged.neoforge.common.conditions.WithConditions;
import net.neoforged.neoforge.registries.DeferredBlock;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public final class WireLoot implements DataProvider {
    private final PackOutput.PathProvider paths;
    private final CompletableFuture<HolderLookup.Provider> registries;

    public WireLoot(PackOutput out, CompletableFuture<HolderLookup.Provider> registries) {
        this.paths = out.createRegistryElementsPathProvider(Registries.LOOT_TABLE);
        this.registries = registries;
    }

    private static List<Block> wires() {
        List<Block> wires = new ArrayList<>();
        for (DeferredBlock<? extends Block> wire : IFabBlocks.WIRES.values()) { wires.add(wire.get()); }
        return wires;
    }

    @Override @Nonnull
    public CompletableFuture<?> run(@Nonnull CachedOutput output) {
        return registries.thenCompose(p -> {
            BlockLootSubProvider sub = new BlockLootSubProvider(Set.of(), FeatureFlags.REGISTRY.allFlags(), p) {
                @Override protected void generate() {
                    for (Block wire : wires()) { dropSelf(wire); }
                }

                @Override @Nonnull protected Iterable<Block> getKnownBlocks() { return wires(); }
            };
            List<CompletableFuture<?>> saves = new ArrayList<>();
            sub.generate((key, builder) -> {
                LootTable table = builder.setParamSet(LootContextParamSets.BLOCK).setRandomSequence(key.location()).build();
                JsonElement json = ConditionalOps.createConditionalCodecWithConditions(LootTable.DIRECT_CODEC)
                        .encodeStart(p.createSerializationContext(JsonOps.INSTANCE), Optional.of(new WithConditions<>(table, IFabWiresCondition.INSTANCE)))
                        .getOrThrow();
                saves.add(DataProvider.saveStable(output, json, paths.json(key.location())));
            });
            return CompletableFuture.allOf(saves.toArray(new CompletableFuture<?>[0]));
        });
    }

    @Override @Nonnull
    public String getName() { return "IFab wire loot"; }
}
