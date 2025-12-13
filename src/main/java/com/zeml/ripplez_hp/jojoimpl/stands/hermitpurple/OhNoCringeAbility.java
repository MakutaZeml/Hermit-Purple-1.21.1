package com.zeml.ripplez_hp.jojoimpl.stands.hermitpurple;

import com.github.standobyte.jojo.client.sound.ClientsideSoundsHelper;
import com.github.standobyte.jojo.client.sound.sounds.EntityLingeringSoundInstance;
import com.github.standobyte.jojo.powersystem.ability.AbilityId;
import com.github.standobyte.jojo.powersystem.ability.AbilityType;
import com.github.standobyte.jojo.powersystem.ability.AbilityUsageGroup;
import com.github.standobyte.jojo.powersystem.ability.EntityActionAbility;
import com.github.standobyte.jojo.powersystem.ability.controls.InputMethod;
import com.github.standobyte.jojo.powersystem.entityaction.ActionPhase;
import com.github.standobyte.jojo.powersystem.entityaction.HeldInput;
import com.github.standobyte.jojo.powersystem.standpower.StandPower;
import com.github.standobyte.jojo.powersystem.standpower.entity.StandEntityAbility;
import com.zeml.ripplez_hp.core.HermitPurpleAddon;
import com.zeml.ripplez_hp.init.AddonSoundEvents;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public class OhNoCringeAbility extends HermitAction {

    public OhNoCringeAbility(AbilityType<?> abilityType, AbilityId abilityId) {
        super(abilityType, abilityId);
        setDefaultPhaseLength(ActionPhase.WINDUP,20);
        usageGroup = AbilityUsageGroup.SPECIAL;

    }


    @Override
    public HeldInput onKeyPress(Level level, LivingEntity user, FriendlyByteBuf extraClientInput, InputMethod inputMethod, float clickHoldResolveTime) {
        if(level.isClientSide){
            HermitPurpleAddon.getLogger().debug("Sex");
            /*
            EntityLingeringSoundInstance sound = new EntityLingeringSoundInstance(ClientsideSoundsHelper
                    .withStandSkin(AddonSoundEvents.OH_NO_CRINGE.get(), StandPower.get(user)),user.getSoundSource(),1,1f,user,level);
            ClientsideSoundsHelper.playNonVanillaClassSound(sound);

             */

        }
        return super.onKeyPress(level, user, extraClientInput, inputMethod, clickHoldResolveTime);

    }

   
}
