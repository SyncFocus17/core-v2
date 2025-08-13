package me.azura.azurase.teleport.commands;

import me.azura.azurase.core.command.BaseCommand;
import me.azura.azurase.core.text.Text;
import me.azura.azurase.teleport.TeleportService;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class SimpleTeleportCommands {
	public static final class Tp extends BaseCommand {
		private final TeleportService service;
		public Tp(TeleportService service) { this.service = service; }
		@Override protected String permission() { return "azura.teleport.tp"; }
		@Override protected boolean execute(CommandSender sender, String label, String[] args) {
			if (!(sender instanceof Player player)) { Text.sendPrefixed(sender, "<red>Only players can use this command."); return true; }
			if (args.length < 1) { Text.sendPrefixed(player, "<red>Usage: /tp <player>"); return true; }
			Player target = Bukkit.getPlayerExact(args[0]);
			if (target == null) { Text.sendPrefixed(player, "<red>Player not found."); return true; }
			service.setBack(player, player.getLocation());
			player.teleport(target.getLocation());
			return true; }
	}
	public static final class Tphere extends BaseCommand {
		private final TeleportService service; public Tphere(TeleportService s) { this.service = s; }
		@Override protected String permission() { return "azura.teleport.tphere"; }
		@Override protected boolean execute(CommandSender sender, String label, String[] args) {
			if (!(sender instanceof Player player)) { Text.sendPrefixed(sender, "<red>Only players can use this command."); return true; }
			if (args.length < 1) { Text.sendPrefixed(player, "<red>Usage: /tphere <player>"); return true; }
			Player target = Bukkit.getPlayerExact(args[0]);
			if (target == null) { Text.sendPrefixed(player, "<red>Player not found."); return true; }
			service.setBack(target, target.getLocation());
			target.teleport(player.getLocation());
			return true; }
	}
	public static final class Back extends BaseCommand {
		private final TeleportService service; public Back(TeleportService s) { this.service = s; }
		@Override protected String permission() { return "azura.teleport.back"; }
		@Override protected boolean execute(CommandSender sender, String label, String[] args) {
			if (!(sender instanceof Player player)) { Text.sendPrefixed(sender, "<red>Only players can use this command."); return true; }
			var loc = service.getBack(player);
			if (loc == null) { Text.sendPrefixed(player, "<red>No previous location recorded."); return true; }
			player.teleport(loc);
			Text.sendPrefixed(player, "<gray>Teleported back to your last location.");
			return true; }
	}
}