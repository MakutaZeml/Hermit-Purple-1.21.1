package com.zeml.ripplez_hp.jojoimpl.stands.hermitpurple;

import com.github.standobyte.jojo.powersystem.ability.AbilityId;
import com.github.standobyte.jojo.powersystem.ability.AbilityType;
import com.github.standobyte.jojo.powersystem.ability.AbilityUsageGroup;
import com.github.standobyte.jojo.powersystem.entityaction.ActionOBB;
import com.github.standobyte.jojo.powersystem.entityaction.ActionPhase;
import com.github.standobyte.jojo.powersystem.entityaction.EntityActionInstance;
import com.github.standobyte.jojo.powersystem.entityaction.type.EntityActionType;
import com.github.standobyte.jojo.util.MathUtil;
import com.github.standobyte.jojo.util.hitboxes.ExtendableOBB;
import com.github.standobyte.jojo.util.hitboxes.OBBCollisionUtil;
import com.github.standobyte.jojo.util.hitboxes.OrientedBoundingBox;
import com.zeml.ripplez_hp.core.HermitPurpleAddon;
import com.zeml.ripplez_hp.core.packets.server.StandSoundPacket;
import com.zeml.ripplez_hp.init.AddonSoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;

public class HermitGrabAbility extends HermitAction{

    public HermitGrabAbility(AbilityType<?> abilityType, AbilityId abilityId) {
        super(abilityType, abilityId, HermitGrabbing::new);
        usageGroup = AbilityUsageGroup.COMBAT;
        setDefaultPhaseLength(ActionPhase.WINDUP, 25);
        setDefaultPhaseLength(ActionPhase.PERFORM, 71);
        setDefaultPhaseLength(ActionPhase.RECOVERY, 10);
    }


    public static class HermitGrabbing extends EntityActionInstance implements ActionOBB{
        private ExtendableOBB vines;
        private final Set<Entity> entities = new HashSet<>();
        private Vec3 retainPos;
        public HermitGrabbing(EntityActionType ability) {
            super(ability);
        }
        @Override
        public void onActionSet(@Nullable EntityActionInstance prevAction) {
            float lifespan = phasesLength.getFloat(ActionPhase.PERFORM)+ phasesLength.getFloat(ActionPhase.RECOVERY)+phasesLength.getFloat(ActionPhase.WINDUP)-5;
            OrientedBoundingBox obb = new OrientedBoundingBox(new Vec3(0, 1.35, 0), 0.25d, 0.25d, 1d, getPerformer().getYRot(), getPerformer().getXRot());
            this.vines = new ExtendableOBB(obb, .92F, (int) lifespan, 0, new Vec3(0, 1.35, 0));
        }

