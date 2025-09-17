package com.zeml.ripplez_hp.util;

import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;

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

}
