package mctmods.immersivefabrication.client.util;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;

public final class QuadRetexture {
    private static final int STRIDE = DefaultVertexFormat.BLOCK.getVertexSize() / 4;
    private static final int UV = 4;

    private QuadRetexture() {}

    public static BakedQuad apply(BakedQuad quad, TextureAtlasSprite sprite) {
        TextureAtlasSprite from = quad.getSprite();
        int[] data = quad.getVertices().clone();
        for (int vertex = 0; vertex < 4; vertex++) {
            int at = vertex * STRIDE + UV;
            data[at] = Float.floatToRawIntBits(sprite.getU(from.getUOffset(Float.intBitsToFloat(data[at]))));
            data[at + 1] = Float.floatToRawIntBits(sprite.getV(from.getVOffset(Float.intBitsToFloat(data[at + 1]))));
        }
        return new BakedQuad(data, quad.getTintIndex(), quad.getDirection(), sprite, quad.isShade(), quad.hasAmbientOcclusion());
    }
}
