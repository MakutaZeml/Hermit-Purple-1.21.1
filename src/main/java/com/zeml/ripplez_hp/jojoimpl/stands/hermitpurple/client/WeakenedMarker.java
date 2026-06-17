package com.zeml.ripplez_hp.jojoimpl.stands.hermitpurple.client;

import com.github.standobyte.jojo.client.ui.marker.MarkerRenderer;
import com.github.standobyte.jojo.powersystem.standpower.StandPower;
import com.zeml.ripplez_hp.init.power.AddonStandAbilities;
import com.zeml.ripplez_hp.jojoimpl.stands.hermitpurple.WeakBlockEffect;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;

import java.util.List;

public class WeakenedMarker extends MarkerRenderer {

    public WeakenedMarker(Minecraft minecraft){
        super("hp_weak",minecraft);
        renderThroughBlocks = true;
        useStandSkinColor = true;

    }

    @Override
    protected boolean shouldRender() {
        LocalPlayer player = mc.player;
        StandPower power = StandPower.get(player);
        if(power != null && power.hasPower()){
            return power.userStandEffects.getEffectsOfType(AddonStandAbilities.WEAK_BLOCK_EFFECT.get()).findAny().isPresent();
        }
        return false;
    }

    @Override
    protected void updatePositions(List<MarkerInstance> list, float v) {
        LocalPlayer player = mc.player;
        StandPower power = StandPower.get(player);
        if(power != null && power.hasPower()){
            List<WeakBlockEffect> effects = power.userStandEffects.getEffectsOfType(AddonStandAbilities.WEAK_BLOCK_EFFECT.get()).toList();
            for (WeakBlockEffect effect: effects){
                if(effect.getCenter() != null){
                    list.add(new MarkerInstance(blockMarkerPos(effect.getCenter())));
                }
            }
        }
    }
}
