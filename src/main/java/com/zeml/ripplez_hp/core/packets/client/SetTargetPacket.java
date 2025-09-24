package com.zeml.ripplez_hp.core.packets.client;

import com.github.standobyte.jojo.core.PacketsRegister;
import com.github.standobyte.jojo.core.packet.fromserver.TrAimTargetPacket;
import com.github.standobyte.jojo.powersystem.entityaction.LivingComponentAction;
import com.github.standobyte.jojo.powersystem.standpower.entity.StandEntity;
import com.github.standobyte.jojo.util.StandUtil;
import com.github.standobyte.jojo.util.target.ActionTarget;
import com.zeml.ripplez_hp.core.HermitPackets;
import com.zeml.ripplez_hp.init.AddonDataAttachmentTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class SetTargetPacket implements CustomPacketPayload {
    private final int mode;
    private final String target;

    public SetTargetPacket(int mode, String target) {
        this.mode = mode;
        this.target = target;
    }





    private static CustomPacketPayload.Type<SetTargetPacket> type;

    public static class Handler implements HermitPackets.PacketOGHandler<SetTargetPacket> {

        public Handler(ResourceLocation packetId) {
            type = new CustomPacketPayload.Type<>(packetId);
        }

        @Override
        public Type<SetTargetPacket> type() {
            return type;
        }

        @Override
        public void encode(SetTargetPacket packet, RegistryFriendlyByteBuf buf) {
            buf.writeInt(packet.mode);
            buf.writeUtf(packet.target);
        }

        @Override
        public SetTargetPacket decode(RegistryFriendlyByteBuf buf) {
            return new SetTargetPacket(buf.readInt(), buf.readUtf());
        }

        @Override
        public void handle(SetTargetPacket payload, IPayloadContext context) {
            Player player = context.player();
            player.setData(AddonDataAttachmentTypes.MODE, payload.mode);
            player.setData(AddonDataAttachmentTypes.TARGET, payload.target);
        }

    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return type;
    }

}
