package me.azura.azurase.homes;

import me.azura.azurase.core.AzuraSEPlugin;
import me.azura.azurase.core.util.LocationUtil;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class HomesService {
	private final File file;
	private final YamlConfiguration yaml;
	private final int defaultLimit;
	private final String permPrefix;

	public HomesService(AzuraSEPlugin plugin) {
		this.file = new File(plugin.getDataFolder(), "homes.yml");
		this.yaml = YamlConfiguration.loadConfiguration(file);
		this.defaultLimit = plugin.getConfig().getInt("homes.default-limit", 1);
		this.permPrefix = plugin.getConfig().getString("homes.permission-prefix", "azura.homes.limit.");
	}

	public int getLimit(org.bukkit.entity.Player player) {
		int limit = defaultLimit;
		for (int i = 64; i >= 1; i--) {
			if (player.hasPermission(permPrefix + i)) { limit = i; break; }
		}
		return limit;
	}

	public Map<String, Location> getHomes(UUID uuid) {
		Map<String, Location> map = new HashMap<>();
		var sec = yaml.getConfigurationSection("players." + uuid);
		if (sec == null) return map;
		for (String name : sec.getKeys(false)) {
			String s = sec.getString(name);
			Location l = s == null ? null : LocationUtil.deserialize(s);
			if (l != null) map.put(name, l);
		}
		return map;
	}

	public void setHome(UUID uuid, String name, Location loc) {
		yaml.set("players." + uuid + "." + name, LocationUtil.serialize(loc));
		save();
	}

	public void delHome(UUID uuid, String name) {
		yaml.set("players." + uuid + "." + name, null);
		save();
	}

	private void save() {
		try { yaml.save(file); } catch (IOException ignored) {}
	}
}