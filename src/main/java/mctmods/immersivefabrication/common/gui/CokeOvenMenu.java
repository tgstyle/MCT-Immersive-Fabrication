package mctmods.immersivefabrication.common.gui;

import mctmods.immersivefabrication.common.multiblocks.stone.cokeoven.CokeOvenFuel;
import mctmods.immersivefabrication.common.multiblocks.stone.cokeoven.CokeOvenRules;
import mctmods.immersivefabrication.common.multiblocks.stone.cokeoven.logic.CokeOvenLogic;

import com.immersiveconvergence.api.gui.BaseContainerMenu;
import com.immersiveconvergence.api.gui.MenuSyncData;
import com.immersiveconvergence.api.gui.ModSlot;
import com.immersiveconvergence.api.util.MarkableFluidTank;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import javax.annotation.Nullable;

public class CokeOvenMenu extends BaseContainerMenu {
    private final MarkableFluidTank rcog;
    private final MarkableFluidTank flue;
    private final int[] burn = new int[2];
    private int progress;
    private int doorOpen;
    private int paused;
    private ItemStack chamber = ItemStack.EMPTY;

    public static CokeOvenMenu makeServer(MenuType<CokeOvenMenu> type, int id, Inventory invPlayer, BaseContainerMenu.MultiblockMenuContext<CokeOvenLogic.State> ctx) {
        CokeOvenLogic.State state = ctx.mbContext().getState();
        return new CokeOvenMenu(BaseContainerMenu.multiblockCtx(type, id, ctx), invPlayer, new CokeOvenFuel(state.getInventory(), state::changed), state.getRcog(), state.getFlue(), state);
    }

    public static CokeOvenMenu makeClient(MenuType<CokeOvenMenu> type, int id, Inventory invPlayer) {
        return new CokeOvenMenu(BaseContainerMenu.clientCtx(type, id), invPlayer, new CokeOvenFuel(NonNullList.withSize(2, ItemStack.EMPTY), () -> {}), new MarkableFluidTank(CokeOvenRules.TANK, v -> {}), new MarkableFluidTank(CokeOvenRules.TANK, v -> {}), null);
    }

    protected CokeOvenMenu(BaseContainerMenu.MenuContext ctx, Inventory inventoryPlayer, IItemHandler fuel, MarkableFluidTank rcog, MarkableFluidTank flue, @Nullable CokeOvenLogic.State state) {
        super(ctx);
        this.rcog = rcog;
        this.flue = flue;
        addSlot(new ModSlot.Fuel(fuel, 0, 39, 41));
        addSlot(new ModSlot.Fuel(fuel, 1, 123, 41));
        ownSlotCount = 2;
        addPlayerInventorySlots(inventoryPlayer);
        addGenericData(MenuSyncData.fluid(rcog));
        addGenericData(MenuSyncData.fluid(flue));
        addGenericData(MenuSyncData.int32(() -> state != null ? state.getCooking().burn(0) : burn[0], i -> burn[0] = i));
        addGenericData(MenuSyncData.int32(() -> state != null ? state.getCooking().burn(1) : burn[1], i -> burn[1] = i));
        addGenericData(MenuSyncData.int32(() -> state != null ? state.getCooking().progress() : progress, i -> progress = i));
        addGenericData(MenuSyncData.int32(() -> state != null ? (state.isDoorOpen() ? 1 : 0) : doorOpen, i -> doorOpen = i));
        addGenericData(MenuSyncData.int32(() -> state != null ? (state.getCooking().paused() ? 1 : 0) : paused, i -> paused = i));
        addGenericData(MenuSyncData.itemStack(() -> state != null ? state.getChamber() : chamber, stack -> chamber = stack));
    }

    public MarkableFluidTank getRcog() { return rcog; }

    public MarkableFluidTank getFlue() { return flue; }

    public int getBurn(int side) { return burn[side]; }

    public boolean isLit() { return burn[0] > 0 || burn[1] > 0; }

    public int getProgress() { return progress; }

    public boolean isDoorOpen() { return doorOpen != 0; }

    public boolean isPaused() { return paused != 0; }

    public ItemStack getChamber() { return chamber; }
}
