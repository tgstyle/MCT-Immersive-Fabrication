package mctmods.immersivefabrication.client.renderer;

import mctmods.immersivefabrication.client.models.IFabModels;
import mctmods.immersivefabrication.common.blocks.metal.structure.logic.TileEntityIndustrialPump;

import com.immersiveconvergence.api.client.ICClientUtils;
import com.immersiveconvergence.api.client.RenderUtils;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;

public class IndustrialPumpRenderer extends TileEntitySpecialRenderer<TileEntityIndustrialPump> {
    private static final double PIVOT_Y = 1.09375D;

    @Override public void render(@Nonnull TileEntityIndustrialPump pump, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        if (pump.isDummy() || !pump.getWorld().isBlockLoaded(pump.getPos(), false)) { return; }
        float angle = pump.spinAngle(pump.getWorld().getTotalWorldTime() + (double)partialTicks);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        GlStateManager.pushMatrix();
        GlStateManager.translate(x + 0.5D, y + PIVOT_Y, z + 0.5D);
        GlStateManager.rotate(angle, 0.0F, 1.0F, 0.0F);
        RenderHelper.disableStandardItemLighting();
        ICClientUtils.bindAtlas();
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);
        buffer.setTranslation(-0.5D, 1.0D - PIVOT_Y, -0.5D);
        RenderUtils.renderModelTESRFancy(IFabModels.pumpShaftQuads(), buffer, pump.getWorld(), pump.getPos().up(), false);
        buffer.setTranslation(0.0D, 0.0D, 0.0D);
        tessellator.draw();
        RenderHelper.enableStandardItemLighting();
        GlStateManager.popMatrix();
    }
}
