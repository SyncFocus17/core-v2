package me.azura.azurase.devtools;

import me.azura.azurase.core.AzuraSEPlugin;
import org.bukkit.Bukkit;

import java.lang.management.ManagementFactory;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public final class DevToolsService {
	private final AzuraSEPlugin plugin;
	private final Map<String, Boolean> debugFlags = new ConcurrentHashMap<>();
	private final Map<String, Boolean> featureFlags = new ConcurrentHashMap<>();
	private final Set<UUID> spyChat = Collections.newSetFromMap(new ConcurrentHashMap<>());
	private final Set<UUID> spyCommands = Collections.newSetFromMap(new ConcurrentHashMap<>());
	private final Set<UUID> spyPackets = Collections.newSetFromMap(new ConcurrentHashMap<>());
	private final Map<UUID, UUID> cmdWatch = new ConcurrentHashMap<>();
	private final List<TpsAlert> alerts = Collections.synchronizedList(new ArrayList<>());
	private int alertTaskId = -1;

	public DevToolsService(AzuraSEPlugin plugin) {
		this.plugin = plugin;
	}

	public void start() {
		if (alertTaskId == -1) {
			alertTaskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, this::checkAlerts, 100L, 100L);
		}
	}

	public void stop() {
		if (alertTaskId != -1) { Bukkit.getScheduler().cancelTask(alertTaskId); alertTaskId = -1; }
	}

	private void checkAlerts() {
		double tps = 20.0;
		try {
			double[] tpsArr = Bukkit.getServer().getTPS();
			if (tpsArr != null && tpsArr.length > 0) tps = tpsArr[0];
		} catch (Throwable ignored) {}
		final double ftps = tps;
		alerts.removeIf(alert -> {
			if (ftps < alert.threshold()) {
				var p = Bukkit.getPlayer(alert.owner());
				if (p != null) p.sendMessage("[Alert] TPS=" + String.format(Locale.US, "%.2f", ftps) + " < " + alert.threshold());
			}
			return false;
		});
	}

	public void setDebug(String key, boolean value) { debugFlags.put(key, value); }
	public boolean isDebug(String key) { return debugFlags.getOrDefault(key, false); }
	public void setFeature(String key, boolean value) { featureFlags.put(key, value); }
	public boolean isFeature(String key) { return featureFlags.getOrDefault(key, false); }

	public void toggleSpyChat(UUID u) { toggle(spyChat, u); }
	public void toggleSpyCommands(UUID u) { toggle(spyCommands, u); }
	public void toggleSpyPackets(UUID u) { toggle(spyPackets, u); }
	private void toggle(Set<UUID> set, UUID u) { if (!set.add(u)) set.remove(u); }
	public boolean isSpyChat(UUID u) { return spyChat.contains(u); }
	public boolean isSpyCommands(UUID u) { return spyCommands.contains(u); }
	public boolean isSpyPackets(UUID u) { return spyPackets.contains(u); }

	public void setCmdWatch(UUID watcher, UUID target) { if (target == null) cmdWatch.remove(watcher); else cmdWatch.put(watcher, target); }
	public Map<UUID, UUID> getCmdWatch() { return cmdWatch; }

	public void addAlert(UUID owner, double threshold) { alerts.add(new TpsAlert(owner, threshold)); }

	public record TpsAlert(UUID owner, double threshold) {}
}