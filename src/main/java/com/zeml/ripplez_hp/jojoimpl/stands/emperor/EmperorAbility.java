package com.zeml.ripplez_hp.jojoimpl.stands.emperor;

import com.github.standobyte.jojo.powersystem.Power;
import com.github.standobyte.jojo.powersystem.ability.AbilityId;
import com.github.standobyte.jojo.powersystem.ability.AbilityType;
import com.github.standobyte.jojo.powersystem.ability.EntityActionAbility;
import com.github.standobyte.jojo.powersystem.ability.condition.ConditionCheck;
import com.github.standobyte.jojo.powersystem.entityaction.ActionAnimIdentifier;
import com.github.standobyte.jojo.powersystem.entityaction.EntityActionInstance;
import com.github.standobyte.jojo.powersystem.entityaction.type.EntityActionType;
import com.zeml.ripplez_hp.mc.item.EmperorItem;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import java.util.function.Function;

import static com.zeml.ripplez_hp.jojoimpl.stands.emperor.ShotAbility.isRight;

public class EmperorAbility extends EntityActionAbility {
    public EmperorAbility(AbilityType<?> abilityType, AbilityId abilityId) {
        super(abilityType, abilityId);
    }

    public EmperorAbility(AbilityType<?> abilityType, AbilityId abilityId, Function<EntityActionType, ? extends EntityActionInstance> createActionObj) {
        super(abilityType, abilityId, createActionObj);
    }

    @Override
    public ConditionCheck checkSpecificConditions(Power<?> context) {
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

    @Override
    public ActionAnimIdentifier getEntityAnim(EntityActionInstance action) {
        if(!isRight(action.getPowerUser())){
            return ActionAnimIdentifier.getOrCreate(abilityId.nameInMoveset().concat("_l"),false);
        }
        return super.getEntityAnim(action);
    }

}
