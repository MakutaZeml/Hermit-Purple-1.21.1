package com.zeml.ripplez_hp.jojoimpl.stands.emperor;

import com.github.standobyte.jojo.powersystem.Power;
import com.github.standobyte.jojo.powersystem.ability.AbilityId;
import com.github.standobyte.jojo.powersystem.ability.AbilityType;
import com.github.standobyte.jojo.powersystem.ability.EntityActionAbility;
import com.github.standobyte.jojo.powersystem.ability.condition.ConditionCheck;
import com.github.standobyte.jojo.powersystem.ability.controls.InputMethod;
import com.github.standobyte.jojo.powersystem.entityaction.HeldInput;
import com.github.standobyte.jojo.powersystem.standpower.StandPower;
import com.github.standobyte.jojo.subsystems.target.ActionTarget;
import com.github.standobyte.jojo.subsystems.target.HitResultUtil;
import com.zeml.ripplez_hp.init.HermitDataComponents;
import com.zeml.ripplez_hp.mc.item.EmperorItem;
import com.zeml.ripplez_hp.mc.item.component.EmperorGunData;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.Optional;

public class StandTargetAbility extends EntityActionAbility {
    public StandTargetAbility(AbilityType<?> abilityType, AbilityId abilityId) {
        super(abilityType, abilityId);
    }

    @Override
    public ConditionCheck checkConditions(Power<?> context) {
        LivingEntity user = context.getUser();
        if(user.getItemInHand(InteractionHand.MAIN_HAND).getItem() instanceof EmperorItem ||
                user.getItemInHand(InteractionHand.OFF_HAND).getItem() instanceof EmperorItem){
            return ConditionCheck.POSITIVE;
        }
        return ConditionCheck.NEGATIVE;
    }


    @Override
    public HeldInput onKeyPress(Level level, LivingEntity user, FriendlyByteBuf extraClientInput, InputMethod inputMethod, float clickHoldResolveTime) {
        if(!level.isClientSide){
            if(user.getItemInHand(InteractionHand.MAIN_HAND).getItem() instanceof EmperorItem){
                ItemStack itemStack = user.getItemInHand(InteractionHand.MAIN_HAND);
                EmperorGunData data = itemStack.get(HermitDataComponents.EMPEROR);
                if(data != null && StandPower.get(user).hasPower()){
                    StandPower power = StandPower.get(user);
                    if(power != null && power.getStandInstance().isPresent() && power.getStandInstance().get().getStandType() != null &&
                            power.getStandInstance().get().getStandType().getStandStats() != null){
                        double maxRange = power.getStandInstance().get().getStandType().getStandStats().rangeMax();
                        ActionTarget target = HitResultUtil.clip(user.getEyePosition(),user.getLookAngle(),maxRange,maxRange,level, entity -> entity.isAlive(),user,0);
                        if(target.getType() == ActionTarget.TargetType.ENTITY && target.getEntity() instanceof LivingEntity){
                            data.setUUIDTarget(Optional.of(target.getEntity().getUUID()));
                        }
                    }
                }
            }else if(user.getItemInHand(InteractionHand.OFF_HAND).getItem() instanceof EmperorItem){
                ItemStack itemStack = user.getItemInHand(InteractionHand.OFF_HAND);
                EmperorGunData data = itemStack.get(HermitDataComponents.EMPEROR);
                if(data != null && StandPower.get(user).hasPower()){
                    StandPower power = StandPower.get(user);
                    if(power != null && power.getStandInstance().isPresent() && power.getStandInstance().get().getStandType() != null &&
                            power.getStandInstance().get().getStandType().getStandStats() != null){
                        double maxRange = power.getStandInstance().get().getStandType().getStandStats().rangeMax();
                        ActionTarget target = HitResultUtil.clip(user.getEyePosition(),user.getLookAngle(),maxRange,maxRange,level,entity -> entity.isAlive(),user,0);
                        if(target.getType() == ActionTarget.TargetType.ENTITY && target.getEntity() instanceof LivingEntity){
                            data.setUUIDTarget(Optional.of(target.getEntity().getUUID()));
                        }
                    }
                }
            }
        }
        return super.onKeyPress(level, user, extraClientInput, inputMethod, clickHoldResolveTime);
    }
}
