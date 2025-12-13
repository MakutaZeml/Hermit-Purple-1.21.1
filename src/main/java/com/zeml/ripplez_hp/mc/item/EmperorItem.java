package com.zeml.ripplez_hp.mc.item;

import com.github.standobyte.jojo.client.sound.ClientsideSoundsHelper;
import com.github.standobyte.jojo.core.packet.fromserver.StandSkinSoundPacket;
import com.github.standobyte.jojo.init.ModStatusEffects;
import com.github.standobyte.jojo.powersystem.standpower.StandPower;
import com.github.standobyte.jojo.powersystem.standpower.entity.StandEntity;
import com.zeml.ripplez_hp.core.HermitPurpleAddon;
import com.zeml.ripplez_hp.core.packets.server.StandSoundPacket;
import com.zeml.ripplez_hp.init.AddonSoundEvents;
import com.zeml.ripplez_hp.init.HermitDataComponents;
import com.zeml.ripplez_hp.jojoimpl.stands.emperor.EmperorBulletEntity;
import com.zeml.ripplez_hp.mc.item.component.EmperorGunData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.EnderpearlItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

public class EmperorItem extends Item {

    public EmperorItem(Properties properties) {
        super(properties);
    }

    @Override
    public void onUseTick(Level level, LivingEntity livingEntity, ItemStack stack, int remainingTicks) {
        EmperorGunData data = stack.get(HermitDataComponents.EMPEROR);
        HermitPurpleAddon.LOGGER.debug("emperor use {}", data);
        if(data != null && data.getUuidOwner().isPresent()){
            float multStamina = livingEntity.hasEffect(ModStatusEffects.RESOLVE)?.75F:1;
            if(livingEntity.isShiftKeyDown()){
                int t = remainingTicks%13;
                boolean shotTick = t==1|| t==3||t==5|| t==7||t==9 || t==11;
                if(shotTick){
                    if(!level.isClientSide()){
                        shot(level,data,multStamina, livingEntity);
                        level.playSound(null,livingEntity.getX(),livingEntity.getY(),livingEntity.getZ(),
                                ClientsideSoundsHelper.withStandSkin(AddonSoundEvents.EMP_SHOT.get(),StandPower.get(livingEntity)),livingEntity.getSoundSource()
                        );
                    }
                }
                if(t == 1){
                    livingEntity.releaseUsingItem();
                    if(livingEntity instanceof Player){
                        ((Player) livingEntity).getCooldowns().addCooldown(this,65);
                    }
                }
            }else {
                int t = remainingTicks%5;
                boolean shotTick = t==4;
                if(shotTick){
                    if(!level.isClientSide()){
                        shot(level,data,multStamina, livingEntity);
                        livingEntity.releaseUsingItem();
                        if(livingEntity instanceof Player){
                            ((Player) livingEntity).getCooldowns().addCooldown(this,10);
                        }
                    }
                }
            }
        }

    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        player.startUsingItem(usedHand);
        ItemStack itemstack = player.getItemInHand(usedHand);
        return InteractionResultHolder.consume(itemstack);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if(!level.isClientSide){
            if(stack.get(HermitDataComponents.EMPEROR) != null){
                EmperorGunData data = stack.get(HermitDataComponents.EMPEROR);
                if(data != null){
                    if(data.getUuidOwner().isPresent()){
                        Entity owner = ((ServerLevel) level).getEntity(data.getUuidOwner().get());
                        if(owner instanceof LivingEntity){
                            LivingEntity user = (LivingEntity) owner;
                            if(StandPower.get(user).hasPower()){
                                StandPower power = StandPower.get(user);
                                if(!power.isSummoned() || !user.isAlive() || duplicate(user) || otherUser(user,stack)){
                                    stack.setCount(0);
                                }
                            }
                        }
                    }
                    if(data.getUuidTarget().isPresent()){
                        Entity target = ((ServerLevel) level).getEntity(data.getUuidTarget().get());
                        if(target == null || !target.isAlive()){
                            data.setUUIDTarget(Optional.empty());
                        }
                    }
                }
            }
        }
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.BOW;
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 10;
    }

