package com.zeml.ripplez_hp.init.power;

import static com.github.standobyte.jojo.core.JojoRegistries.ABILITY_TYPES;

import com.github.standobyte.jojo.powersystem.ability.AbilityType;

import com.zeml.ripplez_hp.jojoimpl.stands.emperor.*;
import com.zeml.ripplez_hp.jojoimpl.stands.hermitpurple.*;
import net.neoforged.neoforge.registries.DeferredHolder;

public final class AddonStandAbilities {
	public static void load() {}

	public static final DeferredHolder<AbilityType<?>,AbilityType<HermitVineWhip>> VINE = ABILITY_TYPES.register(
			"vine",key ->new AbilityType<>(key,HermitVineWhip::new));

	public static final DeferredHolder<AbilityType<?>, AbilityType<OhNoCringeAbility>> CRINGE = ABILITY_TYPES.register(
			"cringe", key -> new AbilityType<>(key, OhNoCringeAbility::new));

	public static final DeferredHolder<AbilityType<?>, AbilityType<MapDoxingAbility>> MAP_DIVINATION = ABILITY_TYPES.register(
			"hp_doxx", key ->new AbilityType<>(key, MapDoxingAbility::new)
	);

	public static final DeferredHolder<AbilityType<?>, AbilityType<HermitAction>> THORNS = ABILITY_TYPES.register(
			"hp_thorns", key ->new AbilityType<>(key, HermitAction::new)
	);

	public static final DeferredHolder<AbilityType<?>, AbilityType<OpenTargetAbility>> SELECT_TARGET = ABILITY_TYPES.register(
			"hp_select", key -> new AbilityType<>(key, OpenTargetAbility::new)
	);

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



}