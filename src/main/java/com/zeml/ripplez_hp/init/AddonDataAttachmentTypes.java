package com.zeml.ripplez_hp.init;

import com.zeml.ripplez_hp.core.HermitPurpleAddon;

import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public final class AddonDataAttachmentTypes {
	public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, HermitPurpleAddon.MOD_ID);
	
//	public static final Supplier<AttachmentType<StandPower>> STAND_POWER = ATTACHMENT_TYPES.register("stand_power",
//			() -> AttachmentType.serializable(entity -> PowerClass._tryAttach(entity, StandPower::new)).build());

	public static final Supplier<AttachmentType<Integer>> MODE = ATTACHMENT_TYPES.register("mode",
		()-> AttachmentType.builder(()->0).build());

	public static final Supplier<AttachmentType<String>> TARGET = ATTACHMENT_TYPES.register("target",
			()->AttachmentType.builder(()->"").build());

	public static final Supplier<AttachmentType<Integer>> COLOR = ATTACHMENT_TYPES.register("color",
			()->AttachmentType.builder(()->0xF070D0).build()
			);

}
