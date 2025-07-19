package org.axolotlj.serverguard.mixins;

import com.mojang.logging.LogUtils;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.login.ServerboundHelloPacket;
import net.minecraft.server.network.ServerLoginPacketListenerImpl;

import org.axolotlj.serverguard.list.blacklist.NameBlacklistManager;
import org.axolotlj.serverguard.list.whitelist.IPWhitelistManager;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.net.InetSocketAddress;

@Mixin(ServerLoginPacketListenerImpl.class)
public abstract class ServerHelloMixin {
	
	@Shadow
	  private Connection connection;

    private static final Logger LOGGER = LogUtils.getLogger();
    
    @Inject(method = "handleHello", at = @At("HEAD"), cancellable = true)
    private void onHandleHello(ServerboundHelloPacket packet, CallbackInfo ci) {
    	//Connection connection = ((ServerStatusPacketListenerAccessor) this).getConnection();
    	if (connection.getRemoteAddress() instanceof InetSocketAddress inet) {
        	

            String ip = inet.getAddress().getHostAddress();
            String playerName = packet.name();

            LOGGER.info("Intercepted login attempt from IP: {} | Name: {}", ip, playerName);
            
            if (NameBlacklistManager.getInstance().isBlacklisted(playerName)) {
            	LOGGER.warn("Blocked login from blacklisted name: {}", playerName);
            	connection.disconnect(Component.literal("You are banned from this server."));
            	 ci.cancel();
            }

            if (!IPWhitelistManager.getInstance().islisted(ip)) {
                LOGGER.warn("Blocked login from non-whitelisted IP: {}", ip);
                connection.disconnect(Component.literal("Your IP address is not whitelisted."));
                ci.cancel(); // Detiene el procesamiento del login
            }
        }
    }
}
