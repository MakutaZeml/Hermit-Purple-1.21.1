package com.zeml.ripplez_hp.jojoimpl.stands.hermitpurple;

import com.github.standobyte.jojo.client.ClientGlobals;
import com.github.standobyte.jojo.client.sound.ClientsideSoundsHelper;
import com.github.standobyte.jojo.client.sound.sounds.EntityLingeringSoundInstance;
import com.github.standobyte.jojo.client.standskin.StandSkin;
import com.github.standobyte.jojo.client.standskin.StandSkinsLoader;
import com.github.standobyte.jojo.powersystem.Power;
import com.github.standobyte.jojo.powersystem.PowerClass;
import com.github.standobyte.jojo.powersystem.ability.*;
import com.github.standobyte.jojo.powersystem.ability.condition.AvailableAbilities;
import com.github.standobyte.jojo.powersystem.ability.condition.ConditionCheck;
import com.github.standobyte.jojo.powersystem.ability.controls.InputMethod;
import com.github.standobyte.jojo.powersystem.ability.input.ActionInputBuffer;
import com.github.standobyte.jojo.powersystem.entityaction.ActionAnimIdentifier;
import com.github.standobyte.jojo.powersystem.entityaction.ActionPhase;
import com.github.standobyte.jojo.powersystem.entityaction.EntityActionInstance;
import com.github.standobyte.jojo.powersystem.entityaction.HeldInput;
import com.github.standobyte.jojo.powersystem.entityaction.type.EntityActionType;
import com.github.standobyte.jojo.powersystem.standpower.StandPower;
import com.github.standobyte.jojo.powersystem.standpower.entity.StandEntityAbility;
import com.mojang.datafixers.util.Pair;
import com.zeml.ripplez_hp.core.HermitPackets;
import com.zeml.ripplez_hp.core.HermitPurpleAddon;
import com.zeml.ripplez_hp.core.packets.client.SetColorPacket;
import com.zeml.ripplez_hp.core.packets.server.StandSoundPacket;
import com.zeml.ripplez_hp.init.AddonDataAttachmentTypes;
import com.zeml.ripplez_hp.init.AddonSoundEvents;
import com.zeml.ripplez_hp.init.power.AddonStandAbilities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.item.component.MapItemColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.saveddata.maps.MapDecorationType;
import net.minecraft.world.level.saveddata.maps.MapDecorationTypes;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MapDoxingAbility extends HermitAction {
    public MapDoxingAbility(AbilityType<?> abilityType, AbilityId abilityId) {
        super(abilityType, abilityId, DoxxingAction::new);
        usageGroup = AbilityUsageGroup.UTILITY;
        setDefaultPhaseLength(ActionPhase.WINDUP,20);
    }

    @Override
    public ConditionCheck checkConditions(Power<?> context) {
        HermitPurpleAddon.getLogger().debug("Condition {}",super.checkConditions(context) == ConditionCheck.POSITIVE);
        return super.checkConditions(context);
    }

    @Override
    public ConditionCheck checkSpecificConditions(Power<?> context) {
        LivingEntity user = context.getUser();
        if(user != null && user.getItemInHand(InteractionHand.OFF_HAND).is(Items.MAP) &&
                user.getItemInHand(InteractionHand.MAIN_HAND).isEmpty()){
            HermitPurpleAddon.getLogger().debug("This is shit{}",user.getItemInHand(InteractionHand.OFF_HAND).is(Items.MAP) &&
                    user.getItemInHand(InteractionHand.MAIN_HAND).isEmpty());
            return ConditionCheck.POSITIVE;
        }
        return ConditionCheck.NEGATIVE;
    }
    




    @Override
    public ActionAnimIdentifier getEntityAnim(EntityActionInstance action) {
        if(action.getPowerUser() instanceof  LivingEntity livingEntity && livingEntity.getMainArm() == HumanoidArm.LEFT){
            return ActionAnimIdentifier.getOrCreate(abilityId.nameInMoveset().concat("_l"),false);
        }
        return super.getEntityAnim(action);
    }
    
    public static class DoxxingAction extends EntityActionInstance {

        public DoxxingAction(EntityActionType ability) {
            super(ability);
        }

        @Override
        public void actionPerformStart() {
            if(level().isClientSide){
                StandSkin skin = StandSkinsLoader.getCurSkin();
                if(skin != null){
                    PacketDistributor.sendToServer(new SetColorPacket(skin.getColor()));
                }
            }
            if(!level().isClientSide){
                HermitPurpleAddon.getLogger().debug("Why is not working?");
                byte scale = performer.isShiftKeyDown()?(byte) 0: (byte)2;
                BlockPos blockPos = null;
                String target = null;
                ItemStack itemStack = performer.getItemInHand(InteractionHand.OFF_HAND);
                if(itemStack.is(Items.MAP)){
                    if(performer.getData(AddonDataAttachmentTypes.HERMIT_DATA).getMode() < 4){
                        Entity entity = DoxingHelper.HPLivingObjectives(performer);
                        if(entity != null){
                            blockPos = entity.getOnPos();
                            target = entity.getName().getString();

                        }
                    }else {
                        switch (performer.getData(AddonDataAttachmentTypes.HERMIT_DATA).getMode()) {
                            case 4 -> {
                                blockPos = DoxingHelper.structurePos(performer);
                                String data = performer.getData(AddonDataAttachmentTypes.HERMIT_DATA).getTarget().split(":")[1];
                                data = data.replace("_", " ");
                                target = data;
                            }
                            case 5 -> {
                                blockPos = DoxingHelper.biomesPos(performer);
                                String biome = "biome.";
                                target = Component.translatable(biome.concat(performer.getData(AddonDataAttachmentTypes.HERMIT_DATA).getTarget().replace(":", "."))).getString();
                            }
                        }
                    }
                    if(blockPos != null){
                        itemStack.setCount(itemStack.getCount() - 1);
                        ServerLevel serverLevel = (ServerLevel) level();
                        ItemStack map = MapItem.create(level(),blockPos.getX(),blockPos.getZ(),scale,true,true);
                        MapItem.renderBiomePreviewMap(serverLevel,map);
                        MapItemSavedData.addTargetDecoration(map,blockPos,"+", MapDecorationTypes.RED_X);
                        String displayName = "filled_map.divination";
                        map.set(DataComponents.ITEM_NAME, Component.translatable(displayName, target));
                        map.set(DataComponents.MAP_COLOR, new MapItemColor(performer.getData(AddonDataAttachmentTypes.HERMIT_DATA).getColor()));
                        performer.setItemInHand(InteractionHand.MAIN_HAND,map);
                        StandPower standPower = StandPower.get(performer);
                        if(standPower != null){
                            standPower.addExp(.2F);
                        }

                        PacketDistributor.sendToPlayersTrackingEntityAndSelf(performer,
                                new StandSoundPacket(performer.getId(),AddonSoundEvents.USER_HP,false,1,1));
                        PacketDistributor.sendToPlayersTrackingEntityAndSelf(performer,
                                new StandSoundPacket(performer.getId(),AddonSoundEvents.SUMMON_HP,true,1,1));
                    }
                }
            }
        }
    }
    
}
