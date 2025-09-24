package com.zeml.ripplez_hp.client.ui.screen;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class HPSelectList extends ContainerObjectSelectionList<HPSelectList.HermitEntry> {
    private final Minecraft minecraft;
    private List<Object> originalList;
    private String filterText = "";
    public HPSelectList(Minecraft minecraft, int width, int height, int y, int itemHeight) {
        super(minecraft, width, height, y, itemHeight);
        this.minecraft = minecraft;
        this.originalList= new ArrayList<>();

    }

    public void updateList(Collection<?> newList){
        this.clearEntries();
        this.originalList = new ArrayList<>(newList);
        newList.forEach(item ->this.addEntry(new HermitEntry(item)));
    }


    public void setFilter(String filter) {
        this.filterText = filter.toLowerCase();

    }

    public boolean isEmpty() {
        return this.children().isEmpty();
    }






    @OnlyIn(Dist.CLIENT)
    public class HermitEntry extends ContainerObjectSelectionList.Entry<HermitEntry>{
        private final Object item;
        private final HPButton hpButton;

        public HermitEntry(Object item){
            this.item = item;
            this.hpButton = new HPButton (width / 2 + 50, 0, 20, 20, button ->onButtonClick());

        }

        @Override
        public List<? extends NarratableEntry> narratables() {
            return null;
        }

        @Override
        public void render(GuiGraphics guiGraphics,int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean hovering, float partialTick) {

        }

        @Override
        public List<? extends GuiEventListener> children() {
            return ImmutableList.of(this.hpButton);
        }

        private void onButtonClick(){

        }
    }
}
