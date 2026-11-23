package com.zeml.ripplez_hp.jojoimpl.stands.hermitpurple.grapple;

import com.github.standobyte.jojo.entityattachment.custom_effect.EntityCustomEffect;
import com.github.standobyte.jojo.powersystem.Power;
import com.github.standobyte.jojo.powersystem.ability.AbilityId;
import com.github.standobyte.jojo.powersystem.ability.AbilityType;
import com.github.standobyte.jojo.powersystem.ability.controls.InputMethod;
import com.github.standobyte.jojo.powersystem.ability.input.ActionInputBuffer;
import com.github.standobyte.jojo.powersystem.entityaction.HeldInput;
import com.github.standobyte.jojo.powersystem.standpower.StandPower;
import com.zeml.ripplez_hp.core.packets.server.StandSoundPacket;
import com.zeml.ripplez_hp.init.AddonSoundEvents;
import com.zeml.ripplez_hp.init.power.AddonStandAbilities;
import com.zeml.ripplez_hp.jojoimpl.stands.hermitpurple.HermitAction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;

public class HermitUnNetAbility extends HermitAction {

    public HermitUnNetAbility(AbilityType<?> abilityType, AbilityId abilityId) {
        super(abilityType, abilityId);
    }


    @Override
    public HeldInput onKeyPress(Level level, LivingEntity user, FriendlyByteBuf extraClientInput, InputMethod inputMethod, float clickHoldResolveTime, ActionInputBuffer.BufferingState bufferingState) {
        StandPower power = StandPower.get(user);
        if (power != null) {
            power.userStandEffects.getEffectsOfType(AddonStandAbilities.NODES_CONTROL.get()).forEach(EntityCustomEffect::remove);
            if (!level.isClientSide) {
                float pitch = (float) (.5 + Math.random());
                PacketDistributor.sendToPlayersTrackingEntityAndSelf(user, new StandSoundPacket(user.getId(), AddonSoundEvents.HP_UNDO, true, 1, pitch));
            }
        }
        return super.onKeyPress(level, user, extraClientInput, inputMethod, clickHoldResolveTime, bufferingState);
    }


}