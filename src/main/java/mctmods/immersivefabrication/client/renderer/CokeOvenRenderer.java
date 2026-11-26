package mctmods.immersivefabrication.client.renderer;

import mctmods.immersivefabrication.client.models.IFabModels;
import mctmods.immersivefabrication.common.multiblocks.stone.cokeoven.CokeOvenFacing;
import mctmods.immersivefabrication.common.multiblocks.stone.cokeoven.CokeOvenRules;
import mctmods.immersivefabrication.common.multiblocks.stone.cokeoven.logic.TileEntityCokeOvenMaster;

import com.immersiveconvergence.api.client.ICClientUtils;
import com.immersiveconvergence.api.client.RenderUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;

public class CokeOvenRenderer extends TileEntitySpecialRenderer<TileEntityCokeOvenMaster> {
    private static final float DOOR_SIGN = 1.0F;
    private static final double PIVOT_X = 0.0D;
    private static final double PIVOT_Y = 0.5D;
    private static final double PIVOT_Z = 1.0D;
    private static final double CHAMBER_Z = -1.0D;
    private static final float ITEM_SCALE = 0.6F;
    private static final float FIRE_MIN = 0.15F;
    private static final float FIRE_MAX = 0.85F;
    private static final float FIRE_HEIGHT = 0.7F;

    @Override public void render(@Nonnull TileEntityCokeOvenMaster oven, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        if (oven.isDummy() || !oven.formed || !oven.getWorld().isBlockLoaded(oven.getPos(), false)) { return; }
        GlStateManager.pushMatrix();
        GlStateManager.translate(x + 0.5D, y + 0.5D, z + 0.5D);
        GlStateManager.rotate(CokeOvenFacing.yRotation(oven.getFacing()), 0.0F, 1.0F, 0.0F);
        GlStateManager.translate(-0.5D, -0.5D, -0.5D);
        drawDoor(oven, partialTicks);
        GlStateManager.translate(0.0D, 0.0D, CHAMBER_Z);
        drawChamber(oven);
        if (oven.getFire() > 0) { drawFire(); }
        GlStateManager.popMatrix();
    }

    private static float doorAngle(TileEntityCokeOvenMaster oven, float partialTicks) {
        float t = MathHelper.clamp(((oven.getWorld().getTotalWorldTime() - oven.getDoorTime()) + partialTicks) / CokeOvenRules.DOOR_TICKS, 0.0F, 1.0F);
        float eased = t * t * (3.0F - 2.0F * t);
        return DOOR_SIGN * CokeOvenRules.DOOR_ANGLE * (oven.isDoorOpen() ? eased : 1.0F - eased);
    }

    private void drawDoor(TileEntityCokeOvenMaster oven, float partialTicks) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        GlStateManager.pushMatrix();
        GlStateManager.translate(PIVOT_X, PIVOT_Y, PIVOT_Z);
        GlStateManager.rotate(doorAngle(oven, partialTicks), 0.0F, 1.0F, 0.0F);
        GlStateManager.translate(-PIVOT_X, -PIVOT_Y, -PIVOT_Z);
        RenderHelper.disableStandardItemLighting();
        ICClientUtils.bindAtlas();
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);
        RenderUtils.renderModelTESRFancy(IFabModels.cokeOvenDoorQuads(), buffer, oven.getWorld(), oven.getPos(), false);
        tessellator.draw();
        RenderHelper.enableStandardItemLighting();
        GlStateManager.popMatrix();
    }

    private void drawChamber(TileEntityCokeOvenMaster oven) {
        ItemStack stack = oven.getChamber();
        if (stack.isEmpty()) { return; }
        boolean glow = oven.getIsActive();
        float lastX = OpenGlHelper.lastBrightnessX;
        float lastY = OpenGlHelper.lastBrightnessY;
        if (glow) {
            float pulse = 0.5F + 0.5F * MathHelper.sin((oven.getWorld().getTotalWorldTime() + Minecraft.getMinecraft().getRenderPartialTicks()) * 0.1F);
            float light = 120.0F + 120.0F * pulse;
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, light, light);
        }
        GlStateManager.pushMatrix();
        GlStateManager.translate(0.5D, 0.5D, 0.5D);
        GlStateManager.scale(ITEM_SCALE, ITEM_SCALE, ITEM_SCALE);
        bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        RenderHelper.enableStandardItemLighting();
        Minecraft.getMinecraft().getRenderItem().renderItem(stack, ItemCameraTransforms.TransformType.NONE);
        GlStateManager.popMatrix();
        if (glow) { OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, lastX, lastY); }
    }

    private void drawFire() {
        TextureAtlasSprite sprite = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite("minecraft:blocks/fire_layer_0");
        float lastX = OpenGlHelper.lastBrightnessX;
        float lastY = OpenGlHelper.lastBrightnessY;
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 240.0F);
        GlStateManager.disableLighting();
        GlStateManager.disableCull();
        bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        double u0 = sprite.getMinU();
        double u1 = sprite.getMaxU();
        double v0 = sprite.getMinV();
        double v1 = sprite.getMaxV();
        float y1 = FIRE_MIN + FIRE_HEIGHT;
        buffer.pos(FIRE_MIN, FIRE_MIN, FIRE_MIN).tex(u1, v1).endVertex();
        buffer.pos(FIRE_MAX, FIRE_MIN, FIRE_MAX).tex(u0, v1).endVertex();
        buffer.pos(FIRE_MAX, y1, FIRE_MAX).tex(u0, v0).endVertex();
        buffer.pos(FIRE_MIN, y1, FIRE_MIN).tex(u1, v0).endVertex();
        buffer.pos(FIRE_MAX, FIRE_MIN, FIRE_MIN).tex(u1, v1).endVertex();
        buffer.pos(FIRE_MIN, FIRE_MIN, FIRE_MAX).tex(u0, v1).endVertex();
        buffer.pos(FIRE_MIN, y1, FIRE_MAX).tex(u0, v0).endVertex();
        buffer.pos(FIRE_MAX, y1, FIRE_MIN).tex(u1, v0).endVertex();
        tessellator.draw();
        GlStateManager.enableCull();
        GlStateManager.enableLighting();
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, lastX, lastY);
    }
}
