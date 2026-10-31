package mctmods.immersivefabrication.client.renderer;

import mctmods.immersivefabrication.client.models.IndustrialPumpModels;
import mctmods.immersivefabrication.common.blocks.metal.structure.logic.IndustrialPumpBlockEntity;

import com.immersiveconvergence.api.client.RenderUtils;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.model.data.ModelData;

import java.util.List;
import javax.annotation.Nonnull;

public class IndustrialPumpRenderer implements BlockEntityRenderer<IndustrialPumpBlockEntity> {
    private static final double PIVOT_Y = 1.09375D;
    private static final RandomSource RANDOM = RandomSource.create();

    @Override public void render(@Nonnull IndustrialPumpBlockEntity pump, float partialTicks, @Nonnull PoseStack poseStack, @Nonnull MultiBufferSource buffer, int packedLight, int packedOverlay) {
        Level level = pump.getLevel();
        if (level == null || pump.isDummy()) { return; }
        BlockPos above = pump.getBlockPos().above();
        List<BakedQuad> quads = IndustrialPumpModels.SHAFT.get().getQuads(null, null, RANDOM, ModelData.EMPTY, null);
        poseStack.pushPose();
        poseStack.translate(0.5D, PIVOT_Y, 0.5D);
        poseStack.mulPose(Axis.YP.rotationDegrees(pump.spinAngle(level.getGameTime() + (double) partialTicks)));
        poseStack.translate(-0.5D, 1.0D - PIVOT_Y, -0.5D);
        RenderUtils.renderModelTESRFancy(quads, buffer.getBuffer(RenderType.solid()), poseStack, level, above, false, 0xffffff, LevelRenderer.getLightColor(level, above));
        poseStack.popPose();
    }
}
