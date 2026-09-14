package com.zeml.ripplez_hp.jojoimpl.stands.hermitpurple.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.github.standobyte.jojo.client.entityanim.IHumanoidAnimModel;
import com.github.standobyte.jojo.client.entityanim.RotpAnimDefinition;
import com.github.standobyte.jojo.client.entityrender.entities.HumanoidLikeModel;
import com.github.standobyte.jojo.client.entityrender.stand.StandEntityRenderer;
import com.github.standobyte.jojo.client.util.functions.ClientUtil;
import com.github.standobyte.jojo.mixininterface.LivingRendererLayers;
import com.github.standobyte.jojo.powersystem.entityaction.ActionAnimIdentifier;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.zeml.ripplez_hp.core.HermitPurpleAddon;
import com.zeml.ripplez_hp.jojoimpl.stands.hermitpurple.client.renderer.HermitPurpleLayer;
import com.zeml.ripplez_hp.jojoimpl.stands.hermitpurple.client.renderer.HermitPurpleVinesLayer;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import org.joml.Quaternionf;

import com.github.standobyte.jojo.client.entityanim.pose.AnimFramePose;
import com.github.standobyte.jojo.client.entityrender.ModelUtil;
import com.github.standobyte.jojo.client.standskin.StandSkin;
import com.github.standobyte.jojo.client.standskin.StandSkinsScreen;
import com.github.standobyte.jojo.mechanics.clothes.mannequin.MannequinEntity;
import com.github.standobyte.jojo.powersystem.PowerClass;
import com.github.standobyte.jojo.powersystem.standpower.StandPower;
import com.github.standobyte.jojo.powersystem.standpower.type.StandType;
import com.github.standobyte.jojo.powersystem.standpower.type.SummonedStand.SyncableSummonedStand;
import com.github.standobyte.v1_21_4_stuff.renderstate.HumanoidRenderState;
import com.github.standobyte.v1_21_4_stuff.renderstate.RenderStateCrutches;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.zeml.ripplez_hp.init.power.AddonStands;
import com.zeml.ripplez_hp.jojoimpl.stands.hermitpurple.client.renderer.HermitPurplePose;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;

public class HermitSkinView extends StandSkinsScreen.SkinView {
	public static final ActionAnimIdentifier POSE_ANIM_ID = ActionAnimIdentifier.getOrCreate("stand_info");
	private final List<RenderLayer<?,?>> layers = new ArrayList<>();
	private AnimFramePose pose;
	public HermitSkinView(StandType standType, StandSkin skin, StandSkinsScreen screen, 
			int x, int y, int standY, int row, int column, boolean isBottomRow) {
		super(standType, skin, screen, x, y, standY, row, column, isBottomRow);
		Minecraft mc = Minecraft.getInstance();
		LivingEntityRenderer<?, ?> renderer = (LivingEntityRenderer<?, ?>) mc.getEntityRenderDispatcher().getRenderer(mc.player);
		for (RenderLayer<?, ?> layer : ((LivingRendererLayers<?, ?>) renderer).jojo_ripples$allLayers()) {
			if (layer instanceof HermitPurpleLayer || layer instanceof  HermitPurpleVinesLayer) {
				this.layers.add(layer);

				RotpAnimDefinition anim = skin.getStandAnimation(POSE_ANIM_ID);
				if (anim != null) {
					this.pose = anim.calcAnimPose(0, 1, null, null).deepCopy();
				}


			}
		}
	}

	@Override
	public void renderStandModel(GuiGraphics gui, float posX, float posY,
								 float scale, float scaleZoom, float yRot, float xRot, float xOffsetRatio, float yOffsetRatio,
								 StandType standType, StandSkin standSkin,
								 boolean extractRenderState, float ticks, int color, StandEntityRenderer.MenuType screen) {
		HermitPurpleAddon.getLogger().debug("sex {}", layers);
		for(RenderLayer<?,?> layer: layers){
			if(layer instanceof HermitPurpleLayer<?,?> layer1){
				HumanoidModel<?> model = layer1.purpleModel.getModel(skin);
				ResourceLocation texture = skin.getTexture(HermitPurpleLayer.HERMIT);

				Quaternionf rotation = new Quaternionf()
						.rotateX(xRot)
						.rotateY(yRot);

				PoseStack poseStack = gui.pose();
				poseStack.pushPose();

				poseStack.translate(posX, posY, 350.0);

				poseStack.translate(xOffsetRatio, yOffsetRatio, 0);
				poseStack.scale(-scale, scale, scale);
				poseStack.scale(scaleZoom, scaleZoom, scaleZoom);
				poseStack.mulPose(rotation);
				poseStack.translate(0, -1.35, 0);
				poseStack.mulPose(Axis.YP.rotationDegrees(180));

				Lighting.setupForEntityInInventory();

				if (pose != null) {
					((IHumanoidAnimModel) model).jojo_ripples$setupHumanoidPose(pose);
				}

				// FIXME doesn't get cropped by OpenGL's scissor
				RenderSystem.runAsFancy(() -> {
					MultiBufferSource bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();
					VertexConsumer buffer = bufferSource.getBuffer(model.renderType(texture));
					model.renderToBuffer(poseStack, buffer, ClientUtil.MAX_LIGHT, OverlayTexture.NO_OVERLAY, color);
				});

				poseStack.popPose();

				Lighting.setupFor3DItems();
			} else if (layer instanceof HermitPurpleVinesLayer<?,?> layer1) {
				HumanoidModel<?> model = layer1.purpleModel.getModel(skin);
				ResourceLocation texture = skin.getTexture(HermitPurpleVinesLayer.HERMIT);

				Quaternionf rotation = new Quaternionf()
						.rotateX(xRot)
						.rotateY(yRot);

				PoseStack poseStack = gui.pose();
				poseStack.pushPose();

				poseStack.translate(posX, posY, 350.0);

				poseStack.translate(xOffsetRatio, yOffsetRatio, 0);
				poseStack.scale(-scale, scale, scale);
				poseStack.scale(scaleZoom, scaleZoom, scaleZoom);
				poseStack.mulPose(rotation);
				poseStack.translate(0, -1.35, 0);
				poseStack.mulPose(Axis.YP.rotationDegrees(180));

				Lighting.setupForEntityInInventory();

				if (pose != null) {
					((IHumanoidAnimModel) model).jojo_ripples$setupHumanoidPose(pose);
				}

				// FIXME doesn't get cropped by OpenGL's scissor
				RenderSystem.runAsFancy(() -> {
					MultiBufferSource bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();
					VertexConsumer buffer = bufferSource.getBuffer(model.renderType(texture));
					model.renderToBuffer(poseStack, buffer, ClientUtil.MAX_LIGHT, OverlayTexture.NO_OVERLAY, color);
				});

				poseStack.popPose();

				Lighting.setupFor3DItems();
			}
		}

	}

}
