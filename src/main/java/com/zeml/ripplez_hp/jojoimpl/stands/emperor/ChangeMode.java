package com.zeml.ripplez_hp.jojoimpl.stands.emperor;

import com.github.standobyte.jojo.powersystem.Power;
import com.github.standobyte.jojo.powersystem.ability.AbilityId;
import com.github.standobyte.jojo.powersystem.ability.AbilityType;
import com.github.standobyte.jojo.powersystem.ability.AbilityUsageGroup;
import com.github.standobyte.jojo.powersystem.ability.EntityActionAbility;
import com.github.standobyte.jojo.powersystem.entityaction.ActionPhase;
import com.github.standobyte.jojo.powersystem.entityaction.EntityActionInstance;
import com.github.standobyte.jojo.powersystem.entityaction.type.EntityActionType;
import com.zeml.ripplez_hp.core.packets.server.GuidedPacket;
import com.zeml.ripplez_hp.init.AddonDataAttachmentTypes;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;

public class ChangeMode extends EntityActionAbility {


    public ChangeMode(AbilityType<?> abilityType, AbilityId abilityId) {
        super(abilityType, abilityId, Change::new);
        usageGroup = AbilityUsageGroup.SPECIAL;
        setDefaultPhaseLength(ActionPhase.WINDUP,5);
        setDefaultPhaseLength(ActionPhase.PERFORM,1);
        setDefaultPhaseLength(ActionPhase.RECOVERY,5);
    }

    public static class Change extends EntityActionInstance {
        public Change(EntityActionType ability) {
            super(ability);
        }


        @Override
        public void actionPerformStart() {
            if(!level().isClientSide ){
                boolean newBol = !performer.getData(AddonDataAttachmentTypes.GUIDED);
                performer.setData(AddonDataAttachmentTypes.GUIDED,newBol);
                if(performer instanceof ServerPlayer player) PacketDistributor.sendToPlayer(player,new GuidedPacket(player.getId(),newBol));
            }
        }
    }

    @Override
    public boolean isAbilityUnlocked(Power<?> context) {
        return isSkillUnlocked(context, "guide_emp");
    }
}