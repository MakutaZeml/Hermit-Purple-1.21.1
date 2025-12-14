package com.zeml.ripplez_hp.jojoimpl.stands.hermitpurple.client.renderer;

import com.github.standobyte.jojo.client.ClientGlobals;
import com.github.standobyte.jojo.client.entityrender.parsemodel.loader.ResourceModelEntry;
import com.github.standobyte.jojo.client.entityrender.parsemodel.loader.RotpGeckoModelLoader;
import com.github.standobyte.jojo.client.firstperson.FirstPersonModelLayer;
import com.github.standobyte.jojo.client.standskin.StandSkin;
import com.github.standobyte.jojo.client.standskin.StandSkinsLoader;
import com.github.standobyte.jojo.client.utils.ModelUtil;
import com.github.standobyte.jojo.mechanics.clothes.mannequin.MannequinEntity;
import com.github.standobyte.jojo.powersystem.standpower.StandPower;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.zeml.ripplez_hp.core.HermitPurpleAddon;
import com.zeml.ripplez_hp.init.power.AddonStands;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;

public class HermitPurpleLayer<T extends LivingEntity, M extends HumanoidModel<T>> extends RenderLayer<T, M> implements FirstPersonModelLayer {
    ResourceModelEntry purpleModel;
    private final ResourceLocation HERMIT = ResourceLocation.tryBuild(HermitPurpleAddon.MOD_ID,"textures/entity/stand/hermito_purple.png");
    private final ResourceLocation THORNS = ResourceLocation.tryBuild(HermitPurpleAddon.MOD_ID,"textures/entity/stand/hermito_torns.png");
    public HermitPurpleLayer(RenderLayerParent<T, M> renderer) {
        super(renderer);
        this.purpleModel = RotpGeckoModelLoader.getInstance().getModelContainer(HermitPurpleAddon.resLoc("hermit_base"));
        this.purpleModel.rendererInit(HermitPurpleModel::new);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, T t, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if(!ClientGlobals.canSeeStands || (t.isInvisible() && !(t instanceof MannequinEntity))){
            return;
        }

        StandPower standData = StandPower.get(t);
        if(standData != null && standData.getPowerType() == AddonStands.HERMIT_PURPLE.get() && standData.isSummoned()){
            boolean slim = ModelUtil.isSlimModel(t);
            StandSkin standSkin = StandSkinsLoader.getInstance().getSkin(standData);
            M parentModel = getParentModel();
            HermitPurpleModel purpleModel = this.purpleModel.getModel(standSkin);
            
            purpleModel.setAllVisible(true);
            parentModel.copyPropertiesTo(purpleModel);
            purpleModel.setSlim(slim);

            ResourceLocation texture = standSkin.getTexture(HERMIT);
            VertexConsumer ivertexbuilder = buffer.getBuffer(RenderType.entityCutoutNoCull(texture));
            purpleModel.renderToBuffer(poseStack,ivertexbuilder,packedLight, OverlayTexture.NO_OVERLAY);

            ResourceLocation thorns_texture = standSkin.getTexture(THORNS);
            VertexConsumer thorns_vertex = buffer.getBuffer(RenderType.entityCutoutNoCull(thorns_texture));
            purpleModel.renderToBuffer(poseStack,thorns_vertex,packedLight, OverlayTexture.NO_OVERLAY);

        }
    }


    @Override
    public void renderHandFirstPerson(HumanoidArm humanoidArm, PoseStack poseStack, MultiBufferSource buffer, int packedLight, LivingEntity t, LivingEntityRenderer<?, ?> livingEntityRenderer) {

        boolean slim = ModelUtil.isSlimModel(t);
        StandPower standData = StandPower.get(t);
        if(standData != null && standData.getPowerType() == AddonStands.HERMIT_PURPLE.get() && standData.isSummoned()) {
            StandSkin standSkin = StandSkinsLoader.getInstance().getSkin(standData);
            ResourceLocation texture = standSkin.getTexture(HERMIT);
            HermitPurpleModel purpleModel = (HermitPurpleModel) this.purpleModel.getModel(standSkin);
            purpleModel.setAllVisible(true);
            purpleModel.setSlim(slim);
            purpleModel.head.visible = false;
            purpleModel.body.visible = false;
            purpleModel.rightLeg.visible = false;
            purpleModel.leftLeg.visible = false;
            purpleModel.hat.visible = false;
            getParentModel().copyPropertiesTo(purpleModel);
            ModelPart arm = FirstPersonModelLayer.getArm(purpleModel, humanoidArm);
            VertexConsumer iVertexBuilder = buffer.getBuffer(RenderType.entityCutoutNoCull(texture));
            arm.xRot = 0.0F;
            arm.render(poseStack, iVertexBuilder, packedLight, OverlayTexture.NO_OVERLAY);
            ModelPart armSlim = humanoidArm == HumanoidArm.LEFT ? purpleModel.leftArmSlim : purpleModel.rightArmSlim;
            armSlim.copyFrom(arm);
            armSlim.render(poseStack, iVertexBuilder, packedLight, OverlayTexture.NO_OVERLAY);
            arm.render(poseStack, iVertexBuilder, packedLight, OverlayTexture.NO_OVERLAY);
        }

    }


}
