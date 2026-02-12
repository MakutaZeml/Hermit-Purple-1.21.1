package com.zeml.ripplez_hp.core.packets.client;

import com.zeml.ripplez_hp.core.HermitPackets;
import com.zeml.ripplez_hp.init.AddonDataAttachmentTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public class SetColorPacket implements CustomPacketPayload {
    private final int color;

    public SetColorPacket(int color) {
        this.color = color;
    }


    private static Type<SetColorPacket> type;

    public static class Handler implements HermitPackets.PacketOGHandler<SetColorPacket> {

        public Handler(ResourceLocation packetId) {
            type = new Type<>(packetId);
        }

        @Override
        public Type<SetColorPacket> type() {
            return type;
        }

        @Override
        public void encode(SetColorPacket packet, RegistryFriendlyByteBuf buf) {
            buf.writeInt(packet.color);
        }

        @Override
        public SetColorPacket decode(RegistryFriendlyByteBuf buf) {
            return new SetColorPacket(buf.readInt());
        }

        @Override
        public void handle(SetColorPacket payload, IPayloadContext context) {
            Player player = context.player();
            player.getData(AddonDataAttachmentTypes.HERMIT_DATA).setColor(payload.color);
        }

    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return type;
    }

}
