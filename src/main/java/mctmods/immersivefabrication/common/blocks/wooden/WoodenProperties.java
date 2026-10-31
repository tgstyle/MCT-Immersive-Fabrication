package mctmods.immersivefabrication.common.blocks.wooden;

import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;

public final class WoodenProperties {
    private WoodenProperties() {}

    public static Properties treated() { return Properties.of().mapColor(MapColor.WOOD).ignitedByLava().instrument(NoteBlockInstrument.BASS).sound(SoundType.WOOD).strength(2.0F, 5.0F); }

    public static Properties treatedFence() { return treated().isViewBlocking((state, level, pos) -> false); }
}
