package org.axolotlj.serverguard.list.blacklist;

import java.io.File;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.axolotlj.serverguard.config.ServerGuardConfig;
import org.axolotlj.serverguard.list.AbstractListManager;

public class NameBlacklistManager extends AbstractListManager {

	private static final NameBlacklistManager INSTANCE = new NameBlacklistManager();

	private NameBlacklistManager() {
		super("name_blacklist.json");
	}

	public static NameBlacklistManager getInstance() {
		return INSTANCE;
	}

	/**
	 * Verifica si el nombre contiene alguna cadena baneada (case insensitive).
	 */
	public synchronized boolean isBlacklisted(String name) {
		if (name == null)
			return false;
		String lowerName = name.toLowerCase(Locale.ROOT);
		return list.stream().map(s -> s.toLowerCase(Locale.ROOT)).anyMatch(lowerName::contains);
	}

	@Override
	public synchronized boolean islisted(String name) {
		if (!ServerGuardConfig.INSTANCE.nameBlacklistEnabled.get()) {
			return false;
		}
		return list.contains(name.toLowerCase(Locale.ROOT));
	}

	@Override
	protected void initializeDefaultsIfNeeded(File file) {
		if (!file.exists() || list().isEmpty()) {
			List<String> defaults = Arrays.asList("serveroverflow", "serverseeker", "cornbread", "cuute", "bunger",
					"matscan", "servercheck", "intersect", "bbcrawler", "threescan", "shepan", "pfcloud", "pfclown",
					"thisisarobbery", "notschesser", "fifthcolumnmc", "dscrdggfabricmc", "joinourdiscord");
			defaults.forEach(this::add);
		}
	}

	@Override
	protected Type getListType() {
		return WhitelistData.class;
	}
}
