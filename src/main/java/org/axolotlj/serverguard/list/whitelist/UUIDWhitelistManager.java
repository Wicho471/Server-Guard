package org.axolotlj.serverguard.list.whitelist;

import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

import org.axolotlj.serverguard.config.ServerGuardConfig;
import org.axolotlj.serverguard.list.AbstractListManager;

public class UUIDWhitelistManager extends AbstractListManager {

    private static final UUIDWhitelistManager INSTANCE = new UUIDWhitelistManager();

    private UUIDWhitelistManager() {
        super("whitelist_uuids.json");
    }

    public static UUIDWhitelistManager getInstance() {
        return INSTANCE;
    }
    
    @Override
    public synchronized boolean islisted(String entry) {
        if (!ServerGuardConfig.INSTANCE.uuidWhitelistEnabled.get()) {
            return true;
        }
        return list.contains(entry);
    }

    @Override
    protected Type getListType() {
        return new TypeToken<WhitelistData>() {}.getType();
    }
}
