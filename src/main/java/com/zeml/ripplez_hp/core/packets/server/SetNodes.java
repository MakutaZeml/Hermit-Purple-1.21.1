package com.zeml.ripplez_hp.core.packets.server;

import com.github.standobyte.jojo.client.ClientProxy;
import com.github.standobyte.jojo.powersystem.standpower.StandPower;
import com.zeml.ripplez_hp.core.HermitPackets;
import com.zeml.ripplez_hp.init.AddonDataAttachmentTypes;
import com.zeml.ripplez_hp.init.power.AddonStandAbilities;
import com.zeml.ripplez_hp.mc.attachments.WeakenedBlockData;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record SetNodes(int entityID, int nodes) implements CustomPacketPayload {
    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return type;
    }


    private static CustomPacketPayload.Type<SetNodes> type;

    public static class Handler implements HermitPackets.PacketOGHandler<SetNodes>{

        public Handler(ResourceLocation packetId) {
            type = new CustomPacketPayload.Type<>(packetId);
        }

        @Override
        public Type<SetNodes> type() {
            return type;
        }


        @Override
        public void encode(SetNodes packet, RegistryFriendlyByteBuf buf) {
            buf.writeInt(packet.entityID);
            buf.writeInt(packet.nodes);

        }

        @Override
        public SetNodes decode(RegistryFriendlyByteBuf buf) {
            return new SetNodes(buf.readInt(), buf.readInt());
        }

        @Override
        public void handle(SetNodes payload, IPayloadContext context) {
            Entity entity = ClientProxy.getEntityById(payload.entityID);
            if(entity instanceof LivingEntity living){
                StandPower standPower = StandPower.get(living);
                if(standPower != null){
                    standPower.userStandEffects.getEffectOfType(AddonStandAbilities.NODES_CONTROL.get()).ifPresent(countNodesEffect -> countNodesEffect.setNodes(payload.nodes));
                }
            }
        }
    }
    
}