package com.zeml.ripplez_hp.jojoimpl.stands.emperor.client;

import org.joml.Quaternionf;

import com.github.standobyte.jojo.client.standskin.StandSkin;
import com.github.standobyte.jojo.client.standskin.StandSkinsScreen;
import com.github.standobyte.jojo.powersystem.standpower.type.StandType;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.zeml.ripplez_hp.init.AddonItems;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.monster.Zombie;

public class EmperorSkinView extends StandSkinsScreen.SkinView {

	public EmperorSkinView(StandType standType, StandSkin skin, StandSkinsScreen screen, 
			int x, int y, int standY, int row, int column, boolean isBottomRow) {
		super(standType, skin, screen, x, y, standY, row, column, isBottomRow);
	}

	@Override
	public void renderStand(GuiGraphics gui, int mouseX, int mouseY, float ticks, boolean isHovered, 
			float posX, float posY, float scale, float scaleZoom, 
			float yRot, float xRot, float xOffsetRatio, float yOffsetRatio) {
        Quaternionf rotation = (new Quaternionf()).rotateX(-xRot).rotateY(-yRot+.75F);
        gui.pose().pushPose();
        gui.pose().translate(posX-25, posY, 350.0);
        gui.pose().translate(xOffsetRatio, yOffsetRatio, 0.0F);
        gui.pose().scale(scale, -scale, scale);
        gui.pose().translate(0.0, 1.25, 0.0);
        gui.pose().scale(scaleZoom*2, scaleZoom*2, scaleZoom*2);
        gui.pose().mulPose(rotation);
        gui.pose().translate(0.0, -1.25, 0.0);
        gui.flush();
        Lighting.setupForEntityInInventory();
        EntityRenderDispatcher renderManager = Minecraft.getInstance().getEntityRenderDispatcher();
        renderManager.setRenderShadow(false);

        Zombie zombie = new Zombie(Minecraft.getInstance().level);
        zombie.setItemInHand(InteractionHand.MAIN_HAND,AddonItems.EMPEROR.toStack());
        zombie.setInvisible(true);
        zombie.setNoAi(true);

        RenderSystem.runAsFancy(()->{
            renderManager.render(zombie, 0.0, 0.0, 0.0, 0.0F, ticks,gui.pose(),gui.bufferSource(), 15728880);
        });
        gui.flush();
        renderManager.setRenderShadow(true);
        gui.pose().popPose();
        Lighting.setupFor3DItems();
	}

}
