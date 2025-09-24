package com.zeml.ripplez_hp.client;

import com.github.standobyte.jojo.core.JojoMod;
import com.zeml.ripplez_hp.client.ui.screen.HPScreenTargetSelect;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;


import java.util.Map;

@EventBusSubscriber(modid = JojoMod.MOD_ID, value = Dist.CLIENT)
public class ClientSetUp {

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onClientSetup0(FMLClientSetupEvent event){

    }

}
