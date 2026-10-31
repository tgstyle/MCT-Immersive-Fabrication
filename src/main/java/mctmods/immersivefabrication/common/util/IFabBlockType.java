package mctmods.immersivefabrication.common.util;

import java.util.Locale;

public interface IFabBlockType {
    String name();

    default String id() { return name().toLowerCase(Locale.ROOT); }
}
