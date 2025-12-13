package com.zeml.ripplez_hp.jojoimpl.stands.emperor;

import com.github.standobyte.jojo.powersystem.ability.AbilityId;
import com.github.standobyte.jojo.powersystem.ability.AbilityType;
import com.github.standobyte.jojo.powersystem.ability.EntityActionAbility;
import com.github.standobyte.jojo.powersystem.entityaction.EntityActionInstance;
import com.github.standobyte.jojo.powersystem.entityaction.type.EntityActionType;

import java.util.function.Function;

public class EmperorAbility extends EntityActionAbility {
    public EmperorAbility(AbilityType<?> abilityType, AbilityId abilityId) {
        super(abilityType, abilityId);
    }

    public EmperorAbility(AbilityType<?> abilityType, AbilityId abilityId, Function<EntityActionType, ? extends EntityActionInstance> createActionObj) {
        super(abilityType, abilityId, createActionObj);
    }
}