    @Nullable
    public static EmperorBulletEntity shot(Level level, EmperorGunData data, float multStamina, LivingEntity shooter){
        Entity entity = ((ServerLevel) level).getEntity(data.getUuidOwner().get());
        if(entity instanceof LivingEntity){
            LivingEntity user = (LivingEntity) entity;
            if(StandPower.get(user).hasPower() && StandPower.get(user).getStamina()>=35*multStamina){
                StandPower power = StandPower.get(user);
                if(power != null && power.getStandInstance().isPresent() && power.getStandInstance().get().getStandType() != null &&
                        power.getStandInstance().get().getStandType().getStandStats() != null){
                    EmperorBulletEntity emperorBullet =  new EmperorBulletEntity(user,level);
                    emperorBullet.setStandPower((float) power.getStandInstance().get().getStandType().getStandStats().power());
                    emperorBullet.setStandRange((float) power.getStandInstance().get().getStandType().getStandStats().rangeMax());
                    setTargetToBullet(emperorBullet,user,data, (float) power.getStandInstance().get().getStandType().getStandStats().rangeMax());
                    if(data.getUuidTarget().isPresent()){
                        Entity target = ((ServerLevel) level).getEntity(data.getUuidTarget().get());
                        if(target != null){
                            emperorBullet.setHomingTarget(target);
                        }
                    }
                    emperorBullet.shootFromRotation(shooter,1F,1F);
                    level.addFreshEntity(emperorBullet);
                    power.consumeStamina(35*multStamina);
                    PacketDistributor.sendToPlayersTrackingEntityAndSelf(user, new StandSoundPacket(user.getId(),AddonSoundEvents.EMP_SHOT,true,1,1));
                    return emperorBullet;
                }
            }
        }
        return null;
    }

    private boolean duplicate(LivingEntity user){
        if(user instanceof Player){
            AtomicInteger count = new AtomicInteger(0);
            ((Player) user).getInventory().items.forEach(itemStack -> {
                if(itemStack.get(HermitDataComponents.EMPEROR) != null){
                    EmperorGunData data = itemStack.get(HermitDataComponents.EMPEROR);
                    if(data.getUuidOwner().isPresent() && data.getUuidOwner().get() == user.getUUID()){
                        count.set(count.get()+1);
                    }
                }
            });
            return count.get()>1;
        }else {
            return false;
        }
    }

    private boolean otherUser(LivingEntity user, ItemStack itemStack){
        if(itemStack.get(HermitDataComponents.EMPEROR) != null){
            EmperorGunData data = itemStack.get(HermitDataComponents.EMPEROR);
            if(data != null && data.getUuidOwner().isPresent()){
                return data.getUuidOwner().get() != user.getUUID();
            }
        }
        return false;
    }


    public static void setTargetToBullet(EmperorBulletEntity bullet, LivingEntity user, EmperorGunData data, float range){
        Optional<LivingEntity> optional = switch (Math.abs(data.getMode() % 4)) {
            case 0 -> getTarget(targets(user, range), user);
            case 1 -> getTarget(targetsHostile(user, range), user);
            case 2 -> getTarget(targetsPlayers(user, range), user);
            default -> Optional.empty();
        };
        optional.ifPresent(bullet::setHomingTarget);
    }

    public static Optional<LivingEntity> getTarget(Stream<LivingEntity> targets, LivingEntity user) {
        Vec3 lookAngle = user.getLookAngle();
        return targets.max((e1, e2) ->
                (int) Math.floor(
                        (lookAngle.dot(e1.getBoundingBox().getCenter().subtract(user.getEyePosition(1.0F)).normalize()) -
                                lookAngle.dot(e2.getBoundingBox().getCenter().subtract(user.getEyePosition(1.0F)).normalize()))
                                * 256));
    }

    public static Stream<LivingEntity> targets(LivingEntity user, float range) {
        return user.level().getEntitiesOfClass(LivingEntity.class,user.getBoundingBox().inflate(range,3,range),LivingEntity::isAlive).stream()
                .filter(livingEntity -> livingEntity != user)
                .filter(livingEntity -> !livingEntity.isAlliedTo(user) && !livingEntity.isSpectator())
                ;
    }

    public static Stream<LivingEntity> targetsHostile(LivingEntity user, float range) {
        return user.level().getEntitiesOfClass(LivingEntity.class,user.getBoundingBox().inflate(range,3,range), LivingEntity::isAlive).stream()
                .filter(livingEntity -> livingEntity != user)
                .filter(livingEntity -> !livingEntity.isAlliedTo(user))
                .filter(livingEntity -> livingEntity instanceof Monster && !livingEntity.isSpectator())
                ;
    }


    public static Stream<LivingEntity> targetsPlayers(LivingEntity player, float range) {
        return player.level().getEntitiesOfClass(LivingEntity.class,player.getBoundingBox().inflate(range,3,range), livingEntity -> livingEntity.isAlive() &&
                        livingEntity != player && ((livingEntity instanceof Player && !livingEntity.isSpectator()) || livingEntity instanceof StandEntity)
                && !livingEntity.isAlliedTo(player)).stream();
    }

}