package com.zeml.ripplez_hp.jojoimpl.stands.hermitpurple.grapple;

import com.github.standobyte.jojo.client.sound.ClientsideSoundsHelper;
import com.github.standobyte.jojo.customobjects.entity_projectile.ThrownBlockEntity;
import com.github.standobyte.jojo.powersystem.ability.AbilityId;
import com.github.standobyte.jojo.powersystem.ability.AbilityType;
import com.github.standobyte.jojo.powersystem.ability.AbilityUsageGroup;
import com.github.standobyte.jojo.powersystem.entityaction.ActionPhase;
import com.github.standobyte.jojo.powersystem.entityaction.EntityActionInstance;
import com.github.standobyte.jojo.powersystem.entityaction.type.EntityActionType;
import com.github.standobyte.jojo.powersystem.standpower.StandPower;
import com.github.standobyte.jojo.powersystem.standpower.entity.StandEntity;
import com.github.standobyte.jojo.powersystem.standpower.entity.StandStatFormulas;
import com.github.standobyte.jojo.subsystems.hitboxes.ExtendableOBB;
import com.github.standobyte.jojo.subsystems.hitboxes.OBBCollisionUtil;
import com.github.standobyte.jojo.subsystems.hitboxes.OrientedBoundingBox;
import com.github.standobyte.jojo.util.functions.JojoModUtil;
import com.github.standobyte.jojo.util.functions.MathUtil;
import com.zeml.ripplez_hp.core.HermitPurpleAddon;
import com.zeml.ripplez_hp.core.packets.server.StandSoundPacket;
import com.zeml.ripplez_hp.init.AddonSoundEvents;
import com.zeml.ripplez_hp.jojoimpl.stands.hermitpurple.HermitAction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.entity.JukeboxBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

public class GrappleEntityAbility extends HermitAction {

    public GrappleEntityAbility(AbilityType<?> abilityType, AbilityId abilityId) {
        super(abilityType, abilityId,GrappleEntity::new);
        setDefaultPhaseLength(ActionPhase.BUTTON_CHARGE,10);
        setDefaultPhaseLength(ActionPhase.WINDUP,40);
        setDefaultPhaseLength(ActionPhase.PERFORM,100);
        setDefaultPhaseLength(ActionPhase.RECOVERY,10);
        usageGroup = AbilityUsageGroup.COMBAT;
    }

    /*
    * BUTTON_CHARGE: COME OUT (NOT AS GAY) A
    * WIND UP: EXTEND AND RETRACT
    * PERFORM: RETAIN ENTITY AT 3 BLOCKS
    * RECOVERY: RECOVERY
    * */

    public static class GrappleEntity extends EntityActionInstance implements SingleGrappleAction {
        private ExtendableOBB vines;
        private Vec3 endPos;
        private Entity grapple;
        public GrappleEntity(EntityActionType ability) {
            super(ability);
        }

        @Override
        public void onActionSet(@Nullable EntityActionInstance prevAction) {
            float lifespan =phasesLength.getFloat(ActionPhase.WINDUP);
            OrientedBoundingBox obb = new OrientedBoundingBox(new Vec3(0, 1.35, 0), 0.25d, 0.25d, 1d, getPerformer().getYRot(), getPerformer().getXRot());
            this.vines = new ExtendableOBB(obb, 1F, (int) lifespan, (int) (phasesLength.getFloat(ActionPhase.WINDUP)/2), new Vec3(0, 1.35, 0));
        }

