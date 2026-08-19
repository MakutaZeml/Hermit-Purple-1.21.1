package com.zeml.ripplez_hp.jojoimpl.stands.hermitpurple;

import com.github.standobyte.jojo.entityattachment.custom_effect.EntityCustomEffectType;
import com.github.standobyte.jojo.powersystem.standpower.effect.StandEffectInstance;
import net.minecraft.world.entity.projectile.Projectile;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import org.jetbrains.annotations.NotNull;

public class HermitThornsEffect extends StandEffectInstance {
    public HermitThornsEffect(@NotNull EntityCustomEffectType<?> effectType) {
        super(effectType);
    }

    @Override
    protected void start() {

    }

    @Override
    protected void tick() {
        if(userPower != null){
            if(!userPower.hasPower() || !userPower.isSummoned()){
                this.remove();
            }
        }
    }

    @Override
    protected void stop() {

    }

    public static void onHurt(LivingDamageEvent.Pre event){
        if(!(event.getSource().getEntity() instanceof Projectile)){

        }
    }
}