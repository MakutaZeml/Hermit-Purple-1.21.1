package com.zeml.ripplez_hp.core.packets.server;

import com.github.standobyte.jojo.client.ClientProxy;
import com.zeml.ripplez_hp.core.HermitPackets;
import com.zeml.ripplez_hp.init.AddonDataAttachmentTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record GuidedPacket(int entityID, boolean guided) implements CustomPacketPayload {

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return type;
    }

    private static CustomPacketPayload.Type<GuidedPacket> type;

    public static class Handler implements HermitPackets.PacketOGHandler<GuidedPacket>{

        public Handler(ResourceLocation packetId){
            type = new CustomPacketPayload.Type<>(packetId);
        }

        @Override
        public Type<GuidedPacket> type() {
            return type;
        }

        @Override
        public void handle(GuidedPacket payload, IPayloadContext context) {
            Entity entity = ClientProxy.getEntityById(payload.entityID);
            if(entity != null){
                entity.setData(AddonDataAttachmentTypes.GUIDED,payload.guided);
            }
        }

        @Override
        public void encode(GuidedPacket packet, RegistryFriendlyByteBuf buf) {
            buf.writeInt(packet.entityID);
            buf.writeBoolean(packet.guided);
        }

        @Override
        public GuidedPacket decode(RegistryFriendlyByteBuf buf) {
            return new GuidedPacket(buf.readInt(),buf.readBoolean());
        }
    }
}
