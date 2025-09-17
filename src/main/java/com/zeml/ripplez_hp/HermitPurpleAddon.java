package com.zeml.ripplez_hp;

import com.zeml.ripplez_hp.init.*;
import com.zeml.ripplez_hp.init.power.AddonPlayerPowers;
import com.zeml.ripplez_hp.init.power.AddonStandAbilities;
import com.zeml.ripplez_hp.init.power.AddonStandEffects;
import com.zeml.ripplez_hp.init.power.AddonStands;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;

@Mod(HermitPurpleAddon.MOD_ID)
public class HermitPurpleAddon {
    public static final String MOD_ID = "ripplez_hp";

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
}