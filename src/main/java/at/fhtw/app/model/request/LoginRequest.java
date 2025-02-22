package at.fhtw.app.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class LoginRequest {
    @JsonProperty("Username")
    private String username;

    @JsonProperty("Password")
    private String password;

}
