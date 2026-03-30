package com.zeml.ripplez_hp.jojoimpl.stands.emperor;

import com.github.standobyte.jojo.init.ModStatusEffects;
import com.github.standobyte.jojo.powersystem.Power;
import com.github.standobyte.jojo.powersystem.ability.AbilityId;
import com.github.standobyte.jojo.powersystem.ability.AbilityType;
import com.github.standobyte.jojo.powersystem.ability.EntityActionAbility;
import com.github.standobyte.jojo.powersystem.ability.condition.ConditionCheck;
import com.github.standobyte.jojo.powersystem.ability.controls.InputMethod;
import com.github.standobyte.jojo.powersystem.ability.input.ActionInputBuffer;
import com.github.standobyte.jojo.powersystem.entityaction.ActionAnimIdentifier;
import com.github.standobyte.jojo.powersystem.entityaction.ActionPhase;
import com.github.standobyte.jojo.powersystem.entityaction.EntityActionInstance;
import com.github.standobyte.jojo.powersystem.entityaction.HeldInput;
import com.zeml.ripplez_hp.init.HermitDataComponents;
import com.zeml.ripplez_hp.mc.item.EmperorItem;
import com.zeml.ripplez_hp.mc.item.component.EmperorGunData;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class ShotAbility extends EntityActionAbility {

    public ShotAbility(AbilityType<?> abilityType, AbilityId abilityId) {
        super(abilityType, abilityId);
        setDefaultPhaseLength(ActionPhase.WINDUP,5);
    }

    @Override
    public ConditionCheck checkConditions(Power<?> context) {
        LivingEntity user = context.getUser();
        if(user.getItemInHand(InteractionHand.MAIN_HAND).getItem() instanceof EmperorItem  emperorItem){
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


    @Override
    public HeldInput onKeyPress(Level level, LivingEntity user, FriendlyByteBuf extraClientInput, InputMethod inputMethod, float clickHoldResolveTime, ActionInputBuffer.BufferingState bufferingState) {
        if(user.getItemInHand(InteractionHand.MAIN_HAND).getItem() instanceof EmperorItem emperorItem){
            if(!level.isClientSide){
                EmperorGunData gunData = user.getItemInHand(InteractionHand.MAIN_HAND).get(HermitDataComponents.EMPEROR);
                if(gunData != null){
                    float multStamina = user.hasEffect(ModStatusEffects.RESOLVE)?.75F:1;
                    EmperorItem.shot(level,gunData,multStamina,user);
                    if(user instanceof Player player){
                        player.getCooldowns().addCooldown(emperorItem,10);
                    }
                }
            }
        }else if(user.getItemInHand(InteractionHand.OFF_HAND).getItem() instanceof EmperorItem emperorItem){
            if(!level.isClientSide){
                EmperorGunData gunData = user.getItemInHand(InteractionHand.OFF_HAND).get(HermitDataComponents.EMPEROR);
                if(gunData != null){
                    float multStamina = user.hasEffect(ModStatusEffects.RESOLVE)?.75F:1;
                    EmperorItem.shot(level,gunData,multStamina,user);
                    if(user instanceof Player player){
                        player.getCooldowns().addCooldown(emperorItem,10);
                    }
                }
            }
        }
        return super.onKeyPress(level, user, extraClientInput, inputMethod, clickHoldResolveTime, bufferingState);
    }

    @Override
    public ActionAnimIdentifier getEntityAnim(EntityActionInstance action) {
        if(!isRight(action.getPowerUser())){
            return ActionAnimIdentifier.getOrCreate(abilityId.nameInMoveset().concat("_l"),false);
        }
        return super.getEntityAnim(action);
    }

    public static boolean isRight(LivingEntity user){
        if(user.getItemInHand(InteractionHand.MAIN_HAND).getItem() instanceof EmperorItem){
            return user.getMainArm() == HumanoidArm.RIGHT;
        }
        if(user.getItemInHand(InteractionHand.OFF_HAND).getItem() instanceof EmperorItem){
            return user.getMainArm() == HumanoidArm.LEFT;
        }

        return false;
    }

}
