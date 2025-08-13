package me.azura.azurase.core.listener;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.TabCompleteEvent;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

public final class CommandBlockListener implements Listener {
	private final Set<String> blocked = new HashSet<>();
	private final boolean hide;

	public CommandBlockListener(org.bukkit.plugin.Plugin plugin) {
		File file = new File(plugin.getDataFolder(), "commandblock.yml");
		YamlConfiguration y = YamlConfiguration.loadConfiguration(file);
		this.hide = y.getBoolean("hide-commands-without-permission", true);
		this.blocked.addAll(y.getStringList("blocked"));
	}

	@EventHandler
	public void onTab(TabCompleteEvent event) {
		if (!hide) return;
		if (!(event.getSender() instanceof org.bukkit.entity.Player player)) return;
		if (event.getBuffer().startsWith("/")) {
			String cmd = event.getBuffer().substring(1).toLowerCase();
			event.getCompletions().removeIf(s -> {
				String base = s.toLowerCase();
				return blocked.contains(base) && !player.hasPermission("azura.command." + base);
			});
		}
	}
}