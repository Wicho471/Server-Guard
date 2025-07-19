package org.axolotlj.serverguard.mixins;

import com.mojang.authlib.GameProfile;
import com.mojang.logging.LogUtils;
import net.minecraft.network.protocol.login.ServerboundKeyPacket;
import net.minecraft.server.network.ServerLoginPacketListenerImpl;
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

	@Inject(method = "handleKey", at = @At("TAIL"))
	private void onHandleKey(ServerboundKeyPacket packet, CallbackInfo ci) {
		if (gameProfile == null) {
			// No interferimos, solo logueamos
			LOGGER.debug("GameProfile is null — skipping UUID analysis.");
			return;
		}

		String name = gameProfile.getName();
		UUID uuid = gameProfile.getId();

		if (uuid != null) {
			// Aquí puedes hacer análisis del UUID si deseas
			LOGGER.info("Player login - Name: {} | UUID: {}", name, uuid);
		} else {
			// UUID es null, no se hace nada
			LOGGER.debug("GameProfile has no UUID yet for player '{}'", name);
		}
	}
}
