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
            prevIP = ServerGuardConfig.getInstance().ipWhitelistEnabled;
            prevUUID = ServerGuardConfig.getInstance().uuidWhitelistEnabled;
            prevName = ServerGuardConfig.getInstance().nameBlacklistEnabled;
        }

        ServerGuardConfig config = ServerGuardConfig.getInstance();
        config.ipWhitelistEnabled = false;
        config.uuidWhitelistEnabled = false;
        config.nameBlacklistEnabled = false;
        config.save();

        this.remainingTicks = seconds * 20;
        this.active = true;
    }

    public void stop() {
        if (!active) return;

        ServerGuardConfig config = ServerGuardConfig.getInstance();
        config.ipWhitelistEnabled = prevIP;
        config.uuidWhitelistEnabled = prevUUID;
        config.nameBlacklistEnabled = prevName;
        config.save();

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

    private ServerGuardConfig getConfig() {
        return ServerGuardConfig.getInstance();
    }
}
