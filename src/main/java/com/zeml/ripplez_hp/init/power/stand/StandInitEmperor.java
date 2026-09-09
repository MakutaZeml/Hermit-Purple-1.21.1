package com.zeml.ripplez_hp.init.power.stand;

import com.github.standobyte.jojo.init.power.ModStands;
import com.github.standobyte.jojo.powersystem.MovesetBuilder;
import com.github.standobyte.jojo.powersystem.ability.controls.InputKey;
import com.github.standobyte.jojo.powersystem.ability.controls.InputMethod;
import com.github.standobyte.jojo.powersystem.standpower.StandStats;
import com.github.standobyte.jojo.powersystem.standpower.StandUnlockableSkill;
import com.github.standobyte.jojo.powersystem.standpower.type.SummonedStand;
import com.zeml.ripplez_hp.init.power.AddonStandAbilities;
import com.zeml.ripplez_hp.powersystem.standpower.type.EmperorType;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;

public class StandInitEmperor {
	public static final String SKIN_VIEW_TYPE = "zemperor";

    @ApiStatus.Internal
    public static EmperorType create(ResourceLocation id){
        return new EmperorType(new StandStats.Builder()
                .power(12)
                .speed(12)
                .range(12, 12)
                .durability(8)
                .precision(2)
                .build(),

                new MovesetBuilder()

                        .addAbility("emp_shot", AddonStandAbilities.EMP_SHOT)
                        .addAbility("emp_shot_barrage",AddonStandAbilities.EMP_SHOT_BARRAGE)
                        .addAbility("emp_target", AddonStandAbilities.EMP_TARGET)
                        .addAbility("emp_d_target",AddonStandAbilities.EMP_D_TARGET)
                        .addAbility("emp_stand_target",AddonStandAbilities.EMP_STAND_TARGET)
                        .addAbility("emp_delete_target",AddonStandAbilities.EMP_DELETE_TARGET)
                        .addAbility("guide_emp",AddonStandAbilities.EMP_GUIDE)
                        .addAbility("guide_barrage_emp",AddonStandAbilities.EMP_BARRAGE_GUIDE)
                        .addAbility("emp_mode",AddonStandAbilities.EMP_MODE)

                        .makeControlScheme("hotbar")
                        .bind("emp_shot",InputMethod.CLICK,InputKey.RMB)
                        .bind("emp_shot_barrage", InputMethod.HOLD, InputKey.RMB)
                        .bind("emp_mode",InputMethod.CLICK,InputKey.MMB)

                        .makeHotbar(0, ModStands.USE_SPECIAL, ModStands.SWITCH_SPECIAL)
                        .addToHotbar("emp_target",0,InputMethod.CLICK)
                        .addHotbarSlotVariation("emp_d_target","emp_target", InputKey.Modifier.CONTROL,InputMethod.CLICK)
                        .addToHotbar("emp_stand_target",0,InputMethod.CLICK)
                        .addToHotbar("emp_delete_target",0,InputMethod.CLICK)

                        .finalizeControlScheme()

                        .addSkill(StandUnlockableSkill.startingAbility("emp_shot"))
                        .addSkill(StandUnlockableSkill.startingAbility("emp_shot_barrage"))
                        .addSkill(StandUnlockableSkill.startingAbility("emp_target"))
                        .addSkill(StandUnlockableSkill.unlockableAbility("emp_stand_target",50))
                        .addSkill(StandUnlockableSkill.unlockableAbility("emp_delete_target",0).prerequisiteSkill("emp_stand_target"))
                        .addSkill(StandUnlockableSkill.unlockableAbility("guide_emp",100).prerequisiteSkill("emp_stand_target"))
                ,id)
        		.init(stand -> {
                    stand.makeSummonedStandObj = SummonedStand.SyncableSummonedStand::new;
                    stand.skinUIType = SKIN_VIEW_TYPE;
                });
    }
}
