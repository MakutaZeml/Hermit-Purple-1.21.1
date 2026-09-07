package com.zeml.ripplez_hp.jojoimpl.stands.hermitpurple;

import com.github.standobyte.jojo.customobjects.DamageSourceModified;
import com.github.standobyte.jojo.entityattachment.custom_effect.EntityCustomEffect;
import com.github.standobyte.jojo.entityattachment.custom_effect.EntityCustomEffectType;
import com.github.standobyte.jojo.init.ModDamageTypes;
import com.github.standobyte.jojo.powersystem.Power;
import com.github.standobyte.jojo.powersystem.PowerClass;
import com.github.standobyte.jojo.powersystem.ability.AbilityId;
import com.github.standobyte.jojo.powersystem.ability.AbilityType;
import com.github.standobyte.jojo.powersystem.ability.AbilityUsageGroup;
import com.github.standobyte.jojo.powersystem.ability.condition.ConditionCheck;
import com.github.standobyte.jojo.powersystem.ability.controls.InputMethod;
import com.github.standobyte.jojo.powersystem.ability.input.ActionInputBuffer;
import com.github.standobyte.jojo.powersystem.entityaction.ActionPhase;
import com.github.standobyte.jojo.powersystem.entityaction.EntityActionInstance;
import com.github.standobyte.jojo.powersystem.entityaction.HeldInput;
import com.github.standobyte.jojo.powersystem.entityaction.type.EntityActionType;
import com.github.standobyte.jojo.powersystem.standpower.StandPower;
import com.github.standobyte.jojo.powersystem.standpower.effect.StandEffectInstance;
import com.github.standobyte.jojo.powersystem.standpower.entity.StandStatFormulas;
import com.github.standobyte.jojo.util.functions.DamageUtil;
import com.zeml.ripplez_hp.core.HermitPurpleAddon;
import com.zeml.ripplez_hp.init.power.AddonStandAbilities;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.damagesource.DamageContainer;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import org.apache.commons.lang3.mutable.MutableInt;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@EventBusSubscriber(modid = HermitPurpleAddon.MOD_ID)
public class HermitThornsAbility extends HermitAction{


    public HermitThornsAbility(AbilityType<?> abilityType, AbilityId abilityId) {
        super(abilityType, abilityId,Thorning::new);
        usageGroup = AbilityUsageGroup.COMBAT;
        setDefaultPhaseLength(ActionPhase.PERFORM,1);
        setDefaultPhaseLength(ActionPhase.RECOVERY,10);

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


    @Override
    public HeldInput onKeyPress(Level level, LivingEntity user, FriendlyByteBuf extraClientInput, InputMethod inputMethod, float clickHoldResolveTime, ActionInputBuffer.BufferingState bufferingState) {
        StandPower standPower = StandPower.get(user);
        if(standPower != null && standPower.hasPower()){
            boolean active = !standPower.userStandEffects.getEffectsOfType(AddonStandAbilities.HERMIT_THORNS_EFFECT.get()).findAny().isEmpty();
            if(active){
                standPower.userStandEffects.getEffectsOfType(AddonStandAbilities.HERMIT_THORNS_EFFECT.get()).forEach(EntityCustomEffect::remove);
            }else {
                if(standPower.getPowerType() != null) standPower.getPowerType().summon(user,standPower);
                HermitThornsEffect effect = AddonStandAbilities.HERMIT_THORNS_EFFECT.get().create(level);
                standPower.userStandEffects.addEffect(effect);
            }

        }
        return super.onKeyPress(level, user, extraClientInput, inputMethod, clickHoldResolveTime, bufferingState);
    }

    public static class Thorning extends EntityActionInstance {
        public Thorning(EntityActionType ability) {
            super(ability);
        }

        @Override
        public void actionPerformStart() {

        }
    }

    public static class HermitThornsEffect  extends StandEffectInstance {
        protected final Map<UUID, MutableInt> blockedEntitiesTimers = new HashMap<>();
        public HermitThornsEffect(@NotNull EntityCustomEffectType<?> effectType) {
            super(effectType);
        }

        @Override
        protected void start() {
        }

        @Override
        protected void tick() {
            if(!level.isClientSide){
                blockedEntitiesTimers.entrySet().removeIf(entityTimerEntry -> entityTimerEntry.getValue().decrementAndGet() <= 0);
            }
            if(userPower != null){
                if(!userPower.hasPower() || !userPower.isSummoned()){
                    this.remove();
                }
            }
        }

        @Override
        protected void stop() {

        }


    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void onStartedStandTakesDamage(LivingIncomingDamageEvent event){
        LivingEntity user = event.getEntity();
        DamageContainer dmg = event.getContainer();
        DamageSource dmgSource = dmg.getSource();
        Entity attacker = event.getSource().getDirectEntity();
        if(dmgSource.isDirect() && ( user!= attacker) && attacker != null && dmgSource.is(Tags.DamageTypes.IS_PHYSICAL)){
            StandPower power = StandPower.get(user);
            if(power != null){
                power.userStandEffects.getEffectOfType(AddonStandAbilities.HERMIT_THORNS_EFFECT.get()).ifPresent(effect->{
                    DamageSourceModified _dmgSource = (DamageSourceModified) dmgSource;
                    if (!dmgSource.is(DamageTypeTags.BYPASSES_COOLDOWN)) {
                        @Nullable UUID attackerId = attacker.getUUID();
                        if ((float) user.invulnerableTime > 10.0F || effect.blockedEntitiesTimers.containsKey(attackerId)) {
                            event.setCanceled(true);
                            return;
                        } else {
                            effect.blockedEntitiesTimers.put(attackerId, new MutableInt(10));
                        }
                    }
                    float reduced = reducedAmount(event.getOriginalAmount(), power);
                    var damageType = DamageUtil.type(user.level(), ModDamageTypes.STAND_ATTACK);
                    DamageSource newDmgSource = new DamageSource(damageType, user);
                    attacker.hurt(newDmgSource,1);
                    //TODO implement Hamon when it's done
                    HermitPurpleAddon.getLogger().debug("sex? {} {}", reduced, attacker);
                    if (reduced< 1) {
                        if (reduced == 0) {
                            event.setCanceled(true);

                        }
                        else {
                            float dmgAmount = dmg.getNewDamage();
                            float newDmg = dmgAmount * reduced;
                            event.setAmount(newDmg);
                        }
                    }

                });
            }
        }
    }


    protected static float reducedAmount(float origDmgAmount, StandPower userPower){
        float blockedRatio = 1;
        if (userPower != null && userPower.usesStamina()) {
            float staminaCost = StandStatFormulas.getGuardStaminaCost(origDmgAmount);
            float stamina = userPower.getStamina();

            if (!userPower.consumeStamina(staminaCost)) {
                blockedRatio *= stamina / staminaCost;
            }
        }
        float dmgCoeff;
        dmgCoeff = 1 - 0.4f * blockedRatio;
        double furtherReductionCap = userPower.getPowerType().getStandStats().durability()/ 2;
        if (origDmgAmount < furtherReductionCap) {
            dmgCoeff *= (float) (origDmgAmount / furtherReductionCap);
        }
        return dmgCoeff;
    }


}