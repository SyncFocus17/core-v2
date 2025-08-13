package me.azura.azurase.vanish;

import org.bukkit.Bukkit;
import org.bukkit.Scoreboard;
import org.bukkit.ScoreboardManager;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.configuration.file.YamlConfiguration;

public final class VanishService {
	private final Plugin plugin;
	private final Set<UUID> vanished = Collections.newSetFromMap(new ConcurrentHashMap<>());
	private final File storeFile;
	private YamlConfiguration yaml;
	private static final String TEAM = "azurase_vanish";

	public VanishService(Plugin plugin) {
		this.plugin = plugin;
		this.storeFile = new File(plugin.getDataFolder(), "vanish.yml");
		this.yaml = YamlConfiguration.loadConfiguration(storeFile);
		for (String s : yaml.getStringList("vanished")) {
			try { vanished.add(UUID.fromString(s)); } catch (Exception ignored) {}
		}
	}

	public void setVanished(Player player, boolean vanish) {
		if (vanish) {
			vanished.add(player.getUniqueId());
		} else {
			vanished.remove(player.getUniqueId());
		}
		save();
		applyVisibility(player);
	}

	public boolean isVanished(Player player) {
		return vanished.contains(player.getUniqueId());
	}

	public void applyVisibility(Player player) {
		boolean v = isVanished(player);
		for (Player other : Bukkit.getOnlinePlayers()) {
			if (other.equals(player)) continue;
			if (v) {
				if (!other.hasPermission("azura.staff.vanish.see")) {
					other.hidePlayer(plugin, player);
				}
				player.setCollidable(false);
			} else {
				other.showPlayer(plugin, player);
				player.setCollidable(true);
			}
		}
		player.setInvisible(v);
		updateTeam(player, v);
	}

	private void updateTeam(Player player, boolean vanished) {
		ScoreboardManager sm = Bukkit.getScoreboardManager();
		if (sm == null) return;
		Scoreboard sb = sm.getMainScoreboard();
		org.bukkit.scoreboard.Team team = sb.getTeam(TEAM);
		if (team == null) {
			team = sb.registerNewTeam(TEAM);
			team.setCanSeeFriendlyInvisibles(false);
			team.setOption(org.bukkit.scoreboard.Team.Option.NAME_TAG_VISIBILITY, org.bukkit.scoreboard.Team.OptionStatus.NEVER);
		}
		if (vanished) {
			team.addEntry(player.getName());
		} else {
			team.removeEntry(player.getName());
		}
	}

	private void save() {
		yaml.set("vanished", vanished.stream().map(UUID::toString).toList());
		try { yaml.save(storeFile); } catch (IOException ignored) {}
	}

	public Set<UUID> getVanished() {
		return vanished;
	}
}