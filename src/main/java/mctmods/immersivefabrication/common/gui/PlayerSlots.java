package mctmods.immersivefabrication.common.gui;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;

import java.util.ArrayList;
import java.util.List;

public final class PlayerSlots {
    private PlayerSlots() {}

    public static List<Slot> playerInventorySlots(InventoryPlayer inv, int y) {
        List<Slot> slots = new ArrayList<>();
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) { slots.add(new Slot(inv, col + row * 9 + 9, 8 + col * 18, y + row * 18)); }
        }
        for (int col = 0; col < 9; col++) { slots.add(new Slot(inv, col, 8 + col * 18, y + 58)); }
        return slots;
    }
}
