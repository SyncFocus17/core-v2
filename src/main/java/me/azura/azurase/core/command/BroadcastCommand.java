package me.azura.azurase.core.command;

import me.azura.azurase.core.text.Text;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

public final class BroadcastCommand extends BaseCommand {
	@Override protected String permission() { return "azura.broadcast"; }
	@Override protected boolean execute(CommandSender sender, String label, String[] args) {
		if (args.length == 0) { Text.sendPrefixed(sender, "<red>Usage: /broadcast <message>"); return true; }
		String msg = String.join(" ", args);
		Component c = Text.parse("<gold><bold>BROADCAST</bold></gold> " + msg);
		Bukkit.getServer().sendMessage(c);
		return true; }
}