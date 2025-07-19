package org.axolotlj.serverguard.command;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import org.axolotlj.serverguard.config.ServerGuardConfig;
import org.axolotlj.serverguard.list.blacklist.NameBlacklistManager;
import org.axolotlj.serverguard.list.whitelist.IPWhitelistManager;
import org.axolotlj.serverguard.list.whitelist.UUIDWhitelistManager;
import org.axolotlj.serverguard.util.ServerGuardProtectionTimer;
import org.slf4j.Logger;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.logging.LogUtils;

import net.minecraft.network.chat.Component;


public class ServerGuardCommand {
	
    private static final Logger LOGGER = LogUtils.getLogger();

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
    	LOGGER.info("Register commands");
        dispatcher.register(Commands.literal("serverguard")
            .requires(source -> source.getEntity() == null) 
            .then(Commands.literal("reload")
            	    .executes(ctx -> {
            	        ServerGuardConfig config = ServerGuardConfig.getInstance();
            	        config.load();

            	        IPWhitelistManager.getInstance().load();
            	        UUIDWhitelistManager.getInstance().load();

            	        ctx.getSource().sendSuccess(() -> Component.literal("ServerGuard configuration and whitelists reloaded."), false);
            	        return 1;
            	    })
            	)
            .then(Commands.literal("ipwhitelist")
                .then(Commands.literal("on").executes(ctx -> {
                    ServerGuardConfig config = ServerGuardConfig.getInstance();
                    config.ipWhitelistEnabled = true;
                    config.save();
                    ctx.getSource().sendSuccess(() -> Component.literal("IP whitelist enabled."), false);
                    return 1;
                }))
                .then(Commands.literal("off").executes(ctx -> {
                    ServerGuardConfig config = ServerGuardConfig.getInstance();
                    config.ipWhitelistEnabled = false;
                    config.save();
                    ctx.getSource().sendSuccess(() -> Component.literal("IP whitelist disabled."), false);
                    return 1;
                }))
                .then(Commands.literal("add")
                    .then(Commands.argument("ip", StringArgumentType.string())
                        .executes(ctx -> {
                            String ip = StringArgumentType.getString(ctx, "ip");
                            IPWhitelistManager.getInstance().add(ip);
                            ctx.getSource().sendSuccess(() -> Component.literal("Added IP: " + ip), false);
                            return 1;
                        })))
                .then(Commands.literal("remove")
                    .then(Commands.argument("ip", StringArgumentType.string())
                        .executes(ctx -> {
                            String ip = StringArgumentType.getString(ctx, "ip");
                            IPWhitelistManager.getInstance().remove(ip);
                            ctx.getSource().sendSuccess(() -> Component.literal("Removed IP: " + ip), false);
                            return 1;
                        })))
                .then(Commands.literal("list")
                    .executes(ctx -> {
                        var ips = IPWhitelistManager.getInstance().list();
                        ctx.getSource().sendSuccess(() -> Component.literal("IP Whitelist: " + String.join(", ", ips)), false);
                        return 1;
                    }))
            )
            .then(Commands.literal("uuidwhitelist")
                .then(Commands.literal("on").executes(ctx -> {
                    ServerGuardConfig config = ServerGuardConfig.getInstance();
                    config.uuidWhitelistEnabled = true;
                    config.save();
                    ctx.getSource().sendSuccess(() -> Component.literal("UUID whitelist enabled."), false);
                    return 1;
                }))
                .then(Commands.literal("off").executes(ctx -> {
                    ServerGuardConfig config = ServerGuardConfig.getInstance();
                    config.uuidWhitelistEnabled = false;
                    config.save();
                    ctx.getSource().sendSuccess(() -> Component.literal("UUID whitelist disabled."), false);
                    return 1;
                }))
                .then(Commands.literal("add")
                    .then(Commands.argument("uuid", StringArgumentType.string())
                        .executes(ctx -> {
                            String uuid = StringArgumentType.getString(ctx, "uuid");
                            UUIDWhitelistManager.getInstance().add(uuid);
                            ctx.getSource().sendSuccess(() -> Component.literal("Added UUID: " + uuid), false);
                            return 1;
                        })))
                .then(Commands.literal("remove")
                    .then(Commands.argument("uuid", StringArgumentType.string())
                        .executes(ctx -> {
                            String uuid = StringArgumentType.getString(ctx, "uuid");
                            UUIDWhitelistManager.getInstance().remove(uuid);
                            ctx.getSource().sendSuccess(() -> Component.literal("Removed UUID: " + uuid), false);
                            return 1;
                        })))
                .then(Commands.literal("list")
                    .executes(ctx -> {
                        var uuids = UUIDWhitelistManager.getInstance().list();
                        ctx.getSource().sendSuccess(() -> Component.literal("UUID Whitelist: " + String.join(", ", uuids)), false);
                        return 1;
                    }))
            )
            .then(Commands.literal("nameblacklist")
            	    .then(Commands.literal("on").executes(ctx -> {
            	        ServerGuardConfig config = ServerGuardConfig.getInstance();
            	        config.nameBlacklistEnabled = true;
            	        config.save();
            	        ctx.getSource().sendSuccess(() -> Component.literal("Name blacklist enabled."), false);
            	        return 1;
            	    }))
            	    .then(Commands.literal("off").executes(ctx -> {
            	        ServerGuardConfig config = ServerGuardConfig.getInstance();
            	        config.nameBlacklistEnabled = false;
            	        config.save();
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
            	        }))
            	)
            .then(Commands.literal("whitelist")
            	    .then(Commands.literal("add")
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
            	        }))
            	)
            	.then(Commands.literal("timer")
            	    .then(Commands.literal("start")
            	        .executes(ctx -> {
            	            ServerGuardProtectionTimer.getInstance().start(60); // default 60 seconds
            	            ctx.getSource().sendSuccess(() -> Component.literal("Protections disabled for 60 seconds."), false);
            	            return 1;
            	        })
            	        .then(Commands.argument("duration", StringArgumentType.string())
            	            .executes(ctx -> {
            	                String raw = StringArgumentType.getString(ctx, "duration");
            	                int duration = parseTime(raw); // parse "30s", "100t", etc.
            	                ServerGuardProtectionTimer.getInstance().start(duration);
            	                ctx.getSource().sendSuccess(() -> Component.literal("Protections disabled for " + duration + " seconds."), false);
            	                return 1;
            	            })))
            	    .then(Commands.literal("stop")
            	        .executes(ctx -> {
            	            ServerGuardProtectionTimer.getInstance().stop();
            	            ctx.getSource().sendSuccess(() -> Component.literal("Timer stopped. Protections restored."), false);
            	            return 1;
            	        }))
            	)

        );
    }
    
    private static int parseTime(String input) {
        try {
            if (input.endsWith("t")) {
                return Integer.parseInt(input.replace("t", "")) / 20;
            } else if (input.endsWith("s")) {
                return Integer.parseInt(input.replace("s", ""));
            } else {
                return Integer.parseInt(input); 
            }
        } catch (NumberFormatException e) {
            return 60;
        }
    }

}
