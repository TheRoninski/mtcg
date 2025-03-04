package at.fhtw.app.model;
import com.fasterxml.jackson.annotation.JsonProperty;

public record Trade(
        @JsonProperty("Id") String id,
        @JsonProperty("CardToTrade") String cardToTrade,
        @JsonProperty("Type") MonsterOrSpell type,
        @JsonProperty("MinimumDamage") int minimumDamage
) { }