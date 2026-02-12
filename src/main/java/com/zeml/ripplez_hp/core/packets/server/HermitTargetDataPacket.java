package com.zeml.ripplez_hp.core.packets.server;

import com.github.standobyte.jojo.client.ClientProxy;
import com.zeml.ripplez_hp.core.HermitPackets;
import com.zeml.ripplez_hp.core.HermitPurpleAddon;
import com.zeml.ripplez_hp.init.AddonDataAttachmentTypes;
import com.zeml.ripplez_hp.jojoimpl.stands.hermitpurple.HermitTargetData;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

public record HermitTargetDataPacket(int userID,int mode, String target, int color, Set<ResourceLocation> structures) implements CustomPacketPayload {


    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return type;
    }


    private static CustomPacketPayload.Type<HermitTargetDataPacket> type;

    public static class Handler implements HermitPackets.PacketOGHandler<HermitTargetDataPacket>{

        public Handler(ResourceLocation packetId) {
            type = new CustomPacketPayload.Type<>(packetId);
        }

        @Override
        public Type<HermitTargetDataPacket> type() {
            return type;
        }


        @Override
        public void encode(HermitTargetDataPacket packet, RegistryFriendlyByteBuf buf) {
            buf.writeInt(packet.userID);
            buf.writeInt(packet.mode);
            buf.writeUtf(packet.target);
            buf.writeInt(packet.color);
            buf.writeInt(packet.structures.size());
            for (ResourceLocation structure : packet.structures){
                ResourceLocation.STREAM_CODEC.encode(buf,structure);
            }
        }

        @Override
        public HermitTargetDataPacket decode(RegistryFriendlyByteBuf buf) {
            int userID = buf.readInt();
            int mode = buf.readInt();
            String target = buf.readUtf();
            int color = buf.readInt();
            int times = buf.readInt();
            Set<ResourceLocation> structures = new HashSet<>();
            for (int i = 0; i < times; i++){
                structures.add(ResourceLocation.STREAM_CODEC.decode(buf));
            }
            return new HermitTargetDataPacket(userID,mode,target,color,structures);
        }

        @Override
        public void handle(HermitTargetDataPacket payload, IPayloadContext context) {
            Entity entity = ClientProxy.getEntityById(payload.userID);
            if(entity != null){
                entity.setData(AddonDataAttachmentTypes.HERMIT_DATA, new HermitTargetData(payload.mode, payload.target, payload.color,payload.structures));
            }
        }
    }


}
