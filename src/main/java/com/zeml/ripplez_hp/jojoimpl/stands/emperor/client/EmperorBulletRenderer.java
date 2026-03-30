package com.zeml.ripplez_hp.jojoimpl.stands.emperor.client;

import com.github.standobyte.jojo.client.entityrender.entities.SimpleEntityRenderer;
import com.github.standobyte.jojo.client.standskin.StandSkin;
import com.github.standobyte.jojo.client.util.functions.ClientUtil;
import com.github.standobyte.jojo.util.functions.MathUtil;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.zeml.ripplez_hp.client.HermitClientUtil;
import com.zeml.ripplez_hp.core.HermitPurpleAddon;
import com.zeml.ripplez_hp.jojoimpl.stands.emperor.entity.EmperorBulletEntity;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix3f;

import java.util.List;

public class EmperorBulletRenderer extends SimpleEntityRenderer<EmperorBulletEntity, EntityModel<EmperorBulletEntity>> {
    protected double maxTrailLen = 4;
    protected float V1 = 0.015625f;
    protected float BEAM_WIDTH = 0.015f;
    private static final ResourceLocation texPath = HermitPurpleAddon.resLoc("textures/entity/projectiles/bullet_trace.png");

    public EmperorBulletRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public ResourceLocation getTextureLocation(EmperorBulletEntity entity) {
        if (this.texFromStandSkin) {
            StandSkin standSkin = this.getStandSkin(entity);
            if (standSkin != null) {
                return standSkin.getTexture(this.texPath);
            }
        }
        return this.texPath;
    }

    @Override
    public void render(EmperorBulletEntity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        List<Vec3> trace = entity.tracePos;
        if (trace.isEmpty()) {
            return;
        }
        poseStack.pushPose();
        poseStack.translate(0, entity.getBbHeight() / 2, 0);
        VertexConsumer vertexBuilder = bufferSource.getBuffer(RenderType.entityTranslucentCull(getTextureLocation(entity)));

        double traceLen = maxTrailLen;
        int i;
        boolean first = true;
        for (i = trace.size() - 1; i > 0 && traceLen > 0; i--) {
            Vec3 posCur = trace.get(i);
            Vec3 posPrev = trace.get(i - 1);
            float u0;
            float u1 = (float) (traceLen / maxTrailLen);

            Vec3 diffBack = posPrev.subtract(posCur);
            double len = diffBack.length();

            if (len > traceLen) {
                posPrev = posCur.add(diffBack.normalize().scale(traceLen));
                traceLen = 0;
            } else {
                traceLen -= len;
            }
            u0 = (float) (traceLen / maxTrailLen);

            trailSegment(posPrev, posCur, u0, u1, poseStack, vertexBuilder, entity, entityYaw, partialTick, first);
            first = false;
        }

        poseStack.popPose();
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }

    protected void trailSegment(Vec3 pos1, Vec3 pos2, float u0, float u1,
                                PoseStack matrixStack, VertexConsumer vertexBuilder,
                                EmperorBulletEntity entity, float yRotation, float partialTick, boolean first) {
        matrixStack.pushPose();
        Vec3 trailSegmentVec = pos1.subtract(pos2);
        float yRot = MathUtil.yRotDegFromVec(trailSegmentVec);
        float xRot = MathUtil.xRotDegFromVec(trailSegmentVec);
        matrixStack.mulPose(Axis.YP.rotationDegrees(-90.0F - yRot));
        matrixStack.mulPose(Axis.ZP.rotationDegrees(-xRot));
        matrixStack.scale(1.0F, BEAM_WIDTH, BEAM_WIDTH);
        Matrix3f lighting = matrixStack.last().normal();
        lighting.identity();
        Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();
        Matrix3f rotationMatrix = new Matrix3f();
        rotationMatrix.rotation(Axis.XP.rotationDegrees(camera.getXRot()));
        lighting.mul(rotationMatrix);
        float length = (float) trailSegmentVec.length();

        if (first) {
            renderFront(matrixStack, new Vec3(0, 0, 1), vertexBuilder);
        }
        renderSide(matrixStack, new Vec3(0, -1,  0),  length, u0, u1, vertexBuilder);
        renderSide(matrixStack, new Vec3(0,  0, -1),  length, u0, u1, vertexBuilder);
        renderSide(matrixStack, new Vec3(0,  1,  0),  length, u0, u1, vertexBuilder);
        renderSide(matrixStack, new Vec3(0,  0,  1),  length, u0, u1, vertexBuilder);

        matrixStack.popPose();
        matrixStack.translate(trailSegmentVec.x, trailSegmentVec.y, trailSegmentVec.z);
    }

