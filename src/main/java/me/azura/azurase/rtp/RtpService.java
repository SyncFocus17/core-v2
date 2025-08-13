package me.azura.azurase.rtp;

import me.azura.azurase.core.AzuraSEPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.Random;
import java.util.concurrent.CompletableFuture;

public final class RtpService {
	private final AzuraSEPlugin plugin;
	private final Random random = new Random();

	public RtpService(AzuraSEPlugin plugin) { this.plugin = plugin; }

	public CompletableFuture<Location> findSafeLocation(World world, int min, int max, int attempts) {
		CompletableFuture<Location> future = new CompletableFuture<>();
		Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
			for (int i=0; i<attempts; i++) {
				int r = min + random.nextInt(Math.max(1, max - min + 1));
				double angle = random.nextDouble() * Math.PI * 2;
				int x = (int) Math.round(Math.cos(angle) * r);
				int z = (int) Math.round(Math.sin(angle) * r);
				CompletableFuture<Location> locFuture = loadAndCheck(world, x, z);
				Location loc = locFuture.join();
				if (loc != null) { future.complete(loc); return; }
			}
			future.complete(null);
		});
		return future;
	}

	private CompletableFuture<Location> loadAndCheck(World world, int x, int z) {
		CompletableFuture<Location> fut = new CompletableFuture<>();
		Bukkit.getScheduler().runTask(plugin, () -> world.getChunkAtAsync(x >> 4, z >> 4, true).thenAccept(chunk -> {
			int y = world.getHighestBlockYAt(x, z);
			Location l = new Location(world, x + 0.5, y + 1, z + 0.5);
			Material below = world.getBlockAt(x, y, z).getType();
			if (below.isSolid() && below != Material.WATER && below != Material.LAVA) {
				fut.complete(l);
			} else {
				fut.complete(null);
			}
		}));
		return fut;
	}

	public void rtp(Player player) {
		World world = Bukkit.getWorld(plugin.getConfig().getString("rtp.world", "world"));
		if (world == null || world.getEnvironment() != World.Environment.NORMAL) { player.sendMessage("RTP unavailable."); return; }
		int min = plugin.getConfig().getInt("rtp.radius.min", 1000);
		int max = plugin.getConfig().getInt("rtp.radius.max", 5000);
		int attempts = plugin.getConfig().getInt("rtp.attempts", 20);
		player.sendMessage("Searching for a safe location...");
		findSafeLocation(world, min, max, attempts).thenAccept(loc -> {
			if (loc == null) { player.sendMessage("Failed to find safe location."); return; }
			Bukkit.getScheduler().runTask(plugin, () -> player.teleport(loc));
		});
	}
}