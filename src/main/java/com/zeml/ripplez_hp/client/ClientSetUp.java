package com.zeml.ripplez_hp.client;

import com.github.standobyte.jojo.client.standskin.StandSkinsScreen;
import com.zeml.ripplez_hp.client.gui.AddonMarkers;
import com.zeml.ripplez_hp.core.HermitPurpleAddon;
import com.zeml.ripplez_hp.init.power.stand.StandInitEmperor;
import com.zeml.ripplez_hp.init.power.stand.StandInitHermitPurple;
import com.zeml.ripplez_hp.jojoimpl.stands.emperor.client.EmperorSkinView;
import com.zeml.ripplez_hp.jojoimpl.stands.hermitpurple.client.HermitSkinView;

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

		StandSkinsScreen.skinViewTypes.put(StandInitHermitPurple.SKIN_VIEW_TYPE, HermitSkinView::new);
		StandSkinsScreen.skinViewTypes.put(StandInitEmperor.SKIN_VIEW_TYPE, EmperorSkinView::new);
    }

}
