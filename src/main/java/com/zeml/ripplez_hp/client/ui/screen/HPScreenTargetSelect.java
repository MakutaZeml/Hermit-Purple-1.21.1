package com.zeml.ripplez_hp.client.ui.screen;

import com.github.standobyte.jojo.client.standskin.StandSkinsLoader;
import com.github.standobyte.jojo.core.JojoRegistries;
import com.github.standobyte.jojo.powersystem.standpower.StandPower;
import com.zeml.ripplez_hp.core.HermitPurpleAddon;
import com.zeml.ripplez_hp.core.packets.client.SetTargetPacket;
import com.zeml.ripplez_hp.init.power.AddonStands;
import net.minecraft.ChatFormatting;
import net.minecraft.client.GameNarrator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.registries.Registries;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.network.PacketDistributor;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;
import java.util.Locale;

@OnlyIn(Dist.CLIENT)
public class HPScreenTargetSelect extends Screen {
    private static final Component TITLE = Component.translatable("gui.hermitpurpletarget.title");
    private static final ResourceLocation BACKGROUND_SPRITE = HermitPurpleAddon.resLoc("hp_target");
    private static final ResourceLocation SEARCH_SPRITE = ResourceLocation.withDefaultNamespace("icon/search");
    private static final Component TAB_PLAYERS = Component.translatable("gui.hermitpurpletarget.tab_players");
    private static final Component TAB_ENTITIES = Component.translatable("gui.hermitpurpletarget.tab_entities");
    private static final Component TAB_STRUCTURES = Component.translatable("gui.hermitpurpletarget.tab_structures");
    private static final Component TAB_BIOMES = Component.translatable("gui.hermitpurpletarget.tab_biomes");
    private static final Component TAB_STANDS = Component.translatable("gui.hermitpurpletarget.tab_stands");
    private static final Component RANDOM = Component.translatable("gui.hermitpurpletarget.random");
    private static final Component TAB_PLAYERS_SELECTED;
    private static final Component TAB_ENTITIES_SELECTED;
    private static final Component TAB_STRUCTURES_SELECTED;
    private static final Component TAB_BIOMES_SELECTED;
    private static final Component TAB_STANDS_SELECTED;
    static final Component EMPTY_SEARCH;
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
    HPSelectList hpSelectList;
    private String lastSearch = "";
    private HPScreenTargetSelect.Mode page = Mode.PLAYERS;
    private HPButton playersButton;
    private HPButton entitiesButton;
    private HPButton structuresButton;
    private HPButton biomesButton;
    private HPButton standsButton;
    private HPButton randomButton;
    EditBox searchBox;
    private int playerCount;
    private boolean initialized;
    @Nullable
    private Component serverLabel;


    public HPScreenTargetSelect() {
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
        return (Component)(this.serverLabel != null
                ? CommonComponents.joinForNarration(super.getNarrationMessage(), this.serverLabel)
                : super.getNarrationMessage());
    }

    @Override
    public void init() {
        this.layout.addTitleHeader(TITLE, this.font);
        this.hpSelectList = new HPSelectList(this.minecraft, this.width, this.listEnd() - 88, 88, 36);
        int i = this.hpSelectList.getRowWidth() / 3;
        int i1 = 64 + 16 * this.backgroundUnits();
        int j = this.hpSelectList.getRowLeft();
        int k = this.hpSelectList.getRowRight();
        this.playersButton = this.addRenderableWidget(
                new HPButton(j, 45, i, 20,p_240243_ -> this.showPage(Mode.PLAYERS))
        );
        this.entitiesButton = this.addRenderableWidget(
                new HPButton((j + k - i) / 2 + 1, 45, i, 20,p_240243_ -> this.showPage(Mode.ENTITIES))
        );
        this.standsButton = this.addRenderableWidget(
                new HPButton(k - i + 1, 45, i, 20,p_240243_ -> this.showPage(Mode.STAND))
        );
        this.structuresButton =this.addRenderableWidget(
                new HPButton(j,i1,i,20,p_240243_ -> this.showPage(Mode.STRUCTURES))
        );
        this.biomesButton =this.addRenderableWidget(
                new HPButton(k - i + 1, i1, i, 20,p_240243_ -> this.showPage(Mode.BIOMES))
        );
        this.randomButton = this.addRenderableWidget(
                new HPButton((j + k - i) / 2 + 1, i1, i, 20,p_240243_ -> PacketDistributor.sendToServer(new SetTargetPacket(0,"")))
        );
        String s = this.searchBox != null ? this.searchBox.getValue() : "";
        this.searchBox = new EditBox(this.font, this.marginX() + 28, 74, 200, 15, Component.empty()) {
            protected MutableComponent createNarrationMessage() {
                return !HPScreenTargetSelect.this.searchBox.getValue().isEmpty() && HPScreenTargetSelect.this.hpSelectList.isEmpty() ? super.createNarrationMessage().append(", ").append(HPScreenTargetSelect.EMPTY_SEARCH) : super.createNarrationMessage();
            }
        };
        this.searchBox.setMaxLength(16);
        this.searchBox.setVisible(true);
        this.searchBox.setTextColor(-1);
        this.searchBox.setValue(s);
        this.searchBox.setResponder(this::checkSearchStringUpdate);
        this.addRenderableWidget(this.searchBox);
    }

