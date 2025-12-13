package com.zeml.ripplez_hp.init.power;

import com.github.standobyte.jojo.core.JojoRegistries;
import com.github.standobyte.jojo.powersystem.MovesetBuilder;
import com.github.standobyte.jojo.powersystem.ability.controls.InputKey;
import com.github.standobyte.jojo.powersystem.ability.controls.InputMethod;
import com.github.standobyte.jojo.powersystem.standpower.StandStats;
import com.github.standobyte.jojo.powersystem.standpower.StandUnlockableSkill;
import com.github.standobyte.jojo.powersystem.standpower.entity.EntityStandType;
import com.github.standobyte.jojo.powersystem.standpower.type.StandType;

import com.zeml.ripplez_hp.core.HermitPurpleAddon;
import com.zeml.ripplez_hp.powersystem.standpower.type.EmperorType;
import com.zeml.ripplez_hp.powersystem.standpower.type.HermitPurpleType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class AddonStands {
	public static final DeferredRegister<StandType> STANDS = DeferredRegister.create(JojoRegistries.DEFAULT_STANDS_REG, HermitPurpleAddon.MOD_ID);
	
	public static final DeferredHolder<StandType, HermitPurpleType> HERMIT_PURPLE = STANDS.register(
			"hermit_purple", id ->
			new HermitPurpleType(
					new StandStats.Builder()
					.power(6)
					.speed(10)
					.range(3, 3)
					.durability(14)
					.precision(5)
					.build(),

					new MovesetBuilder()

					.addAbility("hp_vine",AddonStandAbilities.VINE).withBind(InputMethod.CLICK,InputKey.LMB)
					.addAbility("hp_vine2",AddonStandAbilities.VINE, vine->vine.isSubAbility = true)
					.addAbility("hp_vine3",AddonStandAbilities.VINE, vine->vine.isSubAbility = true)

					.makeHotbar(0, InputKey.X, InputKey.C)
					
					.addAbility("hp_target", AddonStandAbilities.SELECT_TARGET)
					.inHotbar(0, InputMethod.CLICK)
					
					.addAbility("hp_doxx", AddonStandAbilities.MAP_DIVINATION)
					.inHotbar(0, InputMethod.CLICK)

					.addAbility("hp_block", AddonStandAbilities.THORNS)
					.inHotbar(0, InputMethod.HOLD)
					
					.addAbility("cringe", AddonStandAbilities.CRINGE)
					.inHotbar(0, InputMethod.CLICK)

					.addSkill(StandUnlockableSkill.startingAbility("hp_vine"))
					.addSkill(StandUnlockableSkill.startingAbility("hp_doxx"))
					.addSkill(StandUnlockableSkill.unlockableAbility("hp_target",1))
					.addSkill(StandUnlockableSkill.unlockableAbility("hp_block",1))
					.addSkill(StandUnlockableSkill.unlockableAbility("cringe",2))


					, id));

	public static final DeferredHolder<StandType, EmperorType> EMPEROR = STANDS.register(
			"emperor", id ->
					new EmperorType(
							new StandStats.Builder()
									.power(12)
									.speed(12)
									.range(12, 12)
									.durability(8)
									.precision(2)
									.build(),

							new MovesetBuilder()

									.addAbility("emp_shot",AddonStandAbilities.EMP_SHOT).withBind(InputMethod.CLICK,InputKey.RMB)
									.addAbility("emp_shot_barrage",AddonStandAbilities.EMP_SHOT_BARRAGE).withBind(InputMethod.HOLD ,InputKey.RMB)


									.makeHotbar(0, InputKey.X, InputKey.C)

									.addAbility("emp_target", AddonStandAbilities.EMP_TARGET)
									.inHotbar(0, InputMethod.CLICK)
									.addAbility("emp_d_target",AddonStandAbilities.EMP_D_TARGET)
									.inHotbarSlotVariation("emp_target", InputKey.Modifier.CONTROL,InputMethod.CLICK)

									.addAbility("emp_stand_target",AddonStandAbilities.EMP_STAND_TARGET)
									.inHotbar(0,InputMethod.CLICK)
									.addAbility("emp_delete_target",AddonStandAbilities.EMP_DELETE_TARGET)
									.inHotbar(0,InputMethod.CLICK)

									.addSkill(StandUnlockableSkill.startingAbility("emp_shot"))
									.addSkill(StandUnlockableSkill.startingAbility("emp_shot_barrage"))
									.addSkill(StandUnlockableSkill.startingAbility("emp_target"))
									.addSkill(StandUnlockableSkill.unlockableAbility("emp_stand_target",3))
									.addSkill(StandUnlockableSkill.unlockableAbility("emp_delete_target",0).prerequisiteSkill("emp_stand_target"))


							, id));
}
