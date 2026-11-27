package com.zeml.ripplez_hp.jojoimpl.stands.hermitpurple.grapple;

import com.github.standobyte.jojo.powersystem.ability.AbilityId;
import com.github.standobyte.jojo.powersystem.ability.AbilityType;
import com.github.standobyte.jojo.powersystem.entityaction.ActionPhase;
import com.github.standobyte.jojo.powersystem.entityaction.type.EntityActionType;
import com.github.standobyte.jojo.powersystem.standpower.StandPower;
import com.github.standobyte.jojo.util.functions.JojoModUtil;
import com.zeml.ripplez_hp.core.HermitPurpleAddon;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

public class SpiderManBlockAbility extends GrappleBlockAbility {

    public SpiderManBlockAbility(AbilityType<?> abilityType, AbilityId abilityId) {
        super(abilityType, abilityId);
        createActionObj = SpiderMan::new;
    }

    public static class SpiderMan extends GrappleBlockAbility.GrapplingBlock implements SingleGrappleAction {
        private double distance;
        private double boost;

        public SpiderMan(EntityActionType ability) {
            super(ability);
        }

        @Override
        public void performStuff() {
            if (this.endPos != null) {
                performer.resetFallDistance();
                if (endPos.distanceTo(performer.position()) - 1.5 > distance) {
                    leashStoleCode(performer, endPos, distance);
                }
                if (endPos.distanceTo(performer.position()) > distance) {
                    allMightyPotentialCode(performer, endPos, boost);
                    boost = Math.max(0,boost-0.2);
                    StandPower power = StandPower.get(performer);
                    if (power != null) {
                        power.consumeStamina(2);
                        power.addExp(.02f);
                    }
                }
            }
        }

        @Override
        public void performThis(double distance, Vec3 end) {
            this.distance = distance;
            super.performThis(distance, end);
        }

        //Thanks Grappling Hook mod
        private static void allMightyPotentialCode(LivingEntity user, Vec3 pos, double boost) {
            Vec3 vine = pos.subtract(user.position());
            Vec3 delta = user.getDeltaMovement();
            if (delta.dot(vine) < 0) {
                Vec3 projection = projection(delta, vine);
                delta = delta.subtract(projection);
                if (!user.level().isClientSide) {
                    user.setDeltaMovement(delta.add(Math.copySign(boost,delta.x),0,Math.copySign(boost,delta.z)));
                    JojoModUtil.forceSyncMotion(user);
                }
            }
        }

        private static Vec3 projection(Vec3 main, Vec3 second) {
            Vec3 normal = second.normalize();
            double d = main.dot(normal);
            return normal.multiply(d, d, d);
        }

        private static void leashStoleCode(LivingEntity user, Vec3 pos, double distance) {
            double d0 = (pos.x - user.getX()) / distance;
            double d1 = (pos.y - user.getY()) / distance;
            double d2 = (pos.z - user.getZ()) / distance;
            if (!user.level().isClientSide) {
                user.setDeltaMovement(user.getDeltaMovement().add(Math.copySign(d0 * d0 * 0.4, d0), Math.copySign(d1 * d1 * 0.4, d1), Math.copySign(d2 * d2 * 0.4, d2)));
                JojoModUtil.forceSyncMotion(user);
            }
        }

        @Override
        public void onSetPhase(ActionPhase newPhase) {
            super.onSetPhase(newPhase);
            if (newPhase == ActionPhase.PERFORM) {
                if (endPos != null) {
                    this.boost =Math.min(.5, performer.getDeltaMovement().multiply(1,performer.onGround()?0:1,1).length());
                    this.distance = endPos.distanceTo(performer.position());
                }
            }
        }
    }

}