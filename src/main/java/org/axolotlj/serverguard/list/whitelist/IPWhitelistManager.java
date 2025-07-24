package org.axolotlj.serverguard.list.whitelist;

import java.lang.reflect.Type;

import org.axolotlj.serverguard.ServerGuard;
import org.axolotlj.serverguard.config.ServerGuardConfig;
import org.axolotlj.serverguard.list.AbstractListManager;
import org.axolotlj.serverguard.util.IPAddressResolver;

import com.google.gson.reflect.TypeToken;


public class IPWhitelistManager extends AbstractListManager {

    private static final IPWhitelistManager INSTANCE = new IPWhitelistManager();

    private IPWhitelistManager() {
        super("whitelist_ips.json");
    }

    public static IPWhitelistManager getInstance() {
        return INSTANCE;
    }

    @Override
    public synchronized boolean islisted(String entry) {
        if (!ServerGuardConfig.INSTANCE.ipWhitelistEnabled.get()) {
            return true;
        }

        if (IPAddressResolver.isLocal(entry)) {
            try {
                if (!ServerGuard.isDedicatedServer()) return true;
            } catch (Exception e) {
                logger.warn("[ServerGuard] Could not check if server is dedicated: {}", e.getMessage());
            }
        }

        return list.contains(entry);
    }

    
    @Override
    protected Type getListType() {
        return new TypeToken<WhitelistData>() {}.getType();
    }
}
