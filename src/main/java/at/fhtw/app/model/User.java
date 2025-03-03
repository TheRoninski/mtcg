package at.fhtw.app.model;

public record User(int id, String username, String token, int coins, int elo, boolean isAdmin) {
    @Override
    public String toString() {
        return username;
    }

    public String getUsername() {
        return username;
    }
}
