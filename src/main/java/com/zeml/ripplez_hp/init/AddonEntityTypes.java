package com.zeml.ripplez_hp.init;

import com.zeml.ripplez_hp.core.HermitPurpleAddon;
import com.zeml.ripplez_hp.jojoimpl.stands.emperor.entity.EmperorBulletEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

@EventBusSubscriber(modid = HermitPurpleAddon.MOD_ID)
public final class AddonEntityTypes {
	public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(Registries.ENTITY_TYPE, HermitPurpleAddon.MOD_ID);
	
	@SubscribeEvent
	public static void registerAttributes(EntityAttributeCreationEvent event) {
//		event.put(EXAMPLE.get(), ArmorStand.createAttributes().build());
	}

	public static final DeferredHolder<EntityType<?>, EntityType<EmperorBulletEntity>> EMPEROR_BULLET = ENTITY_TYPES.register("emperor_bullet", key ->
			EntityType.Builder.<EmperorBulletEntity>of(EmperorBulletEntity::new, MobCategory.MISC)
			.sized(0.0625F, 0.0625F)
			.clientTrackingRange(10)
			.build(createIDFor(key)));


	
	public static String createIDFor(ResourceLocation key) {
		return key.toString();
	}
}
