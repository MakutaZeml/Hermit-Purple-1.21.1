package com.zeml.ripplez_hp.jojoimpl.stands.emperor.entity;

import com.zeml.ripplez_hp.init.AddonEntityTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class BulletPilot extends TamableAnimal {
    public BulletPilot(EntityType<? extends TamableAnimal> entityType, Level level) {
        super(entityType, level);
    }


    public BulletPilot(Level level,LivingEntity owner){
        super(AddonEntityTypes.EMPEROR_RIDER.get(), level);
        if(owner != null){
            setOwnerUUID(owner.getUUID());
        }
    }


    @Override
    public void tick() {
        super.tick();
        if(this.getVehicle() == null){
            this.remove(RemovalReason.DISCARDED);
        }
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes().add(Attributes.MOVEMENT_SPEED, 0).add(Attributes.MAX_HEALTH, 1).add(Attributes.ATTACK_DAMAGE, 0);
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        return InteractionResult.FAIL;
    }

    @Override
    public boolean isDamageSourceBlocked(DamageSource damageSource) {
        if(damageSource.is(DamageTypes.IN_FIRE) || damageSource.is(DamageTypes.DROWN) || damageSource.is(DamageTypes.SWEET_BERRY_BUSH)
        || damageSource.is(DamageTypes.CACTUS) || damageSource.is(DamageTypes.CAMPFIRE) || damageSource.is(DamageTypes.FALL)){
            return true;
        }
        return super.isDamageSourceBlocked(damageSource);
    }

    @Override
    public boolean isFood(ItemStack itemStack) {
        return false;
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel serverLevel, AgeableMob ageableMob) {
        return null;
    }

    @Override
    public boolean canBeLeashed() {
        return false;
    }

    @Override
    public void die(DamageSource cause) {
    }

    @Override
    public boolean isOrderedToSit() {
        return false;
    }

    @Override
    public boolean shouldTryTeleportToOwner() {
        return false;
    }
}
