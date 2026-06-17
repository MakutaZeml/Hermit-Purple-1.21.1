package com.zeml.ripplez_hp.client.gui;

import com.github.standobyte.jojo.client.ui.marker.MarkerRenderer;
import com.zeml.ripplez_hp.jojoimpl.stands.hermitpurple.client.WeakenedMarker;
import net.minecraft.client.Minecraft;

public class AddonMarkers {

    public static void registerMarkers(Minecraft mc){
        MarkerRenderer.registerMarkerRenderer(new WeakenedMarker(mc));
    }
}
