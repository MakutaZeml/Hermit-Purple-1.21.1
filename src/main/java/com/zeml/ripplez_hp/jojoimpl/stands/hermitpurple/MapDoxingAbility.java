package com.zeml.ripplez_hp.jojoimpl.stands.hermitpurple;

import com.github.standobyte.jojo.client.ClientGlobals;
import com.github.standobyte.jojo.client.sound.ClientsideSoundsHelper;
import com.github.standobyte.jojo.powersystem.Power;
import com.github.standobyte.jojo.powersystem.ability.AbilityId;
import com.github.standobyte.jojo.powersystem.ability.AbilityType;
import com.github.standobyte.jojo.powersystem.ability.condition.ConditionCheck;
import com.github.standobyte.jojo.powersystem.ability.controls.InputMethod;
import com.github.standobyte.jojo.powersystem.entityaction.ActionPhase;
import com.github.standobyte.jojo.powersystem.entityaction.HeldInput;
import com.github.standobyte.jojo.powersystem.standpower.entity.StandEntityAbility;
import com.zeml.ripplez_hp.init.AddonDataAttachmentTypes;
import com.zeml.ripplez_hp.init.AddonSoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.item.component.MapItemColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.maps.MapDecorationType;
import net.minecraft.world.level.saveddata.maps.MapDecorationTypes;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;

public class MapDoxingAbility extends StandEntityAbility {
    public MapDoxingAbility(AbilityType<?> abilityType, AbilityId abilityId) {
        super(abilityType, abilityId);
        setDefaultPhaseLength(ActionPhase.WINDUP,5);
    }

    @Override
    public ConditionCheck checkConditions(Power<?> context) {
        if(context.getUser() != null && context.getUser().getItemInHand(InteractionHand.OFF_HAND).is(Items.MAP) &&
                context.getUser().getItemInHand(InteractionHand.MAIN_HAND).isEmpty()
        ){
            return super.checkConditions(context);
        }
        return ConditionCheck.NEGATIVE;
    }

    @Override
    public HeldInput onKeyPress(Level level, LivingEntity user, FriendlyByteBuf extraClientInput, InputMethod inputMethod, float clickHoldResolveTime) {
        if(level.isClientSide){

        }
        byte scale = user.isShiftKeyDown()?(byte) 0: (byte)2;
        BlockPos blockPos = null;
        String target = null;
        ItemStack itemStack = user.getItemInHand(InteractionHand.OFF_HAND);
        if(itemStack.is(Items.MAP)){
            if(user.getData(AddonDataAttachmentTypes.MODE) <= 4){
                Entity entity = DoxingHelper.HPLivingObjectives(user);
                if(entity != null){
                    blockPos = entity.getOnPos();
                    target = entity.getName().getString();

                }
            }else {

            }
            if(blockPos != null){
                if(!level.isClientSide){
                    itemStack.setCount(itemStack.getCount()-1);
                    ServerLevel serverLevel = (ServerLevel) level;
                    ItemStack map = MapItem.create(level,blockPos.getX(),blockPos.getZ(),scale,true,true);
                    MapItem.renderBiomePreviewMap(serverLevel,map);
                    MapItemSavedData.addTargetDecoration(map,blockPos,"+", MapDecorationTypes.RED_X);
                    String displayName = "filled_map.divination";
                    map.set(DataComponents.ITEM_NAME, Component.translatable(displayName, target));
                    map.set(DataComponents.MAP_COLOR, new MapItemColor(user.getData(AddonDataAttachmentTypes.COLOR)));
                    user.setItemInHand(InteractionHand.MAIN_HAND,map);
                }else {
                    if(ClientGlobals.canHearStands){
                        ClientsideSoundsHelper.playEntityLingeringSound(user,AddonSoundEvents.SUMMON_HP.get(),user.getSoundSource(),1F,1F,level);
                    }
                    ClientsideSoundsHelper.playEntityLingeringSound(user,AddonSoundEvents.USER_HP.get(),user.getSoundSource(),1F,1F,level);

                }
            }

        }

        return super.onKeyPress(level, user, extraClientInput, inputMethod, clickHoldResolveTime);

    }




}
