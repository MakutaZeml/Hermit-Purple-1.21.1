package com.zeml.ripplez_hp.jojoimpl.stands.hermitpurple.grapple;

import com.github.standobyte.jojo.client.sound.ClientsideSoundsHelper;
import com.github.standobyte.jojo.client.sound.sounds.EntityLingeringSoundInstance;
import com.github.standobyte.jojo.powersystem.Power;
import com.github.standobyte.jojo.powersystem.PowerClass;
import com.github.standobyte.jojo.powersystem.ability.AbilityId;
import com.github.standobyte.jojo.powersystem.ability.AbilityType;
import com.github.standobyte.jojo.powersystem.ability.condition.ConditionCheck;
import com.github.standobyte.jojo.powersystem.ability.controls.InputMethod;
import com.github.standobyte.jojo.powersystem.ability.input.ActionInputBuffer;
import com.github.standobyte.jojo.powersystem.entityaction.HeldInput;
import com.github.standobyte.jojo.powersystem.standpower.StandPower;
import com.github.standobyte.jojo.subsystems.hitboxes.OBBCollisionUtil;
import com.zeml.ripplez_hp.core.HermitPurpleAddon;
import com.zeml.ripplez_hp.init.AddonSoundEvents;
import com.zeml.ripplez_hp.init.power.AddonStandAbilities;
import com.zeml.ripplez_hp.jojoimpl.stands.hermitpurple.HermitAction;
import com.zeml.ripplez_hp.jojoimpl.stands.hermitpurple.grapple.entity.NetNodeEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.neoforged.neoforge.network.PacketDistributor;

public class HermitNetAbility extends HermitAction {
    double DISTANCE = 4.5;
    double MAX = 20;

    public HermitNetAbility(AbilityType<?> abilityType, AbilityId abilityId) {
        super(abilityType, abilityId);
    }

    @Override
    public ConditionCheck checkSpecificConditions(Power<?> context) {
        LivingEntity user = context.getUser();
        Vec3 lookVec = user.getLookAngle();
        Vec3 startPos = user.getEyePosition();
        Vec3 endPos = startPos.add(lookVec.scale(DISTANCE));
        BlockHitResult result = user.level().clip(new ClipContext(user.getEyePosition(), endPos, ClipContext.Block.COLLIDER, ClipContext.Fluid.ANY, CollisionContext.empty()));
        BlockState blockCollision = OBBCollisionUtil.getCollidingBlock(user.level(), result.getBlockPos());
        if (blockCollision != null && !blockCollision.isAir() && !blockCollision.liquid()) {
            StandPower power = PowerClass.STAND.cast(context);
            if (power != null) {
                if (power.userStandEffects.getEffectOfType(AddonStandAbilities.NODES_CONTROL.get()).map(CountNodesEffect::getNodes).orElse(0) < MAX) {
                    return super.checkSpecificConditions(context);
                }
            }
        }
        return ConditionCheck.NEGATIVE;
    }

    @Override
    public HeldInput onKeyPress(Level level, LivingEntity user, FriendlyByteBuf extraClientInput, InputMethod inputMethod, float clickHoldResolveTime, ActionInputBuffer.BufferingState bufferingState) {
        StandPower power = StandPower.get(user);
        if (power != null) {
            HermitPurpleAddon.getLogger().debug("pressed {}", !level.isClientSide);
            Vec3 lookVec = user.getLookAngle();
            Vec3 startPos = user.getEyePosition();
            Vec3 endPos = startPos.add(lookVec.scale(DISTANCE));
            BlockHitResult result = user.level().clip(new ClipContext(user.getEyePosition(), endPos, ClipContext.Block.COLLIDER, ClipContext.Fluid.ANY, CollisionContext.empty()));
            BlockState blockCollision = OBBCollisionUtil.getCollidingBlock(user.level(), result.getBlockPos());
            if (blockCollision != null && !blockCollision.isAir() && !blockCollision.liquid()) {
                boolean present = power.userStandEffects.getEffectOfType(AddonStandAbilities.NODES_CONTROL.get()).isPresent();
                CountNodesEffect effect;
                if (!present) {
                    effect = AddonStandAbilities.NODES_CONTROL.get().create(level);
                    power.userStandEffects.addEffect(effect);
                } else {
                    effect = power.userStandEffects.getEffectOfType(AddonStandAbilities.NODES_CONTROL.get()).get();
                }
                Entity target;
                if (effect.getTarget() != null) {
                    target = effect.getTarget();
                } else {
                    target = user;
                }
                NetNodeEntity nodeEntity = new NetNodeEntity(level, user, power.getPowerType().getStandStats().rangeMax() * 10, target);
                nodeEntity.setPos(result.getBlockPos().getCenter());
                level.addFreshEntity(nodeEntity);
                effect.addNode(1);
                effect.withTarget(nodeEntity);
                effect.updateTarget(level);
                if (level.isClientSide) {
                    float pitch = (float) (.5 + Math.random());
                    ClientsideSoundsHelper.playNonVanillaClassSound(new EntityLingeringSoundInstance(ClientsideSoundsHelper.withStandSkin(
                            AddonSoundEvents.HP_TENSION.get(), StandPower.get(user)),
                            user.getSoundSource(), 1, pitch, user, level));
                }
            }
        }
        return super.onKeyPress(level, user, extraClientInput, inputMethod, clickHoldResolveTime, bufferingState);
    }

    @Override
    public Component getName(Power<?> context) {
        StandPower power = PowerClass.STAND.cast(context);
        int nodes = 0;
        if (power != null) {
            nodes = power.userStandEffects.getEffectOfType(AddonStandAbilities.NODES_CONTROL.get()).map(CountNodesEffect::getNodes).orElse(0);
        }
        return Component.translatable(nameTlKey, nodes, Math.round(MAX));
    }
}