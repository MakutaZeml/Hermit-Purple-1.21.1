package com.zeml.ripplez_hp.init.power;

import com.github.standobyte.jojo.core.JojoRegistries;
import com.github.standobyte.jojo.powersystem.standpower.StandEffectType;

import com.zeml.ripplez_hp.HermitPurpleAddon;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class AddonStandEffects {
	public static final DeferredRegister<StandEffectType<?>> STAND_EFFECT_TYPES = DeferredRegister.create(JojoRegistries.STAND_EFFECTS_REG, HermitPurpleAddon.MOD_ID);

}