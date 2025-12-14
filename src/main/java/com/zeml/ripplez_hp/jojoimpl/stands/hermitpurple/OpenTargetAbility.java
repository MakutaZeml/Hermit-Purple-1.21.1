package com.zeml.ripplez_hp.jojoimpl.stands.hermitpurple;

import com.github.standobyte.jojo.client.ClientProxy;
import com.github.standobyte.jojo.powersystem.ability.AbilityId;
import com.github.standobyte.jojo.powersystem.ability.AbilityType;
import com.github.standobyte.jojo.powersystem.ability.AbilityUsageGroup;
import com.github.standobyte.jojo.powersystem.ability.EntityActionAbility;
import com.github.standobyte.jojo.powersystem.ability.controls.InputMethod;
import com.github.standobyte.jojo.powersystem.entityaction.HeldInput;
import com.zeml.ripplez_hp.core.HermitPurpleAddon;
import com.zeml.ripplez_hp.core.packets.server.HermitTargetDataPacket;
import com.zeml.ripplez_hp.init.AddonDataAttachmentTypes;
import com.zeml.ripplez_hp.jojoimpl.stands.hermitpurple.client.HermitTargetScreen;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;

public class OpenTargetAbility extends EntityActionAbility {

    public OpenTargetAbility(AbilityType<?> abilityType, AbilityId abilityId) {
        super(abilityType, abilityId);
        usageGroup = AbilityUsageGroup.UTILITY;

    }

    @Override
    public HeldInput onKeyPress(Level level, LivingEntity user, FriendlyByteBuf extraClientInput, InputMethod inputMethod, float clickHoldResolveTime) {
        if(!level.isClientSide){
            HermitTargetData data = user.getData(AddonDataAttachmentTypes.HERMIT_DATA);
            data.setStructures(level.registryAccess().registryOrThrow(Registries.STRUCTURE).keySet());
            HermitPurpleAddon.getLogger().debug("keyPress {}", level.registryAccess().registryOrThrow(Registries.STRUCTURE).keySet());
            PacketDistributor.sendToPlayer((ServerPlayer) user, new HermitTargetDataPacket(user.getId(), data.mode, data.target, data.color,level.registryAccess().registryOrThrow(Registries.STRUCTURE).keySet()));
        }
        if(level.isClientSide){
            ClientProxy.openScreen(new HermitTargetScreen());
        }
        return super.onKeyPress(level, user, extraClientInput, inputMethod, clickHoldResolveTime);
    }
}
