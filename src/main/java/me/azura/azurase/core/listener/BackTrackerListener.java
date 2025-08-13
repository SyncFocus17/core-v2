package me.azura.azurase.core.listener;

import me.azura.azurase.teleport.TeleportService;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public final class BackTrackerListener implements Listener {
	private final TeleportService service;
	public BackTrackerListener(TeleportService service) { this.service = service; }
	@EventHandler public void onTeleport(PlayerTeleportEvent e) { service.setBack(e.getPlayer(), e.getFrom()); }
	@EventHandler public void onDeath(PlayerDeathEvent e) { service.setBack(e.getEntity(), e.getEntity().getLocation()); }
}