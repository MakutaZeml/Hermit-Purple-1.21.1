package com.zeml.ripplez_hp.jojoimpl.stands.emperor.entity;

import com.github.standobyte.jojo.client.ClientGlobals;
import com.github.standobyte.jojo.customobjects.entity_projectile.ModdedProjectileEntity;
import com.github.standobyte.jojo.powersystem.standpower.StandPower;
import com.github.standobyte.jojo.util.objects_mc.EntityResolver;
import com.zeml.ripplez_hp.init.AddonEntityTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

import java.util.LinkedList;
import java.util.List;

public class EmperorBulletEntity extends ModdedProjectileEntity {
    private EntityResolver homingTarget = new EntityResolver();
    private float standPower = 12;
    private float standRange = 12;
    public final List<Vec3> tracePos = new LinkedList<>();

    public EmperorBulletEntity(EntityType<? extends EmperorBulletEntity> type, Level level) {
        super(type, level);
    }

    public EmperorBulletEntity(LivingEntity shooter, Level level) {
        super(AddonEntityTypes.EMPEROR_BULLET.get(), shooter, level);
        this.setNoGravity(true);
    }

    @Override
    public void tick() {
        super.tick();
        if(!this.level().isClientSide){

        }else {
            Vec3 pos = this.position();

            boolean addPos = true;
            if (this.tracePos.size() > 1) {
                Vec3 lastPos = this.tracePos.get(this.tracePos.size() - 1);
                addPos &= pos.distanceToSqr(lastPos) >= 0.05;
            }
            if (addPos) {
                this.tracePos.add(pos);
            }
        }
    }

    @Override
    protected void moveProjectile() {
        super.moveProjectile();
        Entity target = this.homingTarget.getEntity(level());
        if (target != null) {
            if (!target.isAlive()) {
                this.homingTarget.setEntity(null);
            }
            else if (tickCount >= 3) {
                if((this.getOwner() != null && distanceTo(this.getOwner())<=this.standRange)|| this.getOwner() == null){
                    Vec3 targetPos = target.getBoundingBox().getCenter();
                    Vec3 vecToTarget = targetPos.subtract(this.position());
                    setDeltaMovement(vecToTarget.normalize().scale(this.getDeltaMovement().length()));
                    StandPower standPower = userStandPower.get();
                    if (standPower != null) {

                    }
                    Level level = level();
                    if (level.isClientSide()) {
                        if (ClientGlobals.canSeeStands) {

                        }
                    }
                }

            }
        }
    }

    @Override
    public int ticksLifespan() {
        return 100;
    }

    @Override
    protected float getBaseDamage() {
        if(this.getOwner() != null){
            return Math.max(-(3*standPower/(2*standRange)*(distanceTo(this.getOwner())-standRange)),0);
        }
        return standPower/2;
    }

    @Override
    protected float getMaxHardnessBreakable() {
        return 0.3F;
    }

    @Override
    public boolean standDamage() {
        return true;
    }

    public void setStandPower(float standPower) {
        this.standPower = standPower;
    }

    public void setHomingTarget(Entity homingTarget) {
        this.homingTarget.setEntity(homingTarget);
    }

    public void setStandRange(float standRange) {
        this.standRange = standRange;
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag nbt) {
        super.addAdditionalSaveData(nbt);
        homingTarget.saveNbt(nbt, "HomingTarget");
    }

    @Override
    protected void onHitEntity(EntityHitResult entityRayTraceResult) {
        if (!level().isClientSide() && isAlive()) {
            LivingEntity owner = getOwner();
            if(owner != null){
                StandPower.addExp(owner,.07F);
            }
        }
        super.onHitEntity(entityRayTraceResult);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag nbt) {
        super.readAdditionalSaveData(nbt);
        homingTarget.loadNbt(nbt, "HomingTarget");
    }

    @Override
    public void writeSpawnData(RegistryFriendlyByteBuf buffer) {
        super.writeSpawnData(buffer);
        homingTarget.updateEntity(level());
        homingTarget.writeNetwork(buffer);
    }

    @Override
    public void readSpawnData(RegistryFriendlyByteBuf additionalData) {
        super.readSpawnData(additionalData);
        homingTarget.readNetwork(additionalData);
    }

}
