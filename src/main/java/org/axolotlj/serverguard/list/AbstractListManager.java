package org.axolotlj.serverguard.list;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Type;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.logging.LogUtils;

import net.minecraftforge.fml.loading.FMLPaths;

public abstract class AbstractListManager {

	protected final Logger logger = LogUtils.getLogger();
	protected final Gson gson = new GsonBuilder().setPrettyPrinting().create();

	protected final Set<String> list = new HashSet<>();

	protected final Path configDir = FMLPaths.CONFIGDIR.get().resolve("serverguard");
	protected final File file;

	protected AbstractListManager(String fileName) {
		this.file = configDir.resolve(fileName).toFile();
		load();
	}

	public synchronized boolean islisted(String entry) {
		return list.contains(entry);
	}

	public synchronized void add(String entry) {
		list.add(entry);
		save();
	}

	public synchronized void remove(String entry) {
		list.remove(entry);
		save();
	}

	public synchronized Set<String> list() {
		return Collections.unmodifiableSet(list);
	}

	protected abstract Type getListType();

	public void load() {
		try {
			if (!configDir.toFile().exists()) {
				configDir.toFile().mkdirs();
			}

			if (file.exists()) {
				try (FileReader reader = new FileReader(file)) {
					WhitelistData data = gson.fromJson(reader, getListType());
					if (data != null && data.entries != null) {
						list.clear();
						list.addAll(data.entries);
					}
				}
			}
		} catch (Exception e) {
			logger.error("[ServerGuard] Failed to load whitelist from file: {}", file.getName(), e);
		}
	}

	protected void save() {
		try {
			WhitelistData data = new WhitelistData();
			data.entries = new HashSet<>(list);

			try (FileWriter writer = new FileWriter(file)) {
				gson.toJson(data, writer);
			}
		} catch (Exception e) {
			logger.error("[ServerGuard] Failed to save whitelist to file: {}", file.getName(), e);
		}
	}

	protected static class WhitelistData {
		Set<String> entries;
	}
}
