package com.zeml.ripplez_hp.client.entityrender;

import com.zeml.ripplez_hp.core.HermitPurpleAddon;
import com.zeml.ripplez_hp.init.AddonEntityTypes;
import com.zeml.ripplez_hp.jojoimpl.stands.emperor.client.EmperorBulletRenderer;
import com.zeml.ripplez_hp.jojoimpl.stands.hermitpurple.client.renderer.HermitPurpleVinesLayer;
import com.zeml.ripplez_hp.jojoimpl.stands.hermitpurple.client.renderer.HermitPurpleLayer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

import static com.github.standobyte.jojo.client.entityrender.ModEntityRenderers.castToHumanoid;

@EventBusSubscriber(modid = HermitPurpleAddon.MOD_ID, value = Dist.CLIENT)
public class HermitEntityRenderers {

    @SubscribeEvent
    public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event){
        event.registerEntityRenderer(AddonEntityTypes.EMPEROR_BULLET.get(), EmperorBulletRenderer::new);
    }


    @SubscribeEvent
    public static void addLayers(EntityRenderersEvent.AddLayers event) {
        var renderers = Minecraft.getInstance().getEntityRenderDispatcher();
        for (var renderer : renderers.renderers.values()) {
            castToHumanoid(renderer).ifPresent(HermitEntityRenderers::addHumanoidLayers);
        }
        for (var playerRenderer : renderers.getSkinMap().values()) {
            castToHumanoid(playerRenderer).ifPresent(HermitEntityRenderers::addHumanoidLayers);
        }
    }

    private static <T extends LivingEntity, M extends HumanoidModel<T>> void addHumanoidLayers(LivingEntityRenderer<T, M> renderer) {
        renderer.addLayer(new HermitPurpleLayer<>(renderer));
        renderer.addLayer(new HermitPurpleVinesLayer<>(renderer));
    }


}