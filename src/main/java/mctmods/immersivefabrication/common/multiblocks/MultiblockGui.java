package mctmods.immersivefabrication.common.multiblocks;

import mctmods.immersivefabrication.core.registration.IFabMenuTypes;

import com.immersiveconvergence.api.multiblock.blocks.env.IMultiblockContext;
import com.immersiveconvergence.api.multiblock.blocks.logic.IMultiblockState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;

public record MultiblockGui<S extends IMultiblockState>(IFabMenuTypes.MultiblockContainer<S, ?> menu) {
    public ItemInteractionResult open(IMultiblockContext<S> ctx, BlockPos posInMultiblock, Player player, InteractionHand hand, boolean isClient) {
        if (hand != InteractionHand.MAIN_HAND || player.isShiftKeyDown()) { return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION; }
        if (!isClient && ctx != null) { player.openMenu(this.menu.provide(ctx, posInMultiblock)); }
        return ItemInteractionResult.sidedSuccess(isClient);
    }
}
