package com.zeml.ripplez_hp.core.packets.client;

import com.github.standobyte.jojo.entityattachment.custom_effect.EntityCustomEffect;
import com.github.standobyte.jojo.powersystem.standpower.StandPower;
import com.zeml.ripplez_hp.core.HermitPackets;
import com.zeml.ripplez_hp.init.power.AddonStandAbilities;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public class ClientRemoveNodes implements CustomPacketPayload {

    public ClientRemoveNodes() {
    }


    private static Type<ClientRemoveNodes> type;

    public static class Handler implements HermitPackets.PacketOGHandler<ClientRemoveNodes> {

        public Handler(ResourceLocation packetId) {
            type = new Type<>(packetId);
        }

        @Override
        public Type<ClientRemoveNodes> type() {
            return type;
        }

        @Override
        public void encode(ClientRemoveNodes packet, RegistryFriendlyByteBuf buf) {
        }

        @Override
        public ClientRemoveNodes decode(RegistryFriendlyByteBuf buf) {
            return new ClientRemoveNodes();
        }

        @Override
        public void handle(ClientRemoveNodes payload, IPayloadContext context) {
            Player player = context.player();
            StandPower power = StandPower.get(player);
            if(power != null){
                power.userStandEffects.getEffectsOfType(AddonStandAbilities.NODES_CONTROL.get()).forEach(EntityCustomEffect::remove);
            }

        }

    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return type;
    }
}