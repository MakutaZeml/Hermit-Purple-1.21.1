package com.zeml.ripplez_hp.jojoimpl.stands.hermitpurple;

import com.github.standobyte.jojo.powersystem.ability.AbilityId;
import com.github.standobyte.jojo.powersystem.ability.AbilityType;
import com.github.standobyte.jojo.powersystem.ability.AbilityUsageGroup;
import com.github.standobyte.jojo.powersystem.entityaction.ActionOBB;
import com.github.standobyte.jojo.powersystem.entityaction.ActionPhase;
import com.github.standobyte.jojo.powersystem.entityaction.EntityActionInstance;
import com.github.standobyte.jojo.powersystem.entityaction.type.EntityActionType;
import com.github.standobyte.jojo.powersystem.standpower.StandPower;
import com.github.standobyte.jojo.powersystem.standpower.entity.StandStatFormulas;
import com.github.standobyte.jojo.subsystems.hitboxes.ExtendableOBB;
import com.github.standobyte.jojo.subsystems.hitboxes.OBBCollisionUtil;
import com.github.standobyte.jojo.subsystems.hitboxes.OrientedBoundingBox;
import com.github.standobyte.jojo.util.functions.MathUtil;
import com.zeml.ripplez_hp.core.HermitPurpleAddon;
import com.zeml.ripplez_hp.core.packets.server.StandSoundPacket;
import com.zeml.ripplez_hp.init.AddonSoundEvents;
import com.zeml.ripplez_hp.init.power.AddonStandAbilities;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

public class WeakBlockAbiliy extends HermitAction{
    public WeakBlockAbiliy(AbilityType<?> abilityType, AbilityId abilityId) {
        super(abilityType, abilityId, TargetBlock::new);
        usageGroup = AbilityUsageGroup.SPECIAL;
        setDefaultPhaseLength(ActionPhase.WINDUP, 10);
        setDefaultPhaseLength(ActionPhase.PERFORM, 30);
        setDefaultPhaseLength(ActionPhase.RECOVERY, 15);
    }



    public static class TargetBlock extends EntityActionInstance implements ActionOBB {
        private ExtendableOBB vines;
        private BlockPos blockPos;

        public TargetBlock(EntityActionType ability) {
            super(ability);
        }

        @Override
        public void onActionSet(@Nullable EntityActionInstance prevAction) {
            OrientedBoundingBox obb = new OrientedBoundingBox(new Vec3(0, 1.35, 0), 1d, 1d, 1d, getPerformer().getYRot(), getPerformer().getXRot());
            this.vines = new ExtendableOBB(obb, 0.468990313F, 40,
                    30, new Vec3(0, 1.35, 0));
        }

        @Override
        public void actionTick() {
            HermitPurpleAddon.getLogger().debug("Shit {} {}", phase, curPhaseTick);
            if(extendableOBB() != null){
                Vec3 pos = getPerformer().position();
                Vec3 offset = new Vec3(0.0, 1.5, 0).yRot(-getPerformer().yBodyRot * MathUtil.DEG_TO_RAD);
                this.extendableOBB().updatePosition(level(), pos, offset, getPerformer().getXRot(), getPerformer().getYRot());
                Vec3 endPos = this.extendableOBB().rotatableHitbox().center.add(getPerformer().getLookAngle().scale(extendableOBB().rotatableHitbox().extent.length()));
                if(level().isClientSide){
                    HermitPurpleAddon.getLogger().debug("tambien se estira grr {} {} {}", extendableOBB().getAnimLength(phasePartialTick), extendableOBB().getLength(), endPos.distanceTo(performer.getEyePosition()));
                }else {
                    HermitPurpleAddon.getLogger().debug("tambien se estira {} {} {}", extendableOBB().getAnimLength(phasePartialTick), extendableOBB().getLength(), endPos.distanceTo(performer.getEyePosition()));

                }
                this.extendableOBB().tick();
                switch (getPhase()){
                    case WINDUP -> {
                        BlockHitResult blockHitResult = level().clip(new ClipContext(extendableOBB().rotatableHitbox().center, endPos, ClipContext.Block.COLLIDER, ClipContext.Fluid.ANY, CollisionContext.empty()));
                        BlockState blockCollision = OBBCollisionUtil.getCollidingBlock(level(), blockHitResult.getBlockPos());
                        StandPower power = StandPower.get(performer);
                        if(power != null && power.hasPower()){
                            if (blockCollision != null){
                                StandStatFormulas.BlockMiningTier tier = StandStatFormulas.getStandHarvestLevel(6);
                                if(power.getPowerType() != null && power.getPowerType().getStandStats() != null){
                                    tier = StandStatFormulas.getStandHarvestLevel(power.getPowerType().getStandStats().power()*2);
                                }
                                if(tier.canMine(level().getBlockState(blockHitResult.getBlockPos()))){
                                    this.blockPos = blockHitResult.getBlockPos();
                                    this.extendableOBB().setLifeSpan(30);
                                }else {
                                    setPhase(ActionPhase.RECOVERY,0);
                                }

                            }
                        }

                    }
                    case PERFORM -> {
                        userWalkSpeed = 0;
                        if(blockPos != null){
                            //performer.lookAt(EntityAnchorArgument.Anchor.EYES,blockPos.getCenter());
                        }
                    }
                    case RECOVERY -> {
                        this.extendableOBB().forceRetract(level(),getPerformer(), this.id);
                        if(getPhaseTicksLeft() ==1){
                            this.vines = null;
                        }
                    }
                }
            }
        }

        @Override
        public void onSetPhase(ActionPhase newPhase) {
            if (newPhase == ActionPhase.WINDUP) {
                if(!level().isClientSide){
                    float pitch = (float) (.5+Math.random());
                    PacketDistributor.sendToPlayersTrackingEntityAndSelf(performer,new StandSoundPacket(performer.getId(), AddonSoundEvents.HP_VINE_SWING,true,1, pitch));
                }
            }
        }

        @Override
        public void actionPerformEnd() {
            if(blockPos != null){
                StandPower power = StandPower.get(performer);
                if(power != null && power.hasPower()){
                    StandStatFormulas.BlockMiningTier tier = StandStatFormulas.getStandHarvestLevel(6);
                    if(power.getPowerType() != null && power.getPowerType().getStandStats() != null){
                        tier = StandStatFormulas.getStandHarvestLevel(power.getPowerType().getStandStats().power()*2);
                    }
                    if(tier.canMine(level().getBlockState(blockPos))){
                        WeakBlockEffect blockEffect = AddonStandAbilities.WEAK_BLOCK_EFFECT.get().create(level()).setCenter(blockPos, tier);
                        power.userStandEffects.addEffect(blockEffect);
                        if(!level().isClientSide){
                            float pitch = (float) (.5+Math.random());
                            PacketDistributor.sendToPlayersTrackingEntityAndSelf(performer,new StandSoundPacket(performer.getId(), AddonSoundEvents.HP_TENSION,true,1, pitch));
                        }
                    }

                }
            }
        }

        @Override
        public void onButtonStopHold() {
            if(phase != ActionPhase.RECOVERY){
                setPhase(ActionPhase.RECOVERY,0);
                syncPhaseChanges();
            }
        }

        @Override
        public ExtendableOBB extendableOBB() {
            return vines;
        }


    }

}
