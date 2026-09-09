package com.zeml.ripplez_hp.jojoimpl.stands.emperor;

import com.github.standobyte.jojo.client.util.functions.ClientUtil;
import com.github.standobyte.jojo.init.ModStatusEffects;
import com.github.standobyte.jojo.powersystem.Power;
import com.github.standobyte.jojo.powersystem.PowerClass;
import com.github.standobyte.jojo.powersystem.ability.Ability;
import com.github.standobyte.jojo.powersystem.ability.AbilityId;
import com.github.standobyte.jojo.powersystem.ability.AbilityType;
import com.github.standobyte.jojo.powersystem.ability.condition.AvailableAbilities;
import com.github.standobyte.jojo.powersystem.entityaction.ActionPhase;
import com.github.standobyte.jojo.powersystem.entityaction.EntityActionInstance;
import com.github.standobyte.jojo.powersystem.entityaction.type.EntityActionType;
import com.github.standobyte.jojo.powersystem.standpower.StandPower;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.zeml.ripplez_hp.core.HermitPurpleAddon;
import com.zeml.ripplez_hp.core.packets.server.AnotherGuidedPacket;
import com.zeml.ripplez_hp.core.packets.server.StandSoundPacket;
import com.zeml.ripplez_hp.core.util.EmperorUtil;
import com.zeml.ripplez_hp.init.AddonDataAttachmentTypes;
import com.zeml.ripplez_hp.init.AddonItems;
import com.zeml.ripplez_hp.init.AddonSoundEvents;
import com.zeml.ripplez_hp.init.HermitDataComponents;
import com.zeml.ripplez_hp.init.power.AddonStandAbilities;
import com.zeml.ripplez_hp.jojoimpl.stands.emperor.entity.BulletPilot;
import com.zeml.ripplez_hp.jojoimpl.stands.emperor.entity.EmperorBulletEntity;
import com.zeml.ripplez_hp.mc.item.EmperorItem;
import com.zeml.ripplez_hp.mc.item.component.EmperorGunData;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

public class GuidedBarrageAbility extends ShotBarrageAbility{


    public GuidedBarrageAbility(AbilityType<?> abilityType, AbilityId abilityId) {
        super(abilityType, abilityId);
        createActionObj = BarrageBullets::new;
    }

    @Nullable
    @Override
    public Ability replaceWithSubAbility(Power<?> context, AvailableAbilities abilities) {
        StandPower standPower = PowerClass.STAND.cast(context);
        if(context.getUser() != null && context.getUser().getData(AddonDataAttachmentTypes.GUIDED)
                && standPower != null){
            if(standPower.userStandEffects.getEffectsOfType(AddonStandAbilities.GUIDED_BULLET.get()).findAny().isEmpty()){
                abilities.replaceOtherAbilityWith(context,"emp_shot_barrage",this);
            }
        }
        return super.replaceWithSubAbility(context, abilities);
    }

    @Override
    public void renderAbilityIcon(Power<?> context, GuiGraphics guiGraphics, TextureAtlasSprite sprite, float x, float y, int color) {
        StandPower standPower = PowerClass.STAND.cast(context);
        if (standPower != null){
            PoseStack poseStack = guiGraphics.pose();
            guiGraphics.enableScissor((int) x, (int) y, (int) x + 16, (int) y + 16);
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            poseStack.pushPose();
            poseStack.scale(0.5f, 0.5f, 1);
            poseStack.translate(x, y, 0);
            ClientUtil.renderEntityFace(poseStack, x + 16, y, context.getUser(), 0x80FFFFFF, false);
            poseStack.popPose();
            guiGraphics.disableScissor();
        }
        super.renderAbilityIcon(context, guiGraphics, sprite, x, y, color);
    }

    @Override
    public boolean isAbilityUnlocked(Power<?> context) {
        return isSkillUnlocked(context, "guide_emp");
    }
    public static class BarrageBullets extends EntityActionInstance {
        LivingEntity target;
        public BarrageBullets(EntityActionType ability) {
            super(ability);
        }

        public void actionTick() {
            if(this.phase == ActionPhase.PERFORM){
                int t = Math.round(this.getPhaseTicksLeft())%13;
                boolean shotTick = t==1|| t==3||t==5|| t==7||t==9 || t==11;
                if(shotTick){
                    StandPower power = StandPower.get(performer);
                    if(power != null && power.hasPower() && power.getStandInstance().get().getStandType() != null &&
                            power.getStandInstance().get().getStandType().getStandStats() != null){
                        EmperorBulletEntity emperorBullet =  new EmperorBulletEntity(performer,level());
                        emperorBullet.setStandPower((float) power.getStandInstance().get().getStandType().getStandStats().power());
                        emperorBullet.setStandRange((float) power.getStandInstance().get().getStandType().getStandStats().rangeMax());
                        emperorBullet.setControlled(true);
                        level().addFreshEntity(emperorBullet);
                        if(!this.level().isClientSide){
                            PacketDistributor.sendToPlayersTrackingEntityAndSelf(performer, new StandSoundPacket(performer.getId(), AddonSoundEvents.EMP_SHOT,true,1,1));
                        }
                        if(t == 11){
                            BulletPilot bulletPilot = new BulletPilot(level(),performer);
                            bulletPilot.startRiding(emperorBullet);
                            bulletPilot.copyPosition(emperorBullet);
                            level().addFreshEntity(bulletPilot);
                            GuidedBulletEffect effect = AddonStandAbilities.GUIDED_BULLET.get().create(level());
                            if(!level().isClientSide){
                                power.userStandEffects.addEffect(effect.withTarget(bulletPilot));
                                if(performer instanceof ServerPlayer player){
                                    PacketDistributor.sendToPlayer(player,new AnotherGuidedPacket(player.getId(), bulletPilot.getId()));
                                }
                            }
                            target = bulletPilot;
                        }else if(target != null && !level().isClientSide){
                            emperorBullet.setHomingTarget(target);
                        }
                        HermitPurpleAddon.getLogger().debug("target? {}", target);
                        emperorBullet.shootFromRotation(performer,1F,1F);

                        if(performer instanceof Player player){
                            player.getCooldowns().addCooldown(AddonItems.EMPEROR.asItem(),10);
                        }
                    }
                }
            }
            if(this.phase == ActionPhase.RECOVERY){
                if(performer instanceof Player player){
                    player.getCooldowns().addCooldown(AddonItems.EMPEROR.asItem(),65);
                }
            }
        }
    }
}