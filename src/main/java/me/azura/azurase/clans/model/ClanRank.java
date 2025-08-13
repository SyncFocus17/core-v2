package me.azura.azurase.clans.model;

public enum ClanRank {
	LEADER,
	OFFICER,
	MEMBER;

	public boolean canInvite() { return this == LEADER || this == OFFICER; }
	public boolean canKick(ClanRank target) { return this == LEADER || (this == OFFICER && target == MEMBER); }
	public boolean canPromoteDemote() { return this == LEADER; }
}