package me.azura.azurase.ignore;

import me.azura.azurase.core.AzuraSEPlugin;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public final class IgnoreService {
	private final File file;
	private final YamlConfiguration yaml;

	public IgnoreService(AzuraSEPlugin plugin) {
		this.file = new File(plugin.getDataFolder(), "ignore.yml");
		this.yaml = YamlConfiguration.loadConfiguration(file);
		if (!yaml.isConfigurationSection("players")) yaml.createSection("players");
	}

	public Set<UUID> getIgnored(UUID owner) {
		var list = yaml.getStringList("players." + owner + ".ignored");
		Set<UUID> set = new HashSet<>();
		for (String s : list) { try { set.add(UUID.fromString(s)); } catch (Exception ignored) {} }
		return set;
	}

	public void setIgnored(UUID owner, Set<UUID> ignored) {
		var list = ignored.stream().map(UUID::toString).toList();
		yaml.set("players." + owner + ".ignored", list);
		save();
	}

	public void addIgnore(UUID owner, UUID target) {
		Set<UUID> cur = getIgnored(owner);
		cur.add(target);
		setIgnored(owner, cur);
	}

	public void removeIgnore(UUID owner, UUID target) {
		Set<UUID> cur = getIgnored(owner);
		cur.remove(target);
		setIgnored(owner, cur);
	}

	private void save() {
		try { yaml.save(file); } catch (IOException ignored) {}
	}
}