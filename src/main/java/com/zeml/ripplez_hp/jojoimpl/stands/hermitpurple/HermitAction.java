package com.zeml.ripplez_hp.jojoimpl.stands.hermitpurple;

import com.github.standobyte.jojo.powersystem.ability.AbilityId;
import com.github.standobyte.jojo.powersystem.ability.AbilityType;
import com.github.standobyte.jojo.powersystem.ability.EntityActionAbility;
import com.github.standobyte.jojo.powersystem.entityaction.EntityActionInstance;
import com.github.standobyte.jojo.powersystem.entityaction.type.EntityActionType;
import com.github.standobyte.jojo.powersystem.playerpower.PlayerPower;
import com.github.standobyte.jojo.powersystem.standpower.StandPower;
import com.github.standobyte.jojo.powersystem.standpower.entity.StandEntityAbility;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

import java.util.function.Function;

public class HermitAction extends EntityActionAbility {
    public boolean noFinisherBarDecay = false;
    public HermitAction(AbilityType<?> abilityType, AbilityId abilityId) {
        super(abilityType, abilityId);
    }

    public HermitAction (AbilityType<?> abilityType, AbilityId abilityId,
                         Function<EntityActionType, ? extends EntityActionInstance> createActionObj){
        super(abilityType, abilityId, createActionObj);

    }


}
