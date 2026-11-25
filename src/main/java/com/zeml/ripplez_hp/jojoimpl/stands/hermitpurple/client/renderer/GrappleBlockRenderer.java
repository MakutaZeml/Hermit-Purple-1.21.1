package com.zeml.ripplez_hp.jojoimpl.stands.hermitpurple.client.renderer;

import com.github.standobyte.jojo.client.ClientGlobals;
import com.github.standobyte.jojo.client.entityrender.entities.SimpleEntityRenderer;
import com.github.standobyte.jojo.client.standskin.StandSkin;
import com.github.standobyte.jojo.client.standskin.StandSkinsLoader;
import com.github.standobyte.jojo.powersystem.standpower.StandPower;
import com.github.standobyte.jojo.powersystem.standpower.type.SummonedStand;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.zeml.ripplez_hp.client.HermitClientUtil;
import com.zeml.ripplez_hp.jojoimpl.stands.hermitpurple.grapple.entity.GrappleBlockEntity;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

public class GrappleBlockRenderer extends SimpleEntityRenderer<GrappleBlockEntity, EntityModel<GrappleBlockEntity>> {
    public GrappleBlockRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager);
    }


    @Override
    public void render(GrappleBlockEntity entity, float yRotation, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        if (!entity.isInvisible() && entity.getOwner() != null && ClientGlobals.canSeeStands) {
            int color = 0xc573fb;
            float alpha = 1;
            if (entity.standPower() != null) {
                StandPower power = entity.standPower();
                StandSkin skin = StandSkinsLoader.getInstance().getSkin(power);
                if (power.getSummonedStand() instanceof SummonedStand.SyncableSummonedStand stand) {
                    alpha = stand.unsummonAlpha(partialTick);
                }
                if (skin != null) {
                    color = skin.getColors().secondary();
                }
            }
            poseStack.pushPose();
            double d0 = (double) (entity.getPreciseBodyRotation(partialTick) * 0.017453292F) + 1.5707963267948966;
            Vec3 vec3 = entity.getOwner().position().add(0, entity.getOwner().getBbHeight() * 3 / 4, 0);
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
            int j = HermitClientUtil.getBlockLightL(entity.level(), blockpos1);
            int k = entity.level().getBrightness(LightLayer.SKY, blockpos);
            int l = entity.level().getBrightness(LightLayer.SKY, blockpos1);

            int j1;
            for (j1 = 0; j1 <= 24; ++j1) {
                HermitClientUtil.addVertexPair(vertexconsumer, matrix4f, f, f1, f2, i, j, k, l, 0.025F, 0.025F, f5, f6, j1, false, color, alpha);
            }

            for (j1 = 24; j1 >= 0; --j1) {
                HermitClientUtil.addVertexPair(vertexconsumer, matrix4f, f, f1, f2, i, j, k, l, 0.025F, 0.0F, f5, f6, j1, true, color, alpha);
            }
            poseStack.popPose();

        }
        super.render(entity, yRotation, partialTick, poseStack, buffer, packedLight);
    }

    @Override
    public boolean shouldRender(GrappleBlockEntity livingEntity, Frustum camera, double camX, double camY, double camZ) {
        if (!livingEntity.shouldRender(camX, camY, camZ)) {
            return false;
        } else if (livingEntity.noCulling) {
            return true;
        } else {
            double range = livingEntity.getOwner() == null ? 20 : livingEntity.getOwner().distanceTo(livingEntity);
            AABB aabb = livingEntity.getBoundingBoxForCulling().inflate(.75 + range);

            if (aabb.hasNaN() || aabb.getSize() == 0.0) {
                aabb = new AABB(livingEntity.getX() - 2.0, livingEntity.getY() - 2.0, livingEntity.getZ() - 2.0, livingEntity.getX() + 2.0, livingEntity.getY() + 2.0, livingEntity.getZ() + 2.0);
            }
            return camera.isVisible(aabb);
        }
    }

}