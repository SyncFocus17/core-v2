package me.azura.azurase.core.listener;

import me.azura.azurase.devtools.DevToolsService;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.Map;

public final class CommandSpyListener implements Listener {
	private final DevToolsService svc;
	public CommandSpyListener(DevToolsService svc) { this.svc = svc; }

	@EventHandler
	public void onCommand(PlayerCommandPreprocessEvent e) {
		for (var watcher : svc.getCmdWatch().entrySet()) {
			if (watcher.getValue().equals(e.getPlayer().getUniqueId())) {
				var p = Bukkit.getPlayer(watcher.getKey()); if (p!=null) p.sendMessage("[CmdWatch] "+e.getPlayer().getName()+": "+e.getMessage());
			}
		}
		Bukkit.getOnlinePlayers().stream().filter(p -> svc.isSpyCommands(p.getUniqueId())).forEach(p -> p.sendMessage("[Spy] "+e.getPlayer().getName()+": "+e.getMessage()));
	}
}