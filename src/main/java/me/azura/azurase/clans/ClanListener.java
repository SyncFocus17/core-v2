package me.azura.azurase.clans;

import me.azura.azurase.clans.model.Clan;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Optional;

public final class ClanListener implements Listener {
	private final ClanService service;
	public ClanListener(ClanService service) { this.service = service; }

	@EventHandler
	public void onDamage(EntityDamageByEntityEvent e) {
		if (!(e.getEntity() instanceof Player victim)) return;
		Player damager = null;
		if (e.getDamager() instanceof Player p) {
			damager = p;
		} else if (e.getDamager() instanceof org.bukkit.entity.Projectile proj && proj.getShooter() instanceof Player p2) {
			damager = p2;
		}
		if (damager == null) return;
		Optional<Clan> vc = service.getClanByPlayer(victim.getUniqueId());
		Optional<Clan> dc = service.getClanByPlayer(damager.getUniqueId());
		if (vc.isEmpty() || dc.isEmpty()) return;
		if (vc.get().getId().equals(dc.get().getId()) && !vc.get().isFriendlyFire()) {
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		for (Player p : Bukkit.getOnlinePlayers()) {
			// scoreboard tag updates handled in service
		}
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent e) {
	}
}