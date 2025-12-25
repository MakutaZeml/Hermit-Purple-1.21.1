package com.zeml.ripplez_hp.init;

import com.zeml.ripplez_hp.core.HermitPurpleAddon;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class AddonSoundEvents {
	public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(Registries.SOUND_EVENT, HermitPurpleAddon.MOD_ID);

	public static final DeferredHolder<SoundEvent, SoundEvent> OH_NO_CRINGE = SOUNDS.register("oh_no_cringe", SoundEvent::createVariableRangeEvent);
	public static final DeferredHolder<SoundEvent, SoundEvent> USER_HP = SOUNDS.register("user_hp", SoundEvent::createVariableRangeEvent);

	public static final DeferredHolder<SoundEvent, SoundEvent> SUMMON_HP = SOUNDS.register("summon_hermit", SoundEvent::createVariableRangeEvent);
	public static final DeferredHolder<SoundEvent, SoundEvent> HP_VINE_THROW = SOUNDS.register("vine_throw", SoundEvent::createVariableRangeEvent);
	public static final DeferredHolder<SoundEvent, SoundEvent> HP_VINE_SWING = SOUNDS.register("vine_swing", SoundEvent::createVariableRangeEvent);
	public static final DeferredHolder<SoundEvent, SoundEvent> HP_VINE_CATCH = SOUNDS.register("hp_grapple", SoundEvent::createVariableRangeEvent);
	public static final DeferredHolder<SoundEvent, SoundEvent> EMP_SHOT = SOUNDS.register("emp_shot", SoundEvent::createVariableRangeEvent);


}
