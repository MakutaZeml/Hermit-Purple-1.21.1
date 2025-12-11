package com.zeml.ripplez_hp.jojoimpl.stands.hermitpurple.client;

import com.github.standobyte.jojo.core.JojoRegistries;
import com.zeml.ripplez_hp.core.HermitPurpleAddon;
import com.zeml.ripplez_hp.core.packets.client.SetTargetPacket;
import com.zeml.ripplez_hp.init.AddonDataAttachmentTypes;
import net.minecraft.ChatFormatting;
import net.minecraft.client.GameNarrator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.neoforge.network.PacketDistributor;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;
import java.util.Locale;

public class HermitTargetScreen extends Screen {
    private static final Component TITLE = Component.translatable("gui.hermitpurpletarget.title");
    private static final ResourceLocation BACKGROUND_SPRITE = ResourceLocation.withDefaultNamespace("social_interactions/background");
    private static final ResourceLocation SEARCH_SPRITE = ResourceLocation.withDefaultNamespace("icon/search");
    private static final Component TAB_PLAYERS = Component.translatable("gui.hermitpurpletarget.tab_players");
    private static final Component TAB_ENTITIES = Component.translatable("gui.hermitpurpletarget.tab_entities");
    private static final Component TAB_STRUCTURES = Component.translatable("gui.hermitpurpletarget.tab_structures");
    private static final Component TAB_BIOMES = Component.translatable("gui.hermitpurpletarget.tab_biomes");
    private static final Component TAB_STANDS = Component.translatable("gui.hermitpurpletarget.tab_stands");
    private static final Component RANDOM = Component.translatable("gui.hermitpurpletarget.random");
    private static final Component TAB_PLAYERS_SELECTED = TAB_PLAYERS.plainCopy().withStyle(ChatFormatting.UNDERLINE);
    private static final Component TAB_ENTITIES_SELECTED =TAB_ENTITIES.plainCopy().withStyle(ChatFormatting.UNDERLINE);
    private static final Component TAB_STRUCTURES_SELECTED = TAB_STRUCTURES.plainCopy().withStyle(ChatFormatting.UNDERLINE);
    private static final Component TAB_BIOMES_SELECTED = TAB_BIOMES.plainCopy().withStyle(ChatFormatting.UNDERLINE);
    private static final Component TAB_STANDS_SELECTED = TAB_STANDS.plainCopy().withStyle(ChatFormatting.UNDERLINE);
    static final Component EMPTY_SEARCH = Component.translatable("gui.hermitpurpletarget.search_empty").withStyle(ChatFormatting.GRAY);
    private static final Component SEARCH_HINT = Component.translatable("gui.socialInteractions.search_hint")
            .withStyle(ChatFormatting.ITALIC)
            .withStyle(ChatFormatting.GRAY);
    private static final int BG_BORDER_SIZE = 8;
    private static final int BG_WIDTH = 236;
    private static final int SEARCH_HEIGHT = 16;
    private static final int MARGIN_Y = 64;
    public static final int SEARCH_START = 72;
    public static final int LIST_START = 88;
    private static final int IMAGE_WIDTH = 238;
    private static final int BUTTON_HEIGHT = 20;
    private static final int ITEM_HEIGHT = 36;
    private final HeaderAndFooterLayout layout = new HeaderAndFooterLayout(this);
    HermitList hpSelectList;
    private String lastSearch = "";
    private HermitTargetScreen.Mode page = HermitTargetScreen.Mode.PLAYERS;
    private Button playersButton;
    private Button entitiesButton;
    private Button structuresButton;
    private Button biomesButton;
    private Button standsButton;
    private Button randomButton;
    EditBox searchBox;
    private int playerCount;
    private boolean initialized;
    @Nullable
    private Component serverLabel;

    public HermitTargetScreen() {
        super(TITLE);
        this.updateServerLabel(Minecraft.getInstance());
    }

    private int windowHeight() {
        return Math.max(52, this.height - 128 - 16);
    }
    private int listEnd() {
        return 80 + this.windowHeight() - 8;
    }

