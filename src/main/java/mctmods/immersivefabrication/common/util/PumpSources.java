package mctmods.immersivefabrication.common.util;

import mctmods.immersivefabrication.ImmersiveFabrication;
import mctmods.immersivefabrication.core.IFabCommonConfig;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.registries.ForgeRegistries;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public final class PumpSources {
    private static final String KEEP = "keep";
    private static final String ANY = "*";
    @Nullable private static List<String> parsedFrom;
    private static Map<String, String> rules = new HashMap<>();

    private PumpSources() {}

    public static boolean pumps(Fluid fluid) { return rule(fluid) != null; }

    @Nullable public static BlockState replacement(Fluid fluid) {
        String rule = rule(fluid);
        return rule == null || rule.equals(KEEP) ? null : Objects.requireNonNull(ForgeRegistries.BLOCKS.getValue(ResourceLocation.tryParse(rule))).defaultBlockState();
    }

    @Nullable private static String rule(Fluid fluid) {
        Map<String, String> table = table();
        BlockState block = fluid.defaultFluidState().createLegacyBlock();
        ResourceLocation id = block.isAir() ? null : ForgeRegistries.BLOCKS.getKey(block.getBlock());
        String rule = id == null ? null : table.get(id.toString());
        return rule != null ? rule : table.get(ANY);
    }

    private static Map<String, String> table() {
        List<? extends String> source = IFabCommonConfig.PUMP_SOURCES.get();
        if (!source.equals(parsedFrom)) {
            List<String> copy = List.copyOf(source);
            parsedFrom = copy;
            rules = parse(copy);
        }
        return rules;
    }

    private static Map<String, String> parse(List<String> source) {
        Map<String, String> table = new HashMap<>();
        for (String entry : source) {
            int split = entry.indexOf('=');
            String key = split < 0 ? "" : entry.substring(0, split).trim();
            String value = split < 0 ? "" : entry.substring(split + 1).trim();
            ResourceLocation fluid = key.equals(ANY) ? null : ResourceLocation.tryParse(key.toLowerCase(Locale.ROOT));
            ResourceLocation block = value.equals(KEEP) ? null : ResourceLocation.tryParse(value.toLowerCase(Locale.ROOT));
            if (!valid(key, fluid, value, block)) {
                ImmersiveFabrication.LOGGER.warn("Ignoring pump_sources entry '{}'", entry);
                continue;
            }
            table.put(fluid == null ? ANY : fluid.toString(), block == null ? KEEP : block.toString());
        }
        return table;
    }

    private static boolean valid(String key, @Nullable ResourceLocation fluid, String value, @Nullable ResourceLocation block) {
        if (key.isEmpty() || fluid == null && !key.equals(ANY)) { return false; }
        return value.equals(KEEP) || block != null && ForgeRegistries.BLOCKS.containsKey(block);
    }
}
