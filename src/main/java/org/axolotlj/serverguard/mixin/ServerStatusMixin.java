package org.axolotlj.serverguard.mixin;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Optional;

import org.axolotlj.serverguard.list.whitelist.IPWhitelistManager;
import org.axolotlj.serverguard.mixin.accessor.ServerStatusPacketListenerAccessor;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.logging.LogUtils;

import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.status.ClientboundStatusResponsePacket;
import net.minecraft.network.protocol.status.ServerStatus;
import net.minecraft.network.protocol.status.ServerboundStatusRequestPacket;
import net.minecraft.server.network.ServerStatusPacketListenerImpl;

/**
 * Mixin that logs and intercepts server ping status requests.
 */
@Mixin(ServerStatusPacketListenerImpl.class)
public abstract class ServerStatusMixin {

	private static final Logger LOGGER = LogUtils.getLogger();

	private Connection connection = ((ServerStatusPacketListenerAccessor) (Object) this).getConnection();;

	@Inject(method = "handleStatusRequest", at = @At("HEAD"), cancellable = true)
	private void onHandleStatusRequest(ServerboundStatusRequestPacket packet, CallbackInfo ci) {
		LOGGER.info("[ServerGuard] Received status ping request.");

		String ip = "<unknown>";
		if (connection.getRemoteAddress() instanceof InetSocketAddress inet) {
			ip = inet.getAddress().getHostAddress();
			LOGGER.info("[ServerGuard] Ping request from IP: {}", ip);

			if (!IPWhitelistManager.getInstance().islisted(ip)) {
				LOGGER.warn("[ServerGuard] Blocking ping response for non-whitelisted IP: {}", ip);
				ci.cancel();
				return;
			}
		}

		// Construct a custom ServerStatus response
		ServerStatus customStatus = new ServerStatus(Component.literal("Server is online"),
				Optional.of(new ServerStatus.Players(0, 0, List.of())), Optional.empty(), // No version
				Optional.empty(), // No favicon
				false, Optional.empty());

		logStatus("REPLACED", customStatus);

		// Send response manually
		connection.send(new ClientboundStatusResponsePacket(customStatus));
		ci.cancel(); // Prevent default behavior
	}

	private void logStatus(String label, ServerStatus s) {
		LOGGER.debug("[ServerGuard] --- ServerStatus {} ---", label);
		LOGGER.debug("Description: {}", s.description().getString());

		s.players().ifPresent(p -> {
			LOGGER.debug("Players online: {}", p.online());
			LOGGER.debug("Players max: {}", p.max());
			LOGGER.debug("Players sample: {}", p.sample());
		});

		s.version().ifPresent(v -> {
			LOGGER.debug("Version name: {}", v.name());
			LOGGER.debug("Version protocol: {}", v.protocol());
		});

		LOGGER.debug("Has favicon? {}", s.favicon().isPresent());
	}
}
