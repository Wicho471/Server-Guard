package org.axolotlj.serverguard.command.subcommand;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.arguments.StringArgumentType;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

import org.axolotlj.serverguard.list.blacklist.NameBlacklistManager;
import org.axolotlj.serverguard.command.argument.SuggestionProviders;
import org.axolotlj.serverguard.config.ServerGuardConfig;
import org.axolotlj.serverguard.config.ServerGuardConfigUtil;

/**
 * Comando para gestionar nombres en la lista negra.
 */
public class NameBlacklistCommand {

    public static LiteralArgumentBuilder<CommandSourceStack> build() {
        return Commands.literal("nameblacklist")
                .executes(ctx -> {
                    boolean enabled = ServerGuardConfig.INSTANCE.nameBlacklistEnabled.get();
                    int count = NameBlacklistManager.getInstance().list().size();

                    ctx.getSource().sendSuccess(() -> Component.literal("""
                    		Desc: Bans access based on the entire name (e.g., if the ban is for "Luis" it will deny access to "Luis", "LUIS", "123luis567", etc.)
                            Usage: /serverguard nameblacklist <on|off|add|remove|list>
                            Examples:
                              /serverguard nameblacklist add badword
                              /serverguard nameblacklist remove badword
                              /serverguard nameblacklist list

                            Status: """ + (enabled ? "Enabled" : "Disabled") + "\nPatterns registered: " + count), false);

                    return 1;
                })
        	    .then(Commands.literal("on").executes(ctx -> {
        	    	ServerGuardConfig.INSTANCE.nameBlacklistEnabled.set(true);
                	ServerGuardConfigUtil.save();
        	        ctx.getSource().sendSuccess(() -> Component.literal("Name blacklist enabled."), false);
        	        return 1;
        	    }))
        	    .then(Commands.literal("off").executes(ctx -> {
        	        ServerGuardConfig.INSTANCE.nameBlacklistEnabled.set(false);
                	ServerGuardConfigUtil.save();
        	        ctx.getSource().sendSuccess(() -> Component.literal("Name blacklist disabled."), false);
        	        return 1;
        	    }))
        	    .then(Commands.literal("add")
        	        .then(Commands.argument("substring", StringArgumentType.string())
        	            .executes(ctx -> {
        	                String substring = StringArgumentType.getString(ctx, "substring").toLowerCase();
        	                NameBlacklistManager.getInstance().add(substring);
        	                ctx.getSource().sendSuccess(() -> Component.literal("Added name pattern: " + substring), false);
        	                return 1;
        	            })))
        	    .then(Commands.literal("remove")
        	        .then(Commands.argument("substring", StringArgumentType.string())
        	        	.suggests(SuggestionProviders.NAMEBLACKLIST_SUGGESTIONS)
        	            .executes(ctx -> {
        	                String substring = StringArgumentType.getString(ctx, "substring").toLowerCase();
        	                NameBlacklistManager.getInstance().remove(substring);
        	                ctx.getSource().sendSuccess(() -> Component.literal("Removed name pattern: " + substring), false);
        	                return 1;
        	            })))
        	    .then(Commands.literal("list")
        	        .executes(ctx -> {
        	            var patterns = NameBlacklistManager.getInstance().list();
        	            ctx.getSource().sendSuccess(() -> Component.literal("Name blacklist patterns: " + String.join(", ", patterns)), false);
        	            return 1;
        	        }));
    }
}
