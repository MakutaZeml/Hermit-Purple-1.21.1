package com.zeml.ripplez_hp.core.command;

import com.github.standobyte.jojo.core.command.JojoCommandsCommand;
import com.github.standobyte.jojo.core.command.argument.StandArgument;
import com.github.standobyte.jojo.powersystem.standpower.type.StandType;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.zeml.ripplez_hp.core.HermitPurpleAddon;
import com.zeml.ripplez_hp.init.AddonDataAttachmentTypes;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceArgument;
import net.minecraft.commands.arguments.ResourceOrTagArgument;
import net.minecraft.commands.arguments.ResourceOrTagKeyArgument;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.structure.Structure;

public class HermitTargetCommand {
    private static final SimpleCommandExceptionType ERROR_GIVE_FAILED = new SimpleCommandExceptionType(Component.translatable("commands.hermit.target.failed"));
    private static final DynamicCommandExceptionType ERROR_STRUCTURE_INVALID = new DynamicCommandExceptionType((p_304258_) -> {
        return Component.translatableEscape("commands.locate.structure.invalid", new Object[]{p_304258_});
    });
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext context){
        dispatcher.register(
                Commands.literal("hermit").requires(src -> src.hasPermission(0))
                        .then(Commands.literal("player")
                                .then(Commands.argument("player", EntityArgument.player())
                                        .executes(ctx->setPlayerTarget(ctx.getSource(),EntityArgument.getPlayer(ctx,"player")))
                                )
                        )
                        .then(Commands.literal("entity")
                                .then(Commands.argument("entity", ResourceArgument.resource(context, Registries.ENTITY_TYPE))
                                        .executes(ctx->setTargetEntity(ctx.getSource(), ResourceArgument.getSummonableEntityType(ctx, "entity")))
                                )
                        )
                        .then(Commands.literal("stand")
                                .then(Commands.argument("stand", StandArgument.stand(context))
                                        .executes(ctx-> setTargetStand(ctx.getSource(),StandArgument.getStand(ctx,"stand")))
                                )
                        )
                        .then(Commands.literal("biome")
                                .then(Commands.argument("biome", ResourceOrTagArgument.resourceOrTag(context, Registries.BIOME))
                                        .executes(ctx->setTargetBiome(ctx.getSource(), ResourceOrTagArgument.getResourceOrTag(ctx, "biome", Registries.BIOME)))
                                )
                        )
                        .then(Commands.literal("structure")
                                .then(Commands.argument("structure", ResourceOrTagKeyArgument.resourceOrTagKey(Registries.STRUCTURE))
                                        .executes(p_258233_ -> {
                                                    return setTargetStructure(p_258233_.getSource(), ResourceOrTagKeyArgument.getResourceOrTagKey(p_258233_, "structure", Registries.STRUCTURE, ERROR_STRUCTURE_INVALID));
                                        })
                                )
                        )
                        .then(Commands.literal("random")
                                .executes(ctx->setTargetRandom(ctx.getSource()))
                        )
        );
        JojoCommandsCommand.addCommand("hermit");
    }

    private static int setPlayerTarget(CommandSourceStack source, Entity player) throws CommandSyntaxException {
        int success = 0;
        Entity sourceEntity = source.getEntity();
        if(sourceEntity instanceof LivingEntity){
            sourceEntity.setData(AddonDataAttachmentTypes.MODE,1);
            sourceEntity.setData(AddonDataAttachmentTypes.TARGET,player.getName().getString());
            success = 1;
        }
        if(success ==0 ){
            throw ERROR_GIVE_FAILED.create();
        }else {
            source.sendSuccess( () -> Component.translatable(
                            "commands.hermit.target.succes", player.getName(), sourceEntity.getName()),
                    true);
        }
        return success;
    }

    private static int setTargetEntity(CommandSourceStack source, Holder.Reference<EntityType<?>> resourceLocation)throws CommandSyntaxException{
        int success = 0;
        Entity sourceEntity = source.getEntity();
        if(sourceEntity instanceof LivingEntity){
            HermitPurpleAddon.LOGGER.debug("String {}", resourceLocation.getKey().location());
            sourceEntity.setData(AddonDataAttachmentTypes.MODE,2);
            sourceEntity.setData(AddonDataAttachmentTypes.TARGET,resourceLocation.getKey().location().toString());
            success = 1;
        }
        if(success ==0 ){
            throw ERROR_GIVE_FAILED.create();
        }else {
            source.sendSuccess( () -> Component.translatable(
                            "commands.hermit.target.succes", resourceLocation.getKey().location().toString(), sourceEntity.getName()),
                    true);
        }
        return success;
    }

    private static int setTargetStand(CommandSourceStack source, StandType standType)throws CommandSyntaxException{
        int success = 0;
        Entity sourceEntity = source.getEntity();
        if(sourceEntity instanceof LivingEntity){
            HermitPurpleAddon.LOGGER.debug("String {}", standType.getId() );
            sourceEntity.setData(AddonDataAttachmentTypes.MODE,3);
            sourceEntity.setData(AddonDataAttachmentTypes.TARGET, standType.getId().toString());
            success = 1;
        }
        if(success ==0 ){
            throw ERROR_GIVE_FAILED.create();
        }else {
            source.sendSuccess( () -> Component.translatable(
                            "commands.hermit.target.succes", standType.name.get(), sourceEntity.getName()),
                    true);
        }
        return success;
    }

    private static int setTargetStructure(CommandSourceStack source, ResourceOrTagKeyArgument.Result<Structure> structure)throws CommandSyntaxException{
        int success = 0;
        Entity sourceEntity = source.getEntity();
        if(sourceEntity instanceof LivingEntity){
            sourceEntity.setData(AddonDataAttachmentTypes.MODE,4);
            sourceEntity.setData(AddonDataAttachmentTypes.TARGET,structure.unwrap().left().get().location().toString());
            success = 1;
        }
        if(success ==0 ){
            throw ERROR_GIVE_FAILED.create();
        }else {
            source.sendSuccess( () -> Component.translatable(
                            "commands.hermit.target.succes", structure.unwrap().left().get().location().toString(), sourceEntity.getName()),
                    true);
        }
        return success;
    }


    private static int setTargetBiome(CommandSourceStack source,  ResourceOrTagArgument.Result<Biome> biome)throws CommandSyntaxException{
        int success = 0;
        Entity sourceEntity = source.getEntity();
        if(sourceEntity instanceof LivingEntity){
            sourceEntity.setData(AddonDataAttachmentTypes.MODE,5);
            sourceEntity.setData(AddonDataAttachmentTypes.TARGET,biome.unwrap().left().get().getKey().location().toString());
            success = 1;
        }
        if(success ==0 ){
            throw ERROR_GIVE_FAILED.create();
        }else {
            source.sendSuccess( () -> Component.translatable(
                            "commands.hermit.target.succes", biome.unwrap().left().get().getKey().location().toString(), sourceEntity.getName()),
                    true);
        }
        return success;
    }


    private static int setTargetRandom(CommandSourceStack source)throws CommandSyntaxException{
        int success = 0;
        Entity sourceEntity = source.getEntity();
        if(sourceEntity instanceof LivingEntity){
            sourceEntity.setData(AddonDataAttachmentTypes.MODE,0);
            sourceEntity.setData(AddonDataAttachmentTypes.TARGET,"");
            success = 1;
        }
        if(success ==0 ){
            throw ERROR_GIVE_FAILED.create();
        }else {
            source.sendSuccess( () -> Component.translatable(
                            "commands.hermit.target.succes","commands.hermit.target.random", sourceEntity.getName()),
                    true);
        }
        return success;
    }
}
