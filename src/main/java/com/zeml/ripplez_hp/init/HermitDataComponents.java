package com.zeml.ripplez_hp.init;

import com.github.standobyte.jojo.core.JojoMod;
import com.zeml.ripplez_hp.core.HermitPurpleAddon;
import com.zeml.ripplez_hp.mc.item.component.EmperorGunData;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.ComponentSerialization;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class HermitDataComponents {
    public static final DeferredRegister.DataComponents DATA_COMPONENT_TYPES = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, HermitPurpleAddon.MOD_ID);


    public static final Supplier<DataComponentType<EmperorGunData>> EMPEROR = DATA_COMPONENT_TYPES.registerComponentType("emperor",
            builder -> builder.persistent(EmperorGunData.CODEC).networkSynchronized(EmperorGunData.STREAM_CODEC).cacheEncoding());
}