package org.axolotlj.serverguard.command.subcommand;

import org.axolotlj.serverguard.command.parser.TimeParser;
import org.axolotlj.serverguard.util.ServerGuardProtectionTimer;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

/**
 * Comando para establecer un temporizador de whitelist para un jugador.
 */
public class TimerCommand {

    public static LiteralArgumentBuilder<CommandSourceStack> build() {
        return Commands.literal("timer")
	                .executes(ctx -> {
	                    var timer = ServerGuardProtectionTimer.getInstance();
	                    String status = timer.isActive()
	                        ? "Timer running, time remaining: " + timer.getRemainingTicks()/20 + " seconds."
	                        : "Timer currently inactive.";
	
	                    ctx.getSource().sendSuccess(() -> Component.literal("""
	                    		Desc: Disables all protections for a certain amount of time so that players can more easily log in and be logged into the server console using /serverguard whitelist add <player>
	                            Usage: /timer <start|stop> [duration<s|t>]
	                            Duration format: number followed by 's' for seconds or 't' for ticks (e.g., 30s, 100t)
	                            Example: /timer start 30s
	                            Status: """ + status), false);
	
	                    return 1;
	                })
            	    .then(Commands.literal("start")
            	        .executes(ctx -> {
            	            ServerGuardProtectionTimer.getInstance().start(60); // default 60 seconds
            	            ctx.getSource().sendSuccess(() -> Component.literal("Protections disabled for 60 seconds."), false);
            	            return 1;
            	        })
            	        .then(Commands.argument("duration", StringArgumentType.string())
            	            .executes(ctx -> {
            	                String raw = StringArgumentType.getString(ctx, "duration");
            	                int duration = TimeParser.parse(raw); // parse "30s", "100t", etc.
            	                ServerGuardProtectionTimer.getInstance().start(duration);
            	                ctx.getSource().sendSuccess(() -> Component.literal("Protections disabled for " + duration + " seconds."), false);
            	                return 1;
            	            })))
            	    .then(Commands.literal("stop")
            	        .executes(ctx -> {
            	            ServerGuardProtectionTimer.getInstance().stop();
            	            ctx.getSource().sendSuccess(() -> Component.literal("Timer stopped. Protections restored."), false);
            	            return 1;
            	        }));
    }
}
