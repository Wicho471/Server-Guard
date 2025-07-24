package org.axolotlj.serverguard.config;

import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class ServerGuardConfig {

    public static final ForgeConfigSpec CONFIG_SPEC;
    public static final ServerGuardConfig INSTANCE;

    public final ForgeConfigSpec.BooleanValue ipWhitelistEnabled;
    public final ForgeConfigSpec.BooleanValue uuidWhitelistEnabled;
    public final ForgeConfigSpec.BooleanValue nameBlacklistEnabled;
    public final ForgeConfigSpec.BooleanValue pingAlertsEnabled;
    public final ForgeConfigSpec.BooleanValue connectionAlertsEnabled;

    static {
        Pair<ServerGuardConfig, ForgeConfigSpec> pair = new ForgeConfigSpec.Builder().configure(ServerGuardConfig::new);
        INSTANCE = pair.getLeft();
        CONFIG_SPEC = pair.getRight();
    }

    private ServerGuardConfig(ForgeConfigSpec.Builder builder) {
        builder.push("protection");
	        ipWhitelistEnabled = builder
	            .comment("Enable IP whitelist protection. Only whitelisted IPs will be allowed.")
	            .define("ipWhitelistEnabled", true);
	
	        uuidWhitelistEnabled = builder
	            .comment("Enable UUID whitelist protection. Only whitelisted UUIDs will be allowed.")
	            .define("uuidWhitelistEnabled", false);
	
	        nameBlacklistEnabled = builder
	            .comment("Enable name blacklist protection. Disallows players with names containing banned patterns.")
	            .define("nameBlacklistEnabled", true);
        builder.pop();
        
        builder.push("alerts");
        	pingAlertsEnabled = builder
                .comment("Enable console logging for ping requests. When enabled, all ping attempts to the dedicated server will be printed to the console.")
                .define("pingAlertsEnabled", true);

            connectionAlertsEnabled = builder
                .comment("Enable console logging for connection attempts. When enabled, all join attempts to the dedicated server will be printed to the console.")
                .define("connectionAlertsEnabled", true);
        builder.pop();
    }

    public boolean isIpWhitelistEnabled() {
        return ipWhitelistEnabled.get();
    }

    public boolean isUuidWhitelistEnabled() {
        return uuidWhitelistEnabled.get();
    }

    public boolean isNameBlacklistEnabled() {
        return nameBlacklistEnabled.get();
    }
    
    public boolean isPingAlertsEnabled() {
		return pingAlertsEnabled.get();
	}
    
    public boolean isConnectionAlertsEnabled() {
		return connectionAlertsEnabled.get();
	}
}
