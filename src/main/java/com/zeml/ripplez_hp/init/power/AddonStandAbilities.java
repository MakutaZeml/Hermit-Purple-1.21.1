package com.zeml.ripplez_hp.init.power;

import static com.github.standobyte.jojo.core.JojoRegistries.ABILITY_TYPES;

import com.github.standobyte.jojo.powersystem.ability.AbilityType;

import com.zeml.ripplez_hp.jojoimpl.stands.hermitpurple.HermitAction;
import com.zeml.ripplez_hp.jojoimpl.stands.hermitpurple.MapDoxingAbility;
import com.zeml.ripplez_hp.jojoimpl.stands.hermitpurple.OhNoCringeAbility;
import com.zeml.ripplez_hp.jojoimpl.stands.hermitpurple.OpenTargetAbility;
import net.neoforged.neoforge.registries.DeferredHolder;

public final class AddonStandAbilities {
	public static void load() {}

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


}