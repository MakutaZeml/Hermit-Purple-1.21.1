package com.zeml.ripplez_hp.jojoimpl.stands.hermitpurple.client.renderer;

import com.github.standobyte.jojo.client.ClientGlobals;
import com.github.standobyte.jojo.client.entityanim.IHumanoidAnimModel;
import com.github.standobyte.jojo.client.entityanim.RotpAnimDefinition;
import com.github.standobyte.jojo.client.entityanim.pose.AnimFramePose;
import com.github.standobyte.jojo.client.entityrender.EntityActionRenderState;
import com.github.standobyte.jojo.client.entityrender.RipplesPlayerRenderState.RipplesRenderStateExtensionMixin;
import com.github.standobyte.jojo.client.entityrender.parsemodel.loader.ResourceModelEntry;
import com.github.standobyte.jojo.client.entityrender.parsemodel.loader.RotpGeckoModelLoader;
import com.github.standobyte.jojo.client.firstperson.FirstPersonModelLayer;
import com.github.standobyte.jojo.client.standskin.StandSkin;
import com.github.standobyte.jojo.client.standskin.StandSkinsLoader;
import com.github.standobyte.jojo.mechanics.clothes.mannequin.MannequinEntity;
import com.github.standobyte.jojo.powersystem.entityaction.ActionOBB;
import com.github.standobyte.jojo.powersystem.entityaction.LivingComponentAction;
import com.github.standobyte.jojo.powersystem.standpower.StandPower;
import com.github.standobyte.jojo.powersystem.standpower.type.SummonedStand.SyncableSummonedStand;
import com.github.standobyte.v1_21_4_stuff.missingmethods.ARGB;
import com.github.standobyte.v1_21_4_stuff.renderstate.EntityRenderState;
import com.github.standobyte.v1_21_4_stuff.renderstate.HumanoidRenderState;
import com.github.standobyte.v1_21_4_stuff.renderstate.RenderStateCrutches;
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

public class HermitPurpleVinesLayer <T extends LivingEntity, M extends HumanoidModel<T>> extends RenderLayer<T, M> implements FirstPersonModelLayer {
    ResourceModelEntry purpleModel;
    private final ResourceLocation HERMIT = ResourceLocation.tryBuild(HermitPurpleAddon.MOD_ID,"textures/entity/stand/hp_vine.png");
    public HermitPurpleVinesLayer(RenderLayerParent<T, M> renderer) {
        super(renderer);
        this.purpleModel = RotpGeckoModelLoader.getInstance().getModelContainer(HermitPurpleAddon.resLoc("hermit_vines"));
        this.purpleModel.rendererInit(HermitPurpleVinesModel::new);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, T t, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if(!ClientGlobals.canSeeStands || (t.isInvisible() && !(t instanceof MannequinEntity))){
            return;
        }
        
        HumanoidRenderState renderState = RenderStateCrutches.currentEntityRenderState;
        if (renderState == null) {
        	return;
        }
        EntityActionRenderState actionRenderState = ((RipplesRenderStateExtensionMixin) renderState).get().entityAction;

        StandPower standData = StandPower.get(t);
        if(standData != null && standData.getPowerType() == AddonStands.HERMIT_PURPLE.get() && standData.getSummonedStand() instanceof SyncableSummonedStand stand){
            StandSkin standSkin = StandSkinsLoader.getInstance().getSkin(standData);
            ResourceLocation texture = standSkin.getTexture(HERMIT);
            M parentModel = getParentModel();

            HermitPurpleVinesModel purpleModel = this.purpleModel.getModel(standSkin);

            purpleModel.setAllVisible(true);
            parentModel.copyPropertiesTo(purpleModel);

            EntityRenderState.resetPose(purpleModel);

            if (((IHumanoidAnimModel) parentModel).jojo_rippes$isPlayingAnimation()) {
                AnimFramePose curPlayerPose = actionRenderState.pose;
                RotpAnimDefinition.animate(purpleModel, curPlayerPose);
            }
            float alpha = stand.unsummonAlpha(partialTicks);
            int color = ARGB.white(alpha);
            VertexConsumer ivertexbuilder = buffer.getBuffer(RenderType.entityTranslucent(texture));
            purpleModel.renderToBuffer(poseStack,ivertexbuilder,packedLight, OverlayTexture.NO_OVERLAY, color);

        }
    }

	@Override
	public void renderHandFirstPerson(HumanoidArm side, PoseStack poseStack, MultiBufferSource buffer, int light,
			LivingEntity entity, LivingEntityRenderer<?, ?> entityRenderer, float partialTick) {
		// only renders during an animation anyway, so we don't have to do the vanilla rendering (for now)
	}
}