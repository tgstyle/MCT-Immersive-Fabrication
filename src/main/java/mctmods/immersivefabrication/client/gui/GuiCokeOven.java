package mctmods.immersivefabrication.client.gui;

import mctmods.immersivefabrication.ImmersiveFabrication;
import mctmods.immersivefabrication.common.gui.ContainerCokeOven;
import mctmods.immersivefabrication.common.multiblocks.stone.cokeoven.CokeOvenRules;
import mctmods.immersivefabrication.common.multiblocks.stone.cokeoven.CokeOvenStacks;
import mctmods.immersivefabrication.common.multiblocks.stone.cokeoven.logic.TileEntityCokeOvenMaster;

import com.immersiveconvergence.api.client.gui.GuiICContainerBase;
import com.immersiveconvergence.api.util.ICFluidTank;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.List;

public class GuiCokeOven extends GuiICContainerBase {
    private static final ResourceLocation TEXTURE = new ResourceLocation(ImmersiveFabrication.MODID, "textures/gui/coke_oven.png");
    private static final int PANEL_W = 176;
    private static final int PANEL_H = 166;
    private static final int FLAME_U = 176;
    private static final int FLAME_V = 0;
    private static final int FLAME_SIZE = 14;
    private static final int FLAME_LEFT_X = 40;
    private static final int FLAME_RIGHT_X = 124;
    private static final int FLAME_Y = 22;
    private static final int CHAMBER_X = 80;
    private static final int CHAMBER_Y = 20;
    private static final int CHAMBER_SIZE = 16;
    private static final int GLOW_U = 176;
    private static final int COKE_U = 196;
    private static final int COAL_U = 216;
    private static final int CHAMBER_V = 84;
    private static final int ARROW_U = 196;
    private static final int ARROW_V = 0;
    private static final int ARROW_W = 16;
    private static final int ARROW_H = 22;
    private static final int ARROW_X = 80;
    private static final int ARROW_Y = 42;
    private static final float ARROW_MAX = 19200f;
    private static final int GAS_FRAME_X = 8;
    private static final int FLUE_FRAME_X = 148;
    private static final int TANK_FRAME_Y = 16;
    private static final int GAUGE_U = 176;
    private static final int TANK_V = 31;
    private static final int GAUGE_W = 20;
    private static final int GAUGE_H = 51;
    private static final int FLUID_OFFSET = 2;
    private static final int FLUID_W = 16;
    private static final int FLUID_H = 47;
    private static final int GAS_FLUID_U = 200;
    private static final int FLUE_FLUID_U = 220;
    private final TileEntityCokeOvenMaster tile;

    public GuiCokeOven(InventoryPlayer inventoryPlayer, TileEntityCokeOvenMaster tile) {
        super(new ContainerCokeOven(inventoryPlayer, tile));
        this.tile = tile;
        xSize = PANEL_W;
        ySize = PANEL_H;
    }

    @Override protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GlStateManager.color(1f, 1f, 1f, 1f);
        mc.getTextureManager().bindTexture(TEXTURE);
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, PANEL_W, PANEL_H);
        if (tile.getCooking().burn(0) > 0) { drawTexturedModalRect(guiLeft + FLAME_LEFT_X, guiTop + FLAME_Y, FLAME_U, FLAME_V, FLAME_SIZE, FLAME_SIZE); }
        if (tile.getCooking().burn(1) > 0) { drawTexturedModalRect(guiLeft + FLAME_RIGHT_X, guiTop + FLAME_Y, FLAME_U, FLAME_V, FLAME_SIZE, FLAME_SIZE); }
        drawChamber();
        drawArrow();
        drawTank(tile.getRcog(), GAS_FRAME_X, GAS_FLUID_U);
        drawTank(tile.getFlue(), FLUE_FRAME_X, FLUE_FLUID_U);
    }

    private void drawChamber() {
        ItemStack chamber = tile.getChamber();
        if (CokeOvenStacks.isCoalBlock(chamber)) {
            drawTexturedModalRect(guiLeft + CHAMBER_X, guiTop + CHAMBER_Y, COAL_U, CHAMBER_V, CHAMBER_SIZE, CHAMBER_SIZE);
            if (tile.getCooking().lit() && !tile.isDoorOpen() && !tile.getCooking().paused()) {
                GlStateManager.enableBlend();
                drawTexturedModalRect(guiLeft + CHAMBER_X, guiTop + CHAMBER_Y, GLOW_U, CHAMBER_V, CHAMBER_SIZE, CHAMBER_SIZE);
                GlStateManager.disableBlend();
            }
        }
        else if (CokeOvenStacks.isCokeBlock(chamber)) { drawTexturedModalRect(guiLeft + CHAMBER_X, guiTop + CHAMBER_Y, COKE_U, CHAMBER_V, CHAMBER_SIZE, CHAMBER_SIZE); }
    }

    private void drawArrow() {
        int height = Math.round(ARROW_H * tile.getCooking().progress() / ARROW_MAX);
        if (height > 0) { drawTexturedModalRect(guiLeft + ARROW_X, guiTop + ARROW_Y + ARROW_H - height, ARROW_U, ARROW_V + ARROW_H - height, ARROW_W, height); }
    }

    private void drawTank(ICFluidTank tank, int frameX, int fluidU) {
        int height = Math.round(FLUID_H * Math.min(tank.getFluidAmount(), CokeOvenRules.TANK) / (float) CokeOvenRules.TANK);
        if (height > 0) { drawTexturedModalRect(guiLeft + frameX + FLUID_OFFSET, guiTop + TANK_FRAME_Y + FLUID_OFFSET + FLUID_H - height, fluidU, TANK_V + FLUID_H - height, FLUID_W, height); }
        drawTexturedModalRect(guiLeft + frameX, guiTop + TANK_FRAME_Y, GAUGE_U, TANK_V, GAUGE_W, GAUGE_H);
    }

    @Override protected void renderHoveredToolTip(int mouseX, int mouseY) {
        super.renderHoveredToolTip(mouseX, mouseY);
        tankTooltip(tile.getRcog(), GAS_FRAME_X, mouseX, mouseY);
        tankTooltip(tile.getFlue(), FLUE_FRAME_X, mouseX, mouseY);
    }

    private void tankTooltip(ICFluidTank tank, int frameX, int mouseX, int mouseY) {
        int x = guiLeft + frameX + FLUID_OFFSET;
        int y = guiTop + TANK_FRAME_Y + FLUID_OFFSET;
        if (mouseX < x || mouseX >= x + FLUID_W || mouseY < y || mouseY >= y + FLUID_H) { return; }
        List<String> lines = new ArrayList<>();
        FluidStack fluid = tank.getFluid();
        if (fluid != null && fluid.amount > 0) { lines.add(fluid.getLocalizedName()); }
        lines.add(tank.getFluidAmount() + " / " + CokeOvenRules.TANK + " mB");
        drawHoveringText(lines, mouseX, mouseY);
    }
}
