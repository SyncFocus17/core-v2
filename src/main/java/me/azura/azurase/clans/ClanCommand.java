package me.azura.azurase.clans;

import me.azura.azurase.clans.model.*;
import me.azura.azurase.core.command.BaseCommand;
import me.azura.azurase.core.text.Text;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;

public final class ClanCommand extends BaseCommand {
	private final ClanService service;
	public ClanCommand(ClanService service) { this.service = service; }
	@Override protected String permission() { return "azura.clans.use"; }

	@Override protected boolean execute(CommandSender sender, String label, String[] args) {
		if (!(sender instanceof Player player)) { Text.sendPrefixed(sender, "<red>Only players can use this command."); return true; }
		if (args.length == 0) { Text.sendPrefixed(player, "<gray>/clan create <name> [tag], /clan delete, /clan invite <player>, /clan accept, /clan join <name>, /clan leave, /clan kick <player>, /clan promote <player>, /clan demote <player>, /clan ally <name>, /clan enemy <name>, /clan neutral <name>, /clan chat <clan|ally|enemy|off>, /clan home, /clan sethome, /clan ff <on|off>"); return true; }
		String sub = args[0].toLowerCase(Locale.ROOT);
		return switch (sub) {
			case "create" -> handleCreate(player, args);
			case "delete" -> handleDelete(player);
			case "invite" -> handleInvite(player, args);
			case "accept" -> handleAccept(player);
			case "join" -> handleJoin(player, args);
			case "leave" -> handleLeave(player);
			case "kick" -> handleKick(player, args);
			case "promote" -> handlePromote(player, args);
			case "demote" -> handleDemote(player, args);
			case "ally" -> handleRelation(player, args, RelationshipType.ALLY, true);
			case "enemy" -> handleRelation(player, args, RelationshipType.ENEMY, true);
			case "neutral" -> handleNeutral(player, args);
			case "chat" -> { Text.sendPrefixed(player, "<yellow>Clan chats will be implemented."); yield true; }
			case "home" -> handleHome(player);
			case "sethome" -> handleSetHome(player);
			case "ff" -> handleFriendlyFire(player, args);
			default -> { Text.sendPrefixed(player, "<red>Unknown subcommand."); yield true; }
		};
	}

	private boolean handleCreate(Player player, String[] args) {
		if (service.getClanByPlayer(player.getUniqueId()).isPresent()) { Text.sendPrefixed(player, "<red>You are already in a clan."); return true; }
		if (args.length < 2) { Text.sendPrefixed(player, "<red>Usage: /clan create <name> [tag]"); return true; }
		String name = args[1]; String tag = args.length >= 3 ? args[2] : name.substring(0, Math.min(4, name.length())).toUpperCase();
		service.createClan(player.getUniqueId(), name, tag);
		Text.sendPrefixed(player, "<green>Clan <white>"+name+"</white> created.");
		return true;
	}

	private boolean handleDelete(Player player) {
		var opt = service.getClanByPlayer(player.getUniqueId()); if (opt.isEmpty()) { Text.sendPrefixed(player, "<red>You are not in a clan."); return true; }
		Clan clan = opt.get(); if (!clan.getLeader().equals(player.getUniqueId())) { Text.sendPrefixed(player, "<red>Only leader can delete."); return true; }
		service.deleteClan(clan); Text.sendPrefixed(player, "<red>Clan deleted."); return true;
	}

	private boolean handleInvite(Player player, String[] args) {
		var opt = service.getClanByPlayer(player.getUniqueId()); if (opt.isEmpty()) { Text.sendPrefixed(player, "<red>You are not in a clan."); return true; }
		Clan clan = opt.get(); ClanRank rank = clan.getRank(player.getUniqueId()); if (rank == null || !rank.canInvite()) { Text.sendPrefixed(player, "<red>You cannot invite."); return true; }
		if (args.length < 2) { Text.sendPrefixed(player, "<red>Usage: /clan invite <player>"); return true; }
		Player target = Bukkit.getPlayerExact(args[1]); if (target == null) { Text.sendPrefixed(player, "<red>Player not found."); return true; }
		service.invite(clan.getId(), target.getUniqueId());
		Text.sendPrefixed(player, "<green>Invited <white>"+target.getName()+"</white>.");
		Text.sendPrefixed(target, "<yellow>You were invited to <white>"+clan.getName()+"</white> by <white>"+player.getName()+"</white>. Use /clan accept.");
		return true; }

