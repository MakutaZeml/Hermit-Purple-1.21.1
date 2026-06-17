package com.zeml.ripplez_hp.jojoimpl.stands.hermitpurple;

import com.github.standobyte.jojo.entityattachment.custom_effect.EntityCustomEffectType;
import com.github.standobyte.jojo.powersystem.standpower.effect.StandEffectInstance;
import com.github.standobyte.jojo.powersystem.standpower.entity.StandStatFormulas;
import com.zeml.ripplez_hp.core.HermitPurpleAddon;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class WeakBlockEffect extends StandEffectInstance {
    private BlockPos center;
    private final List<BlockPos> affectedBlocks = new ArrayList<>();
    public WeakBlockEffect(@NotNull EntityCustomEffectType<?> effectType) {
        super(effectType);
        removeOnUserLogout = false;
    }

    @Override
    protected void start() {

    }

    @Override
    protected void tick() {
        if(center != null){
            if(level.getBlockState(center).is(BlockTags.AIR)|| level.getBlockState(center).liquid()){
                this.remove();
            }
            AABB aabb = new AABB(center.getX()-1,center.getY()-1,center.getZ()-1,center.getX()+2,center.getY()+2,center.getZ()+2);
            List<Entity> entities = level.getEntities(null,aabb).stream().filter(entity1 -> entity1 instanceof LivingEntity && entity1 != getStandUser()).toList();
            if(!entities.isEmpty()){
                for (Entity ent: entities){
                        if(ent.mainSupportingBlockPos.map(affectedBlocks::contains).orElse(false)){
                            for (BlockPos pos : affectedBlocks) {
                                level.destroyBlock(pos, true);
                            }
                            break;
                        }
                }
            }
        }
    }

    @Override
    protected void stop() {
    }

    public BlockPos getCenter() {
        return this.center;
    }

    public WeakBlockEffect setCenter(BlockPos blockPos, StandStatFormulas.BlockMiningTier tier){
        center = blockPos;
        Iterable<BlockPos> pos = BlockPos.betweenClosed(blockPos.offset(-1,-1,-1),blockPos.offset(1,1,1));
        pos.forEach(position ->{
            if(!level.getBlockState(position).isAir() && !level.getBlockState(position).liquid() &&
                    tier.canMine(level.getBlockState(position))){
                affectedBlocks.add(position.immutable());
            }
        });
        return this;
    }


    @Override
    protected void writeAdditionalSaveData(CompoundTag nbt) {
        super.writeAdditionalSaveData(nbt);
        nbt.put("center", NbtUtils.writeBlockPos(center));
        nbt.putInt("amount",affectedBlocks.size());
        for(int i=0;i< affectedBlocks.size();i++){
            String sex = "blockPos";
            String nbtName = sex.concat(String.valueOf(i));
            nbt.put(nbtName,NbtUtils.writeBlockPos(affectedBlocks.get(i)));
        }
    }


    @Override
    protected void readAdditionalSaveData(CompoundTag nbt) {
        super.readAdditionalSaveData(nbt);
        Optional<BlockPos> posOptional =  NbtUtils.readBlockPos(nbt,"center");
        posOptional.ifPresent(blockPos -> this.center = blockPos);
        int size = nbt.getInt("amount");
        for (int i =0;i<size;i++){
            String sex = "blockPos";
            String nbtName = sex.concat(String.valueOf(i));
            Optional<BlockPos> stuff = NbtUtils.readBlockPos(nbt,nbtName);
            stuff.ifPresent(blockPos -> {
                if(!affectedBlocks.contains(blockPos)){
                    affectedBlocks.add(blockPos);
                }
            });
        }
    }


    @Override
    public void writeAdditionalPacketData(FriendlyByteBuf buf, boolean sendingToUser) {
        super.writeAdditionalPacketData(buf, sendingToUser);
        buf.writeBlockPos(center);
        buf.writeInt(affectedBlocks.size());
        for (BlockPos pos : affectedBlocks) {
            buf.writeBlockPos(pos);
        }
    }

    @Override
    public void readAdditionalPacketData(FriendlyByteBuf buf, boolean clientIsUser) {
        super.readAdditionalPacketData(buf, clientIsUser);
        this.center = buf.readBlockPos();
        int size = buf.readInt();
        for (int i=0;i<size;i++){
            BlockPos pos = buf.readBlockPos();
            affectedBlocks.add(pos);
        }
    }

    @Override
    public void syncWithTrackingOrUser(ServerPlayer user) {
        super.syncWithTrackingOrUser(user);
    }
}
