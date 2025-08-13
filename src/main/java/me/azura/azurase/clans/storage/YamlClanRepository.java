package me.azura.azurase.clans.storage;

import me.azura.azurase.clans.model.*;
import me.azura.azurase.core.AzuraSEPlugin;
import me.azura.azurase.core.util.LocationUtil;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;

public final class YamlClanRepository implements ClanRepository {
	private final File file;
	private final YamlConfiguration yaml;

	public YamlClanRepository(AzuraSEPlugin plugin) {
		this.file = new File(plugin.getDataFolder(), "clans.yml");
		this.yaml = YamlConfiguration.loadConfiguration(file);
	}

	@Override
	public Optional<Clan> findById(UUID id) { return Optional.ofNullable(loadClan(id.toString())); }

	@Override
	public Optional<Clan> findByName(String name) {
		ConfigurationSection sec = yaml.getConfigurationSection("clans");
		if (sec == null) return Optional.empty();
		for (String key : sec.getKeys(false)) {
			String n = yaml.getString("clans." + key + ".name");
			if (name.equalsIgnoreCase(n)) return Optional.ofNullable(loadClan(key));
		}
		return Optional.empty();
	}

	@Override
	public void save(Clan clan) {
		String key = clan.getId().toString();
		yaml.set("clans." + key + ".name", clan.getName());
		yaml.set("clans." + key + ".tag", clan.getTag());
		yaml.set("clans." + key + ".leader", clan.getLeader().toString());
		List<String> members = new ArrayList<>();
		List<String> officers = new ArrayList<>();
		for (ClanMember m : clan.getMembers().values()) {
			if (m.rank() == ClanRank.LEADER) continue;
			(m.rank() == ClanRank.OFFICER ? officers : members).add(m.uuid().toString());
		}
		yaml.set("clans." + key + ".members", members);
		yaml.set("clans." + key + ".officers", officers);
		yaml.set("clans." + key + ".allies", toStringList(clan.getAllies()));
		yaml.set("clans." + key + ".enemies", toStringList(clan.getEnemies()));
		yaml.set("clans." + key + ".home", clan.getHome() == null ? null : LocationUtil.serialize(clan.getHome()));
		yaml.set("clans." + key + ".friendlyFire", clan.isFriendlyFire());
		saveFile();
	}

	@Override
	public void delete(UUID id) {
		yaml.set("clans." + id, null);
		saveFile();
	}

	@Override
	public Collection<Clan> findAll() {
		ConfigurationSection sec = yaml.getConfigurationSection("clans");
		if (sec == null) return List.of();
		List<Clan> list = new ArrayList<>();
		for (String key : sec.getKeys(false)) {
			Clan c = loadClan(key);
			if (c != null) list.add(c);
		}
		return list;
	}

	private Clan loadClan(String key) {
		String name = yaml.getString("clans." + key + ".name");
		if (name == null) return null;
		String tag = yaml.getString("clans." + key + ".tag", name.substring(0, Math.min(4, name.length())).toUpperCase());
		UUID leader = UUID.fromString(yaml.getString("clans." + key + ".leader"));
		Clan clan = new Clan(UUID.fromString(key), name, tag, leader);
		for (String s : yaml.getStringList("clans." + key + ".officers")) clan.addMember(UUID.fromString(s), ClanRank.OFFICER);
		for (String s : yaml.getStringList("clans." + key + ".members")) clan.addMember(UUID.fromString(s), ClanRank.MEMBER);
		yaml.getStringList("clans." + key + ".allies").forEach(s -> clan.getAllies().add(UUID.fromString(s)));
		yaml.getStringList("clans." + key + ".enemies").forEach(s -> clan.getEnemies().add(UUID.fromString(s)));
		String home = yaml.getString("clans." + key + ".home");
		if (home != null) clan.setHome(LocationUtil.deserialize(home));
		clan.setFriendlyFire(yaml.getBoolean("clans." + key + ".friendlyFire", false));
		return clan;
	}

	private List<String> toStringList(Set<UUID> set) {
		return set.stream().map(UUID::toString).toList();
	}

	private void saveFile() {
		try { yaml.save(file); } catch (IOException ignored) {}
	}
}