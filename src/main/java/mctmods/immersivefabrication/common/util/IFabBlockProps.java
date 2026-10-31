package mctmods.immersivefabrication.common.util;

import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;

public final class IFabBlockProps {
    private IFabBlockProps() {}

    public static Properties metal(float hardness, float resistance) { return Properties.of().mapColor(MapColor.METAL).sound(SoundType.METAL).strength(hardness, resistance).requiresCorrectToolForDrops(); }

    public static Properties stone(float hardness, float resistance) { return Properties.of().mapColor(MapColor.STONE).sound(SoundType.STONE).strength(hardness, resistance).requiresCorrectToolForDrops(); }

    public static Properties deepslate(float hardness, float resistance) { return Properties.of().mapColor(MapColor.DEEPSLATE).sound(SoundType.DEEPSLATE).strength(hardness, resistance).requiresCorrectToolForDrops(); }

    public static Properties crop() { return Properties.of().mapColor(MapColor.PLANT).noCollission().randomTicks().instabreak().sound(SoundType.CROP).pushReaction(PushReaction.DESTROY); }

    public static Properties wood(float hardness, float resistance) { return Properties.of().mapColor(MapColor.WOOD).sound(SoundType.WOOD).strength(hardness, resistance).ignitedByLava(); }
}
