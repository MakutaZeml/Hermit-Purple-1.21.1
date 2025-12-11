package com.zeml.ripplez_hp.powersystem.standpower.type;

import com.github.standobyte.jojo.client.standskin.StandSkin;
import com.github.standobyte.jojo.client.standskin.StandSkinsScreen;
import com.github.standobyte.jojo.powersystem.MovesetBuilder;
import com.github.standobyte.jojo.powersystem.standpower.StandPower;
import com.github.standobyte.jojo.powersystem.standpower.StandStats;
import com.github.standobyte.jojo.powersystem.standpower.type.StandType;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.zeml.ripplez_hp.core.util.EmperorUtil;
import com.zeml.ripplez_hp.init.AddonItems;
import com.zeml.ripplez_hp.init.HermitDataComponents;
import com.zeml.ripplez_hp.init.power.AddonStands;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

import org.joml.Quaternionf;


public class EmperorType extends StandType {

    public EmperorType(StandStats stats, MovesetBuilder moveset, ResourceLocation id) {
        super(stats, moveset, id);
    }

    @Override
    public boolean summon(LivingEntity user, StandPower standPower) {
        EmperorUtil.giveEmperor(user,standPower);
        return super.summon(user, standPower);
    }


    @EventBusSubscriber
    public static class EmperorGive{

        @SubscribeEvent(priority = EventPriority.HIGH)
        public static void onPlayerTick(EntityTickEvent.Pre event){
            if(!event.getEntity().level().isClientSide){
                if(event.getEntity() instanceof LivingEntity living){
                    if(StandPower.get(living) != null && StandPower.get(living).getPowerType() == AddonStands.EMPEROR.get() && StandPower.get(living).isSummoned()){
                        if(event.getEntity() instanceof Player player){
                            if(EmperorUtil.noEmperor(player) && living.getItemInHand(InteractionHand.OFF_HAND).getItem() != AddonItems.EMPEROR.asItem()){
                                EmperorUtil.giveEmperor(player,StandPower.get(living));
                            }
                        }else {
                            if(living.getItemInHand(InteractionHand.MAIN_HAND).getItem() != AddonItems.EMPEROR.get().asItem() &&
                                    living.getItemInHand(InteractionHand.OFF_HAND).getItem() != AddonItems.EMPEROR.get().asItem()){
                                EmperorUtil.giveEmperor(living,StandPower.get(living));
                            }
                        }
                    }

                }
            }
        }
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
		};
	}


}