        @Override
        public void actionTick(){
            performer.setYBodyRot(performer.yHeadRot);
            if(extendableOBB() != null){
                //Moving the vine
                Vec3 pos = getPerformer().position();
                Vec3 offset = new Vec3(0.0, 1.5, 0)
                        .yRot(-getPerformer().yBodyRot * MathUtil.DEG_TO_RAD);
                this.extendableOBB().updatePosition(level(), pos, offset, getPerformer().getXRot(), getPerformer().getYRot());


                if(getPhase() == ActionPhase.WINDUP){
                    //Throwing Sounds
                    if(curPhaseTick == 5){
                        if(!level().isClientSide){
                            PacketDistributor.sendToPlayersTrackingEntityAndSelf(performer,new StandSoundPacket(performer.getId(), AddonSoundEvents.HP_VINE_THROW,true,1,1));
                        }
                    }
                    if(curPhaseTick>5){
                        if(!entities.isEmpty()){
                            setPhaseStart(ActionPhase.PERFORM);
                            syncPhaseChanges();
                        }
                        Vec3 endPos = this.extendableOBB().rotatableHitbox().center.add(getPerformer().getLookAngle().scale(extendableOBB().rotatableHitbox().extent.length()));

                        HitResult result = level().clip(new ClipContext(extendableOBB().rotatableHitbox().center, endPos, ClipContext.Block.COLLIDER, ClipContext.Fluid.ANY, CollisionContext.empty()));
                        if (result instanceof BlockHitResult blockHitResult){
                            BlockState blockCollision = OBBCollisionUtil.getCollidingBlock(level(), blockHitResult.getBlockPos());
                            if(blockCollision != null){
                                if(!blockCollision.getCollisionShape(level(),blockHitResult.getBlockPos()).isEmpty()){
                                    setPhaseStart(ActionPhase.PERFORM);
                                    syncPhaseChanges();
                                }
                            }
                        }


                    }

                }
                // Catching the entities while extending or retracting
                if(getPhase() == ActionPhase.WINDUP || getPhase() == ActionPhase.PERFORM){
                    if (!level().isClientSide()){
                        Vec3 endPos = this.extendableOBB().rotatableHitbox().center.add(getPerformer().getLookAngle().scale(extendableOBB().rotatableHitbox().extent.length()));
                        OBBCollisionUtil.getEntitiesInOBB(level(), this.extendableOBB().rotatableHitbox(),
                                entity -> entity != getPerformer() && entity != getPowerUser() &&
                                        entity.distanceToSqr(endPos.x,entity.getY(),endPos.z)<entity.getBbWidth()*1.1
                        ).forEach(entity -> {
                            //FIXME Sound when catch
                            if(!entities.contains(entity)){
                                PacketDistributor.sendToPlayersTrackingEntityAndSelf(performer,new StandSoundPacket(entity.getId(),AddonSoundEvents.HP_VINE_CATCH,true,1,1));
                            }
                            entity.setNoGravity(true);
                            entities.add(entity);
                        });
                    }
                }

                //Move Entities
                if(!entities.isEmpty()){
                    if(!level().isClientSide){
                        Vec3 endPos = this.extendableOBB().rotatableHitbox().center.add(getPerformer().getLookAngle().scale(extendableOBB().rotatableHitbox().extent.length()));
                        if(!entities.isEmpty()){
                            entities.forEach(entity -> {
                                //Maker sure the entities don't get to close
                                retainPos = this.extendableOBB().rotatableHitbox().center.add(performer.getLookAngle().scale(2.3));
                                HermitPurpleAddon.LOGGER.debug("Is this true chat? {} {} {}",phase == ActionPhase.WINDUP,(phase == ActionPhase.PERFORM && this.extendableOBB().rotatableHitbox().extent.length() < 2.3), this.extendableOBB().rotatableHitbox().extent.length());
                                if(phase == ActionPhase.WINDUP || (phase == ActionPhase.PERFORM && this.extendableOBB().rotatableHitbox().extent.length() > 2.3)
                                ){
                                    retainPos = endPos;
                                }
                                entity.teleportTo(retainPos.x,retainPos.y-1.35,retainPos.z);
                                HermitPurpleAddon.LOGGER.debug("distance {} {} {} {} {}", entity, Math.sqrt(entity.distanceToSqr(performer.getX(),entity.getY(),performer.getZ())),curPhaseTick, phase, retainPos);
                            });
                            //Finish when it doesn't have entities
                        }else if(phase == ActionPhase.PERFORM && this.extendableOBB().rotatableHitbox().extent.length() < 2.3){
                            setPhaseStart(ActionPhase.RECOVERY);
                            syncPhaseChanges();
                        }

                    }
                } 


                //Tick
                this.extendableOBB().tick();
            }

            //Leave them all
            if(getPhase() == ActionPhase.RECOVERY && getPhaseTicksLeft()<=1){
                HermitPurpleAddon.getLogger().debug("tru end");
            }
        }

        @Override
        public void actionPerformEnd() {
            if(!level().isClientSide){
                entities.forEach(entity -> entity.setNoGravity(false));

            }
            HermitPurpleAddon.getLogger().debug("end");
            entities.clear();
            vines = null;
        }

        @Override
        public void onButtonStopHold() {
            if(!level().isClientSide){
                entities.forEach(entity -> entity.setNoGravity(false));
            }
            setPhaseStart(ActionPhase.RECOVERY);
            syncPhaseChanges();
        }

        @Override
        public void actionPerformStart() {
            if (!level().isClientSide() && this.extendableOBB() != null){
                this.extendableOBB().forceRetract(level(), getPerformer(), this.id);
            }
        }

        @Override
        public ExtendableOBB extendableOBB() {
            return vines;
        }
    }
}
