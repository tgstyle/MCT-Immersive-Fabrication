package mctmods.immersivefabrication.common.util;

import com.immersiveconvergence.api.block.ICBlockBase.IBlockEnum;

import javax.annotation.Nonnull;
import java.util.Locale;

public interface IFabBlockEnum extends IBlockEnum {
    @Override default int getMeta() { return ((Enum<?>)this).ordinal(); }

    @Override default boolean listForCreative() { return true; }

    @Override @Nonnull default String getName() { return ((Enum<?>)this).name().toLowerCase(Locale.ROOT); }
}
