package org.axolotlj.serverguard.config;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import net.minecraftforge.fml.loading.FMLPaths;

import java.nio.file.Path;

/**
 * Utilidad para guardar configuraciones en Forge manualmente.
 */
public class ServerGuardConfigUtil {

    private static final String CONFIG_FILE_NAME = "serverguard/serverguard-common.toml";

    public static void save() {
        Path configPath = FMLPaths.CONFIGDIR.get().resolve(CONFIG_FILE_NAME);

        try (CommentedFileConfig config = CommentedFileConfig.builder(configPath)
                .preserveInsertionOrder()
                .sync()
                .autosave()
                .writingMode(com.electronwill.nightconfig.core.io.WritingMode.REPLACE)
                .build()) {

            config.load();

            config.set("protection.ipWhitelistEnabled", ServerGuardConfig.INSTANCE.ipWhitelistEnabled.get());
            config.set("protection.uuidWhitelistEnabled", ServerGuardConfig.INSTANCE.uuidWhitelistEnabled.get());
            config.set("protection.nameBlacklistEnabled", ServerGuardConfig.INSTANCE.nameBlacklistEnabled.get());

            config.set("alerts.pingAlertsEnabled", ServerGuardConfig.INSTANCE.pingAlertsEnabled.get());
            config.set("alerts.connectionAlertsEnabled", ServerGuardConfig.INSTANCE.connectionAlertsEnabled.get());

            config.save();

        } catch (Exception e) {
            System.err.println("[ServerGuard] Failed to manually save config: " + e.getMessage());
        }
        reload();
    }

    
    public static void reload() {
        Path configPath = FMLPaths.CONFIGDIR.get().resolve(CONFIG_FILE_NAME);

        try (CommentedFileConfig config = CommentedFileConfig.builder(configPath)
                .preserveInsertionOrder()
                .sync()
                .autosave()
                .build()) {

            config.load();
            ServerGuardConfig.CONFIG_SPEC.setConfig(config);

        } catch (Exception e) {
            System.err.println("[ServerGuard] Failed to reload config: " + e.getMessage());
        }
    }

}
