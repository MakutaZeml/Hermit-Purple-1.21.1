package com.zeml.ripplez_hp.jojoimpl.stands.hermitpurple;

import com.github.standobyte.jojo.entityattachment.SynchronizableEntityData;
import com.zeml.ripplez_hp.core.HermitPackets;
import com.zeml.ripplez_hp.core.packets.server.HermitTargetDataPacket;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.Set;

public class HermitTargetData implements SynchronizableEntityData {
    protected int mode;
    protected String target;
    protected int color;
    protected Set<ResourceLocation> structures;

    public HermitTargetData(){
        this(0,"",0xF070D0, null);
    }

    public HermitTargetData(int mode, String target, int color, Set<ResourceLocation> structures){
        this.mode = mode;
        this.target = target;
        this.color = color;
        this.structures = structures;
    }

    public int getMode() {
        return this.mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public String getTarget() {
        return this.target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public int getColor() {
        return this.color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public Set<ResourceLocation> getStructures() {
        return this.structures;
    }

    public void setStructures(Set<ResourceLocation> structures) {
        this.structures = structures;
    }

    @Override
    public void syncToTracking(ServerPlayer serverPlayer) {
        PacketDistributor.sendToPlayer(serverPlayer,new HermitTargetDataPacket(serverPlayer.getId(),this.mode, this.target,this.color,this.structures));
    }
}
