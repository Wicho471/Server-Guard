package org.axolotlj.serverguard.mixin.accessor;

import net.minecraft.network.Connection;
import net.minecraft.server.network.ServerStatusPacketListenerImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ServerStatusPacketListenerImpl.class)
public interface ServerStatusPacketListenerAccessor {
    @Accessor("connection")
    Connection getConnection();
}
