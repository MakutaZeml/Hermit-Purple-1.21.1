package com.zeml.ripplez_hp.jojoimpl.stands.hermitpurple;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class HermitPurpleBlock {


    public static void blockInteraction(Level level, BlockPos blockPos){
        BlockState blockState = level.getBlockState(blockPos);
        if(!level.isClientSide){
            if(blockState.getOptionalValue(BlockStateProperties.LIT).isPresent() && !(blockState.is(BlockTags.CAMPFIRES) || blockState.is(BlockTags.CANDLES))) {
                boolean turning = !blockState.getValue(BlockStateProperties.LIT);
                BlockState newBlock = blockState.setValue(BlockStateProperties.LIT,turning);
                level.setBlock(blockPos,newBlock,2);
            }
            if(blockState.getOptionalValue(BlockStateProperties.DISARMED).isPresent()){
                boolean turning = !blockState.getValue(BlockStateProperties.DISARMED);
                BlockState newBlock = blockState.setValue(BlockStateProperties.DISARMED,turning);
                level.setBlock(blockPos,newBlock,2);
            }
            if(blockState.getOptionalValue(BlockStateProperties.LOCKED).isPresent()){
                boolean turning = !blockState.getValue(BlockStateProperties.LOCKED);
                BlockState newBlock = blockState.setValue(BlockStateProperties.LOCKED,turning);
                level.setBlock(blockPos,newBlock,2);
            }
            if(blockState.getOptionalValue(BlockStateProperties.TRIGGERED).isPresent()){
                boolean turning = !blockState.getValue(BlockStateProperties.TRIGGERED);
                BlockState newBlock = blockState.setValue(BlockStateProperties.TRIGGERED,turning);
                level.setBlock(blockPos,newBlock,2);
            }
        }
    }
}
