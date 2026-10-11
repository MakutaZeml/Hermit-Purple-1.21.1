package com.zeml.ripplez_hp.mc.attachments;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class WeakenedBlockData {
    Set<BlockPos> weakBlocks;

    public WeakenedBlockData(){
        weakBlocks = new HashSet<>();
    }

    public Set<BlockPos> getWeakBlocks() {
        return weakBlocks;
    }

    public void addWeakBlocks(BlockPos pos){
        weakBlocks.add(pos.immutable());
    }

    public void removeWeakBlock(BlockPos pos){
        weakBlocks.remove(pos);
    }

    public boolean hasBlock(BlockPos pos){
        return weakBlocks.contains(pos);
    }

    public static final Codec<WeakenedBlockData> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    BlockPos.CODEC.listOf().optionalFieldOf("weak_blocks", List.of())
                            .forGetter(data -> List.copyOf(data.getWeakBlocks()))
            ).apply(instance, list -> {
                WeakenedBlockData data = new WeakenedBlockData();
                list.forEach(data::addWeakBlocks);
                return data;
            })

    );
    @Override
    public String toString() {
        return super.toString().concat(" weak ").concat(weakBlocks.toString());
    }
}