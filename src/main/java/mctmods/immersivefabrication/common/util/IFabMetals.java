package mctmods.immersivefabrication.common.util;

import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

public final class IFabMetals {
    public static final List<String> ALL = List.of("copper", "aluminum", "lead", "silver", "nickel", "uranium", "constantan", "steel", "iron", "gold");
    public static final List<String> PLATES = Stream.concat(ALL.stream(), Stream.of("graphite")).toList();
    public static final List<String> RODS = Stream.concat(ALL.stream(), Stream.of("treated")).toList();
    public static final List<String> WIRES = List.of("copper", "aluminum", "acsr", "copper_insulated", "aluminum_insulated");
    public static final List<String> FORMS = List.of("ingot", "nugget", "dust", "plate", "rod", "wire");
    private static final Set<String> IN_VANILLA = Set.of("copper_ingot", "iron_ingot", "gold_ingot", "iron_nugget", "gold_nugget");

    private IFabMetals() {}

    public static List<String> names(String form) {
        return switch (form) {
            case "rod" -> RODS;
            case "plate" -> PLATES;
            case "wire" -> WIRES;
            default -> ALL;
        };
    }

    public static String id(String metal, String form) { return metal + "_" + form; }

    public static boolean inVanilla(String metal, String form) { return IN_VANILLA.contains(id(metal, form)); }
}