	private boolean handleAccept(Player player) {
		var invited = service.consumeInvite(player.getUniqueId()); if (invited.isEmpty()) { Text.sendPrefixed(player, "<red>No pending invite."); return true; }
		Clan clan = invited.get(); service.addMember(clan, player.getUniqueId(), ClanRank.MEMBER);
		Text.sendPrefixed(player, "<green>You joined <white>"+clan.getName()+"</white>."); return true; }

	private boolean handleJoin(Player player, String[] args) {
		if (args.length < 2) { Text.sendPrefixed(player, "<red>Usage: /clan join <name>"); return true; }
		Text.sendPrefixed(player, "<red>Direct join disabled. Ask for an invite."); return true; }

	private boolean handleLeave(Player player) {
		var opt = service.getClanByPlayer(player.getUniqueId()); if (opt.isEmpty()) { Text.sendPrefixed(player, "<red>You are not in a clan."); return true; }
		Clan clan = opt.get(); if (clan.getLeader().equals(player.getUniqueId()) && clan.getMembers().size() > 1) { Text.sendPrefixed(player, "<red>Transfer leadership first."); return true; }
		service.removeMember(clan, player.getUniqueId()); Text.sendPrefixed(player, "<yellow>You left <white>"+clan.getName()+"</white>."); return true; }

	private boolean handleKick(Player player, String[] args) {
		var opt = service.getClanByPlayer(player.getUniqueId()); if (opt.isEmpty()) { Text.sendPrefixed(player, "<red>You are not in a clan."); return true; }
		Clan clan = opt.get(); ClanRank rank = clan.getRank(player.getUniqueId()); if (args.length < 2) { Text.sendPrefixed(player, "<red>Usage: /clan kick <player>"); return true; }
		Player target = Bukkit.getPlayerExact(args[1]); if (target == null) { Text.sendPrefixed(player, "<red>Player not found."); return true; }
		ClanRank tr = clan.getRank(target.getUniqueId()); if (tr == null) { Text.sendPrefixed(player, "<red>They are not in your clan."); return true; }
		if (rank == null || !rank.canKick(tr)) { Text.sendPrefixed(player, "<red>You cannot kick that rank."); return true; }
		service.removeMember(clan, target.getUniqueId()); Text.sendPrefixed(player, "<yellow>Kicked <white>"+target.getName()+"</white>."); return true; }

	private boolean handlePromote(Player player, String[] args) {
		var opt = service.getClanByPlayer(player.getUniqueId()); if (opt.isEmpty()) { Text.sendPrefixed(player, "<red>You are not in a clan."); return true; }
		Clan clan = opt.get(); if (!clan.getLeader().equals(player.getUniqueId())) { Text.sendPrefixed(player, "<red>Only leader can promote/demote."); return true; }
		if (args.length < 2) { Text.sendPrefixed(player, "<red>Usage: /clan promote <player>"); return true; }
		Player target = Bukkit.getPlayerExact(args[1]); if (target == null) { Text.sendPrefixed(player, "<red>Player not found."); return true; }
		if (!clan.isMember(target.getUniqueId())) { Text.sendPrefixed(player, "<red>They are not in your clan."); return true; }
		clan.addMember(target.getUniqueId(), ClanRank.OFFICER); service.addMember(clan, target.getUniqueId(), ClanRank.OFFICER);
		Text.sendPrefixed(player, "<green>Promoted."); return true; }

