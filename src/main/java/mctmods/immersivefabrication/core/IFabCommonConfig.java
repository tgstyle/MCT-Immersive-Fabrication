package mctmods.immersivefabrication.core;

import com.immersiveconvergence.api.ICMods;

import mctmods.immersivefabrication.ImmersiveFabrication;

import net.minecraftforge.common.ForgeConfigSpec;

import java.util.List;

public final class IFabCommonConfig {
    public static final ForgeConfigSpec SPEC;
    public static final ForgeConfigSpec.BooleanValue WIRES_WITH_IE;
    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> PUMP_SOURCES;
    public static final ForgeConfigSpec.IntValue PUMP_KEEP_MIN_SOURCES;
    private static final String FILE = ImmersiveFabrication.MODID + "-common.toml";
    private static final String WIRES_PATH = "wires.enable_with_immersive_engineering";

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        builder.push("wires");
        WIRES_WITH_IE = builder.comment("Add the wires, terminals, insulators, anchors and transformers when Immersive Engineering is installed. Without Immersive Engineering they are always added. Requires a restart.").define("enable_with_immersive_engineering", false);
        builder.pop();
        builder.push("pump");
        PUMP_SOURCES = builder.comment("What the Industrial Pump does with each fluid source it pumps: the block id of the fluid, then = and either keep or a block id. keep leaves the source, a block id replaces it (minecraft:air removes it), * covers fluids not listed, and a fluid with no entry and no * is not pumped.").defineListAllowEmpty("pump_sources", List.of("minecraft:water=keep", "minecraft:lava=minecraft:cobblestone"), entry -> entry instanceof String);
        PUMP_KEEP_MIN_SOURCES = builder.comment("How many of its four horizontal neighbors must be sources of the same fluid before the Industrial Pump draws from a kept source.").defineInRange("pump_keep_min_sources", 1, 0, 4);
        builder.pop();
        SPEC = builder.build();
    }

    private IFabCommonConfig() {}

    public static boolean wires() { return !ICMods.immersiveEngineering() || ICMods.earlyBoolean(FILE, WIRES_PATH, false); }
}
