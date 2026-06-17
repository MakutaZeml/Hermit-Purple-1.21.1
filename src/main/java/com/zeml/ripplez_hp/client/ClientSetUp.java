package com.zeml.ripplez_hp.client;

import com.github.standobyte.jojo.client.ModMarkers;
import com.github.standobyte.jojo.client.itemrender.ModItemModelOverrides;
import com.github.standobyte.jojo.core.JojoMod;
import com.github.standobyte.jojo.subsystems.entity_puppetcontrol.client.stand.StandHudElements;
import com.zeml.ripplez_hp.client.gui.AddonMarkers;
import com.zeml.ripplez_hp.core.HermitPurpleAddon;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

@EventBusSubscriber(modid = HermitPurpleAddon.MOD_ID, value = Dist.CLIENT)
public class ClientSetUp {

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onClientSetup0(FMLClientSetupEvent event){

    }

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        Minecraft mc = Minecraft.getInstance();
        AddonMarkers.registerMarkers(mc);
    }

}
