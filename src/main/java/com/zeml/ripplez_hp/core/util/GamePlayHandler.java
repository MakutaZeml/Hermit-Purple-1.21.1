package com.zeml.ripplez_hp.core.util;

import com.github.standobyte.jojo.powersystem.standpower.StandPower;
import com.zeml.ripplez_hp.init.AddonItems;
import com.zeml.ripplez_hp.init.HermitDataComponents;
import com.zeml.ripplez_hp.mc.item.component.EmperorGunData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

@EventBusSubscriber
public class GamePlayHandler {

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void blockDamage(LivingDamageEvent.Post event){
        DamageSource dmgSource = event.getSource();
        LivingEntity living = event.getEntity();
        if(!dmgSource.is(DamageTypeTags.IS_EXPLOSION) && !dmgSource.is(DamageTypeTags.IS_FIRE) &&
                !dmgSource.is(DamageTypeTags.IS_FREEZING) && !dmgSource.is(DamageTypeTags.IS_DROWNING) &&
                !dmgSource.is(DamageTypeTags.IS_FALL) && !dmgSource.is(DamageTypeTags.IS_PROJECTILE)){

        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onItem(EntityJoinLevelEvent event){
        if(event.getEntity() instanceof ItemEntity item){
            if(item.getItem().get(HermitDataComponents.EMPEROR.get()) != null){
                ItemStack emperor = item.getItem();
                EmperorGunData data = emperor.get(HermitDataComponents.EMPEROR.get());
                if(data.getUuidOwner().isPresent()){
                    if(((ServerLevel) item.level()).getEntity(data.getUuidOwner().get()) instanceof LivingEntity user){
                        if (!user.getItemInHand(InteractionHand.MAIN_HAND).isEmpty()) {
                            if (user.getItemInHand(InteractionHand.OFF_HAND).isEmpty()) {
                                user.setItemInHand(InteractionHand.OFF_HAND, emperor);
                            } else {
                                ItemStack itemStack = user.getItemInHand(InteractionHand.MAIN_HAND);
                                ItemEntity itemEntity = new ItemEntity(user.level(), user.getX(), user.getY(), user.getZ(), itemStack);
                                user.level().addFreshEntity(itemEntity);
                                user.setItemInHand(InteractionHand.MAIN_HAND, emperor);
                            }
                        } else {
                            user.setItemInHand(InteractionHand.MAIN_HAND, emperor);
                        }
                        event.setCanceled(true);
                    }
                }
            }
        }
    }

}
