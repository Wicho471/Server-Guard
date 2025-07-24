package org.axolotlj.serverguard.command.subcommand;

import org.axolotlj.serverguard.command.argument.SuggestionProviders;
import org.axolotlj.serverguard.list.whitelist.IPWhitelistManager;
import org.axolotlj.serverguard.list.whitelist.UUIDWhitelistManager;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

/**
 * Comando para activar o desactivar el estado global de la whitelist.
 */
public class WhitelistCommand {

    public static LiteralArgumentBuilder<CommandSourceStack> build() {
        return Commands.literal("whitelist")
	                .executes(ctx -> {
	                    var ipCount = IPWhitelistManager.getInstance().list().size();
	                    var uuidCount = UUIDWhitelistManager.getInstance().list().size();
	
	                    ctx.getSource().sendSuccess(() -> Component.literal("""
	                    		Desc: Adds info from the player logged into whitelists like IP White List and UUID White List
	                            Usage: /serverguard whitelist <add|remove|list>
	                            Examples:
	                              /serverguard whitelist add <player>
	                              /serverguard whitelist remove <player>
	                              /serverguard whitelist list
	
	                            Whitelisted IPs: """ + ipCount + "\nWhitelisted UUIDs: " + uuidCount), false);
	                    return 1;
	                })
            	    .then(Commands.literal("add")
            	        .then(Commands.argument("player", StringArgumentType.string())
            	        	.suggests(SuggestionProviders.ONLINE_PLAYERS)
            	            .executes(ctx -> {
            	                String playerName = StringArgumentType.getString(ctx, "player");
            	                var server = ctx.getSource().getServer();
            	                var player = server.getPlayerList().getPlayerByName(playerName);

            	                if (player == null) {
            	                    ctx.getSource().sendFailure(Component.literal("Player not found: " + playerName));
            	                    return 0;
            	                }

            	                String uuid = player.getUUID().toString();
            	                var address = player.connection.connection.getRemoteAddress();
            	                String ip = "unknown";
            	                if (address instanceof java.net.InetSocketAddress inet) {
            	                    ip = inet.getAddress().getHostAddress();
            	                }

            	                IPWhitelistManager.getInstance().add(ip);
            	                UUIDWhitelistManager.getInstance().add(uuid);

            	                String message = "Whitelisted player: " + playerName + " | IP: " + ip + " | UUID: " + uuid;

            	                ctx.getSource().sendSuccess(() -> Component.literal(message), false);
            	                return 1;
            	            })))
            	    .then(Commands.literal("remove")
            	        .then(Commands.argument("player", StringArgumentType.string())
            	            .executes(ctx -> {
            	                String playerName = StringArgumentType.getString(ctx, "player");
            	                var server = ctx.getSource().getServer();
            	                var player = server.getPlayerList().getPlayerByName(playerName);

            	                if (player == null) {
            	                    ctx.getSource().sendFailure(Component.literal("Player not found: " + playerName));
            	                    return 0;
            	                }

            	                String uuid = player.getUUID().toString();
            	                var address = player.connection.connection.getRemoteAddress();
            	                String ip = "unknown";
            	                if (address instanceof java.net.InetSocketAddress inet) {
            	                    ip = inet.getAddress().getHostAddress();
            	                }

            	                IPWhitelistManager.getInstance().remove(ip);
            	                UUIDWhitelistManager.getInstance().remove(uuid);
            	                String message = "Removed from whitelist: " + playerName + " | IP: " + ip + " | UUID: " + uuid;
            	                ctx.getSource().sendSuccess(() -> Component.literal(message), false);
            	                return 1;
            	            })))
            	    .then(Commands.literal("list")
            	        .executes(ctx -> {
            	            var ips = IPWhitelistManager.getInstance().list();
            	            var uuids = UUIDWhitelistManager.getInstance().list();
            	            ctx.getSource().sendSuccess(() -> Component.literal("Whitelisted IPs: " + String.join(", ", ips)), false);
            	            ctx.getSource().sendSuccess(() -> Component.literal("Whitelisted UUIDs: " + String.join(", ", uuids)), false);
            	            return 1;
            	        }));
    }
}
