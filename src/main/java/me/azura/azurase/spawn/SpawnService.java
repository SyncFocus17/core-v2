package me.azura.azurase.spawn;

import me.azura.azurase.core.AzuraSEPlugin;
import me.azura.azurase.core.util.LocationUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public final class SpawnService {
	private final AzuraSEPlugin plugin;
	private Location spawn;
	private final File file;
	private final YamlConfiguration yaml;

	public SpawnService(AzuraSEPlugin plugin) {
		this.plugin = plugin;
		this.file = new File(plugin.getDataFolder(), "spawn.yml");
		this.yaml = YamlConfiguration.loadConfiguration(file);
		String s = yaml.getString("spawn");
		if (s != null) this.spawn = LocationUtil.deserialize(s);
	}

	public Location getSpawn() { return spawn != null ? spawn.clone() : Bukkit.getWorlds().get(0).getSpawnLocation(); }

	public void setSpawn(Location loc) {
		this.spawn = loc.clone();
		yaml.set("spawn", LocationUtil.serialize(loc));
		try { yaml.save(file); } catch (IOException ignored) {}
	}
}