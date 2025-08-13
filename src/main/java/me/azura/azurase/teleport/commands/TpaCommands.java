package me.azura.azurase.teleport.commands;

import me.azura.azurase.core.command.BaseCommand;
import me.azura.azurase.core.text.Text;
import me.azura.azurase.teleport.TeleportService;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.time.Instant;

public final class TpaCommands {
	public static final class Tpa extends BaseCommand {
		private final TeleportService service;
		private final long expirySeconds;
		public Tpa(TeleportService service, long expirySeconds) { this.service = service; this.expirySeconds = expirySeconds; }
		@Override protected String permission() { return "azura.teleport.tpa"; }
		@Override protected boolean execute(CommandSender sender, String label, String[] args) {
			if (!(sender instanceof Player player)) { Text.sendPrefixed(sender, "<red>Only players can use this command."); return true; }
			if (args.length < 1) { Text.sendPrefixed(player, "<red>Usage: /tpa <player>"); return true; }
			Player target = Bukkit.getPlayerExact(args[0]);
			if (target == null) { Text.sendPrefixed(player, "<red>Player not found."); return true; }
			service.requestTeleport(player, target, false, expirySeconds);
			Text.sendPrefixed(player, "<green>TPA request sent to <white>" + target.getName() + "</white>.");
			Text.sendPrefixed(target, "<yellow>" + player.getName() + " requested to teleport to you. /tpaccept or /tpdeny");
			return true; }
	}
	public static final class Tpahere extends BaseCommand {
		private final TeleportService service; private final long expirySeconds;
		public Tpahere(TeleportService service, long expirySeconds) { this.service = service; this.expirySeconds = expirySeconds; }
		@Override protected String permission() { return "azura.teleport.tpahere"; }
		@Override protected boolean execute(CommandSender sender, String label, String[] args) {
			if (!(sender instanceof Player player)) { Text.sendPrefixed(sender, "<red>Only players can use this command."); return true; }
			if (args.length < 1) { Text.sendPrefixed(player, "<red>Usage: /tpahere <player>"); return true; }
			Player target = Bukkit.getPlayerExact(args[0]);
			if (target == null) { Text.sendPrefixed(player, "<red>Player not found."); return true; }
			service.requestTeleport(player, target, true, expirySeconds);
			Text.sendPrefixed(player, "<green>TPA-here request sent to <white>" + target.getName() + "</white>.");
			Text.sendPrefixed(target, "<yellow>" + player.getName() + " requested you to teleport to them. /tpaccept or /tpdeny");
			return true; }
	}
	public static final class Tpaccept extends BaseCommand {
		private final TeleportService service;
		public Tpaccept(TeleportService service) { this.service = service; }
		@Override protected String permission() { return "azura.teleport.tpaccept"; }
		@Override protected boolean execute(CommandSender sender, String label, String[] args) {
			if (!(sender instanceof Player player)) { Text.sendPrefixed(sender, "<red>Only players can use this command."); return true; }
			TeleportService.TeleportRequest req = service.getIncoming(player);
			if (req == null) { Text.sendPrefixed(player, "<red>No pending request."); return true; }
			if (req.expiresAt().isBefore(Instant.now())) { service.clearRequest(req.sender()); Text.sendPrefixed(player, "<red>Request expired."); return true; }
			Player senderP = Bukkit.getPlayer(req.sender()); Player targetP = Bukkit.getPlayer(req.target());
			if (senderP == null || targetP == null) { service.clearRequest(req.sender()); Text.sendPrefixed(player, "<red>Player offline."); return true; }
			service.consumeIncoming(player);
			if (req.here()) { senderP.teleport(targetP.getLocation()); } else { targetP.teleport(senderP.getLocation()); }
			Text.sendPrefixed(player, "<green>Teleport request accepted.");
			Text.sendPrefixed(senderP, "<green>Your teleport request was accepted.");
			return true; }
	}
	public static final class Tpdeny extends BaseCommand {
		private final TeleportService service;
		public Tpdeny(TeleportService service) { this.service = service; }
		@Override protected String permission() { return "azura.teleport.tpdeny"; }
		@Override protected boolean execute(CommandSender sender, String label, String[] args) {
			if (!(sender instanceof Player player)) { Text.sendPrefixed(sender, "<red>Only players can use this command."); return true; }
			TeleportService.TeleportRequest req = service.consumeIncoming(player);
			if (req == null) { Text.sendPrefixed(player, "<red>No pending request."); return true; }
			Player senderP = Bukkit.getPlayer(req.sender());
			if (senderP != null) Text.sendPrefixed(senderP, "<red>Your teleport request was denied.");
			Text.sendPrefixed(player, "<red>Teleport request denied.");
			return true; }
	}
}