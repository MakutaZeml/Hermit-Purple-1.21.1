package com.zeml.ripplez_hp.init;

import com.zeml.ripplez_hp.core.HermitPurpleAddon;

import com.zeml.ripplez_hp.jojoimpl.stands.hermitpurple.HermitTargetData;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public final class AddonDataAttachmentTypes {
	public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, HermitPurpleAddon.MOD_ID);
	

	public static final Supplier<AttachmentType<HermitTargetData>> HERMIT_DATA = ATTACHMENT_TYPES.register("hermit_target",
			()-> AttachmentType.builder(HermitTargetData::new).build()
			);

	public static final Supplier<AttachmentType<Boolean>> GUIDED = ATTACHMENT_TYPES.register("guided",
			()-> AttachmentType.builder(()->false).build());

}