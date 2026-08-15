package com.zeml.ripplez_hp.jojoimpl.stands.hermitpurple.client.renderer;

import com.github.standobyte.jojo.client.entityrender.HiddenModelPartsUtil;
import com.github.standobyte.jojo.client.entityrender.entities.HumanoidLikeModel;
import com.github.standobyte.v1_21_4_stuff.missingmethods.Model_1_21_2plus;

import net.minecraft.client.model.geom.ModelPart;

public class HermitPurpleVinesModel extends HumanoidLikeModel {

    public final ModelPart main_vine;
    public final ModelPart main_vine2;
    public final ModelPart main_vine3;
    
    public HermitPurpleVinesModel(ModelPart root) {
        super(root);
        
        this.main_vine = ((Model_1_21_2plus) this).jojo_ripples$getAnyDescendantWithName("hidden.main_vine").orElse(null);
        this.main_vine2 = ((Model_1_21_2plus) this).jojo_ripples$getAnyDescendantWithName("hidden.second_vine").orElse(null);
        this.main_vine3 = ((Model_1_21_2plus) this).jojo_ripples$getAnyDescendantWithName("hidden.third_vine").orElse(null);


        HiddenModelPartsUtil.initHiddenParts(this);
    }
}
