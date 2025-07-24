package org.axolotlj.serverguard.command;

import com.mojang.brigadier.CommandDispatcher;

import net.minecraft.commands.CommandSourceStack;

import org.axolotlj.serverguard.command.subcommand.*;

public class ServerGuardCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
            ReloadCommand.build()
                .then(IPWhitelistCommand.build())
                .then(UUIDWhitelistCommand.build())
                .then(NameBlacklistCommand.build())
                .then(WhitelistCommand.build())
                .then(TimerCommand.build())
                .then(ReloadCommand.build())
        );
    }
}
