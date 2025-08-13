package me.azura.azurase.core;

import org.bukkit.Bukkit;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.ServicesManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public final class AzuraSEPlugin extends JavaPlugin {
	private Logger logger;
	private ServiceContainer services;

	@Override
	public void onLoad() {
		this.logger = getLogger();
		this.services = new ServiceContainer(this);
	}

	@Override
	public void onEnable() {
		long startNanos = System.nanoTime();
		this.logger.info("Starting AzuraSE v" + getDescription().getVersion() + " on " + Bukkit.getVersion());

		// Load configs
		saveDefaultConfig();
		saveResourceIfNotExists("messages.yml");
		saveResourceIfNotExists("clans.yml");
		saveResourceIfNotExists("homes.yml");
		saveResourceIfNotExists("spawn.yml");
		saveResourceIfNotExists("combat.yml");
		saveResourceIfNotExists("ignore.yml");
		saveResourceIfNotExists("wireless.yml");
		saveResourceIfNotExists("commandblock.yml");

		// Initialize services and register API facades
		this.services.initialize();
		registerApis();

		// Register commands/listeners
		this.services.registerCommandsAndListeners();

		long tookMs = (System.nanoTime() - startNanos) / 1_000_000L;
		this.logger.info("AzuraSE enabled in " + tookMs + " ms.");
	}

	@Override
	public void onDisable() {
		long startNanos = System.nanoTime();
		try {
			if (services != null) {
				services.shutdown();
			}
		} finally {
			long tookMs = (System.nanoTime() - startNanos) / 1_000_000L;
			getLogger().info("AzuraSE disabled in " + tookMs + " ms.");
		}
	}

	private void registerApis() {
		ServicesManager sm = getServer().getServicesManager();
		// Example: sm.register(SomeApi.class, services.getSomeApi(), this, ServicePriority.Normal);
	}

	private void saveResourceIfNotExists(String path) {
		if (getResource(path) == null) {
			return;
		}
		if (!new java.io.File(getDataFolder(), path).exists()) {
			saveResource(path, false);
		}
	}
}