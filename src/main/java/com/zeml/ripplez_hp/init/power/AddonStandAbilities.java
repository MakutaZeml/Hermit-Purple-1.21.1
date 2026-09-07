package com.zeml.ripplez_hp.init.power;

import static com.github.standobyte.jojo.core.JojoRegistries.ABILITY_TYPES;
import static com.github.standobyte.jojo.init.power.ModStandAbilities.STAND_EFFECT_TYPES;

import com.github.standobyte.jojo.entityattachment.custom_effect.EntityCustomEffectType;
import com.github.standobyte.jojo.powersystem.ability.AbilityType;

import com.zeml.ripplez_hp.jojoimpl.stands.emperor.*;
import com.zeml.ripplez_hp.jojoimpl.stands.hermitpurple.*;
import com.zeml.ripplez_hp.jojoimpl.stands.hermitpurple.WeakBlockEffect;
import net.neoforged.neoforge.registries.DeferredHolder;

public final class AddonStandAbilities {
	public static void load() {}

	public static final DeferredHolder<AbilityType<?>,AbilityType<HermitVineWhip>> VINE = ABILITY_TYPES.register(
			"vine",key ->new AbilityType<>(key,HermitVineWhip::new));


	public static final DeferredHolder<AbilityType<?>,AbilityType<HermitHeavyVineWhip>> VINE_HEAVY = ABILITY_TYPES.register(
			"vine_heavy",key ->new AbilityType<>(key,HermitHeavyVineWhip::new));

	public static final DeferredHolder<AbilityType<?>,AbilityType<HermitGrabAbility>> VINE_GRAB = ABILITY_TYPES.register(
			"vine_grab",key ->new AbilityType<>(key, HermitGrabAbility::new));


	public static final DeferredHolder<AbilityType<?>, AbilityType<OhNoCringeAbility>> CRINGE = ABILITY_TYPES.register(
			"cringe", key -> new AbilityType<>(key, OhNoCringeAbility::new));

	public static final DeferredHolder<AbilityType<?>, AbilityType<MapDoxingAbility>> MAP_DIVINATION = ABILITY_TYPES.register(
			"hp_doxx", key ->new AbilityType<>(key, MapDoxingAbility::new)
	);

	public static final DeferredHolder<AbilityType<?>, AbilityType<HermitCompassAbility>> COMPASS_DIVINATION = ABILITY_TYPES.register(
			"hp_compass", key ->new AbilityType<>(key, HermitCompassAbility::new)
	);

	public static final DeferredHolder<AbilityType<?>, AbilityType<CameraDoxAbility>> CAMERA_DIVINATION = ABILITY_TYPES.register(
			"hp_camera", key ->new AbilityType<>(key, CameraDoxAbility::new)
	);
	public static final DeferredHolder<AbilityType<?>, AbilityType<HermitAction>> THORNS = ABILITY_TYPES.register(
			"hp_thorn", key -> new AbilityType<>(key, HermitThornsAbility::new)
	);

	public static final DeferredHolder<AbilityType<?>, AbilityType<OpenTargetAbility>> SELECT_TARGET = ABILITY_TYPES.register(
			"hp_select", key -> new AbilityType<>(key, OpenTargetAbility::new)
	);

	public static final DeferredHolder<AbilityType<?>, AbilityType<WeakBlockAbiliy>> WEAK_BLOCK = ABILITY_TYPES.register(
			"hp_weak", key -> new AbilityType<>(key, WeakBlockAbiliy::new)
	);
	public static final DeferredHolder<EntityCustomEffectType<?>, EntityCustomEffectType<WeakBlockEffect>> WEAK_BLOCK_EFFECT = STAND_EFFECT_TYPES.register(
			"hp_weak_block", key -> new EntityCustomEffectType<>(key, WeakBlockEffect::new));

	public static final DeferredHolder<EntityCustomEffectType<?>, EntityCustomEffectType<HermitThornsAbility.HermitThornsEffect>> HERMIT_THORNS_EFFECT = STAND_EFFECT_TYPES.register(
			"hp_thorns", key -> new EntityCustomEffectType<>(key, HermitThornsAbility.HermitThornsEffect::new));

	public static final DeferredHolder<AbilityType<?>, AbilityType<TargetSelectAbility>> EMP_TARGET = ABILITY_TYPES.register(
			"emp_target", key -> new AbilityType<>(key, TargetSelectAbility::new)
	);

	public static final DeferredHolder<AbilityType<?>, AbilityType<TargetDSelectAbility>> EMP_D_TARGET = ABILITY_TYPES.register(
			"emp_d_target", key -> new AbilityType<>(key, TargetDSelectAbility::new)
	);

	public static final DeferredHolder<AbilityType<?>, AbilityType<StandTargetAbility>> EMP_STAND_TARGET = ABILITY_TYPES.register(
			"emp_stand_target", key -> new AbilityType<>(key, StandTargetAbility::new)
	);

	public static final DeferredHolder<AbilityType<?>, AbilityType<DeleteStandTargetAbility>> EMP_DELETE_TARGET = ABILITY_TYPES.register(
			"emp_delete_target", key -> new AbilityType<>(key, DeleteStandTargetAbility::new)
	);

	public static final DeferredHolder<AbilityType<?>, AbilityType<ShotAbility>> EMP_SHOT = ABILITY_TYPES.register(
			"emp_shot", key -> new AbilityType<>(key, ShotAbility::new)
	);


	public static final DeferredHolder<AbilityType<?>, AbilityType<ShotBarrageAbility>> EMP_SHOT_BARRAGE = ABILITY_TYPES.register(
			"emp_shot_barrage", key -> new AbilityType<>(key, ShotBarrageAbility::new)
	);

	public static final DeferredHolder<AbilityType<?>, AbilityType<GuideBulletAbility>> EMP_GUIDE = ABILITY_TYPES.register(
			"emp_guide", key -> new AbilityType<>(key, GuideBulletAbility::new)
	);

	public static final DeferredHolder<AbilityType<?>, AbilityType<ChangeMode>> EMP_MODE = ABILITY_TYPES.register(
			"emp_mode", key -> new AbilityType<>(key, ChangeMode::new)
	);

	public static final DeferredHolder<EntityCustomEffectType<?>, EntityCustomEffectType<GuidedBulletEffect>> GUIDED_BULLET = STAND_EFFECT_TYPES.register(
			"emp_guided_bullet", key -> new EntityCustomEffectType<>(key, GuidedBulletEffect::new));

}