package com.zeml.ripplez_hp.core;

import com.mojang.logging.LogUtils;
import com.zeml.ripplez_hp.init.*;
import com.zeml.ripplez_hp.init.power.AddonPlayerPowers;
import com.zeml.ripplez_hp.init.power.AddonStandAbilities;
import com.zeml.ripplez_hp.init.power.AddonStandEffects;
import com.zeml.ripplez_hp.init.power.AddonStands;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import org.slf4j.Logger;

@Mod(HermitPurpleAddon.MOD_ID)
public class HermitPurpleAddon {
    public static final String MOD_ID = "ripplez_hp";
    @Deprecated
    public static final Logger LOGGER = LogUtils.getLogger();
    public HermitPurpleAddon(IEventBus modEventBus, ModContainer modContainer) {
        AddonPlayerPowers.PLAYER_POWERS.register(modEventBus);
        AddonStandAbilities.load();
        AddonStandEffects.STAND_EFFECT_TYPES.register(modEventBus);
        AddonStands.STANDS.register(modEventBus);

        AddonBlocks.BLOCKS.register(modEventBus);
        AddonEntityTypes.ENTITY_TYPES.register(modEventBus);
        AddonDataAttachmentTypes.ATTACHMENT_TYPES.register(modEventBus);
        AddonItems.ITEMS.register(modEventBus);
        AddonSoundEvents.SOUNDS.register(modEventBus);
    }

    @SubscribeEvent
    private void registerNetwork(RegisterPayloadHandlersEvent event){
        LOGGER.debug("Packets register happening");
        HermitPackets.register(event);
    }

    public static ResourceLocation resLoc(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }

    public static Logger getLogger() {
        return LOGGER;
    }

}