package mctmods.immersivefabrication.core.registration;

import mctmods.immersivefabrication.common.worldgen.OreGenerator;

import net.minecraftforge.fml.common.registry.GameRegistry;

public final class IFabWorldGen {

    private IFabWorldGen() {}

    public static void register() { GameRegistry.registerWorldGenerator(new OreGenerator(), 0); }
}
