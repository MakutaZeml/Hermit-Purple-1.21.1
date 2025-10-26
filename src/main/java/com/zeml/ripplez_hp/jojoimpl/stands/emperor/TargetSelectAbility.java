package com.zeml.ripplez_hp.jojoimpl.stands.emperor;

import com.github.standobyte.jojo.powersystem.Power;
import com.github.standobyte.jojo.powersystem.ability.AbilityId;
import com.github.standobyte.jojo.powersystem.ability.AbilityType;
import com.github.standobyte.jojo.powersystem.ability.EntityActionAbility;
import com.github.standobyte.jojo.powersystem.ability.condition.ConditionCheck;
import com.github.standobyte.jojo.powersystem.ability.controls.InputMethod;
import com.github.standobyte.jojo.powersystem.entityaction.HeldInput;
import com.zeml.ripplez_hp.core.HermitPurpleAddon;
import com.zeml.ripplez_hp.init.HermitDataComponents;
import com.zeml.ripplez_hp.mc.item.EmperorItem;
import com.zeml.ripplez_hp.mc.item.component.EmperorGunData;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class TargetSelectAbility extends EntityActionAbility {

    public TargetSelectAbility(AbilityType<?> abilityType, AbilityId abilityId) {
        super(abilityType, abilityId);
        this.nameLess = Component.translatable("jojo_ripples.skill.emp_target");
        this.random = Component.translatable("ripplez_hp.type.random");
        this.players = Component.translatable("ripplez_hp.type.players");
        this.hostile = Component.translatable( "ripplez_hp.type.hostile");
        this.gun = Component.translatable("ripplez_hp.type.non");
        this.spriteHostile = this.spriteName+"_hostile";
        this.spritePlayer = this.spriteName+ "_player";
        this.spriteGun = this.spriteName+"_gun";
    }

    @Override
    public ConditionCheck checkConditions(Power<?> context) {
        LivingEntity user = context.getUser();
        if(user.getItemInHand(InteractionHand.MAIN_HAND).getItem() instanceof EmperorItem ||
                user.getItemInHand(InteractionHand.OFF_HAND).getItem() instanceof EmperorItem){
            return ConditionCheck.POSITIVE;
        }
        return ConditionCheck.NEGATIVE;
    }

    @Override
    public HeldInput onKeyPress(Level level, LivingEntity user, FriendlyByteBuf extraClientInput, InputMethod inputMethod, float clickHoldResolveTime) {
        if(user.getItemInHand(InteractionHand.MAIN_HAND).getItem() instanceof EmperorItem){
            HermitPurpleAddon.LOGGER.debug("ID {}", this.abilityId);
            ItemStack itemStack = user.getItemInHand(InteractionHand.MAIN_HAND);
            EmperorGunData data = itemStack.get(HermitDataComponents.EMPEROR);
            if(data != null){
                data.setMode(data.getMode()-1);
            }
        }else if(user.getItemInHand(InteractionHand.OFF_HAND).getItem() instanceof EmperorItem){
            ItemStack itemStack = user.getItemInHand(InteractionHand.OFF_HAND);
            EmperorGunData data = itemStack.get(HermitDataComponents.EMPEROR);
            if(data != null){
                data.setMode(data.getMode()-1);
            }
        }
        return super.onKeyPress(level, user, extraClientInput, inputMethod, clickHoldResolveTime);
    }

    protected Component nameLess;
    protected Component random;
    protected Component players;
    protected Component hostile;
    protected Component gun;

    @Override
    public Component getName(Power<?> context) {
        LivingEntity user = context.getUser();
        if(user.getItemInHand(InteractionHand.MAIN_HAND).getItem() instanceof EmperorItem){
            ItemStack itemStack = user.getItemInHand(InteractionHand.MAIN_HAND);
            EmperorGunData data = itemStack.get(HermitDataComponents.EMPEROR);
            if(data != null){
                return getData(data);
            }
        }else  if(user.getItemInHand(InteractionHand.OFF_HAND).getItem() instanceof EmperorItem){
            ItemStack itemStack = user.getItemInHand(InteractionHand.OFF_HAND);
            EmperorGunData data = itemStack.get(HermitDataComponents.EMPEROR);
            if(data != null){
                return getData(data);
            }
        }
        return this.nameLess;
    }

    private Component getData(EmperorGunData data){
        return switch (Math.abs(data.getMode() % 4)) {
            case 0 -> Component.translatable("jojo_ripples.ability.emp_target", random);
            case 1 -> Component.translatable("jojo_ripples.ability.emp_target", hostile);
            case 2 -> Component.translatable("jojo_ripples.ability.emp_target", players);
            case 3 -> gun;
            default -> this.nameLess;
        };
    }


    protected String spriteHostile;
    protected String spritePlayer;
    protected String spriteGun;

    @Override
    public String getSpriteName(Power<?> context) {
        LivingEntity user = context.getUser();
        if(user.getItemInHand(InteractionHand.MAIN_HAND).getItem() instanceof EmperorItem){
            ItemStack itemStack = user.getItemInHand(InteractionHand.MAIN_HAND);
            EmperorGunData data = itemStack.get(HermitDataComponents.EMPEROR);
            if(data != null){
                return getModSprite(data);
            }
        }else if(user.getItemInHand(InteractionHand.OFF_HAND).getItem() instanceof EmperorItem){
            ItemStack itemStack = user.getItemInHand(InteractionHand.OFF_HAND);
            EmperorGunData data = itemStack.get(HermitDataComponents.EMPEROR);
            if(data != null){
                return getModSprite(data);
            }
        }
        return super.getSpriteName(context);
    }


    protected String getModSprite(EmperorGunData data){
        return switch (Math.abs(data.getMode() % 4)) {
            case 1 -> this.spriteHostile;
            case 2 -> this.spritePlayer;
            case 3 -> this.spriteGun;
            default -> this.spriteName;
        };
    }
}
