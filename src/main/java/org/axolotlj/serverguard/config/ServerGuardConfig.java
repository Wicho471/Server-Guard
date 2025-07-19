package org.axolotlj.serverguard.config;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

import org.slf4j.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.logging.LogUtils;

import net.minecraftforge.fml.loading.FMLPaths;

public class ServerGuardConfig {

    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final File CONFIG_FILE = FMLPaths.CONFIGDIR.get().resolve("serverguard-config.json").toFile();

    public boolean ipWhitelistEnabled = false;
    public boolean uuidWhitelistEnabled = false;
    public boolean nameBlacklistEnabled = false;

    private static ServerGuardConfig INSTANCE;

    public static ServerGuardConfig getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ServerGuardConfig();
            INSTANCE.load();
        }
        return INSTANCE;
    }

    public void load() {
        try {
            if (CONFIG_FILE.exists()) {
                try (FileReader reader = new FileReader(CONFIG_FILE)) {
                    ServerGuardConfig config = GSON.fromJson(reader, ServerGuardConfig.class);
                    this.ipWhitelistEnabled = config.ipWhitelistEnabled;
                    this.uuidWhitelistEnabled = config.uuidWhitelistEnabled;
                    this.nameBlacklistEnabled = config.nameBlacklistEnabled;
                }
            } else {
                save(); 
            }
        } catch (Exception e) {
            LOGGER.error("[ServerGuard] Failed to load configuration", e);
        }
    }

    public void save() {
        try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
            GSON.toJson(this, writer);
        } catch (Exception e) {
            LOGGER.error("[ServerGuard] Failed to save configuration", e);
        }
    }
}
