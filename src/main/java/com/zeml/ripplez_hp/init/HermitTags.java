package com.zeml.ripplez_hp.init;

import com.zeml.ripplez_hp.core.HermitPurpleAddon;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class HermitTags {

    public static class Items{
        public static final TagKey<Item> CAMERA = TagKey.create(Registries.ITEM,
                HermitPurpleAddon.resLoc("camera"));
    }
}
