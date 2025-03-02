package at.fhtw.app.model;

public record Trade(String id, String cardToTrade, MonsterOrSpell type, int minimumDamage) { }
