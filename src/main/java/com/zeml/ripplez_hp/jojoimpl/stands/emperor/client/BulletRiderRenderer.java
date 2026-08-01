package com.zeml.ripplez_hp.jojoimpl.stands.emperor.client;

import com.github.standobyte.jojo.client.entityrender.entities.SimpleEntityRenderer;
import com.zeml.ripplez_hp.jojoimpl.stands.emperor.entity.BulletPilot;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class BulletRiderRenderer extends SimpleEntityRenderer<BulletPilot, EntityModel<BulletPilot>> {
    public BulletRiderRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager);
    }
}
