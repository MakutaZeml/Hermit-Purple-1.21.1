package com.zeml.ripplez_hp.core.packets.server;

import com.github.standobyte.jojo.client.ClientProxy;
import com.zeml.ripplez_hp.core.HermitPackets;
import com.zeml.ripplez_hp.init.AddonDataAttachmentTypes;
import com.zeml.ripplez_hp.jojoimpl.stands.hermitpurple.HermitTargetData;
import com.zeml.ripplez_hp.mc.attachments.WeakenedBlockData;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

public record WeakedBlockPacket(int sender, BlockPos pos, boolean added) implements CustomPacketPayload {
    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return type;
    }


    private static CustomPacketPayload.Type<WeakedBlockPacket> type;

    public static class Handler implements HermitPackets.PacketOGHandler<WeakedBlockPacket>{

        public Handler(ResourceLocation packetId) {
            type = new CustomPacketPayload.Type<>(packetId);
        }

        @Override
        public Type<WeakedBlockPacket> type() {
            return type;
        }


        @Override
        public void encode(WeakedBlockPacket packet, RegistryFriendlyByteBuf buf) {
            buf.writeInt(buf.readInt());
            buf.writeBlockPos(packet.pos);
            buf.writeBoolean(packet.added);
        }

        @Override
        public WeakedBlockPacket decode(RegistryFriendlyByteBuf buf) {
            return new WeakedBlockPacket(buf.readInt(), buf.readBlockPos(), buf.readBoolean());
        }

        @Override
        public void handle(WeakedBlockPacket payload, IPayloadContext context) {
            Entity entity = ClientProxy.getEntityById(payload.sender);
            if(entity != null){
                WeakenedBlockData data =  entity.level().getData(AddonDataAttachmentTypes.WEAK);
                if(payload.added){
                    data.addWeakBlocks(payload.pos);
                }else {
                    data.removeWeakBlock(payload.pos);
                }
            }
        }
    }
}