        @Override
        public void actionTick() {
            Vec3 pos = getPerformer().position();
            Vec3 offset = new Vec3(0.07, 1.5, 1)
                    .yRot(-getPerformer().yBodyRot * MathUtil.DEG_TO_RAD);
            switch (getPhase()){
                case WINDUP -> {
                    if(vines != null){
                        this.vines.updatePosition(level(), pos, offset, getPerformer().getXRot(), getPerformer().getYRot());
                        double factor = vines.rotatableHitbox().extent.length();
                        if(this.vines.isRetracting()){
                            factor = Math.max(vines.rotatableHitbox().extent.length(),3);
                        }
                        endPos = this.vines.rotatableHitbox().center.add(getPerformer().getLookAngle().scale(factor));
                        OBBCollisionUtil.getEntitiesInOBB(level(), this.vines.rotatableHitbox(), entity -> entity != getPerformer() && entity != getPowerUser()).forEach(entity -> {
                            grapple = entity;
                        });
                        BlockHitResult result = level().clip(new ClipContext(vines.rotatableHitbox().center, endPos, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, CollisionContext.empty()));
                        BlockState blockCollision = OBBCollisionUtil.getCollidingBlock(level(), result.getBlockPos());
                        if (blockCollision != null){
                            StandPower power = StandPower.get(performer);
                            if(power != null && power.getCurTypeData() != null && power.getCurTypeData().isSkillUnlocked("block_toss") && grapple == null){
                               /*
                               //TODO Implement when isn't broken
                                ThrownBlockEntity thrownBlockEntity = new ThrownBlockEntity(performer,level(),blockCollision,result.getBlockPos());
                                level().addFreshEntity(thrownBlockEntity);
                                level().destroyBlock(result.getBlockPos(),false);
                                grapple = thrownBlockEntity;*/
                            }
                            this.vines.forceRetract(level(),performer,this.id);
                        }
                        if(grapple != null){
                            this.vines.forceRetract(level(),performer,this.id);
                            if(grapple instanceof Mob psycho){
                                psycho.getNavigation().stop();
                            }
                            Vec3 vecTo = endPos.subtract(grapple.position().add(0,1.5,0));
                            dragTarget(grapple,vecTo.normalize());
                        }
                    }
                    if(curPhaseTick > phasesLength.getFloat(ActionPhase.WINDUP)/2){
                        vines.forceRetract(level(),performer,id);
                    }
                    this.vines.tick();
                }
                case PERFORM -> {
                    if(grapple == null){
                        setPhase(ActionPhase.RECOVERY, 0);
                        syncPhaseChanges();
                    }else {
                        grapple.resetFallDistance();
                        this.vines.updatePosition(level(), pos, offset, getPerformer().getXRot(), getPerformer().getYRot());
                        endPos = this.vines.rotatableHitbox().center.add(getPerformer().getLookAngle().scale(3));
                        Vec3 vecTo = endPos.subtract(grapple.position().add(0,1.5,0));
                        if(vecTo.length() > 1){
                            dragTarget(grapple,vecTo.normalize());
                        }else {
                            dragTarget(grapple,Vec3.ZERO);
                        }
                        StandPower standPower = StandPower.get(performer);
                        if(standPower != null){
                            standPower.addExp(.03f);
                            standPower.consumeStamina(2);
                        }
                    }
                }
            }
        }

        @Override
        public void onSetPhase(ActionPhase newPhase) {
            switch (newPhase){
                case PERFORM -> {

                }
                case WINDUP -> {
                    if(!level().isClientSide){
                        StandPower power = StandPower.get(performer);
                        if (power != null && !level().isClientSide) {
                            PacketDistributor.sendToPlayersTrackingEntityAndSelf(performer, new StandSoundPacket(performer.getId(), AddonSoundEvents.HP_VINE_THROW, true, 1, 1));
                        }
                    }
                }
                case RECOVERY -> {
                    grapple = null;
                    endPos = null;
                }
            }
        }

        @Override
        public void onButtonStopHold() {
            if(getPhase() != ActionPhase.RECOVERY){
                setPhase(ActionPhase.RECOVERY,0);
                syncPhaseChanges();
            }
        }

        @Override
        public @Nullable Vec3 getEndPos() {
            if(grapple != null){
                return grapple.position().add(0,grapple.getBbHeight()/2,0);
            }
            return endPos;
        }

        protected void dragTarget(Entity entity, Vec3 vec) {
            entity = entity.getRootVehicle();
            doDragEntity(entity, vec);
            if (entity instanceof StandEntity) {
                LivingEntity standUser = ((StandEntity) entity).getUser();
                if (standUser != null) {
                    doDragEntity(entity, vec);
                }
            }
        }

        private void doDragEntity(Entity entity, Vec3 vec) {
            if(entity != null){
                entity.setDeltaMovement(vec);
                if(!entity.level().isClientSide) JojoModUtil.forceSyncMotion(entity);
            }

        }
    }

}