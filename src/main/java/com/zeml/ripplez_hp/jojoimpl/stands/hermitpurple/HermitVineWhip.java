package com.zeml.ripplez_hp.jojoimpl.stands.hermitpurple;

import com.github.standobyte.jojo.client.ClientGlobals;
import com.github.standobyte.jojo.client.sound.ClientsideSoundsHelper;
import com.github.standobyte.jojo.init.ModDamageTypes;
import com.github.standobyte.jojo.init.ModSoundEvents;
import com.github.standobyte.jojo.powersystem.Moveset;
import com.github.standobyte.jojo.powersystem.Power;
import com.github.standobyte.jojo.powersystem.PowerClass;
import com.github.standobyte.jojo.powersystem.ability.Ability;
import com.github.standobyte.jojo.powersystem.ability.AbilityId;
import com.github.standobyte.jojo.powersystem.ability.AbilityType;
import com.github.standobyte.jojo.powersystem.ability.AbilityUsageGroup;
import com.github.standobyte.jojo.powersystem.ability.condition.AvailableAbilities;
import com.github.standobyte.jojo.powersystem.entityaction.ActionOBB;
import com.github.standobyte.jojo.powersystem.entityaction.ActionPhase;
import com.github.standobyte.jojo.powersystem.entityaction.EntityActionInstance;
import com.github.standobyte.jojo.powersystem.entityaction.LivingComponentAction;
import com.github.standobyte.jojo.powersystem.entityaction.type.EntityActionType;
import com.github.standobyte.jojo.powersystem.standpower.StandPower;
import com.github.standobyte.jojo.powersystem.standpower.entity.StandEntity;
import com.github.standobyte.jojo.powersystem.standpower.entity.StandOffsetFromUser;
import com.github.standobyte.jojo.powersystem.standpower.entity.StandStatFormulas;
import com.github.standobyte.jojo.subsystems.hitboxes.ExtendableOBB;
import com.github.standobyte.jojo.subsystems.hitboxes.OBBCollisionUtil;
import com.github.standobyte.jojo.subsystems.hitboxes.OrientedBoundingBox;
import com.github.standobyte.jojo.util.functions.DamageUtil;
import com.github.standobyte.jojo.util.functions.MathUtil;
import com.zeml.ripplez_hp.core.HermitPurpleAddon;
import com.zeml.ripplez_hp.core.packets.server.StandSoundPacket;
import com.zeml.ripplez_hp.init.AddonSoundEvents;
import net.minecraft.Util;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class HermitVineWhip extends HermitAction{

    public HermitVineWhip(AbilityType<?> abilityType, AbilityId abilityId) {
        super(abilityType, abilityId, HermitVine::new);
        usageGroup = AbilityUsageGroup.COMBAT;
        setDefaultPhaseLength(ActionPhase.WINDUP, 4);
        setDefaultPhaseLength(ActionPhase.PERFORM, 6);
        setDefaultPhaseLength(ActionPhase.RECOVERY, 5);

    }

    @Nullable
    @Override
    public Ability replaceWithSubAbility(Power<?> context, AvailableAbilities abilities) {
        StandPower standPower = PowerClass.STAND.cast(context);
        if (standPower != null) {
            Moveset moveset = standPower.getMoveset();

            Ability punch = getVineCombo(context.getUser(), moveset);
            if (punch != null) return punch;
        }

        return super.replaceWithSubAbility(context, abilities);
    }


    @Deprecated
    protected List<String> punchNames = Util.make(new ArrayList<>(), list -> {
        list.add("hp_vine");
        list.add("hp_vine2");

    });


    protected Ability getVineCombo(LivingEntity livingEntity, Moveset moveset){
        int startFromVine = 0;
        AbilityId curAbility = LivingComponentAction.getComponent(livingEntity).comboString.getLast();
        int size = punchNames.size();
        if (curAbility != null) {
            String actionName = curAbility.nameInMoveset();
            for (int i = 0; i < size; i++) {
                if (punchNames.get(i).equals(actionName)) {
                    startFromVine = i + 1;
                    break;
                }
            }
        }

        for (int i = 0; i < size; i++) {
            int index = (startFromVine + i) % size;
            String nextPunchName = punchNames.get(index);
            Ability nextPunch = moveset.getAbility(nextPunchName);
            if (nextPunch != null) {
                return nextPunch;
            }
        }

        return null;
    }


    @Override
    public void initActionFromConfig(EntityActionInstance action, Level level, LivingEntity standUser, LivingEntity standEntity) {
        super.initActionFromConfig(action, level, standUser, standEntity);
    }


    public static class HermitVine extends EntityActionInstance implements ActionOBB {
        public HermitVine(EntityActionType ability) {
            super(ability);
        }

        private ExtendableOBB vines;

        @Override
        public void onActionSet(@Nullable EntityActionInstance prevAction) {
            super.onActionSet(prevAction);
            OrientedBoundingBox obb = new OrientedBoundingBox(new Vec3(0, 1.35, 0), 1.5d, .5d, 8d, getPerformer().getYRot(), getPerformer().getXRot());
            this.vines = new ExtendableOBB(obb, 0.8F, (int) phasesLength.get(ActionPhase.PERFORM).floatValue(), 10, new Vec3(0, 1.35, 0));
        }

        @Override
        public void actionPerformStart() {
            LivingEntity user = getPowerUser();
            StandPower standPower = StandPower.get(user);
            standPower.consumeStamina(10);
        }

        @Override
        public void actionTick() {
            if (getPhase() == ActionPhase.PERFORM && extendableOBB() != null) {
                Vec3 pos = getPerformer().position();
                Vec3 offset = new Vec3(0.0, 1.5, 0.1)
                        .yRot(-getPerformer().yBodyRot * MathUtil.DEG_TO_RAD);

                if( LivingComponentAction.getComponent(performer).getAction().ability.getAbilityId().nameInMoveset().toString().contains("2")){
                    this.extendableOBB().updatePosition(level(), pos, offset, getPerformer().getXRot(), (getPerformer().getYRot()+30*(curPhaseTick-3)));
                }else {
                    this.extendableOBB().updatePosition(level(), pos, offset, getPerformer().getXRot(), (getPerformer().getYRot()-30*(curPhaseTick-3)));
                }
                if (!level().isClientSide()) {
                    StandPower standPower = StandPower.get(performer);
                    OBBCollisionUtil.getEntitiesInOBB(level(), this.extendableOBB().rotatableHitbox(), entity -> entity != getPerformer() && entity != getPowerUser() && performer.getVehicle() != entity).forEach(entity -> {
                        var damageType = DamageUtil.type(level(), ModDamageTypes.STAND_ATTACK);
                        DamageSource dmgSource = new DamageSource(damageType, performer);
                        float dmgAmount = StandStatFormulas.getLightAttackDamage(StandPower.get(performer).getPowerType().getStandStats().power());
                        if( Explosion.getSeenPercent(this.extendableOBB().rotatableHitbox().center, entity) != 0){
                            entity.hurt(dmgSource, dmgAmount );
                            if(standPower != null){
                                standPower.addExp(.05F);
                            }
                        }
                        // TODO Add hamon interaction when implemented and Block Interaction
                    });
                }
                this.extendableOBB().tick();

            }
        }




        @Override
        public void onSetPhase(ActionPhase newPhase) {
            Level level = level();
            if (newPhase == ActionPhase.PERFORM) {
                if(!level.isClientSide){
                    float pitch = (float) (.5+Math.random());
                    PacketDistributor.sendToPlayersTrackingEntityAndSelf(performer,new StandSoundPacket(performer.getId(), AddonSoundEvents.HP_VINE_SWING,true,1, pitch));
                }
            }
            if (newPhase == ActionPhase.RECOVERY) {
                vines = null;
            }
        }

        @Override
        public ExtendableOBB extendableOBB() {
            return vines;
        }
    }

}
