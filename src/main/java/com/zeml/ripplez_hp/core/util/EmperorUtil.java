package com.zeml.ripplez_hp.core.util;

import com.github.standobyte.jojo.init.ModItemDataComponents;
import com.github.standobyte.jojo.powersystem.standpower.StandPower;
import com.zeml.ripplez_hp.core.HermitPurpleAddon;
import com.zeml.ripplez_hp.init.AddonItems;
import com.zeml.ripplez_hp.init.HermitDataComponents;
import com.zeml.ripplez_hp.mc.item.component.EmperorGunData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.HashMap;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

public class EmperorUtil {
    protected static HashMap<ResourceLocation, ItemStack> cache = new HashMap<>();
    public static final ResourceLocation model = HermitPurpleAddon.resLoc("emperor");
    public static void giveEmperor(LivingEntity user, StandPower standPower){
        ItemStack emperor = new ItemStack(AddonItems.EMPEROR.asItem());
        emperor.set(HermitDataComponents.EMPEROR.get(), new EmperorGunData(Optional.of(user.getUUID()), Optional.empty(),0));
        if(!user.getItemInHand(InteractionHand.MAIN_HAND).isEmpty()){
            if(user.getItemInHand(InteractionHand.OFF_HAND).isEmpty()){
                user.setItemInHand(InteractionHand.OFF_HAND,emperor);
            }else {
                ItemStack itemStack = user.getItemInHand(InteractionHand.MAIN_HAND);
                ItemEntity item = new ItemEntity(user.level(),user.getX(),user.getY(),user.getZ(),itemStack);
                user.level().addFreshEntity(item);
                user.setItemInHand(InteractionHand.MAIN_HAND,emperor);
            }
        }else {
            user.setItemInHand(InteractionHand.MAIN_HAND,emperor);
        }
    }

    private static ItemStack standSkinEmperor(StandPower power){
        return cache.computeIfAbsent(model,modelPath->{
            ItemStack emperor = new ItemStack(AddonItems.EMPEROR.asItem());
            if (modelPath != null) {
                emperor.set(ModItemDataComponents.ITEM_MODEL.get(), modelPath);
            }
            return emperor;
        });
    }


    public static boolean noEmperor(LivingEntity user){
        if(user instanceof Player){
            AtomicInteger count = new AtomicInteger(0);
            ((Player) user).getInventory().items.forEach(itemStack -> {
                if(itemStack.get(HermitDataComponents.EMPEROR) != null){
                    EmperorGunData data = itemStack.get(HermitDataComponents.EMPEROR);
                    if(data.getUuidOwner().isPresent() && data.getUuidOwner().get() == user.getUUID()){
                        count.set(count.get()+1);
                    }
                }
            });
            return count.get() == 0;
        }else {
            return false;
        }
    }

}