    private void showPage(HPScreenTargetSelect.Mode page){
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
            switch (page){
                case PLAYERS:
                    this.playersButton.setMessage(TAB_PLAYERS_SELECTED);
                    collection = this.minecraft.player.connection.getOnlinePlayerIds();
                    break;
                case BIOMES:
                    this.biomesButton.setMessage(TAB_BIOMES_SELECTED);
                    collection = minecraft.level.registryAccess().registryOrThrow(Registries.BIOME).keySet();
                    break;
                case STAND:
                    this.standsButton.setMessage(TAB_STANDS_SELECTED);
                    collection = minecraft.level.registryAccess().registryOrThrow(JojoRegistries.DEFAULT_STANDS_REG.key()).keySet();
                    break;
                case STRUCTURES:
                    this.standsButton.setMessage(TAB_STANDS_SELECTED);
                    collection = minecraft.level.registryAccess().registryOrThrow(Registries.STRUCTURE).keySet();
                    break;
                case ENTITIES:
                    this.entitiesButton.setMessage(TAB_ENTITIES_SELECTED);
                    collection = minecraft.level.registryAccess().registryOrThrow(Registries.ENTITY_TYPE).keySet();
                    break;
                default:
                    collection = Collections.EMPTY_LIST;
                    break;
            }
        }
        HermitPurpleAddon.LOGGER.debug("{}", collection);
        this.hpSelectList.updateList(collection);
        GameNarrator gamenarrator = this.minecraft.getNarrator();
        if (!this.searchBox.getValue().isEmpty() && this.hpSelectList.isEmpty() && !this.searchBox.isFocused()) {
            gamenarrator.sayNow(EMPTY_SEARCH.getString());
        }else if (flag){

        }

    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        int i = this.marginX() + 3;
        Player t = Minecraft.getInstance().player;
        ResourceLocation hermit = BACKGROUND_SPRITE;
        if(StandPower.getOptional(t).isPresent() && StandPower.get(t).getPowerType() == AddonStands.HERMIT_PURPLE.get()){
            hermit = StandSkinsLoader.getInstance().getSkin(StandPower.get(t)).getTexture(BACKGROUND_SPRITE);
        }
        guiGraphics.blitSprite(hermit, i, 64, 236, this.windowHeight() + 16);
        guiGraphics.blitSprite(SEARCH_SPRITE, i + 10, 76, 12, 12);
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
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (!this.searchBox.isFocused() && this.minecraft.options.keySocialInteractions.matches(keyCode, scanCode)) {
            this.onClose();
            return true;
        } else {
            return super.keyPressed(keyCode, scanCode, modifiers);
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

    static {
        TAB_PLAYERS_SELECTED = TAB_PLAYERS.plainCopy().withStyle(ChatFormatting.UNDERLINE);
        TAB_ENTITIES_SELECTED = TAB_ENTITIES.plainCopy().withStyle(ChatFormatting.UNDERLINE);
        TAB_STRUCTURES_SELECTED = TAB_STRUCTURES.plainCopy().withStyle(ChatFormatting.UNDERLINE);
        TAB_BIOMES_SELECTED = TAB_BIOMES.plainCopy().withStyle(ChatFormatting.UNDERLINE);
        TAB_STANDS_SELECTED = TAB_STANDS.plainCopy().withStyle(ChatFormatting.UNDERLINE);
        EMPTY_SEARCH = Component.translatable("gui.hermitpurpletarget.search_empty").withStyle(ChatFormatting.GRAY);
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

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @OnlyIn(Dist.CLIENT)
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

}
