package at.fhtw.app.model;
import com.fasterxml.jackson.annotation.JsonProperty;


public record Credentials(
        @JsonProperty("Username") String username,
        @JsonProperty("Password") String password
) { }
