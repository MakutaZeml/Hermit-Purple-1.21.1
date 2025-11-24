package com.zeml.ripplez_hp.core.packets.server;

import com.github.standobyte.jojo.client.ClientGlobals;
import com.github.standobyte.jojo.client.ClientProxy;
import com.github.standobyte.jojo.client.sound.ClientsideSoundsHelper;
import com.github.standobyte.jojo.client.sound.sounds.EntityLingeringSoundInstance;
import com.github.standobyte.jojo.powersystem.standpower.StandPower;
import com.zeml.ripplez_hp.core.HermitPackets;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record StandSoundPacket(int entityId, Holder<SoundEvent> sound, boolean onlyForStandUsers,
                               float volume, float pitch) implements CustomPacketPayload {

    private static CustomPacketPayload.Type<StandSoundPacket> type;

    public static class Handler implements HermitPackets.PacketCodecHandler<StandSoundPacket> {

        public Handler(ResourceLocation packetId) {
            type = new CustomPacketPayload.Type<>(packetId);
        }

        @Override
        public CustomPacketPayload.Type<StandSoundPacket> type() {
            return type;
        }

        @Override
        public StreamCodec<? super RegistryFriendlyByteBuf, StandSoundPacket> reader() {
            return STREAM_CODEC;
        }


        public static final StreamCodec<RegistryFriendlyByteBuf, StandSoundPacket> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.INT, StandSoundPacket::entityId,
                SoundEvent.STREAM_CODEC, StandSoundPacket::sound,
                ByteBufCodecs.BOOL, StandSoundPacket::onlyForStandUsers,
                ByteBufCodecs.FLOAT, StandSoundPacket::volume,
                ByteBufCodecs.FLOAT, StandSoundPacket::pitch,
                StandSoundPacket::new);

        @Override
        public void handle(StandSoundPacket payload, IPayloadContext context) {
            if (!payload.onlyForStandUsers || ClientGlobals.canHearStands) {
                SoundEvent soundEvent = payload.sound.value();
                if (soundEvent != null) {
                    Entity entity = ClientProxy.getEntityById(payload.entityId);
                    if (entity instanceof LivingEntity living) {
                        StandPower power = StandPower.get(living);
                        if(power != null){
                            EntityLingeringSoundInstance sound = new EntityLingeringSoundInstance(ClientsideSoundsHelper.withStandSkin(
                                    soundEvent, power),
                                    living.getSoundSource(), payload.volume, payload.pitch, living, living.level());
                            ClientsideSoundsHelper.playNonVanillaClassSound(sound);
                        }
                    }
                }
            }
        }

    }

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return type;
    }


}
