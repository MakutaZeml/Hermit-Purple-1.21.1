package com.zeml.ripplez_hp.jojoimpl.stands.hermitpurple.client.renderer;

import com.github.standobyte.jojo.client.entityanim.AnimationLoader;
import com.github.standobyte.jojo.client.entityanim.AnimationSet;
import com.github.standobyte.jojo.client.entityanim.RotpAnimDefinition;
import com.github.standobyte.jojo.client.entityanim.molang.AnimMolangQuery;
import com.github.standobyte.jojo.client.entityanim.pose.AnimFramePose;
import com.github.standobyte.jojo.powersystem.entityaction.ActionAnimIdentifier;
import com.zeml.ripplez_hp.init.power.AddonStands;
import net.minecraft.world.entity.LivingEntity;

public class HermitPurplePose {

    public static RotpAnimDefinition armorAnim() {
        AnimationSet animSet = AnimationLoader.getInstance().getAnimSet(AddonStands.HERMIT_PURPLE.getId());
        if (animSet == null) return null;
        return animSet.getNamedAnim(ActionAnimIdentifier.getOrCreate("stand_info", false));
    }

    public static AnimFramePose armorPose(LivingEntity living, float partialTick) {
        RotpAnimDefinition anim = armorAnim();
        if (anim == null) return null;
        return anim.calcAnimPose(0.0F, 1.0F, AnimMolangQuery.AnimMolangVariables.extract(living, partialTick), null);
    }
}
