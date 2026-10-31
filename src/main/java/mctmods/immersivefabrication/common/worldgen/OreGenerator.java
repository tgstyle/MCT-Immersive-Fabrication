package mctmods.immersivefabrication.common.worldgen;

import mctmods.immersivefabrication.common.blocks.metal.ore.OreType;
import mctmods.immersivefabrication.core.registration.IFabBlocks;

import net.minecraft.block.state.pattern.BlockMatcher;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.feature.WorldGenMinable;
import net.minecraftforge.fml.common.IWorldGenerator;

import java.util.Random;

public class OreGenerator implements IWorldGenerator {
    private final WorldGenMinable[] veins = new WorldGenMinable[OreType.values().length];

    public OreGenerator() {
        for (OreType type : OreType.values()) { veins[type.ordinal()] = new WorldGenMinable(IFabBlocks.ore.getStateFromMeta(type.getMeta()), type.veinSize, BlockMatcher.forBlock(Blocks.STONE)); }
    }

    @Override public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
        if (world.provider.getDimension() != 0) { return; }
        for (OreType type : OreType.values()) {
            for (int vein = 0; vein < type.veinsPerChunk; vein++) {
                if (random.nextInt(100) < type.chance) { veins[type.ordinal()].generate(world, random, new BlockPos(chunkX * 16 + random.nextInt(16), type.minY + random.nextInt(type.maxY - type.minY), chunkZ * 16 + random.nextInt(16))); }
            }
        }
    }
}
