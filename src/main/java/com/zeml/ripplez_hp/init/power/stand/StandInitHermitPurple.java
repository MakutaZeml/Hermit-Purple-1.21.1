package com.zeml.ripplez_hp.init.power.stand;

import com.github.standobyte.jojo.init.power.ModStands;
import com.github.standobyte.jojo.powersystem.MovesetBuilder;
import com.github.standobyte.jojo.powersystem.ability.controls.InputKey;
import com.github.standobyte.jojo.powersystem.ability.controls.InputMethod;
import com.github.standobyte.jojo.powersystem.standpower.StandStats;
import com.github.standobyte.jojo.powersystem.standpower.StandUnlockableSkill;
import com.zeml.ripplez_hp.init.power.AddonStandAbilities;
import com.zeml.ripplez_hp.powersystem.standpower.type.HermitPurpleType;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;

public class StandInitHermitPurple {

    @ApiStatus.Internal
    public static HermitPurpleType create(ResourceLocation id){
        return new HermitPurpleType(
                new StandStats.Builder()
                        .power(6)
                        .speed(10)
                        .range(3, 3)
                        .durability(14)
                        .precision(5)
                        .build(),

                new MovesetBuilder()
                        .addAbility("hp_vine", AddonStandAbilities.VINE)
                        .addAbility("hp_vine2",AddonStandAbilities.VINE, vine->vine.isSubAbility = true)
                        .addAbility("hp_grab",AddonStandAbilities.VINE_GRAB)
                        .addAbility("hp_vine_heavy",AddonStandAbilities.VINE_HEAVY)
                        .addAbility("hp_target", AddonStandAbilities.SELECT_TARGET)
                        .addAbility("hp_doxx", AddonStandAbilities.MAP_DIVINATION)
                        .addAbility("hp_compass",AddonStandAbilities.COMPASS_DIVINATION, compass -> compass.isSubAbility = true)
                        .addAbility("hp_block", AddonStandAbilities.THORNS)
                        .addAbility("cringe", AddonStandAbilities.CRINGE)


                        .makeControlScheme("hotbar")
                        .bind("hp_vine", InputMethod.CLICK,InputKey.LMB)
                        .bind("hp_grab",InputMethod.HOLD,InputKey.LMB)
                        .bind("hp_vine_heavy",InputMethod.CLICK,InputKey.RMB)

                        .makeHotbar(0, ModStands.USE_SPECIAL, ModStands.SWITCH_SPECIAL)
                        .addToHotbar("hp_target",0,InputMethod.CLICK)
                        .addToHotbar("hp_doxx",0,InputMethod.CLICK)
                        .addToHotbar("hp_block",0,InputMethod.HOLD)
                        .addToHotbar("cringe",0,InputMethod.CLICK)

                        .finalizeControlScheme()

                        .addSkill(StandUnlockableSkill.startingAbility("hp_vine"))
                        .addSkill(StandUnlockableSkill.unlockableAbility("hp_grab",50))
                        .addSkill(StandUnlockableSkill.startingAbility("hp_vine_heavy"))
                        .addSkill(StandUnlockableSkill.startingAbility("hp_doxx"))
                        .addSkill(StandUnlockableSkill.startingAbility("hp_target"))
                        .addSkill(StandUnlockableSkill.unlockableAbility("hp_block",50))
                        .addSkill(StandUnlockableSkill.unlockableAbility("cringe",20))
                ,id
        );
    }

}
