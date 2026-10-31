package mctmods.immersivefabrication.common.blocks.plant.flax;

import net.minecraft.util.IStringSerializable;

import javax.annotation.Nonnull;
import java.util.Locale;

public enum FlaxStage implements IStringSerializable {
    STAGE0, STAGE1, STAGE2, STAGE3, STAGE4, STAGE5;

    public static final FlaxStage RIPE = STAGE5;

    @Override @Nonnull public String getName() { return name().toLowerCase(Locale.ROOT); }
}
