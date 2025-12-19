package com.zeml.ripplez_hp.jojoimpl.stands.hermitpurple;

import com.github.standobyte.jojo.powersystem.Power;
import com.github.standobyte.jojo.powersystem.ability.AbilityId;
import com.github.standobyte.jojo.powersystem.ability.AbilityType;
import com.github.standobyte.jojo.powersystem.ability.AbilityUsageGroup;
import com.github.standobyte.jojo.powersystem.ability.condition.ConditionCheck;
import com.github.standobyte.jojo.powersystem.entityaction.ActionAnimIdentifier;
import com.github.standobyte.jojo.powersystem.entityaction.ActionPhase;
import com.github.standobyte.jojo.powersystem.entityaction.EntityActionInstance;
import com.github.standobyte.jojo.powersystem.entityaction.type.EntityActionType;
import com.zeml.ripplez_hp.core.HermitPurpleAddon;
import com.zeml.ripplez_hp.core.packets.server.StandSoundPacket;
import com.zeml.ripplez_hp.init.AddonDataAttachmentTypes;
import com.zeml.ripplez_hp.init.AddonSoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.LodestoneTracker;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.Optional;

public class HermitCompassAbility extends HermitAction{


    public HermitCompassAbility(AbilityType<?> abilityType, AbilityId abilityId) {
        super(abilityType, abilityId, HermitCompass::new);
        setDefaultPhaseLength(ActionPhase.WINDUP,10);
        setDefaultPhaseLength(ActionPhase.PERFORM,200);
        usageGroup = AbilityUsageGroup.UTILITY;
    }

    @Override
    public ConditionCheck checkConditions(Power<?> context) {
        ItemStack stack = context.getUser().getItemInHand(InteractionHand.OFF_HAND);
        ItemStack main = context.getUser().getMainHandItem();
        if(context.getUser() != null && stack.is(Items.COMPASS)){
            if((main.isEmpty() && stack.getCount()>1)||(stack.getCount()==1)){
                return super.checkConditions(context);

            }
        }
        return ConditionCheck.NEGATIVE;
    }

    @Override
    public void initActionFromConfig(EntityActionInstance action, Level level, LivingEntity standUser, LivingEntity standEntity) {
        super.initActionFromConfig(action, level, standUser, standEntity);
    }

    @Override
    public ActionAnimIdentifier getEntityAnim(EntityActionInstance action) {
        if(action.getPowerUser() instanceof  LivingEntity livingEntity && livingEntity.getMainArm() == HumanoidArm.LEFT){
            return ActionAnimIdentifier.getOrCreate(abilityId.nameInMoveset().concat("_l"),false);
        }
        return super.getEntityAnim(action);
    }

    public static class HermitCompass extends EntityActionInstance{
        private Entity entity;
        private ResourceKey<Level> dimension;
        private ItemStack brujula;
        public HermitCompass(EntityActionType ability) {
            super(ability);
        }

        @Override
        public void onButtonStopHold() {
            if (getPhase() != ActionPhase.RECOVERY) {
                setPhaseStart(ActionPhase.RECOVERY);
                syncPhaseChanges();
            }
        }

        @Override
        public void actionTick() {
            HermitPurpleAddon.getLogger().debug("Compass {} {} {} {}", this.entity == null,this.brujula == null,phase,curPhaseTick);
            if(phase == ActionPhase.PERFORM){
                if(!this.level().isClientSide && brujula != null && curPhaseTick % 20 == 1 && this.entity != null){
                    HermitPurpleAddon.getLogger().debug("is this {} {}", curPhaseTick, entity);
                    GlobalPos globalPos = new GlobalPos(this.dimension, this.entity.getOnPos());
                    LodestoneTracker tracker = new LodestoneTracker(Optional.of(globalPos), false);
                    this.brujula.set(DataComponents.LODESTONE_TRACKER, tracker);
                }
            }
        }

        @Override
        public void actionPerformStart() {
            if(!level().isClientSide){
                BlockPos blockPos = null;
                String target = null;
                LivingEntity user = performer;
                boolean self = false;
                ItemStack itemStack = user.getItemInHand(InteractionHand.OFF_HAND);
                if (itemStack.is(Items.COMPASS)) {
                    if (user.getData(AddonDataAttachmentTypes.HERMIT_DATA).getMode() < 4) {
                        Entity entityTarget = DoxingHelper.HPLivingObjectives(user);
                        if (entityTarget != null) {
                            this.entity = entityTarget;
                            blockPos = entityTarget.getOnPos();
                            target = entityTarget.getName().getString();
                            this.dimension = entityTarget.level().dimension();
                            self = entityTarget == user;
                        }
                    } else {
                        switch (user.getData(AddonDataAttachmentTypes.HERMIT_DATA).getMode()) {
                            case 4 -> {
                                blockPos = DoxingHelper.structurePos(user);
                                String data = user.getData(AddonDataAttachmentTypes.HERMIT_DATA).getTarget().split(":")[1];
                                data = data.replace("_", " ");
                                target = data;
                            }
                            case 5 -> {
                                blockPos = DoxingHelper.biomesPos(user);
                                String biome = "biome.";
                                target = Component.translatable(biome.concat(user.getData(AddonDataAttachmentTypes.HERMIT_DATA).getTarget().replace(":", "."))).getString();
                            }
                        }
                    }
                    if (blockPos != null) {
                        itemStack.shrink(1);
                        ItemStack compass = new ItemStack(Items.COMPASS);
                        if (user.getData(AddonDataAttachmentTypes.HERMIT_DATA).getMode() < 4 && !self) {
                            if (this.dimension != null) {
                                GlobalPos globalPos = new GlobalPos(this.dimension, blockPos);
                                LodestoneTracker tracker = new LodestoneTracker(Optional.of(globalPos), false);
                                compass.set(DataComponents.LODESTONE_TRACKER, tracker);
                            }
                        } else if (self && user instanceof Player player) {
                            LodestoneTracker tracker = new LodestoneTracker(player.getLastDeathLocation(), false);
                            compass.set(DataComponents.LODESTONE_TRACKER, tracker);
                        } else {
                            GlobalPos globalPos = new GlobalPos(this.level().dimension(), blockPos);
                            LodestoneTracker tracker = new LodestoneTracker(Optional.of(globalPos), false);
                            compass.set(DataComponents.LODESTONE_TRACKER, tracker);
                        }
                        String displayName = "filled_map.divination";
                        compass.set(DataComponents.ITEM_NAME, Component.translatable(displayName, target));
                        user.setItemInHand(InteractionHand.MAIN_HAND,itemStack);
                        user.setItemInHand(InteractionHand.OFF_HAND, compass);
                        if(user.getData(AddonDataAttachmentTypes.HERMIT_DATA).getMode() > 4 || this.entity == performer){
                            setPhaseStart(ActionPhase.RECOVERY);
                        }else {
                            this.brujula = user.getItemInHand(InteractionHand.OFF_HAND);
                        }
                        PacketDistributor.sendToPlayersTrackingEntityAndSelf(user,
                                new StandSoundPacket(user.getId(), AddonSoundEvents.USER_HP,false,1,1));
                        PacketDistributor.sendToPlayersTrackingEntityAndSelf(user,
                                new StandSoundPacket(user.getId(),AddonSoundEvents.SUMMON_HP,true,1,1));

                    }
                }
            }

        }
    }
}
