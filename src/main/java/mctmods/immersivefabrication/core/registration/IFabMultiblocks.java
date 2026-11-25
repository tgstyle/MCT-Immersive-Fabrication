package mctmods.immersivefabrication.core.registration;

import mctmods.immersivefabrication.common.multiblocks.stone.cokeoven.CokeOvenMultiblock;

import com.immersiveconvergence.api.multiblock.MultiblockRegistry;

public final class IFabMultiblocks {
    private IFabMultiblocks() {}

    public static void register() { MultiblockRegistry.register(CokeOvenMultiblock.instance); }
}
