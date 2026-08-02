package com.zeml.ripplez_hp.jojoimpl.stands.emperor;

import com.github.standobyte.jojo.powersystem.Power;
import com.github.standobyte.jojo.powersystem.PowerClass;
import com.github.standobyte.jojo.powersystem.ability.Ability;
import com.github.standobyte.jojo.powersystem.ability.AbilityId;
import com.github.standobyte.jojo.powersystem.ability.AbilityType;
import com.github.standobyte.jojo.powersystem.ability.EntityActionAbility;
import com.github.standobyte.jojo.powersystem.ability.condition.AvailableAbilities;
import com.github.standobyte.jojo.powersystem.ability.condition.ConditionCheck;
import com.github.standobyte.jojo.powersystem.entityaction.ActionAnimIdentifier;
import com.github.standobyte.jojo.powersystem.entityaction.ActionPhase;
import com.github.standobyte.jojo.powersystem.entityaction.EntityActionInstance;
import com.github.standobyte.jojo.powersystem.entityaction.type.EntityActionType;
import com.github.standobyte.jojo.powersystem.standpower.StandPower;
import com.zeml.ripplez_hp.core.packets.server.AnotherGuidedPacket;
import com.zeml.ripplez_hp.core.packets.server.StandSoundPacket;
import com.zeml.ripplez_hp.init.AddonDataAttachmentTypes;
import com.zeml.ripplez_hp.init.AddonSoundEvents;
import com.zeml.ripplez_hp.init.power.AddonStandAbilities;
import com.zeml.ripplez_hp.jojoimpl.stands.emperor.entity.BulletPilot;
import com.zeml.ripplez_hp.jojoimpl.stands.emperor.entity.EmperorBulletEntity;
import com.zeml.ripplez_hp.mc.item.EmperorItem;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

public class GuideBulletAbility extends EntityActionAbility {

    public GuideBulletAbility(AbilityType<?> abilityType, AbilityId abilityId) {
        super(abilityType, abilityId, Guided::new);
        setDefaultPhaseLength(ActionPhase.WINDUP,5);
    }

    @Nullable
    @Override
    public Ability replaceWithSubAbility(Power<?> context, AvailableAbilities abilities) {
        StandPower standPower = PowerClass.STAND.cast(context);
        if(context.getUser() != null && context.getUser().getData(AddonDataAttachmentTypes.GUIDED)
                && standPower != null){
            if(standPower.userStandEffects.getEffectsOfType(AddonStandAbilities.GUIDED_BULLET.get()).findAny().isEmpty()){
                abilities.replaceOtherAbilityWith(context,"emp_shot",this);
            }
        }
        return super.replaceWithSubAbility(context, abilities);
    }

    @Override
    public ConditionCheck checkSpecificConditions(Power<?> context) {
        LivingEntity user = context.getUser();
        if(user.getItemInHand(InteractionHand.MAIN_HAND).getItem() instanceof EmperorItem emperorItem){
            if(user instanceof Player player && player.getCooldowns().isOnCooldown(emperorItem)){
                return ConditionCheck.NEGATIVE;
            }
            return ConditionCheck.POSITIVE;
        }
        if(user.getItemInHand(InteractionHand.OFF_HAND).getItem() instanceof EmperorItem emperorItem){
            if(user instanceof Player player && player.getCooldowns().isOnCooldown(emperorItem)){
                return ConditionCheck.NEGATIVE;
            }
            return ConditionCheck.POSITIVE;
        }
        return ConditionCheck.NEGATIVE;
    }


    public static class Guided extends EntityActionInstance {
        public Guided(EntityActionType ability) {
            super(ability);
        }

        @Override
        public void actionPerformStart() {
            StandPower power = StandPower.get(performer);
            if(power != null && power.hasPower() && power.getStandInstance().get().getStandType() != null &&
                    power.getStandInstance().get().getStandType().getStandStats() != null){
                EmperorBulletEntity emperorBullet =  new EmperorBulletEntity(performer,level());
                emperorBullet.setStandPower((float) power.getStandInstance().get().getStandType().getStandStats().power());
                emperorBullet.setStandRange((float) power.getStandInstance().get().getStandType().getStandStats().rangeMax());
                emperorBullet.shootFromRotation(performer,1F,1F);
                level().addFreshEntity(emperorBullet);
                if(!level().isClientSide){
                    PacketDistributor.sendToPlayersTrackingEntityAndSelf(performer, new StandSoundPacket(performer.getId(), AddonSoundEvents.EMP_SHOT,true,1,1));
                }
                BulletPilot bulletPilot = new BulletPilot(level(),performer);
                bulletPilot.startRiding(emperorBullet);
                bulletPilot.copyPosition(emperorBullet);
                level().addFreshEntity(bulletPilot);
                GuidedBulletEffect effect = AddonStandAbilities.GUIDED_BULLET.get().create(level());
                if(!level().isClientSide){
                    power.userStandEffects.addEffect(effect.withTarget(bulletPilot));
                    if(performer instanceof ServerPlayer player){
                        PacketDistributor.sendToPlayer(player,new AnotherGuidedPacket(player.getId(), bulletPilot.getId()));
                    }
                }
            }

        }
    }

    @Override
    public ActionAnimIdentifier getEntityAnim(EntityActionInstance action) {
        if(!isRight(action.getPowerUser())){
            return ActionAnimIdentifier.getOrCreate(abilityId.nameInMoveset().concat("_l"),false);
        }
        return super.getEntityAnim(action);
    }


    public static boolean isRight(LivingEntity user){
        if(user.getItemInHand(InteractionHand.MAIN_HAND).getItem() instanceof EmperorItem){
            return user.getMainArm() == HumanoidArm.RIGHT;
        }
        if(user.getItemInHand(InteractionHand.OFF_HAND).getItem() instanceof EmperorItem){
            return user.getMainArm() == HumanoidArm.LEFT;
        }

        return false;
    }
}
