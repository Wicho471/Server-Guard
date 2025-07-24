package org.axolotlj.serverguard.util;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.axolotlj.serverguard.config.ServerGuardConfig;

public class ServerGuardProtectionTimer {

    private static final ServerGuardProtectionTimer INSTANCE = new ServerGuardProtectionTimer();

    private int remainingTicks = 0;
    private boolean active = false;

    private boolean prevIP, prevUUID, prevName;

    private ServerGuardProtectionTimer() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    public static ServerGuardProtectionTimer getInstance() {
        return INSTANCE;
    }

    public void start(int seconds) {
        if (!active) {
            prevIP = ServerGuardConfig.INSTANCE.ipWhitelistEnabled.get();
            prevUUID = ServerGuardConfig.INSTANCE.uuidWhitelistEnabled.get();
            prevName = ServerGuardConfig.INSTANCE.nameBlacklistEnabled.get();
        }

        ServerGuardConfig.INSTANCE.ipWhitelistEnabled.set(false);
        ServerGuardConfig.INSTANCE.uuidWhitelistEnabled.set(false);
        ServerGuardConfig.INSTANCE.nameBlacklistEnabled.set(false);

        this.remainingTicks = seconds * 20;
        this.active = true;
    }

    public void stop() {
        if (!active) return;

        ServerGuardConfig.INSTANCE.ipWhitelistEnabled.set(prevIP);
        ServerGuardConfig.INSTANCE.uuidWhitelistEnabled.set(prevUUID);
        ServerGuardConfig.INSTANCE.nameBlacklistEnabled.set(prevName);

        active = false;
        remainingTicks = 0;
    }

    @SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent event) {
        if (!active || event.phase != TickEvent.Phase.END) return;

        if (--remainingTicks <= 0) {
            stop();
        }
    }
    
    public boolean isActive() {
		return active;
	}
    
    public int getRemainingTicks() {
		return remainingTicks;
	}
}
