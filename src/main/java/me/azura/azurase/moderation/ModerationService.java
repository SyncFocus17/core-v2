package me.azura.azurase.moderation;

import me.azura.azurase.core.AzuraSEPlugin;
import me.azura.azurase.core.util.TimeUtil;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public final class ModerationService {
	private final AzuraSEPlugin plugin;
	private final File file;
	private final YamlConfiguration yaml;
	private final Map<UUID, MuteEntry> mutes = new ConcurrentHashMap<>();
	private final Set<UUID> staffChatEnabled = Collections.newSetFromMap(new ConcurrentHashMap<>());

	public ModerationService(AzuraSEPlugin plugin) {
		this.plugin = plugin;
		this.file = new File(plugin.getDataFolder(), "moderation.yml");
		this.yaml = YamlConfiguration.loadConfiguration(file);
		load();
	}

	private void load() {
		var sec = yaml.getConfigurationSection("mutes");
		if (sec != null) {
			for (String key : sec.getKeys(false)) {
				UUID u = UUID.fromString(key);
				String reason = yaml.getString("mutes."+key+".reason", "Muted");
				long until = yaml.getLong("mutes."+key+".until", 0L);
				mutes.put(u, new MuteEntry(u, reason, until == 0 ? null : Instant.ofEpochMilli(until)));
			}
		}
	}

	private void save() {
		for (MuteEntry me : mutes.values()) {
			String key = me.player().toString();
			yaml.set("mutes."+key+".reason", me.reason());
			yaml.set("mutes."+key+".until", me.until() == null ? 0L : me.until().toEpochMilli());
		}
		try { yaml.save(file); } catch (IOException ignored) {}
	}

	public void mute(UUID player, String reason, Instant until) {
		mutes.put(player, new MuteEntry(player, reason, until));
		save();
	}

	public void unmute(UUID player) {
		mutes.remove(player);
		yaml.set("mutes."+player, null);
		save();
	}

	public Optional<MuteEntry> getMute(UUID player) {
		MuteEntry me = mutes.get(player);
		if (me == null) return Optional.empty();
		if (me.until() != null && me.until().isBefore(Instant.now())) {
			unmute(player);
			return Optional.empty();
		}
		return Optional.of(me);
	}

	public void toggleStaffChat(UUID player) {
		if (staffChatEnabled.contains(player)) staffChatEnabled.remove(player); else staffChatEnabled.add(player);
	}

	public boolean isStaffChatEnabled(UUID player) { return staffChatEnabled.contains(player); }

	public record MuteEntry(UUID player, String reason, Instant until) {}
}