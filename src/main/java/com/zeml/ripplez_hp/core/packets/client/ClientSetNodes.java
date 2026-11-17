package com.zeml.ripplez_hp.core.packets.client;

import com.github.standobyte.jojo.powersystem.standpower.StandPower;
import com.zeml.ripplez_hp.core.HermitPackets;
import com.zeml.ripplez_hp.init.power.AddonStandAbilities;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public class ClientSetNodes implements CustomPacketPayload {
    private final int nodes;

    public ClientSetNodes(int nodes) {
        this.nodes = nodes;
    }


    private static Type<ClientSetNodes> type;

    public static class Handler implements HermitPackets.PacketOGHandler<ClientSetNodes> {

        public Handler(ResourceLocation packetId) {
            type = new Type<>(packetId);
        }

        @Override
        public Type<ClientSetNodes> type() {
            return type;
        }

        @Override
        public void encode(ClientSetNodes packet, RegistryFriendlyByteBuf buf) {
            buf.writeInt(packet.nodes);
        }

        @Override
        public ClientSetNodes decode(RegistryFriendlyByteBuf buf) {
            return new ClientSetNodes(buf.readInt());
        }

        @Override
        public void handle(ClientSetNodes payload, IPayloadContext context) {
            Player player = context.player();
            StandPower power = StandPower.get(player);
            if(power != null){
                power.userStandEffects.getEffectOfType(AddonStandAbilities.NODES_CONTROL.get()).ifPresent(countNodesEffect -> countNodesEffect.setNodes(payload.nodes));
            }

        }

    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return type;
    }
}