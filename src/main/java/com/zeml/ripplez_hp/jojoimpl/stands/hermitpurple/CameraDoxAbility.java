package com.zeml.ripplez_hp.jojoimpl.stands.hermitpurple;

import com.github.standobyte.jojo.powersystem.Power;
import com.github.standobyte.jojo.powersystem.PowerClass;
import com.github.standobyte.jojo.powersystem.ability.Ability;
import com.github.standobyte.jojo.powersystem.ability.AbilityId;
import com.github.standobyte.jojo.powersystem.ability.AbilityType;
import com.github.standobyte.jojo.powersystem.ability.condition.AvailableAbilities;
import com.github.standobyte.jojo.powersystem.standpower.StandPower;
import com.zeml.ripplez_hp.init.HermitTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.Nullable;

public class CameraDoxAbility extends HermitAction{

    public CameraDoxAbility(AbilityType<?> abilityType, AbilityId abilityId) {
        super(abilityType, abilityId);
    }


    @Nullable
    @Override
    public Ability replaceWithSubAbility(Power<?> context, AvailableAbilities abilities) {
        StandPower standPower = PowerClass.STAND.cast(context);
        if(context.getUser() != null && context.getUser().getItemInHand(InteractionHand.OFF_HAND).is(HermitTags.Items.CAMERA)
                && standPower != null){
            abilities.replaceOtherAbilityWith(context,"hp_doxx",this);
        }
        return super.replaceWithSubAbility(context, abilities);
    }

}