package org.axolotlj.serverguard.mixins;

import java.net.InetSocketAddress;

import org.axolotlj.serverguard.config.ServerGuardConfig;
import org.axolotlj.serverguard.list.whitelist.IPWhitelistManager;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.logging.LogUtils;

import net.minecraft.network.Connection;
import net.minecraft.network.protocol.status.ServerboundStatusRequestPacket;
import net.minecraft.server.network.ServerStatusPacketListenerImpl;

/**
 * Mixin that logs and intercepts server ping status requests.
 */
@Mixin(ServerStatusPacketListenerImpl.class)
public abstract class ServerStatusMixin {

	@Shadow
	private Connection connection;

	private static final Logger LOGGER = LogUtils.getLogger();

	@Inject(method = "handleStatusRequest", at = @At("HEAD"), cancellable = true)
	private void onHandleStatusRequest(ServerboundStatusRequestPacket packet, CallbackInfo ci) {
		try {
			boolean alerts = ServerGuardConfig.INSTANCE.isPingAlertsEnabled();
			if (connection == null) {
				LOGGER.error("[ServerGuard][info] Connection object is null! Aborting.");
				return;
			}

			String ip = "<unknown>";
			if (connection.getRemoteAddress() instanceof InetSocketAddress inet) {
				ip = inet.getAddress().getHostAddress();
				if (alerts) LOGGER.info("[ServerGuard] Ping request from IP: {}", ip);

				if (!IPWhitelistManager.getInstance().islisted(ip)) {
					if (alerts) LOGGER.warn("[ServerGuard] Blocking ping response for non-whitelisted IP: {}", ip);
					connection.channel().close();
					ci.cancel();
					return;
				}
			} else {
				LOGGER.info("[ServerGuard][info] Remote address is not an InetSocketAddress: {}",
						connection.getRemoteAddress());
			}

		} catch (ClassCastException e) {
			LOGGER.error(
					"[ServerGuard][ERROR] Failed to cast 'this' to ServerStatusPacketListenerAccessor. Is the mixin applied?",
					e);
		} catch (Exception e) {
			LOGGER.error("[ServerGuard][ERROR] Unexpected exception in onHandleStatusRequest", e);
		}
	}
}
