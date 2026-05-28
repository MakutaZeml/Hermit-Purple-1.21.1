package com.zeml.ripplez_hp.init.power;

import com.github.standobyte.jojo.core.JojoRegistries;
import com.github.standobyte.jojo.init.power.ModStands;
import com.github.standobyte.jojo.powersystem.MovesetBuilder;
import com.github.standobyte.jojo.powersystem.ability.controls.InputKey;
import com.github.standobyte.jojo.powersystem.ability.controls.InputMethod;
import com.github.standobyte.jojo.powersystem.standpower.StandStats;
import com.github.standobyte.jojo.powersystem.standpower.StandUnlockableSkill;
import com.github.standobyte.jojo.powersystem.standpower.entity.EntityStandType;
import com.github.standobyte.jojo.powersystem.standpower.type.StandType;

import com.zeml.ripplez_hp.core.HermitPurpleAddon;
import com.zeml.ripplez_hp.init.power.stand.StandInitEmperor;
import com.zeml.ripplez_hp.init.power.stand.StandInitHermitPurple;
import com.zeml.ripplez_hp.powersystem.standpower.type.EmperorType;
import com.zeml.ripplez_hp.powersystem.standpower.type.HermitPurpleType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class AddonStands {
	public static final DeferredRegister<StandType> STANDS = DeferredRegister.create(JojoRegistries.DEFAULT_STANDS_REG, HermitPurpleAddon.MOD_ID);
	
	public static final DeferredHolder<StandType, HermitPurpleType> HERMIT_PURPLE = STANDS.register("hermit_purple",StandInitHermitPurple::create);

	public static final DeferredHolder<StandType, EmperorType> EMPEROR = STANDS.register("emperor", StandInitEmperor::create);

	static{
		ModStands.PLAYER_CAN_GET_FROM_ARROW.add(HERMIT_PURPLE);
		ModStands.PLAYER_CAN_GET_FROM_ARROW.add(EMPEROR);
	}
}
