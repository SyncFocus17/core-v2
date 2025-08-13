package me.azura.azurase.clans;

import me.azura.azurase.clans.model.*;
import me.azura.azurase.clans.storage.ClanRepository;
import org.bukkit.Bukkit;
import org.bukkit.Scoreboard;
import org.bukkit.ScoreboardManager;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public final class ClanService {
	private final ClanRepository repository;
	private final Map<UUID, UUID> playerToClan = new ConcurrentHashMap<>();
	private final Map<UUID, UUID> invites = new ConcurrentHashMap<>(); // target -> clan

	public ClanService(ClanRepository repository) {
		this.repository = repository;
		repository.findAll().forEach(this::indexClan);
		updateScoreboardTags();
	}

	private void indexClan(Clan clan) {
		for (UUID u : clan.getMembers().keySet()) playerToClan.put(u, clan.getId());
	}

	public Optional<Clan> getClanByPlayer(UUID player) {
		UUID id = playerToClan.get(player);
		return id == null ? Optional.empty() : repository.findById(id);
	}

	public Optional<Clan> findByName(String name) { return repository.findByName(name); }

	public Clan createClan(UUID creator, String name, String tag) {
		Clan clan = new Clan(UUID.randomUUID(), name, tag, creator);
		repository.save(clan);
		playerToClan.put(creator, clan.getId());
		updateScoreboardTags();
		return clan;
	}

	public void deleteClan(Clan clan) {
		repository.delete(clan.getId());
		clan.getMembers().keySet().forEach(playerToClan::remove);
		updateScoreboardTags();
	}

	public void invite(UUID clanId, UUID target) { invites.put(target, clanId); }
	public Optional<Clan> consumeInvite(UUID target) { UUID id = invites.remove(target); return id == null ? Optional.empty() : repository.findById(id); }
	public boolean hasInvite(UUID target) { return invites.containsKey(target); }

	public void addMember(Clan clan, UUID uuid, ClanRank rank) { clan.addMember(uuid, rank); repository.save(clan); playerToClan.put(uuid, clan.getId()); updateScoreboardTags(); }
	public void removeMember(Clan clan, UUID uuid) { clan.removeMember(uuid); repository.save(clan); playerToClan.remove(uuid); updateScoreboardTags(); }

	public void setRelation(Clan a, Clan b, RelationshipType rel, boolean add) {
		if (rel == RelationshipType.ALLY) { if (add) { a.getAllies().add(b.getId()); } else { a.getAllies().remove(b.getId()); } }
		if (rel == RelationshipType.ENEMY) { if (add) { a.getEnemies().add(b.getId()); } else { a.getEnemies().remove(b.getId()); } }
		repository.save(a);
	}

	public void setHome(Clan clan, org.bukkit.Location loc) { clan.setHome(loc); repository.save(clan); }
	public Optional<org.bukkit.Location> getHome(Clan clan) { return Optional.ofNullable(clan.getHome()); }
	public void setFriendlyFire(Clan clan, boolean enabled) { clan.setFriendlyFire(enabled); repository.save(clan); }

	private void updateScoreboardTags() {
		ScoreboardManager sm = Bukkit.getScoreboardManager();
		if (sm == null) return;
		Scoreboard sb = sm.getMainScoreboard();
		for (Clan clan : repository.findAll()) {
			String teamName = ("clan_" + clan.getTag()).toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9_]", "_");
			org.bukkit.scoreboard.Team team = sb.getTeam(teamName);
			if (team == null) team = sb.registerNewTeam(teamName);
			team.setPrefix("[" + clan.getTag() + "] ");
			for (UUID u : clan.getMembers().keySet()) {
				Player p = Bukkit.getPlayer(u);
				if (p != null) team.addEntry(p.getName());
			}
		}
	}
}