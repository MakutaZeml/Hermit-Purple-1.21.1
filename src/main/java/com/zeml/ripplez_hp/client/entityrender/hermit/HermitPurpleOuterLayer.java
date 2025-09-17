package com.zeml.ripplez_hp.client.entityrender.hermit;

import com.github.standobyte.jojo.client.ClientGlobals;
import com.github.standobyte.jojo.client.standskin.StandSkinsLoader;
import com.github.standobyte.jojo.powersystem.standpower.StandPower;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.zeml.ripplez_hp.HermitPurpleAddon;
import com.zeml.ripplez_hp.init.power.AddonStands;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.Base64;

@OnlyIn(Dist.CLIENT)
public class HermitPurpleOuterLayer<T extends LivingEntity, M extends HumanoidModel<T>> extends RenderLayer<T, M> {
    private final ResourceLocation HERMIT = ResourceLocation.tryBuild(HermitPurpleAddon.MOD_ID,"textures/entity/stand/hermito_purple_out.png");

    public HermitPurpleOuterLayer(RenderLayerParent<T, M> renderer) {
        super(renderer);
    }


    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, T t, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if(!ClientGlobals.canSeeStands || t.isInvisible()){
            return;
        }
        boolean slim = false;
        ResourceLocation hermit = HERMIT;
        if(t instanceof Player){
            if(isSlimSkin(((Player) t).getGameProfile())){
                slim = true;
            }
        }
        if(StandPower.getOptional(t).isPresent() && StandPower.get(t).getPowerType() == AddonStands.HERMIT_PURPLE.get() && StandPower.get(t).isSummoned()){
            ResourceLocation texture = StandSkinsLoader.getInstance().getSkin(StandPower.get(t)).getTexture(hermit);
            M parentModel = getParentModel();
            HumanoidModel<T> hermitModel = new HumanoidModel<>(slim? Minecraft.getInstance().getEntityModels().bakeLayer(ModelLayers.PLAYER_SLIM_OUTER_ARMOR) :
                    Minecraft.getInstance().getEntityModels().bakeLayer(ModelLayers.PLAYER_OUTER_ARMOR));
            parentModel.copyPropertiesTo(hermitModel);

            VertexConsumer iVertexBuilder = buffer.getBuffer(RenderType.entityCutoutNoCull(texture));
            hermitModel.renderToBuffer(poseStack,iVertexBuilder,packedLight, OverlayTexture.NO_OVERLAY);
        }
    }

    private static final JsonParser PARSER = new JsonParser();
    public boolean isSlimSkin(GameProfile profile) {
        Property textures = profile.getProperties().get("textures").stream().findFirst().orElse(null);
        if (textures == null) {
            return DefaultPlayerSkin.get(profile.getId()).equals("slim");
        }
        try {
            String json = new String(Base64.getDecoder().decode(textures.value()));
            JsonObject jsonObject = PARSER.parse(json).getAsJsonObject();
            if (jsonObject.has("textures")) {
                JsonObject texturesObj = jsonObject.getAsJsonObject("textures");
                if (texturesObj.has("SKIN")) {
                    JsonObject skin = texturesObj.getAsJsonObject("SKIN");
                    if (skin.has("metadata")) {
                        JsonObject metadata = skin.getAsJsonObject("metadata");
                        if (metadata.has("model")) {
                            return metadata.get("model").getAsString().equals("slim");
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
