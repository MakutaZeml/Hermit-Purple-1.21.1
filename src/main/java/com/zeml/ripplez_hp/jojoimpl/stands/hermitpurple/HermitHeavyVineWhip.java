package com.zeml.ripplez_hp.jojoimpl.stands.hermitpurple;

import com.github.standobyte.jojo.init.ModDamageTypes;
import com.github.standobyte.jojo.powersystem.ability.AbilityId;
import com.github.standobyte.jojo.powersystem.ability.AbilityType;
import com.github.standobyte.jojo.powersystem.ability.AbilityUsageGroup;
import com.github.standobyte.jojo.powersystem.entityaction.ActionOBB;
import com.github.standobyte.jojo.powersystem.entityaction.ActionPhase;
import com.github.standobyte.jojo.powersystem.entityaction.EntityActionInstance;
import com.github.standobyte.jojo.powersystem.entityaction.type.EntityActionType;
import com.github.standobyte.jojo.powersystem.standpower.StandPower;
import com.github.standobyte.jojo.powersystem.standpower.entity.StandOffsetFromUser;
import com.github.standobyte.jojo.powersystem.standpower.entity.StandStatFormulas;
import com.github.standobyte.jojo.util.MathUtil;
import com.github.standobyte.jojo.util.damage.DamageUtil;
import com.github.standobyte.jojo.util.damage.RipplesModifiedDamageSource;
import com.github.standobyte.jojo.util.hitboxes.ExtendableOBB;
import com.github.standobyte.jojo.util.hitboxes.OBBCollisionUtil;
import com.github.standobyte.jojo.util.hitboxes.OrientedBoundingBox;
import com.zeml.ripplez_hp.core.HermitPurpleAddon;
import com.zeml.ripplez_hp.core.packets.server.StandSoundPacket;
import com.zeml.ripplez_hp.init.AddonSoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class HermitHeavyVineWhip extends HermitAction{


    public HermitHeavyVineWhip(AbilityType<?> abilityType, AbilityId abilityId) {
        super(abilityType, abilityId, HermitHeavyVine::new);
        usageGroup = AbilityUsageGroup.COMBAT;
        setDefaultPhaseLength(ActionPhase.WINDUP, StandStatFormulas.getHeavyAttackWindup(10, 0)*2/3);
        setDefaultPhaseLength(ActionPhase.PERFORM, 60);
        setDefaultPhaseLength(ActionPhase.RECOVERY, 10);

    }


    public static class HermitHeavyVine extends EntityActionInstance implements ActionOBB {

        public HermitHeavyVine(EntityActionType ability) {
            super(ability);
        }

        private ExtendableOBB vines;
        private Set<Entity> entities = new HashSet<>();
        private final Map<Entity,Float> entitesD = new HashMap<>();

        @Override
        public void onActionSet(@Nullable EntityActionInstance prevAction) {
            setStandOffset(0, 1.5, StandOffsetFromUser.Rotations.HEAD_XY, true);
            OrientedBoundingBox obb = new OrientedBoundingBox(new Vec3(0, 1.35, 0), 1d, 1d, 1d, getPerformer().getYRot(), getPerformer().getXRot());
            this.vines = new ExtendableOBB(obb, .8F, phasesLength.get(ActionPhase.PERFORM).intValue()+phasesLength.get(ActionPhase.RECOVERY).intValue(),
                    phasesLength.get(ActionPhase.PERFORM).intValue()+phasesLength.get(ActionPhase.RECOVERY).intValue()-5,
                    new Vec3(0, 1.35, 0));
        }

        @Override
        public void actionPerformStart() {
            LivingEntity user = getPowerUser();
            StandPower standPower = StandPower.get(user);
            standPower.consumeStamina(25);
            if(!level().isClientSide){
                PacketDistributor.sendToPlayersTrackingEntityAndSelf(performer,new StandSoundPacket(performer.getId(), AddonSoundEvents.HP_VINE_THROW,true,1, 1));

            }
        }

        @Override
        public void actionTick() {
            performer.setYBodyRot(performer.yHeadRot);
            if (getPhase() == ActionPhase.PERFORM && extendableOBB() != null){
                Vec3 pos = getPerformer().position();
                Vec3 offset = new Vec3(0.0, 1.5, 0.2)
                        .yRot(-getPerformer().yBodyRot * MathUtil.DEG_TO_RAD);
                this.extendableOBB().updatePosition(level(), pos, offset, getPerformer().getXRot(), getPerformer().getYRot());
                if (!level().isClientSide()){
                    Vec3 endPos = this.extendableOBB().rotatableHitbox().center.add(getPerformer().getLookAngle().scale(extendableOBB().rotatableHitbox().extent.length()));
                    OBBCollisionUtil.getEntitiesInOBB(level(), this.extendableOBB().rotatableHitbox(), entity -> entity != getPerformer() && entity != getPowerUser() && !entitesD.containsKey(entity)).forEach(entity -> {
                        var damageType = DamageUtil.type(level(), ModDamageTypes.STAND_ATTACK);
                        DamageSource dmgSource = new DamageSource(damageType, performer);
                        RipplesModifiedDamageSource knockBack = (RipplesModifiedDamageSource) dmgSource;
                        knockBack.jojo_ripples$modifyKnockback(0,0);
                        float dmgAmount = (float) StandPower.get(performer).getPowerType().getStandStats().power();
                        if(entity.hurt(dmgSource, dmgAmount)){
                            entity.setNoGravity(true);
                            entitesD.putIfAbsent(entity,(float)(entity.distanceTo(performer)/1.5));
                        }

                        // TODO Add hamon interaction when implemented and Block Interaction
                    });
                    if(!entitesD.isEmpty()){
                        entitesD.forEach(((entity, aFloat) -> {
                            Vec3 move = this.extendableOBB().rotatableHitbox().center.add(getPerformer().getLookAngle().scale(aFloat));
                            entity.teleportTo(move.x,Math.max(move.y-1.35,performer.getY()),move.z);

                        }));
                    }


                    HitResult result = level().clip(new ClipContext(extendableOBB().rotatableHitbox().center, endPos, ClipContext.Block.COLLIDER, ClipContext.Fluid.ANY, CollisionContext.empty()));
                    if (result instanceof BlockHitResult blockHitResult){
                        BlockState blockCollision = OBBCollisionUtil.getCollidingBlock(level(), blockHitResult.getBlockPos());
                        if (blockCollision != null){
                            // TODO Add button, lever and other interactions
                            this.extendableOBB().forceRetract(level(), getPerformer(), this.id);
                        }
                    }

                }
                this.extendableOBB().tick();
                if (this.extendableOBB().isRetracted()){
                    setPhaseStart(ActionPhase.RECOVERY);
                    syncPhaseChanges();
                }
            }


            if(this.getPhase() == ActionPhase.RECOVERY){
                if(getPhaseTicksLeft()==0){
                    vines = null;
                }
                entitesD.forEach(((entity, aFloat) -> entity.setNoGravity(false)));
                entitesD.clear();
            }
        }



        @Override
        public void actionPerformEnd() {
            this.extendableOBB().forceRetract(level(), getPerformer(), this.id);

        }

        @Override
        public ExtendableOBB extendableOBB() {
            return vines;
        }
    }
}