    private int marginX() {
        return (this.width - 238) / 2;
    }
    @Override
    public Component getNarrationMessage() {
        return (this.serverLabel != null
                ? CommonComponents.joinForNarration(super.getNarrationMessage(), this.serverLabel)
                : super.getNarrationMessage());
    }

    @Override
    protected void init(){
        this.layout.addTitleHeader(TITLE, this.font);
        this.hpSelectList = new HermitList(this.minecraft,this.width,this.listEnd() - 100,88,36);
        int i = this.hpSelectList.getRowWidth() / 3;
        int j = this.hpSelectList.getRowLeft();
        int k = this.hpSelectList.getRowRight();
        int i1 = 64 + 16 * this.backgroundUnits();
        this.playersButton = this.addRenderableWidget(Button.builder(TAB_PLAYERS,
                (onPress)->this.showPage(HermitTargetScreen.Mode.PLAYERS)).bounds(j, 45, i, 20).build());
        this.entitiesButton = this.addRenderableWidget(Button.builder(TAB_ENTITIES,
                (onPress)->this.showPage(HermitTargetScreen.Mode.ENTITIES)).bounds((j + k - i) / 2 + 1, 45, i, 20).build());
        this.standsButton = this.addRenderableWidget(Button.builder(TAB_STANDS,
                (onPress)->this.showPage(HermitTargetScreen.Mode.STAND)).bounds(k - i + 1, 45, i, 20).build());
        this.structuresButton = this.addRenderableWidget(Button.builder(TAB_STRUCTURES,
                (onPress)->this.showPage(HermitTargetScreen.Mode.STRUCTURES)).bounds(j,i1,i,20).build());
        this.biomesButton = this.addRenderableWidget(Button.builder(TAB_BIOMES,
                (onPress)->this.showPage(HermitTargetScreen.Mode.BIOMES)).bounds(k - i + 1, i1, i, 20).build());
        this.randomButton = this.addRenderableWidget(Button.builder(RANDOM,
                (onPress)-> PacketDistributor.sendToServer(new SetTargetPacket(0,""))).bounds((j + k - i) / 2 + 1, i1, i, 20).build());

        String s = this.searchBox != null ? this.searchBox.getValue() : "";
        this.searchBox = new EditBox(this.font, this.marginX() + 28, 74, 200, 15, Component.empty()) {
            protected MutableComponent createNarrationMessage() {
                return !HermitTargetScreen.this.searchBox.getValue().isEmpty() && HermitTargetScreen.this.hpSelectList.isEmpty() ? super.createNarrationMessage().append(", ").append(HermitTargetScreen.EMPTY_SEARCH) : super.createNarrationMessage();
            }
        };
        this.searchBox.setMaxLength(16);
        this.searchBox.setVisible(true);
        this.searchBox.setTextColor(-1);
        this.searchBox.setValue(s);
        this.searchBox.setResponder(this::checkSearchStringUpdate);
        this.addRenderableWidget(this.searchBox);
        this.showPage(this.page);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private void showPage(HermitTargetScreen.Mode page){
        this.page = page;
        this.playersButton.setMessage(TAB_PLAYERS);
        this.biomesButton.setMessage(TAB_BIOMES);
        this.entitiesButton.setMessage(TAB_ENTITIES);
        this.randomButton.setMessage(RANDOM);
        this.standsButton.setMessage(TAB_STANDS);
        this.structuresButton.setMessage(TAB_STRUCTURES);
        boolean flag = false;
        Collection<?> collection = null;
        if(minecraft.level != null){
            switch (page) {
                case PLAYERS -> {
                    this.hpSelectList.setMode(1);
                    this.playersButton.setMessage(TAB_PLAYERS_SELECTED);
                    collection = this.minecraft.player.connection.getOnlinePlayerIds();
                }
                case BIOMES -> {
                    this.hpSelectList.setMode(5);
                    this.biomesButton.setMessage(TAB_BIOMES_SELECTED);
                    collection = minecraft.level.registryAccess().registryOrThrow(Registries.BIOME).keySet();
                }
                case STAND -> {
                    this.hpSelectList.setMode(3);
                    this.standsButton.setMessage(TAB_STANDS_SELECTED);
                    collection = minecraft.level.registryAccess().registryOrThrow(JojoRegistries.DEFAULT_STANDS_REG.key()).keySet();
                }
                case STRUCTURES -> {
                    this.hpSelectList.setMode(4);
                    this.structuresButton.setMessage(TAB_STRUCTURES_SELECTED);
                    HermitPurpleAddon.getLogger().debug("Structures {}", minecraft.player.getData(AddonDataAttachmentTypes.HERMIT_DATA).getStructures());
                    collection = minecraft.player.getData(AddonDataAttachmentTypes.HERMIT_DATA).getStructures();
                }
                case ENTITIES -> {
                    this.hpSelectList.setMode(2);
                    this.entitiesButton.setMessage(TAB_ENTITIES_SELECTED);
                    collection = minecraft.level.registryAccess().registryOrThrow(Registries.ENTITY_TYPE).keySet();
                }
                default -> collection = Collections.EMPTY_LIST;
            }
        }
        HermitPurpleAddon.LOGGER.debug("{}", collection);
        this.hpSelectList.updateList(collection, this.hpSelectList.getScrollAmount());
        GameNarrator gamenarrator = this.minecraft.getNarrator();
        if (!this.searchBox.getValue().isEmpty() && this.hpSelectList.isEmpty() && !this.searchBox.isFocused()) {
            gamenarrator.sayNow(EMPTY_SEARCH.getString());
        }else if (flag){

        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.updateServerLabel(this.minecraft);
        if (this.serverLabel != null) {
            guiGraphics.drawString(this.minecraft.font, this.serverLabel, this.marginX() + 8, 35, -1);
        }

        if (!this.hpSelectList.isEmpty()) {
            this.hpSelectList.render(guiGraphics, mouseX, mouseY, partialTick);
        } else if (!this.searchBox.getValue().isEmpty()) {
            guiGraphics.drawCenteredString(this.minecraft.font, EMPTY_SEARCH, this.width / 2, (72 + this.listEnd()) / 2, -1);
        }
    }


    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        int i = this.marginX() + 3;
        guiGraphics.blitSprite(BACKGROUND_SPRITE, i, 64, 236, this.windowHeight() + 16);
        guiGraphics.blitSprite(SEARCH_SPRITE, i + 10, 76, 12, 12);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (!this.searchBox.isFocused() && this.minecraft.options.keySocialInteractions.matches(keyCode, scanCode)) {
            this.onClose();
            return true;
        } else {
            return super.keyPressed(keyCode, scanCode, modifiers);
        }
    }

    private void updateServerLabel(Minecraft minecraft) {
        int i = minecraft.getConnection().getOnlinePlayers().size();
        if (this.playerCount != i) {
            String s = "";
            ServerData serverdata = minecraft.getCurrentServer();
            if (minecraft.isLocalServer()) {
                s = minecraft.getSingleplayerServer().getMotd();
            } else if (serverdata != null) {
                s = serverdata.name;
            }

            if (i > 1) {
                this.serverLabel = Component.translatable("gui.socialInteractions.server_label.multiple", s, i);
            } else {
                this.serverLabel = Component.translatable("gui.socialInteractions.server_label.single", s, i);
            }

            this.playerCount = i;
        }
    }

    private void checkSearchStringUpdate(String newText) {
        newText = newText.toLowerCase(Locale.ROOT);
        if (!newText.equals(this.lastSearch)) {
            this.hpSelectList.setFilter(newText);
            this.lastSearch = newText;
            this.showPage(this.page);
        }
    }

    @Override
    public void resize(Minecraft minecraft, int width, int height) {
        String searchString = this.searchBox.getValue();
        this.init(minecraft, width, height);
        this.searchBox.setValue(searchString);
    }

    enum Mode{
        PLAYERS,
        ENTITIES,
        STRUCTURES,
        STAND,
        BIOMES;
    }
    private int backgroundUnits() {
        return this.windowHeight() / 16;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        double scroll = Math.max(0,Math.min(hpSelectList.getMaxScroll(),hpSelectList.getScrollAmount()-50*scrollY)) ;
        hpSelectList.setScrollAmount(scroll);
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if(hpSelectList.isMouseOver(mouseX,mouseY)){
            hpSelectList.mouseClicked(mouseX,mouseY,button);
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }
}
