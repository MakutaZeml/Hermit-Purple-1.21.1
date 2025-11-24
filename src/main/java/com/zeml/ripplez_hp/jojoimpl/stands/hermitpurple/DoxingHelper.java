package com.zeml.ripplez_hp.jojoimpl.stands.hermitpurple;

import com.github.standobyte.jojo.powersystem.standpower.StandPower;
import com.github.standobyte.jojo.util.StandUtil;
import com.zeml.ripplez_hp.core.HermitPurpleAddon;
import com.zeml.ripplez_hp.init.AddonDataAttachmentTypes;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import com.mojang.datafixers.util.Pair;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.structure.Structure;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class DoxingHelper {

    /*
    Modes:
    0: Random
    1: Players
    2: Entity Type
    3: Stand Type
    4: Structure
    5: Biomes
     */

    @Nullable
    public static Entity HPLivingObjectives(@NotNull LivingEntity user){
        return switch (user.getData(AddonDataAttachmentTypes.HERMIT_DATA).getMode()) {
            case 1 -> HPPlayer(user);
            case 2 -> HPEntities(user);
            case 3 -> HPUser(user);
            default -> HPGeneric(user);
        };
    }

    @Nullable
    private static LivingEntity HPGeneric(@NotNull LivingEntity user){
        List<LivingEntity> livings = user.level().getEntitiesOfClass(LivingEntity.class,user.getBoundingBox().inflate(250) , living->living.isAlive() && ((LivingEntity) living).getMaxHealth()>30);
        if(!livings.isEmpty()){
            int n = livings.size();
            int i = (int) Math.floor(n*Math.random());
            return livings.get(i);
        }
        return null;
    }

    @Nullable
    private static Player HPPlayer(@NotNull LivingEntity user){
        return user.level().players().stream().map(player -> {
            if(player.getName().getString().equals(user.getData(AddonDataAttachmentTypes.HERMIT_DATA).getTarget())){
                return player;
            } else if (player.getUUID().toString().equals(user.getData(AddonDataAttachmentTypes.HERMIT_DATA).getTarget())) {
                return player;
            }
                    return null;
        }
        ).findAny().orElse(null);
    }



    @Nullable
    private static Entity HPEntities(@NotNull LivingEntity user){
        List<Entity> entities = user.level().getEntitiesOfClass(Entity.class,user.getBoundingBox().inflate(1000),
                entity ->entity.isAlive() && entity.getType().getDescriptionId().replace("entity.","")
                        .replace(".",":").equals(user.getData(AddonDataAttachmentTypes.HERMIT_DATA).getTarget()));
        if(!entities.isEmpty()){
            int max = entities.size();
            return entities.get((int) Math.floor(max*Math.random()));
        }
        return null;
    }

    @Nullable
    private static LivingEntity HPUser(@NotNull LivingEntity user){
        List<LivingEntity> entities = user.level().getEntitiesOfClass(LivingEntity.class, user.getBoundingBox().inflate(1000),
                entity -> entity.isAlive() &&  StandUtil.isEntityStandUser(entity) && StandPower.get(entity).getPowerType().getId().toString().equals(user.getData(AddonDataAttachmentTypes.HERMIT_DATA).getTarget()));
        if(!entities.isEmpty()){
            return entities.stream().findAny().orElse(null);
        }
        return null;
    }

    @Nullable
    public static BlockPos biomesPos(@NotNull LivingEntity user){
        Pair<BlockPos, Holder<Biome>> pair = ((ServerLevel) user.level()).getChunkSource().getGenerator().getBiomeSource()
                .findClosestBiome3d(user.getOnPos(),3000,8,8,
                        biomeHolder -> biomeHolder.getRegisteredName().toString().equals(user.getData(AddonDataAttachmentTypes.HERMIT_DATA).getTarget()),
                        ((ServerLevel) user.level()).getChunkSource().randomState().sampler(),user.level()
                        );
        if(pair != null){
            return pair.getFirst();
        }
        return null;
    }

    @Nullable
    public static BlockPos structurePos(@NotNull LivingEntity user){
        ServerLevel level = (ServerLevel) user.level();
        Registry<Structure> structures = level.registryAccess().registryOrThrow(Registries.STRUCTURE);
        String structure = user.getData(AddonDataAttachmentTypes.HERMIT_DATA).getTarget();
        if(structure.contains("cities")){
            structure = structure.replace("cities","city");
        }
        Optional<Holder.Reference<Structure>> structureHolder = structures.getHolder(structures.getId(ResourceLocation.tryParse(structure)));
        HermitPurpleAddon.LOGGER.debug("Pair {}", structureHolder);
        if(structureHolder.isPresent()){
            HolderSet<Structure> holderSet = HolderSet.direct(structureHolder.get());
            return level.getChunkSource().getGenerator().findNearestMapStructure(level, holderSet,user.getOnPos(),3000,false).getFirst();
        }
        return null;
    }




}
