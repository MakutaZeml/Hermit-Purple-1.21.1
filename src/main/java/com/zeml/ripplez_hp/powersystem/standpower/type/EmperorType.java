package com.zeml.ripplez_hp.powersystem.standpower.type;

import com.github.standobyte.jojo.init.ModSoundEvents;
import com.github.standobyte.jojo.mechanics.voiceline.VoiceLineServerSide;
import com.github.standobyte.jojo.network.s2c.StandSkinSoundPacket;
import com.github.standobyte.jojo.network.s2c.TrNonEntityStandSummonPacket;
import com.github.standobyte.jojo.powersystem.MovesetBuilder;
import com.github.standobyte.jojo.powersystem.standpower.StandPower;
import com.github.standobyte.jojo.powersystem.standpower.StandStats;
import com.github.standobyte.jojo.powersystem.standpower.entity.EntityStandType;
import com.github.standobyte.jojo.powersystem.standpower.type.StandType;
import com.github.standobyte.jojo.powersystem.standpower.type.SummonedStand;
import com.zeml.ripplez_hp.core.util.EmperorUtil;
import com.zeml.ripplez_hp.init.AddonItems;
import com.zeml.ripplez_hp.init.power.AddonStands;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

import net.neoforged.neoforge.network.PacketDistributor;


public class EmperorType extends StandType {

    public EmperorType(StandStats stats, MovesetBuilder moveset, ResourceLocation id) {
        super(stats, moveset, id);
    }

	@Override
	public boolean summon(LivingEntity user, StandPower standPower) {
		EmperorUtil.giveEmperor(user,standPower);
		if(!user.level().isClientSide){
			if (!user.isShiftKeyDown()) {
				VoiceLineServerSide.play(user, ModSoundEvents.VOICELINE_STAND_SUMMON);
			}
		}
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


}
