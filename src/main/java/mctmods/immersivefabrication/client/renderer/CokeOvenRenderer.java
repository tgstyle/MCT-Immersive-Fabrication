package mctmods.immersivefabrication.client.renderer;

import mctmods.immersivefabrication.client.models.CokeOvenModels;
import mctmods.immersivefabrication.common.multiblocks.stone.cokeoven.CokeOvenFacing;
import mctmods.immersivefabrication.common.multiblocks.stone.cokeoven.CokeOvenRules;
import mctmods.immersivefabrication.common.multiblocks.stone.cokeoven.logic.CokeOvenLogic;

import com.immersiveconvergence.api.client.RenderUtils;
import com.immersiveconvergence.api.multiblock.blocks.registry.MultiblockBlockEntityMaster;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.client.model.data.ModelData;

import javax.annotation.Nonnull;

public class CokeOvenRenderer implements BlockEntityRenderer<MultiblockBlockEntityMaster<CokeOvenLogic.State>> {
    private static final float DOOR_SIGN = 1.0F;
    private static final double PIVOT_X = 0.0D;
    private static final double PIVOT_Y = 0.5D;
    private static final double PIVOT_Z = 1.0D;
    private static final double CHAMBER_Z = -1.0D;
    private static final float ITEM_SCALE = 0.6F;
    private static final float FIRE_MIN = 0.15F;
    private static final float FIRE_MAX = 0.85F;
    private static final float FIRE_HEIGHT = 0.7F;
    private static final ResourceLocation FIRE_SPRITE = ResourceLocation.fromNamespaceAndPath("minecraft", "block/fire_0");
    private static final RandomSource RANDOM = RandomSource.create();

    @Override public void render(@Nonnull MultiblockBlockEntityMaster<CokeOvenLogic.State> oven, float partialTicks, @Nonnull PoseStack poseStack, @Nonnull MultiBufferSource buffer, int packedLight, int packedOverlay) {
        Level level = oven.getLevel();
        CokeOvenLogic.State state = oven.getHelper().getState();
        if (level == null) { return; }
        Direction facing = oven.getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING);
        poseStack.pushPose();
        poseStack.translate(0.5D, 0.5D, 0.5D);
        poseStack.mulPose(Axis.YP.rotationDegrees(CokeOvenFacing.yRotation(facing)));
        poseStack.translate(-0.5D, -0.5D, -0.5D);
        drawDoor(oven.getBlockPos(), level, state, partialTicks, poseStack, buffer, packedLight);
        poseStack.translate(0.0D, 0.0D, CHAMBER_Z);
        drawChamber(oven.getBlockPos(), level, state, partialTicks, poseStack, buffer, packedLight);
        if (state.getFire() > 0) { drawFire(poseStack, buffer.getBuffer(RenderType.cutout())); }
        poseStack.popPose();
    }

    private static float doorAngle(Level level, CokeOvenLogic.State state, float partialTicks) {
        float t = Mth.clamp(((level.getGameTime() - state.getDoorTime()) + partialTicks) / CokeOvenRules.DOOR_TICKS, 0.0F, 1.0F);
        float eased = t * t * (3.0F - 2.0F * t);
        return DOOR_SIGN * CokeOvenRules.DOOR_ANGLE * (state.isDoorOpen() ? eased : 1.0F - eased);
    }

    private static void drawDoor(BlockPos pos, Level level, CokeOvenLogic.State state, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();
        poseStack.translate(PIVOT_X, PIVOT_Y, PIVOT_Z);
        poseStack.mulPose(Axis.YP.rotationDegrees(doorAngle(level, state, partialTicks)));
        poseStack.translate(-PIVOT_X, -PIVOT_Y, -PIVOT_Z);
        RenderUtils.renderModelTESRFancy(CokeOvenModels.DOOR.get().getQuads(null, null, RANDOM, ModelData.EMPTY, null), buffer.getBuffer(RenderType.solid()), poseStack, level, pos, false, 0xffffff, packedLight);
        poseStack.popPose();
    }

    private static void drawChamber(BlockPos pos, Level level, CokeOvenLogic.State state, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        ItemStack stack = state.getChamber();
        if (stack.isEmpty()) { return; }
        int light = packedLight;
        if (state.isActive()) {
            float pulse = 0.5F + 0.5F * Mth.sin((level.getGameTime() + partialTicks) * 0.1F);
            int glow = (int) ((120.0F + 120.0F * pulse) / 16.0F);
            light = LightTexture.pack(glow, glow);
        }
        poseStack.pushPose();
        poseStack.translate(0.5D, 0.5D, 0.5D);
        poseStack.scale(ITEM_SCALE, ITEM_SCALE, ITEM_SCALE);
        Minecraft.getInstance().getItemRenderer().renderStatic(stack, ItemDisplayContext.NONE, light, OverlayTexture.NO_OVERLAY, poseStack, buffer, level, (int) pos.asLong());
        poseStack.popPose();
    }

    private static void drawFire(PoseStack poseStack, VertexConsumer consumer) {
        TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(FIRE_SPRITE);
        PoseStack.Pose pose = poseStack.last();
        float u0 = sprite.getU0();
        float u1 = sprite.getU1();
        float v0 = sprite.getV0();
        float v1 = sprite.getV1();
        float y1 = FIRE_MIN + FIRE_HEIGHT;
        float[][] first = {{FIRE_MIN, FIRE_MIN, FIRE_MIN, u1, v1}, {FIRE_MAX, FIRE_MIN, FIRE_MAX, u0, v1}, {FIRE_MAX, y1, FIRE_MAX, u0, v0}, {FIRE_MIN, y1, FIRE_MIN, u1, v0}};
        float[][] second = {{FIRE_MAX, FIRE_MIN, FIRE_MIN, u1, v1}, {FIRE_MIN, FIRE_MIN, FIRE_MAX, u0, v1}, {FIRE_MIN, y1, FIRE_MAX, u0, v0}, {FIRE_MAX, y1, FIRE_MIN, u1, v0}};
        for (float[][] quad : new float[][][] {first, second}) {
            for (int i = 0; i < 4; i++) { vertex(consumer, pose, quad[i]); }
            for (int i = 3; i >= 0; i--) { vertex(consumer, pose, quad[i]); }
        }
    }

    private static void vertex(VertexConsumer consumer, PoseStack.Pose pose, float[] v) { consumer.vertex(pose.pose(), v[0], v[1], v[2]).color(255, 255, 255, 255).uv(v[3], v[4]).uv2(LightTexture.FULL_BRIGHT).normal(pose.normal(), 0.0F, 1.0F, 0.0F).endVertex(); }
}
