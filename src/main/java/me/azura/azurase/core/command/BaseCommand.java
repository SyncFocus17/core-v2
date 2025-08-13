package me.azura.azurase.core.command;

import me.azura.azurase.core.text.Text;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.Collections;
import java.util.List;

public abstract class BaseCommand implements CommandExecutor, TabCompleter {
	protected abstract String permission();

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		String perm = permission();
		if (perm != null && !perm.isEmpty() && !sender.hasPermission(perm)) {
			Text.sendPrefixed(sender, "<red>You do not have permission to do that.");
			return true;
		}
		return execute(sender, label, args);
	}

	protected abstract boolean execute(CommandSender sender, String label, String[] args);

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		return Collections.emptyList();
	}
}