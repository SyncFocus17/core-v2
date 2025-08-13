package me.azura.azurase.spawn;

import me.azura.azurase.core.command.BaseCommand;
import me.azura.azurase.core.text.Text;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class SpawnCommands {
	public static final class Spawn extends BaseCommand {
		private final SpawnService service; public Spawn(SpawnService s) { this.service = s; }
		@Override protected String permission() { return "azura.spawn.use"; }
		@Override protected boolean execute(CommandSender sender, String label, String[] args) {
			if (!(sender instanceof Player player)) { Text.sendPrefixed(sender, "<red>Only players can use this command."); return true; }
			player.teleport(service.getSpawn());
			Text.sendPrefixed(player, "<gray>Teleported to spawn.");
			return true; }
	}
	public static final class SetSpawn extends BaseCommand {
		private final SpawnService service; public SetSpawn(SpawnService s) { this.service = s; }
		@Override protected String permission() { return "azura.spawn.set"; }
		@Override protected boolean execute(CommandSender sender, String label, String[] args) {
			if (!(sender instanceof Player player)) { Text.sendPrefixed(sender, "<red>Only players can use this command."); return true; }
			service.setSpawn(player.getLocation());
			Text.sendPrefixed(player, "<green>Spawn set.");
			return true; }
	}
}