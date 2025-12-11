package com.zeml.ripplez_hp.jojoimpl.stands.hermitpurple.client.renderer;

import com.github.standobyte.jojo.client.entityanim.playerbend.IPlayerBendModel;
import com.github.standobyte.jojo.client.entityanim.playerbend.IPlayerLimbBend;
import com.github.standobyte.v1_21_4_stuff.Reminder;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.HumanoidArm;

import java.util.ArrayList;
import java.util.HashMap;

public class HermitPurpleModel extends HumanoidModel {
    public final ModelPart rightArmSlim;
    public final ModelPart leftArmSlim;

    private static final String[] BASE_HUMANOID_PARTS = new String[] { "head", "body", "right_arm", "left_arm", "right_leg", "left_leg", "right_arm_slim", "left_arm_slim" };
    protected static ModelPart addMissing(ModelPart root) {
        for (String basePartName : BASE_HUMANOID_PARTS) {
            root.children.putIfAbsent(basePartName, new ModelPart(new ArrayList<>(), new HashMap<>()));
        }
        Reminder.thatHatIsHeadChildNow();
        root/*.getChild("head")*/.children.putIfAbsent("hat", new ModelPart(new ArrayList<>(), new HashMap<>()));
        return root;
    }
    public HermitPurpleModel(ModelPart root) {
        super(addMissing(root));
        this.rightArmSlim = root.getChild("right_arm_slim");
        this.leftArmSlim = root.getChild("left_arm_slim");
        IPlayerBendModel thisBends = (IPlayerBendModel) this;
        ((IPlayerLimbBend) (Object) rightArmSlim).jojo_ripples$setBendBone(thisBends.jojo_ripples$animRightArmBend(), false);
        ((IPlayerLimbBend) (Object) leftArmSlim).jojo_ripples$setBendBone(thisBends.jojo_ripples$animLeftArmBend(), false);
    }
    
    @Override
    public void setAllVisible(boolean visible) {
        super.setAllVisible(visible);
        this.rightArmSlim.visible = visible;
        this.leftArmSlim.visible = visible;
    }

    public void setSlim(boolean slim){
        if (slim) {
            leftArm.visible = false;
            rightArm.visible = false;
        }
        else {
            leftArmSlim.visible = false;
            rightArmSlim.visible = false;
        }
    }

    @Override
    protected Iterable<ModelPart> bodyParts() {
        return Iterables.concat(super.bodyParts(), ImmutableList.of(rightArmSlim, leftArmSlim));
    }
    @Override
    protected ModelPart getArm(HumanoidArm side) {
        return switch (side) {
            case LEFT -> !leftArm.visible && leftArmSlim.visible ? leftArmSlim : leftArm;
            case RIGHT -> !rightArm.visible && rightArmSlim.visible ? rightArmSlim : rightArm;
        };
    }

    public void poseLayer(HumanoidModel<?> originalModel) {
        this.head.copyFrom(originalModel.head);
        this.body.copyFrom(originalModel.body);
        this.rightArm.copyFrom(originalModel.rightArm);
        this.leftArm.copyFrom(originalModel.leftArm);
        this.rightArmSlim.copyFrom(originalModel.rightArm);
        this.leftArmSlim.copyFrom(originalModel.leftArm);
        this.rightLeg.copyFrom(originalModel.rightLeg);
        this.leftLeg.copyFrom(originalModel.leftLeg);
    }
}