	private boolean handleDemote(Player player, String[] args) {
		var opt = service.getClanByPlayer(player.getUniqueId()); if (opt.isEmpty()) { Text.sendPrefixed(player, "<red>You are not in a clan."); return true; }
		Clan clan = opt.get(); if (!clan.getLeader().equals(player.getUniqueId())) { Text.sendPrefixed(player, "<red>Only leader can promote/demote."); return true; }
		if (args.length < 2) { Text.sendPrefixed(player, "<red>Usage: /clan demote <player>"); return true; }
		Player target = Bukkit.getPlayerExact(args[1]); if (target == null) { Text.sendPrefixed(player, "<red>Player not found."); return true; }
		if (!clan.isMember(target.getUniqueId())) { Text.sendPrefixed(player, "<red>They are not in your clan."); return true; }
		clan.addMember(target.getUniqueId(), ClanRank.MEMBER); service.addMember(clan, target.getUniqueId(), ClanRank.MEMBER);
		Text.sendPrefixed(player, "<yellow>Demoted."); return true; }

	private boolean handleRelation(Player player, String[] args, RelationshipType rel, boolean add) {
		if (args.length < 2) { Text.sendPrefixed(player, "<red>Usage: /clan "+(rel==RelationshipType.ALLY?"ally":"enemy")+" <name>"); return true; }
		var opt = service.getClanByPlayer(player.getUniqueId()); if (opt.isEmpty()) { Text.sendPrefixed(player, "<red>You are not in a clan."); return true; }
		var other = service.repository.findByName(args[1]); if (other.isEmpty()) { Text.sendPrefixed(player, "<red>Clan not found."); return true; }
		service.setRelation(opt.get(), other.get(), rel, add); Text.sendPrefixed(player, "<green>Relation updated."); return true; }

	private boolean handleNeutral(Player player, String[] args) { return handleRelation(player, args, RelationshipType.ALLY, false); }
	private boolean handleHome(Player player) {
		var opt = service.getClanByPlayer(player.getUniqueId()); if (opt.isEmpty()) { Text.sendPrefixed(player, "<red>You are not in a clan."); return true; }
		var home = service.getHome(opt.get()); if (home.isEmpty()) { Text.sendPrefixed(player, "<red>No clan home set."); return true; }
		player.teleport(home.get()); Text.sendPrefixed(player, "<gray>Teleported to clan home."); return true; }
	private boolean handleSetHome(Player player) {
		var opt = service.getClanByPlayer(player.getUniqueId()); if (opt.isEmpty()) { Text.sendPrefixed(player, "<red>You are not in a clan."); return true; }
		Clan clan = opt.get(); if (!clan.getLeader().equals(player.getUniqueId()) && clan.getRank(player.getUniqueId()) != ClanRank.OFFICER) { Text.sendPrefixed(player, "<red>Only leader/officers can set home."); return true; }
		service.setHome(clan, player.getLocation()); Text.sendPrefixed(player, "<green>Clan home set."); return true; }
	private boolean handleFriendlyFire(Player player, String[] args) {
		var opt = service.getClanByPlayer(player.getUniqueId()); if (opt.isEmpty()) { Text.sendPrefixed(player, "<red>You are not in a clan."); return true; }
		Clan clan = opt.get(); if (!clan.getLeader().equals(player.getUniqueId())) { Text.sendPrefixed(player, "<red>Only leader can toggle friendly-fire."); return true; }
		if (args.length < 2) { Text.sendPrefixed(player, "<red>Usage: /clan ff <on|off>"); return true; }
		boolean on = args[1].equalsIgnoreCase("on"); service.setFriendlyFire(clan, on); Text.sendPrefixed(player, "<yellow>Friendly-fire "+(on?"enabled":"disabled")+"."); return true; }

	@Override public List<String> onTabComplete(CommandSender s, org.bukkit.command.Command c, String a, String[] args) {
		if (args.length == 1) return Arrays.asList("create","delete","invite","accept","join","leave","kick","promote","demote","ally","enemy","neutral","chat","home","sethome","ff");
		return List.of(); }
}