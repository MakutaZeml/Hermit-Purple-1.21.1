package com.zeml.ripplez_hp.client.ui.screen;

import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.client.extensions.IAbstractWidgetExtension;

@SuppressWarnings("deprecation")
public class HPButton extends Button implements IAbstractWidgetExtension{


    protected HPButton(int x, int y, int width, int height, Component message, OnPress onPress, CreateNarration createNarration) {
        super(x, y, width, height, message, onPress, createNarration);
    }
}
