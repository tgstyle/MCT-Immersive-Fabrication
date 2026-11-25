package mctmods.immersivefabrication.core.registration;

import mctmods.immersivefabrication.ImmersiveFabrication;
import mctmods.immersivefabrication.common.multiblocks.stone.cokeoven.CokeOvenMultiblock;
import mctmods.immersivefabrication.common.multiblocks.stone.cokeoven.logic.CokeOvenLogic;
import mctmods.immersivefabrication.common.util.IFabBlockProps;

import com.immersiveconvergence.api.block.ModBlockItem;
import com.immersiveconvergence.api.multiblock.MultiblockBuilder;
import com.immersiveconvergence.api.multiblock.MultiblockPartBlockNonMirror;
import com.immersiveconvergence.api.multiblock.MultiblockRegistry;
import com.immersiveconvergence.api.multiblock.blocks.MultiblockRegistration;
import net.minecraft.resources.ResourceLocation;

public final class IFabMultiblocks {
    public static final MultiblockRegistration<CokeOvenLogic.State> COKE_OVEN = new MultiblockBuilder<>(new CokeOvenLogic(), ResourceLocation.fromNamespaceAndPath(ImmersiveFabrication.MODID, "coke_oven"))
            .notMirrored()
            .customBlock(IFabBlocks.REGISTER, IFabItems.REGISTER, r -> new MultiblockPartBlockNonMirror<>(IFabBlockProps.stone(2.0F, 10.0F).noOcclusion(), r), ModBlockItem::new)
            .customBEs(IFabBlockEntities.REGISTER)
            .structure(() -> CokeOvenMultiblock.INSTANCE)
            .build();

    private static boolean registered;

    private IFabMultiblocks() {}

    public static void forceClassLoad() {}

    public static void register() {
        if (registered) { return; }
        registered = true;
        MultiblockRegistry.register(CokeOvenMultiblock.INSTANCE);
    }
}
