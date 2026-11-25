package mctmods.immersivefabrication.common.multiblocks.stone.cokeoven;

import mctmods.immersivefabrication.common.multiblocks.stone.cokeoven.logic.TileEntityCokeOvenMaster;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.items.ItemHandlerHelper;

public final class CokeOvenDoor {
    public static final BlockPos DOOR_CELL = new BlockPos(1, 1, 0);

    private CokeOvenDoor() {}

    public static boolean use(TileEntityCokeOvenMaster master, EntityPlayer player, EnumHand hand, ItemStack held, EnumFacing side, BlockPos cell) {
        if (!DOOR_CELL.equals(cell) || side != master.getFacing().getOpposite() || player.isSneaking()) { return false; }
        if (master.getWorld().isRemote) { return true; }
        long now = master.getWorld().getTotalWorldTime();
        ItemStack chamber = master.getChamber();
        if (!master.isDoorOpen()) {
            if (master.getCooking().progress() > 0 && CokeOvenStacks.isCoalBlock(chamber)) {
                master.setChamber(ItemStack.EMPTY);
                master.getCooking().lose();
                master.setFire(CokeOvenRules.FIRE_TICKS);
            }
            master.setDoor(true, now);
        }
        else if (CokeOvenStacks.isCoalBlock(held) && chamber.isEmpty()) {
            master.setChamber(ItemHandlerHelper.copyStackWithSize(held, 1));
            if (!player.capabilities.isCreativeMode) { held.shrink(1); }
        }
        else if (held.isEmpty() && !chamber.isEmpty()) {
            ItemHandlerHelper.giveItemToPlayer(player, chamber, hand == EnumHand.MAIN_HAND ? player.inventory.currentItem : -1);
            master.setChamber(ItemStack.EMPTY);
        }
        else { master.setDoor(false, now); }
        master.sync();
        return true;
    }
}
