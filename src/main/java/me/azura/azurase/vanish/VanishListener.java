package me.azura.azurase.vanish;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public final class VanishListener implements Listener {
	private final VanishService service;

	public VanishListener(VanishService service) {
		this.service = service;
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		Player joined = event.getPlayer();
		service.applyVisibility(joined);
		for (Player p : Bukkit.getOnlinePlayers()) {
			if (service.isVanished(p) && !joined.hasPermission("azura.staff.vanish.see")) {
				joined.hidePlayer(Bukkit.getPluginManager().getPlugin("AzuraSE"), p);
			}
		}
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		// no-op
	}
}