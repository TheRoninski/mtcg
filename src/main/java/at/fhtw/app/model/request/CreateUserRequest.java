package at.fhtw.app.model.request;

import com.fasterxml.jackson.annotation.JsonAlias;

public class CreateUserRequest {

    @JsonAlias("Username")
    private String username;

    @JsonAlias("Password")
    private String password;

    public String getUsername() {
        return this.username;
    }

    public String getPassword() {
        return this.password;
    }
}
