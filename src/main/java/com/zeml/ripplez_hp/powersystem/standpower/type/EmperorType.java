package com.zeml.ripplez_hp.powersystem.standpower.type;

import com.github.standobyte.jojo.init.ModItemDataComponents;
import com.github.standobyte.jojo.powersystem.MovesetBuilder;
import com.github.standobyte.jojo.powersystem.standpower.StandPower;
import com.github.standobyte.jojo.powersystem.standpower.StandStats;
import com.github.standobyte.jojo.powersystem.standpower.type.StandType;
import com.zeml.ripplez_hp.core.HermitPurpleAddon;
import com.zeml.ripplez_hp.core.util.EmperorUtil;
import com.zeml.ripplez_hp.init.AddonItems;
import com.zeml.ripplez_hp.init.HermitDataComponents;
import com.zeml.ripplez_hp.init.power.AddonStands;
import com.zeml.ripplez_hp.mc.item.component.EmperorGunData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

import java.util.Optional;


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



}
