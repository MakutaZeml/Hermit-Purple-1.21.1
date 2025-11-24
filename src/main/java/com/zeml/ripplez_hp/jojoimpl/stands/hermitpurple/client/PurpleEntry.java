package com.zeml.ripplez_hp.jojoimpl.stands.hermitpurple.client;

import com.zeml.ripplez_hp.core.HermitPurpleAddon;
import com.zeml.ripplez_hp.core.packets.client.SetTargetPacket;
import com.zeml.ripplez_hp.init.AddonDataAttachmentTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.social.PlayerEntry;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PurpleEntry extends ContainerObjectSelectionList.Entry<PurpleEntry>{
    protected static final WidgetSprites SPRITES = new WidgetSprites(ResourceLocation.withDefaultNamespace("widget/button"), ResourceLocation.withDefaultNamespace("widget/button_disabled"), ResourceLocation.withDefaultNamespace("widget/button_highlighted"));
    private final Minecraft minecraft;
    private final List<AbstractWidget> children;
    private final Object item;
    private final int mode;
    private final String name;
    @Nullable private final ImageButton hpButton;


    public PurpleEntry(Minecraft minecraft, Object o, int mode){
        this.minecraft = minecraft;
        this.item = o;
        this.mode = mode;
        this.name = getName(o);
        this.hpButton = new ImageButton(0, 0, 20, 20,SPRITES,(ctx)->{
            HermitPurpleAddon.getLogger().debug("Packet Sent {} {}", item.toString(),this.mode);
            PacketDistributor.sendToServer(new SetTargetPacket(this.mode,item.toString()));
        });
        this.children = new ArrayList<>();
        this.children.add(this.hpButton);

    }

    public @NotNull List<? extends GuiEventListener> children() {
        return this.children;
    }

    public @NotNull List<? extends NarratableEntry> narratables() {
        return this.children;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean hovering, float partialTick) {
        int i = left + 4;
        int j = top + (height - 24) / 2;
        int k = i + 24 + 4;
        int l = top + (height - 18) / 2;
        guiGraphics.drawString(minecraft.font,this.name, k, l + 12,0xFFFFFF,false);
        if(this.hpButton != null){
            this.hpButton.setX(left + (width - this.hpButton.getWidth() - 4));
            this.hpButton.setY(top + (height - this.hpButton.getHeight()) / 2);
            this.hpButton.render(guiGraphics, mouseX, mouseY, partialTick);
        }

    }


    @Nullable
    public Component getNameComponent(){
        switch (this.mode) {
            case 1 -> {
                if (this.item instanceof UUID uuid && this.minecraft.level != null) {
                    return this.minecraft.level.getPlayerByUUID(uuid).getName();
                }
            }
            case 2 -> {
                String entity = "entity.";
                entity = entity.concat(item.toString().replace(":", "."));
                return Component.translatable(entity);
            }
            case 3 -> {
                String stand = "stand.";
                stand = stand.concat(item.toString().replace(":", "."));
                return Component.translatable(stand);
            }
            case 5 -> {
                String biome = "biome.";
                biome = biome.concat(item.toString().replace(":", "."));
                return Component.translatable(biome);
            }
            default -> {
                String data = item.toString().split(":")[1];
                data = data.replace("_", " ");
                return Component.literal(data);
            }
        }
        return null;
    }

    public int getMode() {
        return mode;
    }


    public String getName(Object object){
        Component nameComponent = getNameComponent();
        if(nameComponent != null){
            return nameComponent.getString();
        }
        return object.toString();
    }

    public Object getItem() {
        return item;
    }

    @Override
    public String toString() {
        return name;
    }
}
