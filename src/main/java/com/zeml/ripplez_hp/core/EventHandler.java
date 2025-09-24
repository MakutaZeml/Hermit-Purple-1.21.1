package com.zeml.ripplez_hp.core;


import com.mojang.brigadier.CommandDispatcher;
import com.zeml.ripplez_hp.core.command.HermitTargetCommand;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

@EventBusSubscriber(modid = HermitPurpleAddon.MOD_ID)
public class EventHandler {
    @SubscribeEvent
    public static void registerCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        CommandBuildContext context = event.getBuildContext();
        HermitTargetCommand.register(dispatcher, context);
    }
}
