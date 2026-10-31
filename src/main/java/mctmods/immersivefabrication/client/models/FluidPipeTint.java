package mctmods.immersivefabrication.client.models;

import mctmods.immersivefabrication.common.blocks.metal.structure.logic.FluidPipeBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.util.FastColor;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

public final class FluidPipeTint {
    private static final int IRON_TINT = 0xFF9EA2A6;

    private FluidPipeTint() {}

    @SuppressWarnings("unused") public static int block(BlockState state, @Nullable BlockAndTintGetter level, @Nullable BlockPos pos, int index) {
        return level != null && pos != null && level.getBlockEntity(pos) instanceof FluidPipeBlockEntity pipe ? tint(pipe.getColor()) : IRON_TINT;
    }

    @SuppressWarnings("unused") public static int item(ItemStack stack, int index) { return tint(FluidPipeBlockEntity.stackColor(stack)); }

    private static int tint(int color) {
        if (color < 0) { return IRON_TINT; }
        float[] rgb = DyeColor.byId(color).getTextureDiffuseColors();
        return FastColor.ARGB32.color(255, mix(rgb[0], 16), mix(rgb[1], 8), mix(rgb[2], 0));
    }

    private static int mix(float dye, int shift) { return (Math.round(dye * 255.0F) + (IRON_TINT >> shift & 0xFF)) / 2; }
}
