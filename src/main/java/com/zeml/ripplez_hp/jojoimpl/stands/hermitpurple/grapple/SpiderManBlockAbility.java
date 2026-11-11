package com.zeml.ripplez_hp.jojoimpl.stands.hermitpurple.grapple;

import com.github.standobyte.jojo.powersystem.ability.AbilityId;
import com.github.standobyte.jojo.powersystem.ability.AbilityType;
import com.github.standobyte.jojo.powersystem.entityaction.ActionPhase;
import com.github.standobyte.jojo.powersystem.entityaction.type.EntityActionType;
import com.github.standobyte.jojo.powersystem.standpower.StandPower;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

public class SpiderManBlockAbility extends GrappleBlockAbility {

    public SpiderManBlockAbility(AbilityType<?> abilityType, AbilityId abilityId) {
        super(abilityType, abilityId);
        createActionObj = SpiderMan::new;
    }

    public static class SpiderMan extends GrappleBlockAbility.GrapplingBlock implements SingleGrappleAction{
        private double distance;
        public SpiderMan(EntityActionType ability) {
            super(ability);
        }

        @Override
        public void performStuff() {
            vines = null;
            if(this.endPos != null){
                performer.resetFallDistance();
                if(endPos.distanceTo(performer.position()) > distance){
                    leashStoleCode(performer,endPos,distance);
                    StandPower power = StandPower.get(performer);
                    if(power != null){
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

        private static void leashStoleCode(LivingEntity user, Vec3 pos, double distance){
            double d0 = (pos.x - user.getX()) / distance;
            double d1 = (pos.y - user.getY()) / distance;
            double d2 = (pos.z - user.getZ()) / distance;
            user.setDeltaMovement(user.getDeltaMovement().add(Math.copySign(d0 * d0 * 0.4, d0), Math.copySign(d1 * d1 * 0.4, d1), Math.copySign(d2 * d2 * 0.4, d2)));

        }

        @Override
        public void onSetPhase(ActionPhase newPhase) {
            super.onSetPhase(newPhase);
            if(newPhase == ActionPhase.PERFORM){
                if(endPos != null){
                    this.distance = endPos.distanceTo(performer.position());
                }
            }
        }
    }

}