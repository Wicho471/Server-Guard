package org.axolotlj.serverguard.mixin;

import com.mojang.logging.LogUtils;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.login.ServerboundHelloPacket;
import net.minecraft.server.network.ServerLoginPacketListenerImpl;

import org.axolotlj.serverguard.list.blacklist.NameBlacklistManager;
import org.axolotlj.serverguard.list.whitelist.IPWhitelistManager;
import org.axolotlj.serverguard.mixin.accessor.ServerStatusPacketListenerAccessor;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.net.InetSocketAddress;

@Mixin(ServerLoginPacketListenerImpl.class)
public abstract class ServerHelloMixin {

    private static final Logger LOGGER = LogUtils.getLogger();

    private Connection connection = ((ServerStatusPacketListenerAccessor) (Object) this).getConnection();

    @Inject(method = "handleHello", at = @At("HEAD"), cancellable = true)
    private void onHandleHello(ServerboundHelloPacket packet, CallbackInfo ci) {
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
