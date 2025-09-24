package com.zeml.ripplez_hp.init;

import com.zeml.ripplez_hp.core.HermitPurpleAddon;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.registries.DeferredRegister;

@EventBusSubscriber(modid = HermitPurpleAddon.MOD_ID)
public final class AddonEntityTypes {
	public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(Registries.ENTITY_TYPE, HermitPurpleAddon.MOD_ID);
	
	@SubscribeEvent
	public static void registerAttributes(EntityAttributeCreationEvent event) {
//		event.put(EXAMPLE.get(), ArmorStand.createAttributes().build());
	}

//	public static final DeferredHolder<EntityType<?>, EntityType<MannequinEntity>> EXAMPLE = ENTITY_TYPES.register("example", key ->
//			EntityType.Builder.<MannequinEntity>of(MannequinEntity::new, MobCategory.MISC)
//			.sized(0.5F, 1.975F)
//			.eyeHeight(1.7775F)
//			.clientTrackingRange(10)
//			.build(createIDFor(key)));
	
	public static String createIDFor(ResourceLocation key) {
		return key.toString();
	}
}
