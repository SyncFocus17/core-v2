package me.azura.azurase.teleport;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class TeleportService {
	private final Map<UUID, Location> backLocations = new ConcurrentHashMap<>();
	private final Map<UUID, TeleportRequest> incoming = new ConcurrentHashMap<>();
	private final Map<UUID, TeleportRequest> outgoing = new ConcurrentHashMap<>();
	private final Map<UUID, Instant> cooldowns = new ConcurrentHashMap<>();

	public void setBack(Player player, Location location) {
		if (location != null) backLocations.put(player.getUniqueId(), location.clone());
	}

	public Location getBack(Player player) { return backLocations.get(player.getUniqueId()); }

	public void requestTeleport(Player sender, Player target, boolean here, long expirySeconds) {
		TeleportRequest req = new TeleportRequest(sender.getUniqueId(), target.getUniqueId(), here, Instant.now().plusSeconds(expirySeconds));
		incoming.put(target.getUniqueId(), req);
		outgoing.put(sender.getUniqueId(), req);
	}

	public TeleportRequest getIncoming(Player target) { return incoming.get(target.getUniqueId()); }

	public TeleportRequest consumeIncoming(Player target) { return incoming.remove(target.getUniqueId()); }

	public void clearRequest(UUID uuid) {
		TeleportRequest req = outgoing.remove(uuid);
		if (req != null) incoming.remove(req.target());
	}

	public record TeleportRequest(UUID sender, UUID target, boolean here, Instant expiresAt) {}
}