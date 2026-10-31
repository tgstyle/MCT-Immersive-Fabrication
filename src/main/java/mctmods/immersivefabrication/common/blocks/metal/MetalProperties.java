package mctmods.immersivefabrication.common.blocks.metal;

import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;

public final class MetalProperties {
    private MetalProperties() {}

    public static Properties storage(boolean steel) { return Properties.of().mapColor(MapColor.METAL).sound(steel ? SoundType.NETHERITE_BLOCK : SoundType.METAL).strength(5.0F, 10.0F).requiresCorrectToolForDrops(); }

    public static Properties cladding() { return Properties.of().mapColor(MapColor.METAL).sound(SoundType.METAL).strength(2.0F, 2.0F); }

    public static Properties ore() { return Properties.of().mapColor(MapColor.STONE).instrument(NoteBlockInstrument.BASEDRUM).strength(3.0F, 3.0F).requiresCorrectToolForDrops(); }

    public static Properties deepslateOre() { return Properties.of().mapColor(MapColor.DEEPSLATE).instrument(NoteBlockInstrument.BASEDRUM).sound(SoundType.DEEPSLATE).strength(4.5F, 3.0F).requiresCorrectToolForDrops(); }

    public static Properties rawBlock() { return Properties.of().mapColor(MapColor.STONE).instrument(NoteBlockInstrument.BASEDRUM).strength(5.0F, 6.0F).requiresCorrectToolForDrops(); }

    public static Properties engineering() { return Properties.of().mapColor(MapColor.METAL).sound(SoundType.METAL).strength(3.0F, 15.0F).requiresCorrectToolForDrops(); }

    public static Properties fence() { return engineering().isViewBlocking((state, level, pos) -> false); }

    public static Properties frame() { return fence().noOcclusion(); }
}
