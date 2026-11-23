package com.zeml.ripplez_hp.jojoimpl.stands.hermitpurple.grapple.entity;

import com.github.standobyte.jojo.customobjects.entity_projectile.DamagingEntity;
import com.github.standobyte.jojo.init.ModDamageTypes;
import com.github.standobyte.jojo.powersystem.standpower.StandPower;
import com.github.standobyte.jojo.powersystem.standpower.entity.StandStatFormulas;
import com.github.standobyte.jojo.subsystems.hitboxes.ExtendableOBB;
import com.github.standobyte.jojo.subsystems.hitboxes.OBBCollisionUtil;
import com.github.standobyte.jojo.subsystems.hitboxes.OrientedBoundingBox;
import com.github.standobyte.jojo.util.functions.DamageUtil;
import com.github.standobyte.jojo.util.functions.MathUtil;
import com.github.standobyte.jojo.util.objects_mc.EntityResolver;
import com.zeml.ripplez_hp.core.packets.server.SetNodes;
import com.zeml.ripplez_hp.init.AddonEntityTypes;
import com.zeml.ripplez_hp.init.power.AddonStandAbilities;
import com.zeml.ripplez_hp.jojoimpl.stands.hermitpurple.grapple.CountNodesEffect;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

public class NetNodeEntity extends DamagingEntity {
    protected static final EntityDataAccessor<Boolean> BROKEN = SynchedEntityData.defineId(NetNodeEntity.class, EntityDataSerializers.BOOLEAN);

    protected EntityResolver homingTarget = new EntityResolver();
    private double RANGE = 30;
    private ExtendableOBB vines;

    public NetNodeEntity(EntityType<? extends DamagingEntity> entityType, Level level) {
        super(entityType, level);
    }

    public NetNodeEntity(Level level, LivingEntity owner, double range, Entity target) {
        this(level, owner);
        setRange(range);
        setTarget(target);
    }

    public NetNodeEntity(Level level, LivingEntity owner) {
        super(AddonEntityTypes.HERMIT_NODE.get(), owner, level);
        setNoGravity(true);
    }

    @Override
    public void tick() {
        super.tick();
        if (getOwner() instanceof LivingEntity user) {
            StandPower power = StandPower.get(user);
            if (power == null || !power.isSummoned() || this.distanceTo(user) > RANGE || !power.userStandEffects.getEffectOfType(AddonStandAbilities.NODES_CONTROL.get()).isPresent()) {
                this.remove(RemovalReason.DISCARDED);
            }
            if (homingTarget.getEntity(level()) instanceof NetNodeEntity nodeEntity) {
                this.lookAt(EntityAnchorArgument.Anchor.EYES, nodeEntity.position());
                if (!isBroken()) {
                    if (vines != null) {
                        if (vines.getLength() > distanceTo(nodeEntity)) {
                            vines.setMovementSpeed(0);
                        }
                        vines.tick();
                        Vec3 offset = new Vec3(0.0, 0, 0)
                                .yRot(-this.yRotO * MathUtil.DEG_TO_RAD);
                        this.vines.updatePosition(level(), position(), offset, this.getXRot(), this.getYRot());
                        OBBCollisionUtil.getEntitiesInOBB(level(), vines.rotatableHitbox(),
                                entity -> !entity.isAlliedTo(user) && entity.isAlive() && entity != user && entity instanceof LivingEntity).forEach(entity -> {
                            var damageType = DamageUtil.type(level(), ModDamageTypes.STAND_ATTACK);
                            DamageSource dmgSource = new DamageSource(damageType, user);
                            //TODO Add Hamon when is ready
                            float dmgAmount = StandStatFormulas.getLightAttackDamage(StandPower.get(user).getPowerType().getStandStats().power());
                            entity.hurt(dmgSource, dmgAmount / 2);
                            itBroke();
                        });
                    }
                    if (level().getBlockState(this.blockPosition()).isAir()) {
                        itBroke();
                    }
                } else {
                    if (nodeEntity.isBroken()) {
                        nodeEntity.remove(RemovalReason.DISCARDED);
                    }
                }
            }
        } else {
            this.remove(RemovalReason.DISCARDED);
        }

    }

    public void setRange(double RANGE) {
        this.RANGE = RANGE;
    }

    public double getRANGE() {
        return RANGE;
    }

    public void setTarget(Entity target) {
        this.homingTarget.setEntity(target);
        OrientedBoundingBox obb = new OrientedBoundingBox(new Vec3(0, 0, 0), 0.25d, 0.25d, distanceTo(target), this.getYRot(), this.getXRot());
        this.vines = new ExtendableOBB(obb, distanceTo(target), ticksLifespan(), 1, new Vec3(0, 0, 0));
    }

    @Nullable
    public Entity getTarget() {
        return this.homingTarget.getEntity(level());
    }

    @Nullable
    public ExtendableOBB getVines() {
        return vines;
    }

    @Override
    public boolean isAttackable() {
        return false;
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag nbt) {
        super.addAdditionalSaveData(nbt);
        homingTarget.saveNbt(nbt, "HomingTarget");
        nbt.putBoolean("broken", entityData.get(BROKEN));
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag nbt) {
        super.readAdditionalSaveData(nbt);
        homingTarget.loadNbt(nbt, "HomingTarget");
        entityData.set(BROKEN, nbt.getBoolean("broken"));
    }


    public boolean isBroken() {
        return entityData.get(BROKEN);
    }

    public void setBroken(boolean broken) {
        entityData.set(BROKEN, broken);
    }

    public void itBroke() {
        if (userStandPower.get() != null) {
            if (!isBroken()) {
                userStandPower.get().userStandEffects.getEffectOfType(AddonStandAbilities.NODES_CONTROL.get()).ifPresent(effect -> effect.addNode(-1));
                if (getOwner() instanceof ServerPlayer player) {
                    PacketDistributor.sendToPlayer(player, new SetNodes(player.getId(), userStandPower.get().userStandEffects.getEffectOfType(AddonStandAbilities.NODES_CONTROL.get()).map(CountNodesEffect::getNodes).orElse(0)));
                }
            }
        }
        setBroken(true);
        vines = null;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(BROKEN, false);
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

    @Override
    public int ticksLifespan() {
        return Integer.MAX_VALUE;
    }

    @Override
    protected float getBaseDamage() {
        return 0;
    }

    @Override
    protected float getMaxHardnessBreakable() {
        return 0;
    }

    @Override
    public boolean standDamage() {
        return true;
    }

    @Override
    protected void onHitEntity(EntityHitResult entityRayTraceResult) {
    }

    @Nullable
    public StandPower getStandPower(){
        return userStandPower.get();
    }

    @Override
    protected void onHitBlock(BlockHitResult blockRayTraceResult) {

    }
}