    private void renderSide(PoseStack matrixStack, Vec3 lightNormal, float length, float u0, float u1, VertexConsumer vertexBuilder) {
        int packedLight = ClientUtil.MAX_LIGHT;
        float v0 = 0;
        float v1 = V1;
        matrixStack.mulPose(Axis.XP.rotationDegrees(90.0F));
        matrixStack.pushPose();

        matrixStack.translate(0, 0, 1f);

        PoseStack.Pose matrix = matrixStack.last();
        HermitClientUtil.vertex(matrix, vertexBuilder,
                packedLight, OverlayTexture.NO_OVERLAY, 1, 1, 1, 1,
                0, -1, 0,
                u1, v0,
                (float) lightNormal.x(), (float)lightNormal.y(), (float)lightNormal.z());
        HermitClientUtil.vertex(matrix, vertexBuilder,
                packedLight, OverlayTexture.NO_OVERLAY, 1, 1, 1, 1,
                length, -1, 0,
                u0, v0,
                (float)lightNormal.x(),(float) lightNormal.y(),(float) lightNormal.z());
        HermitClientUtil.vertex(matrix, vertexBuilder,
                packedLight, OverlayTexture.NO_OVERLAY, 1, 1, 1, 1,
                length, 1, 0,
                u0, v1,
                (float)lightNormal.x(),(float) lightNormal.y(),(float) lightNormal.z());
        HermitClientUtil.vertex(matrix, vertexBuilder,
                packedLight, OverlayTexture.NO_OVERLAY, 1, 1, 1, 1,
                0, 1, 0,
                u1, v1,
                (float)lightNormal.x(), (float)lightNormal.y(), (float)lightNormal.z());

        matrixStack.mulPose(Axis.XP.rotationDegrees(180.0F));
        HermitClientUtil.vertex(matrix, vertexBuilder,
                packedLight, OverlayTexture.NO_OVERLAY, 1, 1, 1, 1,
                0, -1, 0,
                u1, v0,
                (float)-lightNormal.x(), (float)-lightNormal.y(), (float)-lightNormal.z());
        HermitClientUtil.vertex(matrix, vertexBuilder,
                packedLight, OverlayTexture.NO_OVERLAY, 1, 1, 1, 1,
                length, -1, 0,
                u0, v0,
                (float)-lightNormal.x(), (float)-lightNormal.y(), (float)-lightNormal.z());
        HermitClientUtil.vertex(matrix, vertexBuilder,
                packedLight, OverlayTexture.NO_OVERLAY, 1, 1, 1, 1,
                length, 1, 0,
                u0, v1,
                (float)-lightNormal.x(), (float)-lightNormal.y(), (float)-lightNormal.z());
        HermitClientUtil.vertex(matrix, vertexBuilder,
                packedLight, OverlayTexture.NO_OVERLAY, 1, 1, 1, 1,
                0, 1, 0,
                u1, v1,
                (float)-lightNormal.x(), (float)-lightNormal.y(), (float)-lightNormal.z());

        matrixStack.popPose();
    }

    private void renderFront(PoseStack matrixStack, Vec3 lightNormal, VertexConsumer vertexBuilder) {
        int packedLight = ClientUtil.MAX_LIGHT;
        float u0 = 0;
        float u1 = u0 + V1;
        float v0 = V1;
        float v1 = v0 + V1;
        matrixStack.pushPose();

        matrixStack.mulPose(Axis.YP.rotationDegrees(90.0F));

        PoseStack.Pose matrix = matrixStack.last();
        HermitClientUtil.vertex(matrix, vertexBuilder,
                packedLight, OverlayTexture.NO_OVERLAY, 1, 1, 1, 1,
                -1, -1, 0,
                u1, v0,
                (float) lightNormal.x(), (float)lightNormal.y(),(float) lightNormal.z());
        HermitClientUtil.vertex(matrix, vertexBuilder,
                packedLight, OverlayTexture.NO_OVERLAY, 1, 1, 1, 1,
                1, -1, 0,
                u0, v0,
                (float)lightNormal.x(), (float)lightNormal.y(), (float)lightNormal.z());
        HermitClientUtil.vertex(matrix, vertexBuilder,
                packedLight, OverlayTexture.NO_OVERLAY, 1, 1, 1, 1,
                1, 1, 0,
                u0, v1,
                (float)lightNormal.x(), (float)lightNormal.y(), (float)lightNormal.z());
        HermitClientUtil.vertex(matrix, vertexBuilder,
                packedLight, OverlayTexture.NO_OVERLAY, 1, 1, 1, 1,
                -1, 1, 0,
                u1, v1,
                (float)lightNormal.x(), (float)lightNormal.y(), (float)lightNormal.z());

        matrixStack.mulPose(Axis.XP.rotationDegrees(180.0F));
        HermitClientUtil.vertex(matrix, vertexBuilder,
                packedLight, OverlayTexture.NO_OVERLAY, 1, 1, 1, 1,
                -1, -1, 0,
                u1, v0,
                (float)-lightNormal.x(), (float)-lightNormal.y(), (float)-lightNormal.z());
        HermitClientUtil.vertex(matrix, vertexBuilder,
                packedLight, OverlayTexture.NO_OVERLAY, 1, 1, 1, 1,
                1, -1, 0,
                u0, v0,
                (float)-lightNormal.x(), (float)-lightNormal.y(), (float)-lightNormal.z());
        HermitClientUtil.vertex(matrix, vertexBuilder,
                packedLight, OverlayTexture.NO_OVERLAY, 1, 1, 1, 1,
                1, 1, 0,
                u0, v1,
                (float)-lightNormal.x(), (float)-lightNormal.y(), (float)-lightNormal.z());
        HermitClientUtil.vertex(matrix, vertexBuilder,
                packedLight, OverlayTexture.NO_OVERLAY, 1, 1, 1, 1,
                -1, 1, 0,
                u1, v1,
                (float)-lightNormal.x(), (float)-lightNormal.y(), (float)-lightNormal.z());

        matrixStack.popPose();

    }

}
