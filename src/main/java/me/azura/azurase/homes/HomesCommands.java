package me.azura.azurase.homes;

import me.azura.azurase.core.command.BaseCommand;
import me.azura.azurase.core.text.Text;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class HomesCommands {
	public static final class Home extends BaseCommand {
		private final HomesService service; public Home(HomesService s) { this.service = s; }
		@Override protected String permission() { return "azura.homes.home"; }
		@Override protected boolean execute(CommandSender sender, String label, String[] args) {
			if (!(sender instanceof Player player)) { Text.sendPrefixed(sender, "<red>Only players can use this command."); return true; }
			String name = args.length == 0 ? "home" : args[0].toLowerCase();
			Map<String, org.bukkit.Location> homes = service.getHomes(player.getUniqueId());
			org.bukkit.Location l = homes.get(name);
			if (l == null) { Text.sendPrefixed(player, "<red>Home not found."); return true; }
			player.teleport(l); Text.sendPrefixed(player, "<gray>Teleported to home <white>"+name+"</white>."); return true; }
		@Override public List<String> onTabComplete(CommandSender s, org.bukkit.command.Command c, String a, String[] args) {
			if (s instanceof Player p && args.length == 1) return new ArrayList<>(service.getHomes(p.getUniqueId()).keySet());
			return List.of(); }
	}
	public static final class Sethome extends BaseCommand {
		private final HomesService service; public Sethome(HomesService s) { this.service = s; }
		@Override protected String permission() { return "azura.homes.sethome"; }
		@Override protected boolean execute(CommandSender sender, String label, String[] args) {
			if (!(sender instanceof Player player)) { Text.sendPrefixed(sender, "<red>Only players can use this command."); return true; }
			String name = args.length == 0 ? "home" : args[0].toLowerCase();
			var homes = service.getHomes(player.getUniqueId());
			int limit = service.getLimit(player);
			if (!homes.containsKey(name) && homes.size() >= limit) { Text.sendPrefixed(player, "<red>You reached your home limit (<white>"+limit+"</white>)."); return true; }
			service.setHome(player.getUniqueId(), name, player.getLocation());
			Text.sendPrefixed(player, "<green>Home <white>"+name+"</white> set.");
			return true; }
	}
	public static final class Delhome extends BaseCommand {
		private final HomesService service; public Delhome(HomesService s) { this.service = s; }
		@Override protected String permission() { return "azura.homes.delhome"; }
		@Override protected boolean execute(CommandSender sender, String label, String[] args) {
			if (!(sender instanceof Player player)) { Text.sendPrefixed(sender, "<red>Only players can use this command."); return true; }
			if (args.length < 1) { Text.sendPrefixed(player, "<red>Usage: /delhome <name>"); return true; }
			service.delHome(player.getUniqueId(), args[0].toLowerCase());
			Text.sendPrefixed(player, "<yellow>Home <white>"+args[0].toLowerCase()+"</white> deleted.");
			return true; }
	}
}