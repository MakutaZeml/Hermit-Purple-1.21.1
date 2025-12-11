package com.zeml.ripplez_hp.jojoimpl.stands.hermitpurple.client.renderer;

import com.github.standobyte.jojo.client.ClientGlobals;
import com.github.standobyte.jojo.client.entityrender.parsemodel.loader.RotpGeckoModelLoader;
import com.github.standobyte.jojo.client.firstperson.FirstPersonModelLayer;
import com.github.standobyte.jojo.client.standskin.StandSkinsLoader;
import com.github.standobyte.jojo.mechanics.clothes.mannequin.MannequinEntity;
import com.github.standobyte.jojo.powersystem.standpower.StandPower;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.zeml.ripplez_hp.core.HermitPurpleAddon;
import com.zeml.ripplez_hp.init.power.AddonStands;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;

public class HermitPurpleLayer<T extends LivingEntity, M extends HumanoidModel<T>> extends RenderLayer<T, M> implements FirstPersonModelLayer {
    HermitPurpleModel purpleModel;
    HermitPurpleModel thorns;
    private final ResourceLocation HERMIT = ResourceLocation.tryBuild(HermitPurpleAddon.MOD_ID,"textures/entity/stand/hermito_purple.png");
    private final ResourceLocation THORNS = ResourceLocation.tryBuild(HermitPurpleAddon.MOD_ID,"textures/entity/stand/hermito_torns.png");
    public HermitPurpleLayer(RenderLayerParent<T, M> renderer) {
        super(renderer);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, T t, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if(!ClientGlobals.canSeeStands || (t.isInvisible() && !(t instanceof MannequinEntity))){
            return;
        }

        StandPower standData = StandPower.get(t);
        if(standData != null && standData.getPowerType() == AddonStands.HERMIT_PURPLE.get() && standData.isSummoned()){
            boolean slim = t instanceof MannequinEntity mannequin && mannequin.isSlim() || t instanceof AbstractClientPlayer player && player.getSkin().model() == PlayerSkin.Model.SLIM;
            ResourceLocation texture = StandSkinsLoader.getInstance().getSkin(StandPower.get(t)).getTexture(HERMIT);
            M parentModel = getParentModel();
            purpleModel = new HermitPurpleModel(RotpGeckoModelLoader.getInstance().getModelDefinition(HermitPurpleAddon.resLoc("hermit_base")).bakeRoot());
            parentModel.copyPropertiesTo(purpleModel);
            purpleModel.setSlim(slim);
            purpleModel.poseLayer(parentModel);
            VertexConsumer ivertexbuilder = buffer.getBuffer(RenderType.entityCutoutNoCull(texture));
            purpleModel.renderToBuffer(poseStack,ivertexbuilder,packedLight, OverlayTexture.NO_OVERLAY);

            ResourceLocation thorns_texture = StandSkinsLoader.getInstance().getSkin(StandPower.get(t)).getTexture(THORNS);
            thorns = new HermitPurpleModel(RotpGeckoModelLoader.getInstance().getModelDefinition(HermitPurpleAddon.resLoc("hermit_base")).bakeRoot());
            parentModel.copyPropertiesTo(thorns);
            thorns.setSlim(slim);
            thorns.poseLayer(parentModel);
            VertexConsumer thorns_vertex = buffer.getBuffer(RenderType.entityCutoutNoCull(thorns_texture));
            purpleModel.renderToBuffer(poseStack,thorns_vertex,packedLight, OverlayTexture.NO_OVERLAY);

        }
    }


    @Override
    public void renderHandFirstPerson(HumanoidArm humanoidArm, PoseStack poseStack, MultiBufferSource buffer, int packedLight, LivingEntity t, LivingEntityRenderer<?, ?> livingEntityRenderer) {

        boolean slim = t instanceof AbstractClientPlayer player && player.getSkin().model() == PlayerSkin.Model.SLIM;
        ResourceLocation hermit = HERMIT;
        if(StandPower.getOptional(t).isPresent() && StandPower.get(t).getPowerType() == AddonStands.HERMIT_PURPLE.get() && StandPower.get(t).isSummoned()) {
            ResourceLocation texture = StandSkinsLoader.getInstance().getSkin(StandPower.get(t)).getTexture(hermit);
            purpleModel = new HermitPurpleModel(RotpGeckoModelLoader.getInstance().getModelDefinition(HermitPurpleAddon.resLoc("hermit_base")).bakeRoot());
            purpleModel.setSlim(slim);
            purpleModel.head.visible = false;
            purpleModel.body.visible = false;
            purpleModel.rightLeg.visible = false;
            purpleModel.leftLeg.visible = false;
            purpleModel.hat.visible = false;
            purpleModel.poseLayer(getParentModel());
            ModelPart arm = FirstPersonModelLayer.getArm(purpleModel, humanoidArm);
            VertexConsumer iVertexBuilder = buffer.getBuffer(RenderType.entityCutoutNoCull(texture));
            arm.xRot = 0.0F;
            arm.render(poseStack, iVertexBuilder, packedLight, OverlayTexture.NO_OVERLAY);
            ModelPart armSlim = humanoidArm == HumanoidArm.LEFT ? purpleModel.leftArmSlim : purpleModel.rightArmSlim;
            armSlim.xRot = 0.0F;
            armSlim.render(poseStack, iVertexBuilder, packedLight, OverlayTexture.NO_OVERLAY);
            arm.render(poseStack, iVertexBuilder, packedLight, OverlayTexture.NO_OVERLAY);
        }

    }


}
