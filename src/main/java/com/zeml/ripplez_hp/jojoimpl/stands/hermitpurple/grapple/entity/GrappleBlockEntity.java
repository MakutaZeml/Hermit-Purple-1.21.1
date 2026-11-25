package com.zeml.ripplez_hp.jojoimpl.stands.hermitpurple.grapple.entity;

import com.github.standobyte.jojo.customobjects.entity_projectile.ModdedProjectileEntity;
import com.github.standobyte.jojo.powersystem.entityaction.LivingComponentAction;
import com.github.standobyte.jojo.powersystem.standpower.StandPower;
import com.github.standobyte.jojo.subsystems.target.ActionTarget;
import com.zeml.ripplez_hp.init.AddonEntityTypes;
import com.zeml.ripplez_hp.jojoimpl.stands.hermitpurple.grapple.GrappleBlockAbility;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class GrappleBlockEntity extends ModdedProjectileEntity {
    protected static final EntityDataAccessor<Boolean> ATTACHED = SynchedEntityData.defineId(GrappleBlockEntity.class, EntityDataSerializers.BOOLEAN);

    public GrappleBlockEntity(LivingEntity shooter, Level level) {
        super(AddonEntityTypes.HERMIT_GRAPPLE.get(), shooter, level);
    }

    public GrappleBlockEntity(EntityType<? extends ModdedProjectileEntity> type, Level level) {
        super(type, level);
    }

    @Override
    public void tick() {
        super.tick();
        if(this.getOwner() != null){
            this.setInvisible(this.getOwner().isInvisible());
            LivingComponentAction action = LivingComponentAction.getComponent(getOwner());
            if(!(action.getAction() instanceof GrappleBlockAbility.GrapplingBlock)){
                this.remove(RemovalReason.DISCARDED);
            }

        }
    }

    @Override
    public int ticksLifespan() {
        return Integer.MAX_VALUE;
    }

    @Override
    protected float getBaseDamage() {
        return 0;
    }

    @Override
    protected void onHitBlock(BlockHitResult blockRayTraceResult) {
        BlockState blockstate = this.level().getBlockState(blockRayTraceResult.getBlockPos());
        blockstate.onProjectileHit(this.level(), blockstate, blockRayTraceResult, this);
        Vec3 vec3 = blockRayTraceResult.getLocation().subtract(this.getX(), this.getY(), this.getZ());
        this.setDeltaMovement(vec3);
        Vec3 vec31 = vec3.normalize().scale(0.05F);
        this.setPosRaw(this.getX() - vec31.x, this.getY() - vec31.y, this.getZ() - vec31.z);
        this.setOnGround(true);
        this.setAttached(true);
    }

    public boolean isAttached() {
        return entityData.get(ATTACHED);
    }

    public void setAttached(boolean attached) {
        entityData.set(ATTACHED, attached);
    }


    @Override
    public boolean canBeDeflected(@Nullable Entity context) {
        return false;
    }

    @Override
    protected void breakProjectile(ActionTarget.TargetType targetType, HitResult hitTarget) {

    }

    @Override
    protected void onHitEntity(EntityHitResult entityRayTraceResult) {

    }

    @Override
    protected float getMaxHardnessBreakable() {
        return 0;
    }

    @Override
    public boolean standDamage() {
        return true;
    }

    @Nullable
    public StandPower standPower(){
        return userStandPower.get();
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(ATTACHED,false);
    }

    @Override
    public void writeSpawnData(RegistryFriendlyByteBuf buffer) {
        super.writeSpawnData(buffer);
        buffer.writeBoolean(this.isAttached());
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag nbt) {
        super.readAdditionalSaveData(nbt);
        this.setAttached(nbt.getBoolean("attached"));
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag nbt) {
        super.addAdditionalSaveData(nbt);
        nbt.putBoolean("attached",this.isAttached());
    }
}