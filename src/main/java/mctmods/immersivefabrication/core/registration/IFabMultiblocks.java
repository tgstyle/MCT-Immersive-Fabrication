package mctmods.immersivefabrication.core.registration;

import mctmods.immersivefabrication.ImmersiveFabrication;
import mctmods.immersivefabrication.common.multiblocks.stone.cokeoven.CokeOvenMultiblock;
import mctmods.immersivefabrication.common.multiblocks.stone.cokeoven.logic.CokeOvenLogic;

import com.immersiveconvergence.api.block.ModBlockItem;
import com.immersiveconvergence.api.multiblock.MultiblockBuilder;
import com.immersiveconvergence.api.multiblock.MultiblockPartBlockNonMirror;
import com.immersiveconvergence.api.multiblock.MultiblockRegistry;
import com.immersiveconvergence.api.multiblock.blocks.MultiblockRegistration;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;

public final class IFabMultiblocks {
    public static final ResourceLocation COKE_OVEN_ID = ResourceLocation.fromNamespaceAndPath(ImmersiveFabrication.MODID, "coke_oven");
    public static final MultiblockRegistration<CokeOvenLogic.State> COKE_OVEN = new MultiblockBuilder<>(new CokeOvenLogic(), COKE_OVEN_ID)
            .notMirrored()
            .customBlock(IFabBlocks.REGISTER, IFabItems.REGISTER, r -> new MultiblockPartBlockNonMirror<>(stoneProperties(), r), ModBlockItem::new)
            .customBEs(IFabBlockEntities.REGISTER)
            .structure(() -> CokeOvenMultiblock.INSTANCE)
            .build();

    private IFabMultiblocks() {}

    private static BlockBehaviour.Properties stoneProperties() { return BlockBehaviour.Properties.of().mapColor(MapColor.STONE).instrument(NoteBlockInstrument.BASEDRUM).sound(SoundType.NETHER_BRICKS).strength(2, 20).noOcclusion(); }

    public static void register() { MultiblockRegistry.register(CokeOvenMultiblock.INSTANCE); }
}
