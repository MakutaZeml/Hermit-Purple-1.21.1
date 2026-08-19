package com.zeml.ripplez_hp.jojoimpl.stands.hermitpurple;

import com.github.standobyte.jojo.entityattachment.custom_effect.EntityCustomEffect;
import com.github.standobyte.jojo.powersystem.Power;
import com.github.standobyte.jojo.powersystem.PowerClass;
import com.github.standobyte.jojo.powersystem.ability.AbilityId;
import com.github.standobyte.jojo.powersystem.ability.AbilityType;
import com.github.standobyte.jojo.powersystem.ability.AbilityUsageGroup;
import com.github.standobyte.jojo.powersystem.ability.condition.ConditionCheck;
import com.github.standobyte.jojo.powersystem.entityaction.ActionPhase;
import com.github.standobyte.jojo.powersystem.entityaction.EntityActionInstance;
import com.github.standobyte.jojo.powersystem.entityaction.type.EntityActionType;
import com.github.standobyte.jojo.powersystem.standpower.StandPower;
import com.zeml.ripplez_hp.core.HermitPurpleAddon;
import com.zeml.ripplez_hp.init.power.AddonStandAbilities;

public class HermitThornsAbility extends HermitAction{


    public HermitThornsAbility(AbilityType<?> abilityType, AbilityId abilityId) {
        super(abilityType, abilityId,Thorning::new);
        usageGroup = AbilityUsageGroup.UTILITY;
        setDefaultPhaseLength(ActionPhase.PERFORM,1);
        setDefaultPhaseLength(ActionPhase.RECOVERY,20);
    }


    @Override
    public ConditionCheck checkSpecificConditions(Power<?> context) {
        StandPower stand = PowerClass.STAND.cast(context);
        if(stand != null && stand.hasPower()){
            if(stand.userStandEffects.getEffectOfType(AddonStandAbilities.HERMIT_THORNS_EFFECT.get()).isPresent()){
                return ConditionCheck.GREEN_HIGHLIGHT;
            }
        }
        return super.checkSpecificConditions(context);
    }



    public static class Thorning extends EntityActionInstance {
        public Thorning(EntityActionType ability) {
            super(ability);
        }

        @Override
        public void actionPerformStart() {
            StandPower standPower = StandPower.get(performer);
            if(standPower != null && standPower.hasPower()){
                boolean active = !standPower.userStandEffects.getEffectsOfType(AddonStandAbilities.HERMIT_THORNS_EFFECT.get()).findAny().isEmpty();
                HermitPurpleAddon.getLogger().debug("sex{}",active);
                if(active){
                    standPower.userStandEffects.getEffectsOfType(AddonStandAbilities.HERMIT_THORNS_EFFECT.get()).forEach(EntityCustomEffect::remove);
                }else {
                    HermitThornsEffect effect = AddonStandAbilities.HERMIT_THORNS_EFFECT.get().create(level());
                    standPower.userStandEffects.addEffect(effect);
                }

            }
        }
    }

}