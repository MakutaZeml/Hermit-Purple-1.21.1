package com.zeml.ripplez_hp.jojoimpl.stands.hermitpurple;

import com.github.standobyte.jojo.powersystem.ability.AbilityId;
import com.github.standobyte.jojo.powersystem.ability.AbilityType;
import com.github.standobyte.jojo.powersystem.ability.EntityActionAbility;
import com.github.standobyte.jojo.powersystem.ability.controls.InputMethod;
import com.github.standobyte.jojo.powersystem.entityaction.HeldInput;
import com.zeml.ripplez_hp.client.ui.screen.HPScreenTargetSelect;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public class OpenTargetAbility extends EntityActionAbility {

    public OpenTargetAbility(AbilityType<?> abilityType, AbilityId abilityId) {
        super(abilityType, abilityId);
    }

    @Override
    public HeldInput onKeyPress(Level level, LivingEntity user, FriendlyByteBuf extraClientInput, InputMethod inputMethod, float clickHoldResolveTime) {
        if(level.isClientSide){
            Minecraft minecraft = Minecraft.getInstance();
            minecraft.setScreen(new HPScreenTargetSelect());
        }
        return super.onKeyPress(level, user, extraClientInput, inputMethod, clickHoldResolveTime);
    }
}
