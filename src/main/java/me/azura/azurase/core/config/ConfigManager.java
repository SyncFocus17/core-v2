package me.azura.azurase.core.config;

import me.azura.azurase.core.AzuraSEPlugin;
import me.azura.azurase.core.text.Text;
import net.kyori.adventure.text.Component;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public final class ConfigManager {
	private final AzuraSEPlugin plugin;
	private FileConfiguration messages;
	private File clansFile;
	private File homesFile;
	private File spawnFile;
	private File combatFile;
	private File ignoreFile;
	private File wirelessFile;
	private File commandBlockFile;

	public ConfigManager(AzuraSEPlugin plugin) {
		this.plugin = plugin;
	}

	public void loadAll() {
		plugin.reloadConfig();
		this.messages = load("messages.yml");
		this.clansFile = new File(plugin.getDataFolder(), "clans.yml");
		this.homesFile = new File(plugin.getDataFolder(), "homes.yml");
		this.spawnFile = new File(plugin.getDataFolder(), "spawn.yml");
		this.combatFile = new File(plugin.getDataFolder(), "combat.yml");
		this.ignoreFile = new File(plugin.getDataFolder(), "ignore.yml");
		this.wirelessFile = new File(plugin.getDataFolder(), "wireless.yml");
		this.commandBlockFile = new File(plugin.getDataFolder(), "commandblock.yml");
		String prefix = messages.getString("prefix", "<gray>[Azura]</gray> ");
		Text.setPrefix(Text.parse(prefix));
	}

	private FileConfiguration load(String name) {
		File file = new File(plugin.getDataFolder(), name);
		return YamlConfiguration.loadConfiguration(file);
	}

	public FileConfiguration messages() { return messages; }
	public File clansFile() { return clansFile; }
	public File homesFile() { return homesFile; }
	public File spawnFile() { return spawnFile; }
	public File combatFile() { return combatFile; }
	public File ignoreFile() { return ignoreFile; }
	public File wirelessFile() { return wirelessFile; }
	public File commandBlockFile() { return commandBlockFile; }

	public void saveYaml(File file, YamlConfiguration yaml) {
		try {
			yaml.save(file);
		} catch (IOException e) {
			plugin.getLogger().warning("Failed to save YAML: " + file.getName() + ": " + e.getMessage());
		}
	}
}