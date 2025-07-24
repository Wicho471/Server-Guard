package org.axolotlj.serverguard.mixins;

import com.mojang.authlib.GameProfile;
import com.mojang.logging.LogUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.login.ServerboundKeyPacket;
import net.minecraft.server.network.ServerLoginPacketListenerImpl;
import org.axolotlj.serverguard.config.ServerGuardConfig;
import org.axolotlj.serverguard.list.whitelist.UUIDWhitelistManager;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(ServerLoginPacketListenerImpl.class)
public abstract class ServerLoginUUIDMixin {

    private static final Logger LOGGER = LogUtils.getLogger();

    @Shadow
    private GameProfile gameProfile;

    @Shadow
    private void disconnect(Component reason) {}

    @Inject(method = "handleKey", at = @At("TAIL"), cancellable = true)
    private void onHandleKey(ServerboundKeyPacket packet, CallbackInfo ci) {
        if (gameProfile == null) {
            LOGGER.debug("GameProfile is null — skipping UUID analysis.");
            return;
        }

        String playerName = gameProfile.getName();
        UUID uuid = gameProfile.getId();
        boolean alerts = ServerGuardConfig.INSTANCE.isConnectionAlertsEnabled();

        if (uuid != null) {
            if (alerts) LOGGER.info("Login attempt - Name: {} | UUID: {}", playerName, uuid);

            if (!UUIDWhitelistManager.getInstance().islisted(uuid.toString())) {
                if (alerts) LOGGER.warn("Blocked login from non-whitelisted UUID: {} (Player: {})", uuid, playerName);
                disconnect(Component.literal("Your UUID is not whitelisted."));
                ci.cancel();
            }

        } else {
            LOGGER.warn("GameProfile has no UUID for player '{}', skipping check.", playerName);
        }
    }
}
