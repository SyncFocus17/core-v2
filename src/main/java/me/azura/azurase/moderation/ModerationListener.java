package me.azura.azurase.moderation;

import me.azura.azurase.core.text.Text;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public final class ModerationListener implements Listener {
	private final ModerationService service;
	public ModerationListener(ModerationService service) { this.service = service; }

	@EventHandler
	public void onChat(AsyncPlayerChatEvent e) {
		var mute = service.getMute(e.getPlayer().getUniqueId());
		if (mute.isPresent()) {
			e.setCancelled(true);
			Text.sendPrefixed(e.getPlayer(), "<red>You are muted." );
			return;
		}
		if (service.isStaffChatEnabled(e.getPlayer().getUniqueId())) {
			e.setCancelled(true);
			Bukkit.getOnlinePlayers().stream().filter(p -> p.hasPermission("azura.staff.chat")).forEach(p -> Text.send(p, "<gold>[Staff] </gold>"+e.getPlayer().getName()+": "+e.getMessage()));
		}
	}
}