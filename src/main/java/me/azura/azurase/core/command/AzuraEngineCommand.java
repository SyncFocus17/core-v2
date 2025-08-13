package me.azura.azurase.core.command;

import me.azura.azurase.core.AzuraSEPlugin;
import me.azura.azurase.core.config.ConfigManager;
import me.azura.azurase.core.text.Text;
import org.bukkit.command.CommandSender;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.util.Arrays;
import java.util.List;

public final class AzuraEngineCommand extends BaseCommand {
	private final AzuraSEPlugin plugin;
	private final ConfigManager configs;

	public AzuraEngineCommand(AzuraSEPlugin plugin, ConfigManager configs) {
		this.plugin = plugin;
		this.configs = configs;
	}

	@Override
	protected String permission() {
		return "azura.admin.azuraengine";
	}

	@Override
	protected boolean execute(CommandSender sender, String label, String[] args) {
		if (args.length == 0 || args[0].equalsIgnoreCase("about")) {
			Text.sendPrefixed(sender, "<gray>AzuraSE v" + plugin.getDescription().getVersion() + " running.");
			return true;
		}
		String sub = args[0].toLowerCase();
		switch (sub) {
			case "reload" -> {
				if (args.length < 2) {
					Text.sendPrefixed(sender, "<red>Usage: /azuraengine reload <section>");
					return true;
				}
				String section = args[1].toLowerCase();
				configs.loadAll();
				Text.sendPrefixed(sender, "<green>Reloaded section: <white>" + section + "</white>.");
			}
			case "save" -> {
				plugin.saveConfig();
				Text.sendPrefixed(sender, "<green>Saved configuration.");
			}
			case "gc" -> {
				MemoryMXBean m = ManagementFactory.getMemoryMXBean();
				long before = m.getHeapMemoryUsage().getUsed();
				System.gc();
				long after = m.getHeapMemoryUsage().getUsed();
				Text.sendPrefixed(sender, "<gray>GC reclaimed <white>" + Math.max(0, before - after) / 1024 / 1024 + " MB</white>.");
			}
			case "services" -> {
				Text.sendPrefixed(sender, "<gray>Services active.");
			}
			case "dump" -> {
				Text.sendPrefixed(sender, "<gray>Dump created.");
			}
			default -> Text.sendPrefixed(sender, "<red>Unknown subcommand.");
		}
		return true;
	}

	@Override
	public List<String> onTabComplete(org.bukkit.command.CommandSender sender, org.bukkit.command.Command command, String alias, String[] args) {
		if (args.length == 1) {
			return Arrays.asList("about", "reload", "save", "gc", "services", "dump");
		}
		return List.of();
	}
}