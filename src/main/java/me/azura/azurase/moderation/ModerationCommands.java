package me.azura.azurase.moderation;

import me.azura.azurase.core.command.BaseCommand;
import me.azura.azurase.core.text.Text;
import me.azura.azurase.core.util.TimeUtil;
import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.time.Duration;
import java.time.Instant;

public final class ModerationCommands {
	public static final class Fly extends BaseCommand {
		@Override protected String permission() { return "azura.staff.fly"; }
		@Override protected boolean execute(CommandSender sender, String label, String[] args) {
			if (!(sender instanceof Player p)) { Text.sendPrefixed(sender, "<red>Only players can use this."); return true; }
			boolean enable = !p.getAllowFlight();
			p.setAllowFlight(enable);
			p.setFlying(enable);
			Text.sendPrefixed(p, enable?"<green>Fly enabled.":"<yellow>Fly disabled."); return true; }
	}
	public static final class Heal extends BaseCommand {
		@Override protected String permission() { return "azura.moderation.heal"; }
		@Override protected boolean execute(CommandSender sender, String label, String[] args) {
			Player target = args.length>=1?Bukkit.getPlayerExact(args[0]):(sender instanceof Player p? p:null);
			if (target==null) { Text.sendPrefixed(sender, "<red>Player not found."); return true; }
			target.setHealth(target.getMaxHealth()); target.setFireTicks(0);
			Text.sendPrefixed(sender, "<green>Healed "+target.getName()+"."); return true; }
	}
	public static final class Feed extends BaseCommand {
		@Override protected String permission() { return "azura.moderation.feed"; }
		@Override protected boolean execute(CommandSender sender, String label, String[] args) {
			Player target = args.length>=1?Bukkit.getPlayerExact(args[0]):(sender instanceof Player p? p:null);
			if (target==null) { Text.sendPrefixed(sender, "<red>Player not found."); return true; }
			target.setFoodLevel(20); target.setSaturation(20f);
			Text.sendPrefixed(sender, "<green>Fed "+target.getName()+"."); return true; }
	}
	public static final class Gm extends BaseCommand {
		@Override protected String permission() { return "azura.moderation.gm"; }
		@Override protected boolean execute(CommandSender sender, String label, String[] args) {
			if (args.length<1) { Text.sendPrefixed(sender, "<red>Usage: /gm <survival|creative|adventure|spectator> [player]"); return true; }
			GameMode gm; try { gm = GameMode.valueOf(args[0].toUpperCase()); } catch (Exception e) { Text.sendPrefixed(sender, "<red>Unknown gamemode."); return true; }
			Player target = args.length>=2?Bukkit.getPlayerExact(args[1]):(sender instanceof Player p? p:null);
			if (target==null) { Text.sendPrefixed(sender, "<red>Player not found."); return true; }
			target.setGameMode(gm); Text.sendPrefixed(sender, "<green>Set gamemode for "+target.getName()+" to "+gm+"."); return true; }
	}
	public static final class Kick extends BaseCommand {
		@Override protected String permission() { return "azura.moderation.kick"; }
		@Override protected boolean execute(CommandSender sender, String label, String[] args) {
			if (args.length<1) { Text.sendPrefixed(sender, "<red>Usage: /kick <player> [reason]"); return true; }
			Player target = Bukkit.getPlayerExact(args[0]); if (target==null) { Text.sendPrefixed(sender, "<red>Player not found."); return true; }
			String reason = args.length>=2?String.join(" ", java.util.Arrays.copyOfRange(args,1,args.length)):"Kicked";
			target.kick(Text.parse("<red>"+reason)); return true; }
	}
	public static final class Ban extends BaseCommand {
		@Override protected String permission() { return "azura.moderation.ban"; }
		@Override protected boolean execute(CommandSender sender, String label, String[] args) {
			if (args.length<1) { Text.sendPrefixed(sender, "<red>Usage: /ban <player> [reason]"); return true; }
			String name = args[0]; String reason = args.length>=2?String.join(" ", java.util.Arrays.copyOfRange(args,1,args.length)):"Banned";
			Bukkit.getBanList(BanList.Type.NAME).addBan(name, reason, null, sender.getName());
			Player target = Bukkit.getPlayerExact(name); if (target!=null) target.kick(Text.parse("<red>"+reason));
			Text.sendPrefixed(sender, "<green>Banned "+name+"."); return true; }
	}
	public static final class Tempban extends BaseCommand {
		@Override protected String permission() { return "azura.moderation.tempban"; }
		@Override protected boolean execute(CommandSender sender, String label, String[] args) {
			if (args.length<2) { Text.sendPrefixed(sender, "<red>Usage: /tempban <player> <duration> [reason]"); return true; }
			String name = args[0]; Duration d = TimeUtil.parseDuration(args[1]); if (d.isZero()) { Text.sendPrefixed(sender, "<red>Invalid duration."); return true; }
			String reason = args.length>=3?String.join(" ", java.util.Arrays.copyOfRange(args,2,args.length)):"Tempbanned";
			Instant until = Instant.now().plus(d);
			Bukkit.getBanList(BanList.Type.NAME).addBan(name, reason, java.util.Date.from(until), sender.getName());
			Player target = Bukkit.getPlayerExact(name); if (target!=null) target.kick(Text.parse("<red>"+reason));
			Text.sendPrefixed(sender, "<green>Tempbanned "+name+" for "+TimeUtil.formatShort(d)+"."); return true; }
	}
	public static final class Mute extends BaseCommand {
		private final ModerationService service; public Mute(ModerationService s) { this.service = s; }
		@Override protected String permission() { return "azura.moderation.mute"; }
		@Override protected boolean execute(CommandSender sender, String label, String[] args) {
			if (args.length<1) { Text.sendPrefixed(sender, "<red>Usage: /mute <player> [duration] [reason]"); return true; }
			Player target = Bukkit.getPlayerExact(args[0]); if (target==null) { Text.sendPrefixed(sender, "<red>Player not found."); return true; }
			Duration d = args.length>=2?TimeUtil.parseDuration(args[1]):Duration.ZERO;
			String reason = args.length>=3?String.join(" ", java.util.Arrays.copyOfRange(args,2,args.length)):"Muted";
			service.mute(target.getUniqueId(), reason, d.isZero()?null:Instant.now().plus(d));
			Text.sendPrefixed(sender, "<yellow>Muted "+target.getName()+"."); return true; }
	}
	public static final class Unmute extends BaseCommand {
		private final ModerationService service; public Unmute(ModerationService s) { this.service = s; }
		@Override protected String permission() { return "azura.moderation.unmute"; }
		@Override protected boolean execute(CommandSender sender, String label, String[] args) {
			if (args.length<1) { Text.sendPrefixed(sender, "<red>Usage: /unmute <player>"); return true; }
			Player target = Bukkit.getPlayerExact(args[0]); if (target==null) { Text.sendPrefixed(sender, "<red>Player not found."); return true; }
			service.unmute(target.getUniqueId()); Text.sendPrefixed(sender, "<green>Unmuted "+target.getName()+"."); return true; }
	}
	public static final class Warn extends BaseCommand {
		@Override protected String permission() { return "azura.moderation.warn"; }
		@Override protected boolean execute(CommandSender sender, String label, String[] args) {
			if (args.length<2) { Text.sendPrefixed(sender, "<red>Usage: /warn <player> <reason>"); return true; }
			Player target = Bukkit.getPlayerExact(args[0]); if (target==null) { Text.sendPrefixed(sender, "<red>Player not found."); return true; }
			String reason = String.join(" ", java.util.Arrays.copyOfRange(args,1,args.length));
			Text.sendPrefixed(target, "<yellow>Warning: <white>"+reason+"</white>");
			Text.sendPrefixed(sender, "<yellow>Warned "+target.getName()+"."); return true; }
	}
	public static final class Invsee extends BaseCommand {
		@Override protected String permission() { return "azura.moderation.invsee"; }
		@Override protected boolean execute(CommandSender sender, String label, String[] args) {
			if (!(sender instanceof Player p)) { Text.sendPrefixed(sender, "<red>Only players can use this."); return true; }
			if (args.length<1) { Text.sendPrefixed(p, "<red>Usage: /invsee <player>"); return true; }
			Player target = Bukkit.getPlayerExact(args[0]); if (target==null) { Text.sendPrefixed(p, "<red>Player not found."); return true; }
			p.openInventory(target.getInventory()); return true; }
	}
	public static final class Clear extends BaseCommand {
		@Override protected String permission() { return "azura.moderation.clear"; }
		@Override protected boolean execute(CommandSender sender, String label, String[] args) {
			Player target = args.length>=1?Bukkit.getPlayerExact(args[0]):(sender instanceof Player p? p:null);
			if (target==null) { Text.sendPrefixed(sender, "<red>Player not found."); return true; }
			target.getInventory().clear(); target.getInventory().setArmorContents(null); target.getInventory().setExtraContents(null);
			Text.sendPrefixed(sender, "<yellow>Cleared inventory of "+target.getName()+"."); return true; }
	}
	public static final class Staffchat extends BaseCommand {
		private final ModerationService service; public Staffchat(ModerationService s) { this.service = s; }
		@Override protected String permission() { return "azura.staff.chat"; }
		@Override protected boolean execute(CommandSender sender, String label, String[] args) {
			if (!(sender instanceof Player p)) { Text.sendPrefixed(sender, "<red>Only players can use this."); return true; }
			service.toggleStaffChat(p.getUniqueId());
			Text.sendPrefixed(p, "<green>Staff chat "+(service.isStaffChatEnabled(p.getUniqueId())?"enabled":"disabled")+"."); return true; }
	}
}