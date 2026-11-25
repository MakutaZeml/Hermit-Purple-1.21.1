package com.zeml.ripplez_hp.init;

import com.zeml.ripplez_hp.core.HermitPurpleAddon;
import com.zeml.ripplez_hp.mc.item.EmperorItem;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import static com.github.standobyte.jojo.init.ModItems.MAIN_TAB;

@EventBusSubscriber(modid = HermitPurpleAddon.MOD_ID)
public final class AddonItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(HermitPurpleAddon.MOD_ID);

    public static final DeferredItem<Item> EMPEROR = ITEMS.registerItem("emperor", EmperorItem::new, new Item.Properties().stacksTo(1));

    @SubscribeEvent
    public static void addToTab(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == MAIN_TAB.getKey()) {

        }
    }
}