package mctmods.immersivefabrication.common.multiblocks.stone.cokeoven;

import mctmods.immersivefabrication.common.multiblocks.stone.cokeoven.logic.CokeOvenLogic;

import com.immersiveconvergence.api.multiblock.blocks.env.IMultiblockContext;
import com.immersiveconvergence.api.multiblock.blocks.env.IMultiblockLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.ItemHandlerHelper;

public final class CokeOvenDoor {
    private CokeOvenDoor() {}

    public static boolean use(IMultiblockContext<CokeOvenLogic.State> ctx, BlockPos cell, Player player, InteractionHand hand, Direction side, boolean isClient) {
        IMultiblockLevel level = ctx.getLevel();
        if (!CokeOvenLogic.DOOR_POI.equals(cell) || side != level.getOrientation().front().getOpposite() || player.isShiftKeyDown()) { return false; }
        if (isClient) { return true; }
        CokeOvenLogic.State state = ctx.getState();
        long now = level.getRawLevel().getGameTime();
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
            state.setChamber(held.copyWithCount(1));
            if (!player.getAbilities().instabuild) { held.shrink(1); }
        }
        else if (held.isEmpty() && !chamber.isEmpty()) {
            ItemHandlerHelper.giveItemToPlayer(player, chamber, hand == InteractionHand.MAIN_HAND ? player.getInventory().selected : -1);
            state.setChamber(ItemStack.EMPTY);
        }
        else { state.setDoor(false, now); }
        ctx.markDirtyAndSync();
        return true;
    }
}
