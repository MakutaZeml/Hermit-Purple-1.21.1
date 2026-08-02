package com.zeml.ripplez_hp.core.packets.server;

import com.github.standobyte.jojo.client.ClientProxy;
import com.github.standobyte.jojo.powersystem.standpower.StandPower;
import com.zeml.ripplez_hp.core.HermitPackets;
import com.zeml.ripplez_hp.init.AddonDataAttachmentTypes;
import com.zeml.ripplez_hp.init.power.AddonStandAbilities;
import com.zeml.ripplez_hp.jojoimpl.stands.emperor.GuidedBulletEffect;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record AnotherGuidedPacket(int entityID, int targetID) implements CustomPacketPayload {
    @Override
    public Type<? extends CustomPacketPayload> type() {
        return type;
    }

    private static CustomPacketPayload.Type<AnotherGuidedPacket> type;

    public static class Handler implements HermitPackets.PacketOGHandler<AnotherGuidedPacket>{

        public Handler(ResourceLocation packetId){
            type = new CustomPacketPayload.Type<>(packetId);
        }

        @Override
        public Type<AnotherGuidedPacket> type() {
            return type;
        }

        @Override
        public void handle(AnotherGuidedPacket payload, IPayloadContext context) {
            Entity entity = ClientProxy.getEntityById(payload.entityID);
            Entity target = ClientProxy.getEntityById(payload.targetID());
            if(entity instanceof LivingEntity user && target instanceof LivingEntity livingEntity){
                StandPower power = StandPower.get(user);
                if(power != null){
                    GuidedBulletEffect effect = AddonStandAbilities.GUIDED_BULLET.get().create(user.level());
                    power.userStandEffects.addEffect(effect.withTarget(livingEntity));
                }
            }
        }

        @Override
        public void encode(AnotherGuidedPacket packet, RegistryFriendlyByteBuf buf) {
            buf.writeInt(packet.entityID);
            buf.writeInt(packet.targetID);
        }

        @Override
        public AnotherGuidedPacket decode(RegistryFriendlyByteBuf buf) {
            return new AnotherGuidedPacket(buf.readInt(),buf.readInt());
        }
    }
}
