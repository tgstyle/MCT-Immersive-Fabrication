package mctmods.immersivefabrication.core;

import com.immersiveconvergence.api.ICMods;

import mctmods.immersivefabrication.ImmersiveFabrication;

import net.minecraftforge.common.config.Config;

@Config(modid = ImmersiveFabrication.MODID, name = "mct_immersivefabrication")
public class IFabCommonConfig {
    public static Wires wires = new Wires();
    public static Pump pump = new Pump();

    public static class Wires {
        @Config.Comment({"Add the wires, terminals, insulators, anchors and transformers when Immersive Engineering is installed [Default=false]. Without Immersive Engineering they are always added."})
        @Config.RequiresMcRestart
        public boolean enableWithImmersiveEngineering = false;
    }

    public static class Pump {
        @Config.Comment({"What the Industrial Pump does with each fluid source it pumps: the block id of the fluid, then = and either keep or a block id. keep leaves the source, a block id replaces it (minecraft:air removes it), * covers fluids not listed, and a fluid with no entry and no * is not pumped."})
        public String[] pump_sources = {"minecraft:water=keep", "minecraft:lava=minecraft:cobblestone"};
        @Config.Comment({"How many of its four horizontal neighbors must be sources of the same fluid before the Industrial Pump draws from a kept source [Default=1]"})
        @Config.RangeInt(min = 0, max = 4)
        public int pump_keep_min_sources = 1;
    }

    public static boolean wires() { return !ICMods.immersiveEngineering() || wires.enableWithImmersiveEngineering; }
}
