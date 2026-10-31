package mctmods.immersivefabrication.common.blocks.plant.flax;

import net.minecraft.util.StringRepresentable;

import javax.annotation.Nonnull;
import java.util.Locale;

public enum FlaxStage implements StringRepresentable {
    STAGE0, STAGE1, STAGE2, STAGE3, STAGE4, STAGE5;

    public static final int RIPE_STAGE = 5;

    @Override @Nonnull public String getSerializedName() { return name().toLowerCase(Locale.ROOT); }
}
