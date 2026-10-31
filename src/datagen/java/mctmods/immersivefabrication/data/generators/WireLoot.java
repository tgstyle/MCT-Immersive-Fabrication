package mctmods.immersivefabrication.data.generators;

import mctmods.immersivefabrication.core.registration.IFabBlocks;

import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.RegistryObject;
import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class WireLoot extends BlockLootSubProvider {
    private final List<Block> known = new ArrayList<>();

    public WireLoot() { super(Set.of(), FeatureFlags.REGISTRY.allFlags()); }

    @Override protected void generate() {
        for (RegistryObject<? extends Block> wire : IFabBlocks.WIRES.values()) {
            known.add(wire.get());
            dropSelf(wire.get());
        }
    }

    @Override @Nonnull protected Iterable<Block> getKnownBlocks() { return known; }
}
