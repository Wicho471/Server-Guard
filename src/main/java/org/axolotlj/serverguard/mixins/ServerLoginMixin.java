package org.axolotlj.serverguard.mixins;

import java.net.InetSocketAddress;

import org.axolotlj.serverguard.list.whitelist.IPWhitelistManager;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.logging.LogUtils;

import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.server.network.ServerLoginPacketListenerImpl;

@Mixin(ServerLoginPacketListenerImpl.class)
public abstract class ServerLoginMixin {
	
	@Shadow
	  private Connection connection;
	
	private static final Logger LOGGER = LogUtils.getLogger();
	
    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void interceptLoginTick(CallbackInfo ci) {
    	if (connection.getRemoteAddress() instanceof InetSocketAddress inet) {
        	
            String ip = inet.getAddress().getHostAddress();
            LOGGER.debug("Player connecting from IP: {}", ip);

            if (!IPWhitelistManager.getInstance().islisted(ip)) {
                LOGGER.info("Blocked connection from non-whitelisted IP: {}", ip);
                connection.disconnect(Component.literal("Your IP address is not whitelisted."));
                ci.cancel();
            }
        }
    }
}
	