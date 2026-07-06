package com.zeml.ripplez_hp.jojoimpl.stands.emperor;

import com.github.standobyte.jojo.powersystem.Power;
import com.github.standobyte.jojo.powersystem.ability.AbilityId;
import com.github.standobyte.jojo.powersystem.ability.AbilityType;
import com.github.standobyte.jojo.powersystem.ability.EntityActionAbility;
import com.github.standobyte.jojo.powersystem.ability.condition.ConditionCheck;
import com.github.standobyte.jojo.powersystem.entityaction.ActionAnimIdentifier;
import com.github.standobyte.jojo.powersystem.entityaction.ActionPhase;
import com.github.standobyte.jojo.powersystem.entityaction.EntityActionInstance;
import com.github.standobyte.jojo.powersystem.entityaction.type.EntityActionType;
import com.github.standobyte.jojo.powersystem.standpower.StandPower;
import com.zeml.ripplez_hp.core.packets.server.StandSoundPacket;
import com.zeml.ripplez_hp.init.AddonSoundEvents;
import com.zeml.ripplez_hp.init.power.AddonStandAbilities;
import com.zeml.ripplez_hp.jojoimpl.stands.emperor.entity.BulletPilot;
import com.zeml.ripplez_hp.jojoimpl.stands.emperor.entity.EmperorBulletEntity;
import com.zeml.ripplez_hp.mc.item.EmperorItem;
import com.zeml.ripplez_hp.mc.item.component.EmperorGunData;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.List;
import java.util.Optional;

public class TestingAbility extends EntityActionAbility {

    public TestingAbility(AbilityType<?> abilityType, AbilityId abilityId) {
        super(abilityType, abilityId, Test::new);
        setDefaultPhaseLength(ActionPhase.WINDUP,5);
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


    public static class Test extends EntityActionInstance {
        public Test(EntityActionType ability) {
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
                power.userStandEffects.addEffect(effect.withTarget(bulletPilot));
            }


            /*
            if(performer != null){
                List<Entity> list = level().getEntities(performer,performer.getBoundingBox().inflate(10),entity -> entity.isAlive() && entity != performer);
                Entity entity = list.getFirst();
                if(entity instanceof LivingEntity living){
                    GuidedBulletEffect effect = AddonStandAbilities.GUIDED_BULLET.get().create(level());
                    StandPower standPower = StandPower.get(performer);
                    if(standPower != null){
                        standPower.userStandEffects.addEffect(effect.withTarget(living));
                    }
                }
            }*/
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
