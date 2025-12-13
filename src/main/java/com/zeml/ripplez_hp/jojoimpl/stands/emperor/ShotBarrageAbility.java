package com.zeml.ripplez_hp.jojoimpl.stands.emperor;

import com.github.standobyte.jojo.init.ModStatusEffects;
import com.github.standobyte.jojo.powersystem.Power;
import com.github.standobyte.jojo.powersystem.ability.AbilityId;
import com.github.standobyte.jojo.powersystem.ability.AbilityType;
import com.github.standobyte.jojo.powersystem.ability.EntityActionAbility;
import com.github.standobyte.jojo.powersystem.ability.condition.ConditionCheck;
import com.github.standobyte.jojo.powersystem.entityaction.ActionPhase;
import com.github.standobyte.jojo.powersystem.entityaction.EntityActionInstance;
import com.github.standobyte.jojo.powersystem.entityaction.type.EntityActionType;
import com.zeml.ripplez_hp.core.HermitPurpleAddon;
import com.zeml.ripplez_hp.init.AddonItems;
import com.zeml.ripplez_hp.init.HermitDataComponents;
import com.zeml.ripplez_hp.mc.item.EmperorItem;
import com.zeml.ripplez_hp.mc.item.component.EmperorGunData;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import java.util.function.Function;

public class ShotBarrageAbility extends EntityActionAbility {

    public ShotBarrageAbility(AbilityType<?> abilityType, AbilityId abilityId) {
        super(abilityType, abilityId, EmperorBarrage::new);
        setDefaultPhaseLength(ActionPhase.WINDUP,5);
        setDefaultPhaseLength(ActionPhase.PERFORM,10);
        setDefaultPhaseLength(ActionPhase.RECOVERY,2);
    }

    @Override
    public ConditionCheck checkConditions(Power<?> context) {
        LivingEntity user = context.getUser();
        if(user.getItemInHand(InteractionHand.MAIN_HAND).getItem() instanceof EmperorItem emperorItem){
            if(user instanceof Player player && player.getCooldowns().isOnCooldown(emperorItem)){
                return ConditionCheck.NEGATIVE;
            }
            return ConditionCheck.POSITIVE;
        }
        if(user.getItemInHand(InteractionHand.OFF_HAND).getItem() instanceof EmperorItem emperorItem){
            if(user instanceof Player player && player.getCooldowns().isOnCooldown(emperorItem)){
                return ConditionCheck.NEGATIVE;
            }
            return ConditionCheck.POSITIVE;
        }
        return ConditionCheck.NEGATIVE;
    }


    public static class EmperorBarrage extends EntityActionInstance{
        public EmperorBarrage(EntityActionType ability) {
            super(ability);
        }


        @Override
        public void actionTick() {
            if(this.phase == ActionPhase.PERFORM){
                int t = Math.round(this.getPhaseTicksLeft())%13;
                boolean shotTick = t==1|| t==3||t==5|| t==7||t==9 || t==11;
                if(shotTick){
                    if(!this.level().isClientSide){
                        if(performer.getItemInHand(InteractionHand.MAIN_HAND).getItem() instanceof EmperorItem emperorItem){
                            EmperorGunData gunData = performer.getItemInHand(InteractionHand.MAIN_HAND).get(HermitDataComponents.EMPEROR);
                            if(gunData != null){
                                float multStamina = performer.hasEffect(ModStatusEffects.RESOLVE)?.75F:1;
                                EmperorItem.shot(this.level() ,gunData,multStamina,performer);
                                if(performer instanceof Player player){
                                    player.getCooldowns().addCooldown(emperorItem,10);
                                }
                            }
                        } else if(performer.getItemInHand(InteractionHand.OFF_HAND).getItem() instanceof EmperorItem emperorItem) {
                            EmperorGunData gunData = performer.getItemInHand(InteractionHand.OFF_HAND).get(HermitDataComponents.EMPEROR);
                            if(gunData != null){
                                float multStamina = performer.hasEffect(ModStatusEffects.RESOLVE)?.75F:1;
                                EmperorItem.shot(this.level(),gunData,multStamina,performer);
                                if(performer instanceof Player player){
                                    player.getCooldowns().addCooldown(emperorItem,10);
                                }
                            }
                        }
                    }
                }
            }
            if(this.phase == ActionPhase.RECOVERY){
                if(performer instanceof Player player){
                    HermitPurpleAddon.getLogger().debug("IS this happening ?");
                    player.getCooldowns().addCooldown(AddonItems.EMPEROR.asItem(),65);
                }
            }
        }
    }
}
