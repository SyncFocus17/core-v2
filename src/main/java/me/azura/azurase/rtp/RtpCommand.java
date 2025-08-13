package me.azura.azurase.rtp;

import me.azura.azurase.core.command.BaseCommand;
import me.azura.azurase.core.text.Text;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class RtpCommand extends BaseCommand {
	private final RtpService service; public RtpCommand(RtpService s) { this.service = s; }
	@Override protected String permission() { return "azura.teleport.rtp"; }
	@Override protected boolean execute(CommandSender sender, String label, String[] args) {
		if (!(sender instanceof Player p)) { Text.sendPrefixed(sender, "<red>Only players can use this."); return true; }
		service.rtp(p); return true; }
}