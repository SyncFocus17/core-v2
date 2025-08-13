package me.azura.azurase.core.command;

import me.azura.azurase.core.text.Text;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class PingCommand extends BaseCommand {
	@Override protected String permission() { return "azura.ping"; }
	@Override protected boolean execute(CommandSender sender, String label, String[] args) {
		Player target;
		if (args.length >= 1) {
			target = Bukkit.getPlayerExact(args[0]);
			if (target == null) { Text.sendPrefixed(sender, "<red>Player not found."); return true; }
		} else {
			if (!(sender instanceof Player p)) { Text.sendPrefixed(sender, "<red>Usage: /ping [player]"); return true; }
			target = p;
		}
		int ping = target.getPing();
		Text.sendPrefixed(sender, "<gray>Ping for <white>" + target.getName() + "</white>: <white>" + ping + "ms</white>.");
		return true; }
}