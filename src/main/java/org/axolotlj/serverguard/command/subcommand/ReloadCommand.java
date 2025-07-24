package org.axolotlj.serverguard.command.subcommand;

import org.axolotlj.serverguard.config.ServerGuardConfigUtil;
import org.axolotlj.serverguard.list.whitelist.IPWhitelistManager;
import org.axolotlj.serverguard.list.whitelist.UUIDWhitelistManager;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

/**
 * Comando para recargar la configuración de ServerGuard.
 */
public class ReloadCommand {

    public static LiteralArgumentBuilder<CommandSourceStack> build() {
        return Commands.literal("serverguard")
            .requires(source -> source.getEntity() == null)
            .then(Commands.literal("reload")
                .executes(ctx -> {
                    ServerGuardConfigUtil.reload();
                    IPWhitelistManager.getInstance().load();
                    UUIDWhitelistManager.getInstance().load();
                    ctx.getSource().sendSuccess(() -> Component.literal("ServerGuard configuration and whitelists reloaded."), false);
                    return 1;
                })
            );
    }
}
