package com.zeml.ripplez_hp.mixin.client;

import com.github.standobyte.jojo.client.ClientGlobals;
import com.github.standobyte.jojo.client.standskin.StandSkin;
import com.github.standobyte.jojo.client.standskin.StandSkinsLoader;
import com.github.standobyte.jojo.powersystem.entityaction.ActionPhase;
import com.github.standobyte.jojo.powersystem.entityaction.LivingComponentAction;
import com.github.standobyte.jojo.powersystem.standpower.StandPower;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.zeml.ripplez_hp.core.HermitPurpleAddon;
import com.zeml.ripplez_hp.jojoimpl.stands.hermitpurple.grapple.GrappleBlockAbility;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = EntityRenderer.class)
public abstract class RenderHermitLeash<T extends Entity>  {

    @Inject(method = "render",at = @At("TAIL"))
    private void onRender(T p_entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, CallbackInfo callbackInfo){
        if(p_entity instanceof LivingEntity livingEntity){
            LivingComponentAction action = LivingComponentAction.getComponent(livingEntity);
            if(action.getAction() instanceof GrappleBlockAbility.GrapplingBlock grapplingBlock && grapplingBlock.getEndPos() != null && grapplingBlock.getPhase() != ActionPhase.RECOVERY){
                if(!ClientGlobals.canSeeStands){
                    return;
                }
                StandPower standPower = StandPower.get(livingEntity);
                StandSkin standSkin = StandSkinsLoader.getInstance().getSkin(standPower);

                poseStack.pushPose();
                double d0 = (double)(p_entity.getPreciseBodyRotation(partialTick) * 0.017453292F) + 1.5707963267948966;
                Vec3 vec3 = grapplingBlock.getEndPos();
                Vec3 vec31 = new Vec3(0,1.5,0);
                double d1 = Math.cos(d0) * vec31.z + Math.sin(d0) * vec31.x;
                double d2 = Math.sin(d0) * vec31.z - Math.cos(d0) * vec31.x;
                double d3 = Mth.lerp(partialTick, p_entity.xo, p_entity.getX()) + d1;
                double d4 = Mth.lerp(partialTick, p_entity.yo, p_entity.getY()) + vec31.y;
                double d5 = Mth.lerp(partialTick, p_entity.zo, p_entity.getZ()) + d2;
                poseStack.translate(d1, vec31.y, d2);
                float f = (float)(vec3.x - d3);
                float f1 = (float)(vec3.y - d4);
                float f2 = (float)(vec3.z - d5);
                VertexConsumer vertexconsumer = bufferSource.getBuffer(RenderType.leash());
                Matrix4f matrix4f = poseStack.last().pose();
                float f4 = Mth.invSqrt(f * f + f2 * f2) * 0.025F / 2.0F;
                float f5 = f2 * f4;
                float f6 = f * f4;
                BlockPos blockpos = BlockPos.containing(vec3);
                BlockPos blockpos1 = BlockPos.containing(vec31);
                int i = this.getBlockLightLevel(p_entity, blockpos);
                int j = this.getBlockLightL(p_entity.level(),blockpos1);
                int k = p_entity.level().getBrightness(LightLayer.SKY, blockpos);
                int l = p_entity.level().getBrightness(LightLayer.SKY, blockpos1);

                int j1;
                for(j1 = 0; j1 <= 24; ++j1) {
                    addVertexPair(vertexconsumer, matrix4f, f, f1, f2, i, j, k, l, 0.025F, 0.025F, f5, f6, j1, false, standSkin.getColors().secondary());
                }

                for(j1 = 24; j1 >= 0; --j1) {
                    addVertexPair(vertexconsumer, matrix4f, f, f1, f2, i, j, k, l, 0.025F, 0.0F, f5, f6, j1, true,standSkin.getColors().secondary());
                }

                poseStack.popPose();


            }
        }
    }

    private int getBlockLightL(Level level, BlockPos pos){
        return level.getBrightness(LightLayer.BLOCK, pos);
    }

    //The Only reason I'm using Entity Render and not Living Render
    @Shadow protected abstract int getBlockLightLevel(T entity,BlockPos blockPos);


    private static void addVertexPair(VertexConsumer buffer, Matrix4f pose, float startX, float startY, float startZ, int entityBlockLight, int holderBlockLight, int entitySkyLight, int holderSkyLight, float yOffset, float dy, float dx, float dz, int index, boolean reverse, int color) {
        float f = (float)index / 24.0F;
        int i = (int)Mth.lerp(f, (float)entityBlockLight, (float)holderBlockLight);
        int j = (int)Mth.lerp(f, (float)entitySkyLight, (float)holderSkyLight);
        int k = LightTexture.pack(i, j);
        float f1 = index % 2 == (reverse ? 1 : 0) ? 0.7F : 1.0F;
        float f2 = (((color& 0xFFFFFF) >> 16) & 0xFF)*f1;
        float f3 = (((color& 0xFFFFFF) >> 8) & 0xFF)*f1;
        float f4 = ((color& 0xFFFFFF) & 0xFF)*f1;
        float f5 = startX * f;
        float f6 = startY > 0.0F ? startY * f * f : startY - startY * (1.0F - f) * (1.0F - f);
        float f7 = startZ * f;
        buffer.addVertex(pose, f5 - dx, f6 + dy, f7 + dz).setColor(f2/255,f3/255,f4/255,1).setLight(k);
        buffer.addVertex(pose, f5 + dx, f6 + yOffset - dy, f7 - dz).setColor(f2/255,f3/255,f4/255,1).setLight(k);
    }





}