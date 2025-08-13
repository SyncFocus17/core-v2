package me.azura.azurase.clans.storage;

import me.azura.azurase.clans.model.Clan;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public interface ClanRepository {
	Optional<Clan> findById(UUID id);
	Optional<Clan> findByName(String name);
	void save(Clan clan);
	void delete(UUID id);
	Collection<Clan> findAll();
}