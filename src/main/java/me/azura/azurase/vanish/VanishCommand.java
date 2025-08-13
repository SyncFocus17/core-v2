package me.azura.azurase.vanish;

import me.azura.azurase.core.command.BaseCommand;
import me.azura.azurase.core.text.Text;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class VanishCommand extends BaseCommand {
	private final VanishService service;

	public VanishCommand(VanishService service) {
		this.service = service;
	}

	@Override
	protected String permission() {
		return "azura.staff.vanish";
	}

	@Override
	protected boolean execute(CommandSender sender, String label, String[] args) {
		if (!(sender instanceof Player player)) {
			Text.sendPrefixed(sender, "<red>Only players can use this command.");
			return true;
		}
		boolean newState = !service.isVanished(player);
		service.setVanished(player, newState);
		Text.sendPrefixed(player, newState ? "<gray>You are now <green>vanished</green>." : "<gray>You are now <red>visible</red>.");
		return true;
	}
}