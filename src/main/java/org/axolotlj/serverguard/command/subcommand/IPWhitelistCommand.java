package org.axolotlj.serverguard.command.subcommand;

import org.axolotlj.serverguard.command.argument.SuggestionProviders;
import org.axolotlj.serverguard.config.ServerGuardConfig;
import org.axolotlj.serverguard.config.ServerGuardConfigUtil;
import org.axolotlj.serverguard.list.whitelist.IPWhitelistManager;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

/**
 * Comando para gestionar direcciones IP en la lista blanca.
 */
public class IPWhitelistCommand {

    public static LiteralArgumentBuilder<CommandSourceStack> build() {
        return Commands.literal("ipwhitelist")
                .executes(ctx -> {
                    boolean enabled = ServerGuardConfig.INSTANCE.ipWhitelistEnabled.get();
                    int count = IPWhitelistManager.getInstance().list().size();

                    ctx.getSource().sendSuccess(() -> Component.literal("""
                            Usage: /serverguard ipwhitelist <on|off|add|remove|list>
                            Examples:
                              /serverguard ipwhitelist add 192.168.1.10
                              /serverguard ipwhitelist remove 192.168.1.10
                              /serverguard ipwhitelist list

                            Estatus: """ + (enabled ? "Enabled" : "Disabled") + "\nIPs registreds: " + count), false);

                    return 1;
                })
                .then(Commands.literal("on").executes(ctx -> {
                	ServerGuardConfig.INSTANCE.ipWhitelistEnabled.set(true);
                	ServerGuardConfigUtil.save();
                    ctx.getSource().sendSuccess(() -> Component.literal("IP whitelist enabled."), false);
                    return 1;
                }))
                .then(Commands.literal("off").executes(ctx -> {
                	ServerGuardConfig.INSTANCE.ipWhitelistEnabled.set(false);
                	ServerGuardConfigUtil.save();
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
                    	.suggests(SuggestionProviders.IP_SUGGESTIONS)
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
                    }));
    }
}
