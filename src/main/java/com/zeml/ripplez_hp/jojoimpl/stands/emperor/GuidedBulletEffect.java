package com.zeml.ripplez_hp.jojoimpl.stands.emperor;

import com.github.standobyte.jojo.client.ClientProxy;
import com.github.standobyte.jojo.entityattachment.ComponentUtil;
import com.github.standobyte.jojo.entityattachment.custom_effect.EntityCustomEffectType;
import com.github.standobyte.jojo.init.ModDataAttachmentTypes;
import com.github.standobyte.jojo.powersystem.standpower.effect.StandEffectInstance;
import com.github.standobyte.jojo.subsystems.entity_puppetcontrol.EntityComponentController;
import com.github.standobyte.jojo.subsystems.entity_puppetcontrol.client.ClientEntityController;
import com.github.standobyte.jojo.subsystems.entity_puppetcontrol.client.mob.ClientMobController;
import com.zeml.ripplez_hp.core.HermitPurpleAddon;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

public class GuidedBulletEffect extends StandEffectInstance {


    public GuidedBulletEffect(@NotNull EntityCustomEffectType<?> effectType) {
        super(effectType);
    }

    @Override
    protected void start() {
        LivingEntity user = getStandUser();
        LivingEntity target = getTargetLiving();
        if(user != null && target != null){
            if(level.isClientSide){
                if (user == ClientProxy.getClientPlayer()){
                    ClientEntityController.setInstance(new ClientMobController(target));
                }
            }else {
                EntityComponentController.setControlTarget(user, target, "entity");
            }
        }
    }

    @Override
    protected void tick() {
        if(getTargetLiving() == null){
            this.remove();
        }

    }

    @Override
    protected void stop() {
        LivingEntity user = getStandUser();
        if(level.isClientSide){
            if (user == ClientProxy.getClientPlayer()){
                ClientEntityController.setInstance(null);
            }
        }else {
            EntityComponentController component = ComponentUtil.getExistingDataOrNull(user, ModDataAttachmentTypes.CONTROLLER);
            if (component != null) {
                component.stopControlling();
            }
        }
        LivingEntity targetLiving = getTargetLiving();
        if(targetLiving != null){
            targetLiving.remove(Entity.RemovalReason.DISCARDED);
        }

    }

    @Override
    public void remove() {
        LivingEntity targetLiving = getTargetLiving();
        if(targetLiving != null){
            targetLiving.remove(Entity.RemovalReason.DISCARDED);
        }
        super.remove();
    }
}