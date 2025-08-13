package me.azura.azurase.ignore;

import me.azura.azurase.core.command.BaseCommand;
import me.azura.azurase.core.text.Text;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class IgnoreCommand extends BaseCommand {
	private final IgnoreService service;
	public IgnoreCommand(IgnoreService service) { this.service = service; }
	@Override protected String permission() { return "azura.ignore.use"; }
	@Override protected boolean execute(CommandSender sender, String label, String[] args) {
		if (!(sender instanceof Player player)) { Text.sendPrefixed(sender, "<red>Only players can use this command."); return true; }
		if (args.length == 0) { Text.sendPrefixed(player, "<red>Usage: /ignore <player|list|remove>"); return true; }
		String sub = args[0].toLowerCase();
		if (sub.equals("list")) {
			var set = service.getIgnored(player.getUniqueId());
			List<String> names = new ArrayList<>();
			for (UUID u : set) { OfflinePlayer op = Bukkit.getOfflinePlayer(u); names.add(op.getName() == null ? u.toString() : op.getName()); }
			Text.sendPrefixed(player, "<gray>Ignored: <white>" + String.join(", ", names) + "</white>");
			return true;
		}
		if (sub.equals("remove")) {
			if (args.length < 2) { Text.sendPrefixed(player, "<red>Usage: /ignore remove <player>"); return true; }
			Player target = Bukkit.getPlayerExact(args[1]);
			UUID tid = target != null ? target.getUniqueId() : null;
			if (tid == null) { Text.sendPrefixed(player, "<red>Player not found."); return true; }
			service.removeIgnore(player.getUniqueId(), tid);
			Text.sendPrefixed(player, "<green>You stopped ignoring <white>" + target.getName() + "</white>.");
			return true;
		}
		Player target = Bukkit.getPlayerExact(args[0]);
		if (target == null) { Text.sendPrefixed(player, "<red>Player not found."); return true; }
		service.addIgnore(player.getUniqueId(), target.getUniqueId());
		Text.sendPrefixed(player, "<yellow>You are now ignoring <white>" + target.getName() + "</white>.");
		return true; }

	@Override public List<String> onTabComplete(CommandSender sender, org.bukkit.command.Command cmd, String alias, String[] args) {
		if (args.length == 1) { return List.of("list", "remove"); }
		if (args.length == 2 && args[0].equalsIgnoreCase("remove") && sender instanceof Player player) {
			List<String> names = new ArrayList<>();
			for (UUID u : service.getIgnored(player.getUniqueId())) { var op = Bukkit.getOfflinePlayer(u); if (op.getName()!=null) names.add(op.getName()); }
			return names;
		}
		return List.of(); }
}