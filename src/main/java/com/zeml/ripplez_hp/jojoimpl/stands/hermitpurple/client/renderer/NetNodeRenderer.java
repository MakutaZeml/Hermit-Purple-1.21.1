package com.zeml.ripplez_hp.jojoimpl.stands.hermitpurple.client.renderer;

import com.github.standobyte.jojo.client.ClientGlobals;
import com.github.standobyte.jojo.client.entityrender.entities.SimpleEntityRenderer;
import com.github.standobyte.jojo.client.standskin.StandSkin;
import com.github.standobyte.jojo.client.standskin.StandSkinsLoader;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.zeml.ripplez_hp.jojoimpl.stands.hermitpurple.grapple.entity.NetNodeEntity;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

public class NetNodeRenderer extends SimpleEntityRenderer<NetNodeEntity, EntityModel<NetNodeEntity>> {
    public NetNodeRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager);
    }

    @Override
    public void render(NetNodeEntity entity, float yRotation, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        if (!ClientGlobals.canSeeStands) {
            return;
        }
        if (entity.getTarget() != null && !entity.isBroken()) {
            int color = 0xc573fb;
            if (entity.getStandPower() != null) {
                StandSkin skin = StandSkinsLoader.getInstance().getSkin(entity.getStandPower());
                if (skin != null) {
                    color = skin.getColors().secondary();
                }
            }
            poseStack.pushPose();
            double d0 = (double) (entity.getPreciseBodyRotation(partialTick) * 0.017453292F) + 1.5707963267948966;
            Vec3 vec3 = entity.getTarget().position();
            Vec3 vec31 = new Vec3(0, 0, 0);
            double d1 = Math.cos(d0) * vec31.z + Math.sin(d0) * vec31.x;
            double d2 = Math.sin(d0) * vec31.z - Math.cos(d0) * vec31.x;
            double d3 = Mth.lerp(partialTick, entity.xo, entity.getX()) + d1;
            double d4 = Mth.lerp(partialTick, entity.yo, entity.getY()) + vec31.y;
            double d5 = Mth.lerp(partialTick, entity.zo, entity.getZ()) + d2;
            poseStack.translate(d1, vec31.y, d2);
            float f = (float) (vec3.x - d3);
            float f1 = (float) (vec3.y - d4);
            float f2 = (float) (vec3.z - d5);
            VertexConsumer vertexconsumer = buffer.getBuffer(RenderType.leash());
            Matrix4f matrix4f = poseStack.last().pose();
            float f4 = Mth.invSqrt(f * f + f2 * f2) * 0.025F / 2.0F;
            float f5 = f2 * f4;
            float f6 = f * f4;
            BlockPos blockpos = BlockPos.containing(vec3);
            BlockPos blockpos1 = BlockPos.containing(vec31);
            int i = this.getBlockLightLevel(entity, blockpos);
            int j = this.getBlockLightL(entity.level(), blockpos1);
            int k = entity.level().getBrightness(LightLayer.SKY, blockpos);
            int l = entity.level().getBrightness(LightLayer.SKY, blockpos1);

            int j1;
            for (j1 = 0; j1 <= 24; ++j1) {
                addVertexPair(vertexconsumer, matrix4f, f, f1, f2, i, j, k, l, 0.025F, 0.025F, f5, f6, j1, false, color);
            }

            for (j1 = 24; j1 >= 0; --j1) {
                addVertexPair(vertexconsumer, matrix4f, f, f1, f2, i, j, k, l, 0.025F, 0.0F, f5, f6, j1, true, color);
            }
            poseStack.popPose();
        }
        super.render(entity, yRotation, partialTick, poseStack, buffer, packedLight);
    }

    private int getBlockLightL(Level level, BlockPos pos) {
        return level.getBrightness(LightLayer.BLOCK, pos);
    }


    @Override
    public boolean shouldRender(NetNodeEntity livingEntity, Frustum camera, double camX, double camY, double camZ) {
        if (!livingEntity.shouldRender(camX, camY, camZ)) {
            return false;
        } else if (livingEntity.noCulling) {
            return true;
        } else {
            AABB aabb = livingEntity.getBoundingBoxForCulling().inflate(livingEntity.getRANGE() * 1.5);

            if (aabb.hasNaN() || aabb.getSize() == 0.0) {
                aabb = new AABB(livingEntity.getX() - 2.0, livingEntity.getY() - 2.0, livingEntity.getZ() - 2.0, livingEntity.getX() + 2.0, livingEntity.getY() + 2.0, livingEntity.getZ() + 2.0);
            }
            return camera.isVisible(aabb);
        }
    }

    private static void addVertexPair(VertexConsumer buffer, Matrix4f pose, float startX, float startY, float startZ,
                                      int entityBlockLight, int holderBlockLight, int entitySkyLight, int holderSkyLight,
                                      float yOffset, float dy, float dx, float dz, int index, boolean reverse, int color) {
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
        buffer.addVertex(pose, f5 - dx, f6 + dy, f7 + dz).setColor(f2 / 255, f3 / 255, f4 / 255, 1).setLight(k);
        buffer.addVertex(pose, f5 + dx, f6 + yOffset - dy, f7 - dz).setColor(f2 / 255, f3 / 255, f4 / 255, 1).setLight(k);
    }
}