package com.zeml.ripplez_hp.jojoimpl.stands.hermitpurple.grapple;

import com.github.standobyte.jojo.client.sound.ClientsideSoundsHelper;
import com.github.standobyte.jojo.mechanics.voiceline.VoiceLineServerSide;
import com.github.standobyte.jojo.powersystem.ability.AbilityId;
import com.github.standobyte.jojo.powersystem.ability.AbilityType;
import com.github.standobyte.jojo.powersystem.ability.AbilityUsageGroup;
import com.github.standobyte.jojo.powersystem.entityaction.ActionPhase;
import com.github.standobyte.jojo.powersystem.entityaction.EntityActionInstance;
import com.github.standobyte.jojo.powersystem.entityaction.type.EntityActionType;
import com.github.standobyte.jojo.powersystem.standpower.StandPower;
import com.github.standobyte.jojo.powersystem.standpower.entity.StandEntity;
import com.github.standobyte.jojo.subsystems.hitboxes.ExtendableOBB;
import com.github.standobyte.jojo.subsystems.hitboxes.OBBCollisionUtil;
import com.github.standobyte.jojo.subsystems.hitboxes.OrientedBoundingBox;
import com.github.standobyte.jojo.util.functions.MathUtil;
import com.zeml.ripplez_hp.core.HermitPurpleAddon;
import com.zeml.ripplez_hp.core.packets.server.StandSoundPacket;
import com.zeml.ripplez_hp.init.AddonSoundEvents;
import com.zeml.ripplez_hp.jojoimpl.stands.hermitpurple.HermitAction;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.IronBarsBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class GrappleBlockAbility extends HermitAction {
    public GrappleBlockAbility(AbilityType<?> abilityType, AbilityId abilityId) {
        super(abilityType, abilityId,GrapplingBlock::new);
        setDefaultPhaseLength(ActionPhase.BUTTON_CHARGE,10);
        setDefaultPhaseLength(ActionPhase.WINDUP,30);
        setDefaultPhaseLength(ActionPhase.PERFORM,Integer.MAX_VALUE);
        setDefaultPhaseLength(ActionPhase.RECOVERY,10);
        usageGroup = AbilityUsageGroup.UTILITY;
    }


    public static class GrapplingBlock extends EntityActionInstance implements SingleGrappleAction {
        private ExtendableOBB vines;
        private Vec3 endPos;




        public GrapplingBlock(EntityActionType ability) {
            super(ability);
        }

        @Override
        public void onActionSet(@Nullable EntityActionInstance prevAction) {
            float lifespan = phasesLength.getFloat(ActionPhase.WINDUP);
            OrientedBoundingBox obb = new OrientedBoundingBox(new Vec3(0, 1.35, 0), 0.25d, 0.25d, 1d, getPerformer().getYRot(), getPerformer().getXRot());
            this.vines = new ExtendableOBB(obb, 1.5F, (int) lifespan, 0, new Vec3(0, 1.35, 0));
        }

        @Override
        public void actionTick() {
            switch (getPhase()){
                case WINDUP -> {
                    if(vines != null){
                        this.vines.tick();
                        Vec3 pos = getPerformer().position();
                        Vec3 offset = new Vec3(0.0, 1.5, 0)
                                .yRot(-getPerformer().yBodyRot * MathUtil.DEG_TO_RAD);
                        this.vines.updatePosition(level(), pos, offset, getPerformer().getXRot(), getPerformer().getYRot());
                        endPos = this.vines.rotatableHitbox().center.add(getPerformer().getLookAngle().scale(vines.rotatableHitbox().extent.length()));
                        BlockHitResult result = level().clip(new ClipContext(vines.rotatableHitbox().center, endPos, ClipContext.Block.COLLIDER, ClipContext.Fluid.ANY, CollisionContext.empty()));
                        BlockState blockCollision = OBBCollisionUtil.getCollidingBlock(level(), result.getBlockPos());
                        if (blockCollision != null){
                            if(level().isClientSide){
                                if(endPos != null){
                                    StandPower power = StandPower.get(performer);
                                    if(power != null){
                                        SoundEvent event = ClientsideSoundsHelper.withStandSkin(AddonSoundEvents.HP_TENSION.get(),power);
                                        level().playLocalSound(endPos.x,endPos.y,endPos.z,event,performer.getSoundSource(),1,1,false);
                                    }
                                }
                            }
                            setPhase(ActionPhase.PERFORM,0);
                            syncPhaseChanges();
                        } else if (curPhaseTick > 20) {
                            setPhase(ActionPhase.RECOVERY,0);
                            syncPhaseChanges();
                        }


                    }
                }
                case PERFORM -> {
                    vines = null;
                    if(endPos != null){
                        BlockPos blockPos = BlockPos.containing(endPos);
                        if(!level().isClientSide){
                            if((level().getBlockState(blockPos).isAir() || level().getBlockState(blockPos).liquid()) && curPhaseTick>5){
                                setPhase(ActionPhase.RECOVERY,0);
                                syncPhaseChanges();
                            }
                        }
                        performer.resetFallDistance();
                        Vec3 vecTo = endPos.subtract(performer.position().add(0,1.5,0));
                        if(vecTo.length() > 1){
                            dragTarget(performer,vecTo.normalize().multiply(1.5,1.5,1.5));
                            StandPower power = StandPower.get(performer);
                            if(power != null){
                                power.addExp(.01f);
                            }
                        }else {
                            dragTarget(performer,Vec3.ZERO);
                        }
                    }

                }
                case RECOVERY -> {
                    performer.resetFallDistance();
                }
            }
        }

        @Override
        public @Nullable Vec3 getEndPos() {
            return endPos;
        }

        @Override
        public void onSetPhase(ActionPhase newPhase) {
            if (Objects.requireNonNull(newPhase) == ActionPhase.WINDUP) {
                StandPower power = StandPower.get(performer);
                if (power != null && !level().isClientSide) {
                    PacketDistributor.sendToPlayersTrackingEntityAndSelf(performer, new StandSoundPacket(performer.getId(), AddonSoundEvents.HP_VINE_THROW, true, 1, 1));
                }
                BlockPos pos = performer.blockPosition();
                boolean thisIsntTheCar = false;
                for (int i = 0; i < 3; i++) {
                    if (level().getBlockState(pos).is(BlockTags.FENCES) || level().getBlockState(pos).getBlock() instanceof IronBarsBlock) {
                        thisIsntTheCar = true;
                        break;
                    }
                    pos = pos.below();
                }


                if (thisIsntTheCar && !performer.onGround() && !level().isClientSide()) {
                    VoiceLineServerSide.play(performer, AddonSoundEvents.THE_CAR, 0, false);
                }
            }

        }

        @Override
        public void actionPerformEnd() {
            performer.fallDistance = 0;
        }

        @Override
        public void onButtonStopHold() {
            if(getPhase() != ActionPhase.RECOVERY){
                setPhase(ActionPhase.RECOVERY,0);
                syncPhaseChanges();
            }
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
            if (entity instanceof LivingEntity) {
                entity.move(MoverType.PLAYER, vec);
            }
            entity.setDeltaMovement(vec);
        }

        @Override
        public boolean canBeCancelledInto(@Nullable EntityActionType cancellingAbility) {
            if(cancellingAbility instanceof WeakGrappleBlockInput){
                return true;
            }
            return super.canBeCancelledInto(cancellingAbility);
        }
    }


}