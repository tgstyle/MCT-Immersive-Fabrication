package mctmods.immersivefabrication.common.util;

import mctmods.immersivefabrication.ImmersiveFabrication;
import mctmods.immersivefabrication.core.IFabCommonConfig;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public final class PumpSources {
    private static final String KEEP = "keep";
    private static final String ANY = "*";
    @Nullable private static String[] parsedFrom;
    private static Map<String, String> rules = new HashMap<>();

    private PumpSources() {}

    public static boolean pumps(Fluid fluid) { return rule(fluid) != null; }

    @Nullable public static IBlockState replacement(Fluid fluid) {
        String rule = rule(fluid);
        return rule == null || rule.equals(KEEP) ? null : Block.REGISTRY.getObject(new ResourceLocation(rule)).getDefaultState();
    }

    @Nullable private static String rule(Fluid fluid) {
        Map<String, String> table = table();
        Block block = fluid.getBlock();
        ResourceLocation id = block == null ? null : block.getRegistryName();
        String rule = id == null ? null : table.get(id.toString());
        return rule != null ? rule : table.get(ANY);
    }

    private static Map<String, String> table() {
        String[] source = IFabCommonConfig.pump.pump_sources;
        if (!Arrays.equals(source, parsedFrom)) {
            parsedFrom = source.clone();
            rules = parse(source);
        }
        return rules;
    }

    private static Map<String, String> parse(String[] source) {
        Map<String, String> table = new HashMap<>();
        for (String entry : source) {
            int split = entry.indexOf('=');
            String key = split < 0 ? "" : entry.substring(0, split).trim();
            String value = split < 0 ? "" : entry.substring(split + 1).trim();
            if (!valid(key, value)) {
                ImmersiveFabrication.LOGGER.warn("Ignoring pump_sources entry '{}'", entry);
                continue;
            }
            table.put(key.equals(ANY) ? ANY : new ResourceLocation(key).toString(), value.equals(KEEP) ? KEEP : new ResourceLocation(value).toString());
        }
        return table;
    }

    private static boolean valid(String key, String value) { return !key.isEmpty() && (value.equals(KEEP) || Block.REGISTRY.containsKey(new ResourceLocation(value))); }
}
