package com.zeml.ripplez_hp.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import org.joml.Matrix4f;

public class HermitClientUtil {

    public static void vertex(PoseStack.Pose pose, VertexConsumer vertexConsumer,
                              int packedLight, int packedOverlay, float red, float green, float blue, float alpha,
                              float offsetX, float offsetY, float offsetZ,
                              float texU, float texV,
                              float normalX, float normalY, float normalZ) {

        vertexConsumer
                .addVertex(pose.pose(), offsetX, offsetY, offsetZ)
                .setColor(red, green, blue, alpha)
                .setUv(texU, texV)
                .setOverlay(packedOverlay)
                .setUv2(packedLight & 65535, packedLight >> 16 & 65535)
                .setNormal(pose, normalX, normalY, normalZ);
    }

    public static void addVertexPair(VertexConsumer buffer, Matrix4f pose, float startX, float startY, float startZ,
                                     int entityBlockLight, int holderBlockLight, int entitySkyLight, int holderSkyLight,
                                     float yOffset, float dy, float dx, float dz, int index, boolean reverse, int color, float alpha) {
        float f = (float) index / 24.0F;
        int i = (int) Mth.lerp(f, (float) entityBlockLight, (float) holderBlockLight);
        int j = (int) Mth.lerp(f, (float) entitySkyLight, (float) holderSkyLight);
        int k = LightTexture.pack(i, j);
        float f1 = index % 2 == (reverse ? 1 : 0) ? 0.7F : 1.0F;
        float f2 = (((color & 0xFFFFFF) >> 16) & 0xFF) * f1;
        float f3 = (((color & 0xFFFFFF) >> 8) & 0xFF) * f1;
        float f4 = ((color & 0xFFFFFF) & 0xFF) * f1;
        float f5 = startX * f;
        float f6 = startY > 0.0F ? startY * f * f : startY - startY * (1.0F - f) * (1.0F - f);
        float f7 = startZ * f;
        buffer.addVertex(pose, f5 - dx, f6 + dy, f7 + dz).setColor(f2 / 255, f3 / 255, f4 / 255, alpha).setLight(k);
        buffer.addVertex(pose, f5 + dx, f6 + yOffset - dy, f7 - dz).setColor(f2 / 255, f3 / 255, f4 / 255, alpha).setLight(k);
    }

    public static int getBlockLightL(Level level, BlockPos pos) {
        return level.getBrightness(LightLayer.BLOCK, pos);
    }


}
