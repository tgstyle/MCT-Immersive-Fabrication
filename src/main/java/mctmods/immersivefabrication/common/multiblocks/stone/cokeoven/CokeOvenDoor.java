package mctmods.immersivefabrication.common.multiblocks.stone.cokeoven;

import mctmods.immersivefabrication.common.multiblocks.stone.cokeoven.logic.CokeOvenLogic;

import com.immersiveconvergence.api.multiblock.blocks.env.IMultiblockContext;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemHandlerHelper;

public final class CokeOvenDoor {
    private CokeOvenDoor() {}

    public static InteractionResult use(IMultiblockContext<CokeOvenLogic.State> ctx, BlockPos cell, Player player, InteractionHand hand, Direction side, boolean isClient) {
        if (!CokeOvenLogic.DOOR_CELL.equals(cell) || side != ctx.getLevel().getOrientation().front().getOpposite() || player.isShiftKeyDown()) { return InteractionResult.PASS; }
        if (isClient) { return InteractionResult.SUCCESS; }
        CokeOvenLogic.State state = ctx.getState();
        long now = ctx.getLevel().getRawLevel().getGameTime();
        ItemStack held = player.getItemInHand(hand);
        ItemStack chamber = state.getChamber();
        if (!state.isDoorOpen()) {
            if (state.getCooking().progress() > 0 && CokeOvenStacks.isCoalBlock(chamber)) {
                state.setChamber(ItemStack.EMPTY);
                state.getCooking().lose();
                state.setFire(CokeOvenRules.FIRE_TICKS);
            }
            state.setDoor(true, now);
        }
        else if (CokeOvenStacks.isCoalBlock(held) && chamber.isEmpty()) {
            state.setChamber(ItemHandlerHelper.copyStackWithSize(held, 1));
            if (!player.getAbilities().instabuild) { held.shrink(1); }
        }
        else if (held.isEmpty() && !chamber.isEmpty()) {
            ItemHandlerHelper.giveItemToPlayer(player, chamber, hand == InteractionHand.MAIN_HAND ? player.getInventory().selected : -1);
            state.setChamber(ItemStack.EMPTY);
        }
        else { state.setDoor(false, now); }
        ctx.markDirtyAndSync();
        return InteractionResult.SUCCESS;
    }
}
