package com.zeml.ripplez_hp.jojoimpl.stands.hermitpurple;

import com.github.standobyte.jojo.client.sound.ClientsideSoundsHelper;
import com.github.standobyte.jojo.powersystem.ability.AbilityId;
import com.github.standobyte.jojo.powersystem.ability.AbilityType;
import com.github.standobyte.jojo.powersystem.ability.controls.InputMethod;
import com.github.standobyte.jojo.powersystem.entityaction.HeldInput;
import com.github.standobyte.jojo.powersystem.standpower.entity.StandEntityAbility;
import com.zeml.ripplez_hp.init.AddonSoundEvents;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public class OhNoCringeAbility extends StandEntityAbility {

    public OhNoCringeAbility(AbilityType<?> abilityType, AbilityId abilityId) {
        super(abilityType, abilityId);
    }


    @Override
    public HeldInput onKeyPress(Level level, LivingEntity user, FriendlyByteBuf extraClientInput, InputMethod inputMethod, float clickHoldResolveTime) {
        if(level.isClientSide){
            System.out.println("Cringe");
            ClientsideSoundsHelper.playEntityLingeringSound(user, AddonSoundEvents.OH_NO_CRINGE.get(), user.getSoundSource(),1F,1F,level);
        }
        return super.onKeyPress(level, user, extraClientInput, inputMethod, clickHoldResolveTime);

    }

   
}
