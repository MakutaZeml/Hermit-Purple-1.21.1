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
import com.github.standobyte.jojo.subsystems.hitboxes.ExtendableOBB;
import com.github.standobyte.jojo.subsystems.hitboxes.OBBCollisionUtil;
import com.github.standobyte.jojo.subsystems.hitboxes.OrientedBoundingBox;
import com.github.standobyte.jojo.util.functions.DamageUtil;
import com.github.standobyte.jojo.util.functions.MathUtil;
import com.zeml.ripplez_hp.core.packets.server.StandSoundPacket;
import com.zeml.ripplez_hp.init.AddonSoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
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
        setDefaultPhaseLength(ActionPhase.WINDUP, StandStatFormulas.getHeavyAttackWindup(10, 0));
        setDefaultPhaseLength(ActionPhase.PERFORM, 6);
        setDefaultPhaseLength(ActionPhase.RECOVERY, 15);

    }


    public static class HermitHeavyVine extends EntityActionInstance implements ActionOBB {

        public HermitHeavyVine(EntityActionType ability) {
            super(ability);
        }

        private ExtendableOBB vines;
        private Set<Entity> entities = new HashSet<>();

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
                    OBBCollisionUtil.getEntitiesInOBB(level(), this.extendableOBB().rotatableHitbox(), entity -> entity != getPerformer() && entity != getPowerUser() && !entities.contains(entity)).forEach(entity -> {
                        var damageType = DamageUtil.type(level(), ModDamageTypes.STAND_ATTACK);
                        DamageSource dmgSource = new DamageSource(damageType, performer);
                        float dmgAmount = (float) StandPower.get(performer).getPowerType().getStandStats().power();
                        dmgAmount = StandStatFormulas.getHeavyAttackDamage(dmgAmount);
                        entities.add(entity);
                        if(entity.hurt(dmgSource, dmgAmount)){
                            if(Math.random() <=.2){
                                if(entity instanceof LivingEntity livingEntity){
                                    ItemStack mainHand = livingEntity.getItemInHand(InteractionHand.MAIN_HAND);
                                    ItemStack offHand = livingEntity.getItemInHand(InteractionHand.OFF_HAND);
                                    ItemStack cloned;
                                    if(!mainHand.isEmpty()){
                                        cloned = mainHand.copyAndClear();
                                        ItemEntity itemEntity = new ItemEntity(this.level(),livingEntity.getX(),livingEntity.getY(),livingEntity.getZ(),cloned);
                                        itemEntity.setPickUpDelay(100);
                                        level().addFreshEntity(itemEntity);

                                    }else if(!offHand.isEmpty()){
                                        cloned = offHand.copyAndClear();
                                        ItemEntity itemEntity = new ItemEntity(this.level(),livingEntity.getX(),livingEntity.getY(),livingEntity.getZ(),cloned);
                                        itemEntity.setPickUpDelay(100);
                                        level().addFreshEntity(itemEntity);
                                    }
                                }
                            }

                        }

                        // TODO Add hamon interaction when implemented and Block Interaction
                    });



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
                entities.clear();
            }
        }



        @Override
        public void actionPerformEnd() {
            this.extendableOBB().forceRetract(level(), getPerformer(), this.id);

        }

        @Override
        public void onSetPhase(ActionPhase newPhase) {
            Level level = level();
            if (newPhase == ActionPhase.PERFORM) {
                if(!level.isClientSide){
                    float pitch = (float) (.5+Math.random());
                    PacketDistributor.sendToPlayersTrackingEntityAndSelf(performer,new StandSoundPacket(performer.getId(), AddonSoundEvents.HP_VINE_SWING,true,1, pitch));
                }
            }
        }

        @Override
        public ExtendableOBB extendableOBB() {
            return vines;
        }
    }
}
