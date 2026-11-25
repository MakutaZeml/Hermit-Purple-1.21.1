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
import com.github.standobyte.jojo.util.functions.JojoModUtil;
import com.github.standobyte.jojo.util.functions.MathUtil;
import com.zeml.ripplez_hp.core.HermitPurpleAddon;
import com.zeml.ripplez_hp.core.packets.server.StandSoundPacket;
import com.zeml.ripplez_hp.init.AddonSoundEvents;
import com.zeml.ripplez_hp.init.HermitTags;
import com.zeml.ripplez_hp.jojoimpl.stands.hermitpurple.HermitAction;
import com.zeml.ripplez_hp.jojoimpl.stands.hermitpurple.grapple.entity.GrappleBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;


public class GrappleBlockAbility extends HermitAction {
    public GrappleBlockAbility(AbilityType<?> abilityType, AbilityId abilityId) {
        super(abilityType, abilityId, GrapplingBlock::new);
        setDefaultPhaseLength(ActionPhase.BUTTON_CHARGE, 5);
        setDefaultPhaseLength(ActionPhase.WINDUP, 30);
        setDefaultPhaseLength(ActionPhase.PERFORM, Integer.MAX_VALUE);
        setDefaultPhaseLength(ActionPhase.RECOVERY, 10);
        usageGroup = AbilityUsageGroup.UTILITY;
    }


    public static class GrapplingBlock extends EntityActionInstance {
        protected GrappleBlockEntity grappleBlock;
        public Vec3 endPos;

        public GrapplingBlock(EntityActionType ability) {
            super(ability);
        }

        @Override
        public void onActionSet(@Nullable EntityActionInstance prevAction) {


        }

        @Override
        public void actionTick() {
            switch (getPhase()) {
                case WINDUP -> {
                    windUpStuff();
                }
                case PERFORM -> {
                    performStuff();
                }
                case RECOVERY -> {
                    performer.resetFallDistance();
                    if (grappleBlock != null) {
                        grappleBlock.remove(Entity.RemovalReason.DISCARDED);
                        grappleBlock = null;
                    }
                }
            }

        }

        public void windUpStuff() {
            if (grappleBlock != null) {
                endPos = grappleBlock.position();
                if (grappleBlock.isAttached()) {
                    if (level().isClientSide) {
                        if (endPos != null) {
                            StandPower power = StandPower.get(performer);
                            if (power != null) {
                                SoundEvent event = ClientsideSoundsHelper.withStandSkin(AddonSoundEvents.HP_TENSION.get(), power);
                                level().playLocalSound(endPos.x, endPos.y, endPos.z, event, performer.getSoundSource(), 1, 1, false);
                            }
                        }
                    }
                    setPhase(ActionPhase.PERFORM, 0);
                    syncPhaseChanges();
                } else if (curPhaseTick > 20) {
                    setPhase(ActionPhase.RECOVERY, 0);
                    syncPhaseChanges();
                }

            }
        }

        public void performStuff() {
            StandPower power = StandPower.get(performer);
            if (endPos != null) {
                if (power != null) {
                    if (!level().isClientSide) {
                        if ((level().getBlockState(grappleBlock.blockPosition()).isAir()) && curPhaseTick > 5) {
                            setPhase(ActionPhase.RECOVERY, 0);
                            syncPhaseChanges();
                        }
                    }
                    performer.resetFallDistance();
                    Vec3 vecTo = endPos.subtract(performer.position().add(0, 1.5, 0));
                    if (vecTo.length() > 1) {
                        dragTarget(performer, vecTo.normalize().multiply(1.5, 1.5, 1.5));
                        power.addExp(.01f);
                        power.consumeStamina(2);
                    } else {
                        dragTarget(performer, Vec3.ZERO);
                    }
                }

            }
            if (power == null || !power.isSummoned()) {
                setPhase(ActionPhase.RECOVERY, 0);
                syncPhaseChanges();
            }
        }


        public @Nullable Vec3 getEndPos() {
            return endPos;
        }

        @Override
        public void onSetPhase(ActionPhase newPhase) {
            switch (newPhase) {
                case WINDUP -> {
                    grappleBlock = new GrappleBlockEntity(performer, level());
                    grappleBlock.shootFromRotation(performer, 1, 1);
                    level().addFreshEntity(grappleBlock);

                    StandPower power = StandPower.get(performer);
                    if (power != null && !level().isClientSide) {
                        PacketDistributor.sendToPlayersTrackingEntityAndSelf(performer, new StandSoundPacket(performer.getId(), AddonSoundEvents.HP_VINE_THROW, true, 1, 1));
                    }
                    BlockPos pos = performer.blockPosition();
                    boolean thisIsntTheCar = false;
                    for (int i = 0; i < 5; i++) {
                        if (level().getBlockState(pos).is(HermitTags.Blocks.NOT_CAR)) {
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


        }


        public void performThis(double distance, Vec3 end) {
            this.endPos = end;
        }

        @Override
        public void actionPerformEnd() {
            performer.fallDistance = 0;
        }

        @Override
        public void onButtonStopHold() {
            if (getPhase() != ActionPhase.RECOVERY) {
                setPhase(ActionPhase.RECOVERY, 0);
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
            if (!level().isClientSide) {
                entity.setDeltaMovement(vec);
                JojoModUtil.forceSyncMotion(entity);
            }
        }

        @Override
        public boolean canBeCancelledInto(@Nullable EntityActionType cancellingAbility) {
            if (cancellingAbility instanceof WeakGrappleBlockInput) {
                return false;
            }
            return super.canBeCancelledInto(cancellingAbility);
        }
    }


}