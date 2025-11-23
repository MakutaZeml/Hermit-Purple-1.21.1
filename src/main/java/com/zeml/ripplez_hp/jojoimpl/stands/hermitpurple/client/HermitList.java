package com.zeml.ripplez_hp.jojoimpl.stands.hermitpurple.client;

import com.google.common.collect.Lists;
import com.zeml.ripplez_hp.core.HermitPurpleAddon;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.*;

@OnlyIn(Dist.CLIENT)
public class HermitList extends ContainerObjectSelectionList<PurpleEntry> {
    private final List<PurpleEntry> purpleEntries = Lists.newArrayList();;
    @Nullable
    private String filter;
    private int mode = 0;
    private Minecraft minecraft;

    public HermitList(Minecraft minecraft, int width, int height, int y, int itemHeight) {
        super(minecraft, width, height, y, itemHeight);
        this.minecraft = minecraft;
    }

    @Override
    protected void renderListBackground(GuiGraphics guiGraphics) {
    }

    @Override
    protected void renderListSeparators(GuiGraphics guiGraphics) {
    }

    @Override
    protected void enableScissor(GuiGraphics guiGraphics) {
        guiGraphics.enableScissor(this.getX(), this.getY() + 4, this.getRight(), this.getBottom());
    }

    public void updateList(Collection<?> list, double scrollAmount) {
        List<PurpleEntry> purpleList = new ArrayList<>();
        this.addPurpleEntry(list,purpleList);
        this.updateFiltersAndScroll(purpleList, scrollAmount);
    }


    public void setFilter(String filter) {
        this.filter = filter;
    }

    public boolean isEmpty() {
        return this.purpleEntries.isEmpty();
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        HermitPurpleAddon.getLogger().debug("CLICKED? {}",button);
        return super.mouseClicked(mouseX, mouseY, button);
    }

    private void addPurpleEntry(Collection<?> list, List<PurpleEntry> purpleEntries){
        list.forEach(object->{
            purpleEntries.add(new PurpleEntry(minecraft,object,this.mode));
        });
    }

    private void updateFiltersAndScroll(List<PurpleEntry> list, double scrollAmount){
        this.purpleEntries.clear();
        this.purpleEntries.addAll(list);
        this.sortPlayerEntries();
        this.updateFilteredPlayers();
        this.replaceEntries(this.purpleEntries);
        this.setScrollAmount(scrollAmount);
    }

    private void sortPlayerEntries(){
        this.purpleEntries.sort(Comparator.<PurpleEntry,Integer>comparing(purpleEntry -> {
            if(purpleEntry.getMode() == 1 && purpleEntry.getItem() instanceof  UUID uuid){
                if(this.minecraft.isLocalPlayer(uuid)){
                    return 0;
                }
            }
            return 4;
        }).thenComparing(PurpleEntry::toString,String::compareToIgnoreCase));
    }

    private void updateFilteredPlayers(){
        if(this.filter != null){
            this.purpleEntries.removeIf(purpleEntry -> !purpleEntry.toString().toLowerCase().contains(this.filter));
            this.replaceEntries(this.purpleEntries);
        }
    }

}
