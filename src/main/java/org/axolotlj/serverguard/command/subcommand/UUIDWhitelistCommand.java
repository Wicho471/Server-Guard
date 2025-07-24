package org.axolotlj.serverguard.command.subcommand;

import org.axolotlj.serverguard.command.argument.SuggestionProviders;
import org.axolotlj.serverguard.config.ServerGuardConfig;
import org.axolotlj.serverguard.config.ServerGuardConfigUtil;
import org.axolotlj.serverguard.list.whitelist.UUIDWhitelistManager;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

/**
 * Comando para gestionar UUIDs en la lista blanca.
 */
public class UUIDWhitelistCommand {

    public static LiteralArgumentBuilder<CommandSourceStack> build() {
        return Commands.literal("uuidwhitelist")
                .executes(ctx -> {
                    boolean enabled = ServerGuardConfig.INSTANCE.uuidWhitelistEnabled.get();
                    int count = UUIDWhitelistManager.getInstance().list().size();

                    ctx.getSource().sendSuccess(() -> Component.literal("""
                    		Desc: Whitelist based on the game profile UUID player
                            Usage: /serverguard uuidwhitelist <on|off|add|remove|list>
                            Examples:
                              /serverguard uuidwhitelist add 123e4567-e89b-12d3-a456-426614174000
                              /serverguard uuidwhitelist remove 123e4567-e89b-12d3-a456-426614174000
                              /serverguard uuidwhitelist list

                            Status: """ + (enabled ? "Enabled" : "Disabled") + "\nUUIDs registered: " + count), false);

                    return 1;
                })
                .then(Commands.literal("on").executes(ctx -> {
                	ServerGuardConfig.INSTANCE.uuidWhitelistEnabled.set(true);
                	ServerGuardConfigUtil.save();
                    ctx.getSource().sendSuccess(() -> Component.literal("UUID whitelist enabled."), false);
                    return 1;
                }))
                .then(Commands.literal("off").executes(ctx -> {
                	ServerGuardConfig.INSTANCE.uuidWhitelistEnabled.set(false);
                	ServerGuardConfigUtil.save();
                    ctx.getSource().sendSuccess(() -> Component.literal("UUID whitelist disabled."), false);
                    return 1;
                }))
                .then(Commands.literal("add")
                    .then(Commands.argument("uuid", StringArgumentType.string())
                    	.suggests(SuggestionProviders.UUID_SUGGESTIONS)
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
                    }));
    }
}
