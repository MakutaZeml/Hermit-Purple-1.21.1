package com.zeml.ripplez_hp.init.power;

import com.github.standobyte.jojo.core.JojoRegistries;
import com.github.standobyte.jojo.powersystem.MovesetBuilder;
import com.github.standobyte.jojo.powersystem.ability.controls.InputKey;
import com.github.standobyte.jojo.powersystem.ability.controls.InputMethod;
import com.github.standobyte.jojo.powersystem.standpower.StandStats;
import com.github.standobyte.jojo.powersystem.standpower.StandUnlockableSkill;
import com.github.standobyte.jojo.powersystem.standpower.entity.EntityStandType;
import com.github.standobyte.jojo.powersystem.standpower.type.StandType;

import com.zeml.ripplez_hp.HermitPurpleAddon;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class AddonStands {
	public static final DeferredRegister<StandType> STANDS = DeferredRegister.create(JojoRegistries.DEFAULT_STANDS_REG, HermitPurpleAddon.MOD_ID);
	
	public static final DeferredHolder<StandType, EntityStandType> HERMIT_PURPLE = STANDS.register(
			"hermit_purple", id ->
			new EntityStandType(
					new StandStats.Builder()
					.power(6)
					.speed(10)
					.range(3, 3)
					.durability(14)
					.precision(5)
					.build(),

					new MovesetBuilder()

					.makeHotbar(0, InputKey.X, InputKey.C)
					
					.addAbility("hp_target", AddonStandAbilities.CRINGE)
					.inHotbar(0, InputMethod.CLICK)
					
					.addAbility("hp_doxx", AddonStandAbilities.MAP_DIVINATION)
					.inHotbar(0, InputMethod.CLICK)
					
					.addAbility("placeholder3", AddonStandAbilities.CRINGE)
					.inHotbar(0, InputMethod.CLICK)
					
					.addAbility("hp_block", AddonStandAbilities.THORNS)
					.inHotbar(0, InputMethod.HOLD)
					
					.addAbility("cringe", AddonStandAbilities.CRINGE)
					.inHotbar(0, InputMethod.CLICK)

							.addSkill(StandUnlockableSkill.startingAbility("hp_doxx"))
							.addSkill(StandUnlockableSkill.unlockableAbility("hp_target",1))
							.addSkill(StandUnlockableSkill.unlockableAbility("hp_block",1))
							.addSkill(StandUnlockableSkill.startingAbility("cringe"))


					, id));
}
