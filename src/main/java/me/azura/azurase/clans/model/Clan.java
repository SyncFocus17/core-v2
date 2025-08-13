package me.azura.azurase.clans.model;

import org.bukkit.Location;

import java.util.*;

public final class Clan {
	private final UUID id;
	private String name;
	private String tag;
	private UUID leader;
	private final Map<UUID, ClanMember> members;
	private final Set<UUID> allies;
	private final Set<UUID> enemies;
	private Location home;
	private boolean friendlyFire;

	public Clan(UUID id, String name, String tag, UUID leader) {
		this.id = id;
		this.name = name;
		this.tag = tag;
		this.leader = leader;
		this.members = new HashMap<>();
		this.members.put(leader, new ClanMember(leader, ClanRank.LEADER));
		this.allies = new HashSet<>();
		this.enemies = new HashSet<>();
		this.friendlyFire = false;
	}

	public UUID getId() { return id; }
	public String getName() { return name; }
	public void setName(String name) { this.name = name; }
	public String getTag() { return tag; }
	public void setTag(String tag) { this.tag = tag; }
	public UUID getLeader() { return leader; }
	public void setLeader(UUID leader) { this.leader = leader; }
	public Map<UUID, ClanMember> getMembers() { return members; }
	public Set<UUID> getAllies() { return allies; }
	public Set<UUID> getEnemies() { return enemies; }
	public Location getHome() { return home; }
	public void setHome(Location home) { this.home = home; }
	public boolean isFriendlyFire() { return friendlyFire; }
	public void setFriendlyFire(boolean friendlyFire) { this.friendlyFire = friendlyFire; }

	public boolean isMember(UUID uuid) { return members.containsKey(uuid); }
	public ClanRank getRank(UUID uuid) { var m = members.get(uuid); return m == null ? null : m.rank(); }
	public void addMember(UUID uuid, ClanRank rank) { members.put(uuid, new ClanMember(uuid, rank)); }
	public void removeMember(UUID uuid) { members.remove(uuid); if (uuid.equals(leader)) leader = pickNewLeader(); }

	private UUID pickNewLeader() {
		for (ClanMember m : members.values()) if (m.rank() == ClanRank.OFFICER) return m.uuid();
		for (ClanMember m : members.values()) return m.uuid();
		return null;
	}
}