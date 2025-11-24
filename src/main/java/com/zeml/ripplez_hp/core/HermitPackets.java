package com.zeml.ripplez_hp.core;

import com.zeml.ripplez_hp.core.packets.client.SetTargetPacket;
import com.zeml.ripplez_hp.core.packets.server.HermitTargetDataPacket;
import com.zeml.ripplez_hp.core.packets.server.StandSoundPacket;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.handling.IPayloadHandler;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class HermitPackets {

    public static void register(RegisterPayloadHandlersEvent event){
        HermitPurpleAddon.getLogger().debug("IS this PAcketing?");
        PayloadRegistrar registrar = event.registrar("1");
        registerPacket(registrar,PayloadRegistrar::playToServer, new SetTargetPacket.Handler(HermitPurpleAddon.resLoc("hermit")));
        registerPacket(registrar,PayloadRegistrar::playToClient, new HermitTargetDataPacket.Handler(HermitPurpleAddon.resLoc("hermit_data")));
        registerPacket(registrar,PayloadRegistrar::playToClient, new StandSoundPacket.Handler(HermitPurpleAddon.resLoc("stand_sound")));

    }

    public static interface PacketHandler<T extends CustomPacketPayload> {
        CustomPacketPayload.Type<T> type();
        void handle(T payload, IPayloadContext context);
    }

    public static interface PacketOGHandler<T extends CustomPacketPayload> extends HermitPackets.PacketHandler<T> {
        void encode(T packet, RegistryFriendlyByteBuf buf);
        T decode(RegistryFriendlyByteBuf buf);
    }

    public static interface PacketCodecHandler<T extends CustomPacketPayload> extends HermitPackets.PacketHandler<T> {
        StreamCodec<? super RegistryFriendlyByteBuf, T> reader();
    }

    private static <T extends CustomPacketPayload> void registerPacket(PayloadRegistrar registrar, PacketType packetType, HermitPackets.PacketOGHandler<T> handler) {
        packetType.register(registrar, handler.type(), StreamCodec.ofMember(handler::encode, handler::decode), handler::handle);
    }

    private static <T extends CustomPacketPayload> void registerPacket(PayloadRegistrar registrar, PacketType packetType, HermitPackets.PacketCodecHandler<T> handler) {
        packetType.register(registrar, handler.type(), handler.reader(), handler::handle);
    }

    @FunctionalInterface
    private static interface PacketType {
        <T extends CustomPacketPayload> void register(PayloadRegistrar registrar,
                                                      CustomPacketPayload.Type<T> type,
                                                      StreamCodec<? super RegistryFriendlyByteBuf, T> reader,
                                                      IPayloadHandler<T> handler);
    }
}
