package com.zeml.ripplez_hp.jojoimpl.stands.hermitpurple.client.renderer;

import com.github.standobyte.jojo.client.ClientGlobals;
import com.github.standobyte.jojo.client.entityanim.AnimFramePose;
import com.github.standobyte.jojo.client.entityanim.IHumanoidAnimModel;
import com.github.standobyte.jojo.client.entityanim.RotpAnimDefinition;
import com.github.standobyte.jojo.client.entityrender.parsemodel.loader.ResourceModelEntry;
import com.github.standobyte.jojo.client.entityrender.parsemodel.loader.RotpGeckoModelLoader;
import com.github.standobyte.jojo.client.standskin.StandSkin;
import com.github.standobyte.jojo.client.standskin.StandSkinsLoader;
import com.github.standobyte.jojo.mechanics.clothes.mannequin.MannequinEntity;
import com.github.standobyte.jojo.powersystem.standpower.StandPower;
import com.github.standobyte.v1_21_4_stuff.renderstate.EntityRenderState;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.zeml.ripplez_hp.core.HermitPurpleAddon;
import com.zeml.ripplez_hp.init.power.AddonStands;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

public class HermitPurpleVinesLayer <T extends LivingEntity, M extends HumanoidModel<T>> extends RenderLayer<T, M> {
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

        StandPower standData = StandPower.get(t);
        if(standData != null && standData.getPowerType() == AddonStands.HERMIT_PURPLE.get() && standData.isSummoned()){
            StandSkin standSkin = StandSkinsLoader.getInstance().getSkin(standData);
            ResourceLocation texture = standSkin.getTexture(HERMIT);
            M parentModel = getParentModel();

            HermitPurpleVinesModel purpleModel = this.purpleModel.getModel(standSkin);
            
            purpleModel.setAllVisible(true);
            parentModel.copyPropertiesTo(purpleModel);
            
            EntityRenderState.resetPose(purpleModel);
            if (((IHumanoidAnimModel) parentModel).jojo_rippes$isPlayingAnimation()) {
                AnimFramePose curPlayerPose = AnimFramePose.reused;
                RotpAnimDefinition.animate(purpleModel, curPlayerPose);
            }
            
            VertexConsumer ivertexbuilder = buffer.getBuffer(RenderType.entityCutoutNoCull(texture));
            purpleModel.renderToBuffer(poseStack,ivertexbuilder,packedLight, OverlayTexture.NO_OVERLAY);

        }
    }
}
