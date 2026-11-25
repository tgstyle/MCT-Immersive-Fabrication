package mctmods.immersivefabrication.client.gui;

import mctmods.immersivefabrication.ImmersiveFabrication;
import mctmods.immersivefabrication.common.gui.CokeOvenMenu;
import mctmods.immersivefabrication.common.multiblocks.stone.cokeoven.CokeOvenRules;
import mctmods.immersivefabrication.common.multiblocks.stone.cokeoven.CokeOvenStacks;

import com.immersiveconvergence.api.client.gui.BaseContainerScreen;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import javax.annotation.Nonnull;
import java.util.function.Consumer;

public class CokeOvenScreen extends BaseContainerScreen<CokeOvenMenu> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(ImmersiveFabrication.MODID, "textures/gui/coke_oven.png");
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

    public CokeOvenScreen(CokeOvenMenu menu, Inventory inventoryPlayer, Component title) {
        super(menu, inventoryPlayer, title, TEXTURE);
        imageWidth = PANEL_W;
        imageHeight = PANEL_H;
    }

    @Override protected void drawContainerBackgroundPre(@Nonnull GuiGraphics graphics) {
        if (menu.getBurn(0) > 0) { graphics.blit(TEXTURE, leftPos + FLAME_LEFT_X, topPos + FLAME_Y, FLAME_U, FLAME_V, FLAME_SIZE, FLAME_SIZE); }
        if (menu.getBurn(1) > 0) { graphics.blit(TEXTURE, leftPos + FLAME_RIGHT_X, topPos + FLAME_Y, FLAME_U, FLAME_V, FLAME_SIZE, FLAME_SIZE); }
        drawChamber(graphics);
        drawArrow(graphics);
        drawTank(graphics, menu.getRcog(), GAS_FRAME_X, GAS_FLUID_U);
        drawTank(graphics, menu.getFlue(), FLUE_FRAME_X, FLUE_FLUID_U);
    }

    private void drawChamber(GuiGraphics graphics) {
        ItemStack chamber = menu.getChamber();
        if (CokeOvenStacks.isCoalBlock(chamber)) {
            graphics.blit(TEXTURE, leftPos + CHAMBER_X, topPos + CHAMBER_Y, COAL_U, CHAMBER_V, CHAMBER_SIZE, CHAMBER_SIZE);
            if (menu.isLit() && !menu.isDoorOpen() && !menu.isPaused()) {
                RenderSystem.enableBlend();
                graphics.blit(TEXTURE, leftPos + CHAMBER_X, topPos + CHAMBER_Y, GLOW_U, CHAMBER_V, CHAMBER_SIZE, CHAMBER_SIZE);
                RenderSystem.disableBlend();
            }
        }
        else if (CokeOvenStacks.isCokeBlock(chamber)) { graphics.blit(TEXTURE, leftPos + CHAMBER_X, topPos + CHAMBER_Y, COKE_U, CHAMBER_V, CHAMBER_SIZE, CHAMBER_SIZE); }
    }

    private void drawArrow(GuiGraphics graphics) {
        int height = Math.round(ARROW_H * menu.getProgress() / ARROW_MAX);
        if (height > 0) { graphics.blit(TEXTURE, leftPos + ARROW_X, topPos + ARROW_Y + ARROW_H - height, ARROW_U, ARROW_V + ARROW_H - height, ARROW_W, height); }
    }

    private void drawTank(GuiGraphics graphics, FluidTank tank, int frameX, int fluidU) {
        int height = Math.round(FLUID_H * Math.min(tank.getFluidAmount(), CokeOvenRules.TANK) / (float) CokeOvenRules.TANK);
        if (height > 0) { graphics.blit(TEXTURE, leftPos + frameX + FLUID_OFFSET, topPos + TANK_FRAME_Y + FLUID_OFFSET + FLUID_H - height, fluidU, TANK_V + FLUID_H - height, FLUID_W, height); }
        graphics.blit(TEXTURE, leftPos + frameX, topPos + TANK_FRAME_Y, GAUGE_U, TANK_V, GAUGE_W, GAUGE_H);
    }

    @Override protected void renderLabels(@Nonnull GuiGraphics graphics, int mouseX, int mouseY) {}

    @Override protected void gatherAdditionalTooltips(int mouseX, int mouseY, Consumer<Component> addLine) {
        tankTooltip(menu.getRcog(), GAS_FRAME_X, mouseX, mouseY, addLine);
        tankTooltip(menu.getFlue(), FLUE_FRAME_X, mouseX, mouseY, addLine);
    }

    private void tankTooltip(FluidTank tank, int frameX, int mouseX, int mouseY, Consumer<Component> addLine) {
        int x = leftPos + frameX + FLUID_OFFSET;
        int y = topPos + TANK_FRAME_Y + FLUID_OFFSET;
        if (mouseX < x || mouseX >= x + FLUID_W || mouseY < y || mouseY >= y + FLUID_H) { return; }
        FluidStack fluid = tank.getFluid();
        if (!fluid.isEmpty()) { addLine.accept(fluid.getHoverName()); }
        addLine.accept(Component.literal(tank.getFluidAmount() + " / " + CokeOvenRules.TANK + " mB"));
    }
}
