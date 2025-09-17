package com.zeml.ripplez_hp.client.ui.screen;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class HPScreenTargetSelect extends Screen {
    protected HPScreenTargetSelect(Component title) {
        super(title);
    }
}
