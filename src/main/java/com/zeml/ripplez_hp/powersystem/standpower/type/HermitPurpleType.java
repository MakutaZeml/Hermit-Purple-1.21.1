package com.zeml.ripplez_hp.powersystem.standpower.type;

import com.github.standobyte.jojo.init.ModSoundEvents;
import com.github.standobyte.jojo.mechanics.voiceline.VoiceLineServerSide;
import net.minecraft.world.entity.LivingEntity;
import com.github.standobyte.jojo.powersystem.MovesetBuilder;
import com.github.standobyte.jojo.powersystem.standpower.StandPower;
import com.github.standobyte.jojo.powersystem.standpower.StandStats;
import com.github.standobyte.jojo.powersystem.standpower.type.StandType;

import net.minecraft.resources.ResourceLocation;

public class HermitPurpleType extends StandType {
    public HermitPurpleType(StandStats stats, MovesetBuilder moveset, ResourceLocation id) {
        super(stats, moveset, id);
    }

	@Override
	public boolean summon(LivingEntity user, StandPower standPower) {
		if(!user.level().isClientSide){
			if (!user.isShiftKeyDown()) {
				VoiceLineServerSide.play(user, ModSoundEvents.VOICELINE_STAND_SUMMON);
			}
		}
		return super.summon(user, standPower);
	}

}
