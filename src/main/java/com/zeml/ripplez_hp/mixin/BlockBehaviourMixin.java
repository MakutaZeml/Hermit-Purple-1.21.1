package com.zeml.ripplez_hp.mixin;

import com.mojang.serialization.MapCodec;
import com.zeml.ripplez_hp.core.HermitPurpleAddon;
import com.zeml.ripplez_hp.init.AddonDataAttachmentTypes;
import it.unimi.dsi.fastutil.objects.Reference2ObjectArrayMap;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateHolder;
import net.minecraft.world.level.block.state.properties.Property;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = BlockBehaviour.BlockStateBase.class)
public class BlockBehaviourMixin extends StateHolder<Block, BlockState> {

    protected BlockBehaviourMixin(Block owner, Reference2ObjectArrayMap<Property<?>, Comparable<?>> values, MapCodec<BlockState> propertiesCodec) {
        super(owner, values, propertiesCodec);
    }

    @Inject(method = "getDestroySpeed",
            at = @At(value = "RETURN"), cancellable = true)
    private void onGetDestroySpeed(BlockGetter level, BlockPos pos, CallbackInfoReturnable<Float> cl){
        if(level instanceof Level world){
            if(world.getData(AddonDataAttachmentTypes.WEAK).hasBlock(pos) && cl.getReturnValue() > 0){
                HermitPurpleAddon.getLogger().debug("sex {} {} {}",cl.getReturnValue() ,pos, cl.getReturnValue()/2);
                cl.setReturnValue(cl.getReturnValue()/2);
            }
        }
    }



}