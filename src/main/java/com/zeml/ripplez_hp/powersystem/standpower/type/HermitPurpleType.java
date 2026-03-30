package com.zeml.ripplez_hp.powersystem.standpower.type;

import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;


import com.github.standobyte.jojo.client.entityrender.ModelUtil;
import com.github.standobyte.jojo.mechanics.clothes.mannequin.MannequinEntity;
import org.joml.Quaternionf;

import com.github.standobyte.jojo.client.standskin.StandSkin;
import com.github.standobyte.jojo.client.standskin.StandSkinsScreen;
import com.github.standobyte.jojo.powersystem.MovesetBuilder;
import com.github.standobyte.jojo.powersystem.PowerClass;
import com.github.standobyte.jojo.powersystem.standpower.StandPower;
import com.github.standobyte.jojo.powersystem.standpower.StandStats;
import com.github.standobyte.jojo.powersystem.standpower.type.StandType;
import com.github.standobyte.jojo.powersystem.standpower.type.SummonedStand;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.zeml.ripplez_hp.init.power.AddonStands;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.Zombie;

public class HermitPurpleType extends StandType {
    public HermitPurpleType(StandStats stats, MovesetBuilder moveset, ResourceLocation id) {
        super(stats, moveset, id);
    }

	
    @Override
	public StandSkinsScreen.SkinView makeSkinUIElement(StandSkin skin, StandSkinsScreen screen, int x, int y, int standY, int row, int column, boolean isBottomRow) {
		return new StandSkinsScreen.SkinView(this, skin, screen, x, y, standY, row, column, isBottomRow) {



			@Override
			public void renderStand(GuiGraphics gui, int mouseX, int mouseY, float ticks, boolean isHovered, 
					float posX, float posY, float scale, float scaleZoom, 
					float yRot, float xRot, float xOffsetRatio, float yOffsetRatio) {
				Quaternionf rotation = (new Quaternionf()).rotateX(-xRot).rotateY(-yRot+.75F);
		        gui.pose().pushPose();
		        gui.pose().translate(posX, posY, 350.0);
		        gui.pose().translate(xOffsetRatio, yOffsetRatio, 0.0F);
		        gui.pose().scale(scale, -scale, scale);
		        gui.pose().translate(0.0, 1.25, 0.0);
		        gui.pose().scale(scaleZoom, scaleZoom, scaleZoom);
		        gui.pose().mulPose(rotation);
		        gui.pose().translate(0.0, -1.25, 0.0);
		        gui.flush();
		        Lighting.setupForEntityInInventory();
		        EntityRenderDispatcher renderManager = Minecraft.getInstance().getEntityRenderDispatcher();
		        renderManager.setRenderShadow(false);

				MannequinEntity mannequin = new MannequinEntity(Minecraft.getInstance().level);
				mannequin.setInvisible(true);
				mannequin.setSlim(ModelUtil.isSlimModel(Minecraft.getInstance().player));
				PowerClass.STAND.attachPower(mannequin);
		        StandPower standPower = StandPower.get(mannequin);
		        if (standPower != null) {
		            standPower.setStand(AddonStands.HERMIT_PURPLE.get());
		            standPower.setSummonedStand( new SummonedStand.BlankSummonedStand());
		            standPower.setSelectedSkin(Optional.of(skin.skinId));
		        }

		        RenderSystem.runAsFancy(()->{
		            renderManager.render(mannequin, 0.0, 0.0, 0.0, 0.0F, ticks,gui.pose(),gui.bufferSource(), 15728880);
		        });
		        gui.flush();
		        renderManager.setRenderShadow(true);
		        gui.pose().popPose();
		        Lighting.setupFor3DItems();
			}

		};
	}

}
