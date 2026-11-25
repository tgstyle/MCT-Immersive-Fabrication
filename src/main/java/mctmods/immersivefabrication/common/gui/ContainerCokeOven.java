package mctmods.immersivefabrication.common.gui;

import mctmods.immersivefabrication.common.multiblocks.stone.cokeoven.CokeOvenStacks;
import mctmods.immersivefabrication.common.multiblocks.stone.cokeoven.logic.TileEntityCokeOvenMaster;

import com.immersiveconvergence.api.gui.ICContainerBase;
import com.immersiveconvergence.api.gui.ICSlot;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public class ContainerCokeOven extends ICContainerBase<TileEntityCokeOvenMaster> {

    public ContainerCokeOven(InventoryPlayer inventoryPlayer, TileEntityCokeOvenMaster tile) {
        super(inventoryPlayer, tile);
        slotCount = 2;
        addSlotToContainer(new FuelSlot(0, 39, 41));
        addSlotToContainer(new FuelSlot(1, 123, 41));
        for (Slot slot : PlayerSlots.playerInventorySlots(inventoryPlayer, 84)) { addSlotToContainer(slot); }
    }

    private class FuelSlot extends ICSlot {
        FuelSlot(int id, int x, int y) { super(ContainerCokeOven.this, inv, id, x, y); }

        @Override public boolean isItemValid(@Nonnull ItemStack stack) { return CokeOvenStacks.isFuel(stack); }
    }
}
