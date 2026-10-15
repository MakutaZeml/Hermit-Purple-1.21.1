package com.zeml.ripplez_hp.jojoimpl.stands.hermitpurple.grapple;

import com.github.standobyte.jojo.mechanics.voiceline.VoiceLineServerSide;
import com.github.standobyte.jojo.powersystem.ability.AbilityId;
import com.github.standobyte.jojo.powersystem.ability.AbilityType;
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
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
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
    }


    public static class GrapplingBlock extends EntityActionInstance {
        private ExtendableOBB vines;
        private Vec3 endPos;



        public GrapplingBlock(EntityActionType ability) {
            super(ability);
        }

        @Override
        public void onActionSet(@Nullable EntityActionInstance prevAction) {
            float lifespan = phasesLength.getFloat(ActionPhase.WINDUP);
            OrientedBoundingBox obb = new OrientedBoundingBox(new Vec3(0, 1.35, 0), 0.25d, 0.25d, 1d, getPerformer().getYRot(), getPerformer().getXRot());
            this.vines = new ExtendableOBB(obb, 1.0F, (int) lifespan, 0, new Vec3(0, 1.35, 0));
        }

        //TODO go to RECOVERY AFTER THE BLOCK IN endPos gets destroyed
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
                        HermitPurpleAddon.getLogger().debug("WTF?! {} {} {}", curPhaseTick,endPos, blockCollision != null);
                        if (blockCollision != null){
                            setPhase(ActionPhase.PERFORM,0);
                            syncPhaseChanges();
                        } else if (curPhaseTick > 20) {
                            setPhase(ActionPhase.RECOVERY,0);
                            syncPhaseChanges();
                        }
                        if(!level().isClientSide && level() instanceof ServerLevel serverLevel){
                            float f = (float)level().getRandom().nextInt(4) / 24.0F;
                            serverLevel.sendParticles(ParticleTypes.NOTE, endPos.x, endPos.y, endPos.z, 0, f, 0.0, 0.0, 1.0);
                        }

                    }
                }
                case PERFORM -> {
                    if(endPos != null){
                        Vec3 vecTo = endPos.subtract(performer.position().add(0,1.5,0));
                        if(vecTo.length() > 1){
                            dragTarget(performer,vecTo.normalize());
                        }else {
                            dragTarget(performer,Vec3.ZERO);
                        }
                    }

                }
            }
        }

        @Nullable
        public Vec3 getEndPos() {
            return endPos;
        }

        @Override
        public void onSetPhase(ActionPhase newPhase) {
            switch (newPhase){
                case WINDUP -> {
                    StandPower power = StandPower.get(performer);
                    if(power != null && !level().isClientSide){
                        PacketDistributor.sendToPlayersTrackingEntityAndSelf(performer,new StandSoundPacket(performer.getId(), AddonSoundEvents.HP_VINE_SWING,true,1, 1));
                    }
                }
                case PERFORM -> {
                    BlockPos pos = performer.blockPosition();
                    boolean fence = false;
                    for(int i =0; i<3;i++){
                        if(level().getBlockState(pos).is(BlockTags.FENCES) || level().getBlockState(pos).getBlock() instanceof IronBarsBlock){
                            fence = true;
                            break;
                        }
                        pos = pos.below();
                    }
                    if(fence){
                        VoiceLineServerSide.play(performer, AddonSoundEvents.THE_CAR,0,false);
                    }



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
    }


}