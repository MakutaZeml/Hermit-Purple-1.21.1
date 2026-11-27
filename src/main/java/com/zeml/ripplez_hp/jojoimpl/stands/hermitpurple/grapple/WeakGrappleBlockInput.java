package com.zeml.ripplez_hp.jojoimpl.stands.hermitpurple.grapple;

import com.github.standobyte.jojo.client.sound.ClientsideSoundsHelper;
import com.github.standobyte.jojo.client.sound.sounds.EntityLingeringSoundInstance;
import com.github.standobyte.jojo.config.RotpConfig;
import com.github.standobyte.jojo.powersystem.Power;
import com.github.standobyte.jojo.powersystem.PowerClass;
import com.github.standobyte.jojo.powersystem.ability.Ability;
import com.github.standobyte.jojo.powersystem.ability.AbilityId;
import com.github.standobyte.jojo.powersystem.ability.AbilityType;
import com.github.standobyte.jojo.powersystem.ability.condition.AvailableAbilities;
import com.github.standobyte.jojo.powersystem.ability.controls.InputMethod;
import com.github.standobyte.jojo.powersystem.ability.input.ActionInputBuffer;
import com.github.standobyte.jojo.powersystem.entityaction.ActionPhase;
import com.github.standobyte.jojo.powersystem.entityaction.HeldInput;
import com.github.standobyte.jojo.powersystem.entityaction.LivingComponentAction;
import com.github.standobyte.jojo.powersystem.standpower.StandPower;
import com.github.standobyte.jojo.powersystem.standpower.entity.StandStatFormulas;
import com.zeml.ripplez_hp.core.HermitPurpleAddon;
import com.zeml.ripplez_hp.init.AddonSoundEvents;
import com.zeml.ripplez_hp.init.power.AddonStandAbilities;
import com.zeml.ripplez_hp.jojoimpl.stands.hermitpurple.HermitAction;
import com.zeml.ripplez_hp.jojoimpl.stands.hermitpurple.WeakBlockEffect;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;

public class WeakGrappleBlockInput extends HermitAction {
    public WeakGrappleBlockInput(AbilityType<?> abilityType, AbilityId abilityId) {
        super(abilityType, abilityId);
    }

    @Override
    public Ability replaceWithSubAbility(Power<?> context, AvailableAbilities abilities) {
        LivingEntity user = context.getUser();
        if (user == null || !RotpConfig.canStandBreakBlocks(user)) return null;

        StandPower standPower = PowerClass.STAND.cast(context);
        if (standPower == null) return null;
        LivingComponentAction action = LivingComponentAction.getComponent(user);
        if (action.getAction() instanceof GrappleBlockAbility.GrapplingBlock grapplingBlock) {
            if (grapplingBlock.getPhase() == ActionPhase.PERFORM && grapplingBlock.getEndPos() != null
                    && grapplingBlock.getEndPos().distanceTo(user.position()) < 3) {
                abilities.replaceOtherAbilityWith(standPower, "hp_weak", this);
            }
        }
        return super.replaceWithSubAbility(context, abilities);
    }

    @Override
    public HeldInput onKeyPress(Level level, LivingEntity user, FriendlyByteBuf extraClientInput, InputMethod inputMethod, float clickHoldResolveTime, ActionInputBuffer.BufferingState bufferingState) {
        StandPower standPower = StandPower.get(user);
        if (standPower != null) {
            LivingComponentAction action = LivingComponentAction.getComponent(user);
            if (action.getAction() instanceof GrappleBlockAbility.GrapplingBlock grapplingBlock) {
                if (grapplingBlock.getPhase() == ActionPhase.PERFORM && grapplingBlock.getEndPos() != null
                        && grapplingBlock.getEndPos().distanceTo(user.position()) < 3) {
                    StandStatFormulas.BlockMiningTier tier = StandStatFormulas.getStandHarvestLevel(6);
                    if (standPower.getPowerType() != null && standPower.getPowerType().getStandStats() != null) {
                        tier = StandStatFormulas.getStandHarvestLevel(standPower.getPowerType().getStandStats().power() * 2);
                    }
                    HermitPurpleAddon.getLogger().debug("pressed");
                    WeakBlockEffect effect = AddonStandAbilities.WEAK_BLOCK_EFFECT.get().create(level).setCenter(BlockPos.containing(grapplingBlock.getEndPos()), tier, user.getId());
                    standPower.userStandEffects.addEffect(effect);
                    if (level.isClientSide) {
                        float pitch = (float) (.5 + Math.random());
                        ClientsideSoundsHelper.playNonVanillaClassSound(new EntityLingeringSoundInstance(ClientsideSoundsHelper.withStandSkin(
                                AddonSoundEvents.HP_TENSION.get(), standPower),
                                user.getSoundSource(), 1, pitch, user, level));
                    }
                }
            }
        }
        return super.onKeyPress(level, user, extraClientInput, inputMethod, clickHoldResolveTime, bufferingState);
    }


}