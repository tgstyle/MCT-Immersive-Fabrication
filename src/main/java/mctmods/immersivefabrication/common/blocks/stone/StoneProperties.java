package mctmods.immersivefabrication.common.blocks.stone;

import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;

public final class StoneProperties {
    private StoneProperties() {}

    public static Properties concrete() { return Properties.of().mapColor(MapColor.STONE).instrument(NoteBlockInstrument.BASEDRUM).sound(SoundType.STONE).requiresCorrectToolForDrops().strength(2.0F, 10.0F); }

    public static Properties cokeBrick() { return Properties.of().mapColor(MapColor.STONE).instrument(NoteBlockInstrument.BASEDRUM).sound(SoundType.NETHER_BRICKS).requiresCorrectToolForDrops().strength(2.0F, 8.0F); }

    public static Properties blastBrick() { return Properties.of().mapColor(MapColor.COLOR_RED).instrument(NoteBlockInstrument.BASEDRUM).sound(SoundType.NETHER_BRICKS).requiresCorrectToolForDrops().strength(2.0F, 8.0F); }

    public static Properties reinforcedBlastBrick() { return Properties.of().mapColor(MapColor.COLOR_RED).instrument(NoteBlockInstrument.BASEDRUM).sound(SoundType.NETHER_BRICKS).requiresCorrectToolForDrops().strength(2.5F, 12.0F); }

    public static Properties alloyBrick() { return Properties.of().mapColor(MapColor.STONE).instrument(NoteBlockInstrument.BASEDRUM).sound(SoundType.STONE).requiresCorrectToolForDrops().strength(1.75F, 10.0F); }

    public static Properties coalCokeBlock() { return Properties.of().mapColor(MapColor.STONE).instrument(NoteBlockInstrument.BASEDRUM).sound(SoundType.STONE).requiresCorrectToolForDrops().strength(5.0F, 6.0F); }
}
