package com.zeml.ripplez_hp.jojoimpl.stands.hermitpurple.grapple;

import com.github.standobyte.jojo.entityattachment.custom_effect.EntityCustomEffectType;
import com.github.standobyte.jojo.powersystem.standpower.effect.StandEffectInstance;
import com.zeml.ripplez_hp.core.HermitPurpleAddon;
import com.zeml.ripplez_hp.core.packets.client.ClientRemoveNodes;
import net.minecraft.network.FriendlyByteBuf;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

public class CountNodesEffect extends StandEffectInstance {
    private int nodes = 0;

    public CountNodesEffect(@NotNull EntityCustomEffectType<?> effectType) {
        super(effectType);
    }

    @Override
    protected void start() {

    }

    @Override
    protected void tick() {
        if (userPower != null) {
            if (!userPower.isSummoned()) {
                this.remove();
                if (level.isClientSide) {
                    PacketDistributor.sendToServer(new ClientRemoveNodes());
                }
            }
        }
        HermitPurpleAddon.getLogger().debug("nodes sync shit {} {}", nodes, !level.isClientSide);
    }

    @Override
    protected void stop() {

    }

    public void addNode(int nodes) {
        this.nodes += nodes;
    }

    public void setNodes(int nodes) {
        this.nodes = nodes;
    }

    public int getNodes() {
        return nodes;
    }

    @Override
    public void writeAdditionalPacketData(FriendlyByteBuf buf, boolean sendingToUser) {
        super.writeAdditionalPacketData(buf, sendingToUser);
        buf.writeInt(nodes);
    }

    @Override
    public void readAdditionalPacketData(FriendlyByteBuf buf, boolean clientIsUser) {
        super.readAdditionalPacketData(buf, clientIsUser);
        nodes = buf.readInt();
    }
}