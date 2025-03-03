package at.fhtw.httpserver.http;

public enum ContentType {
    PLAIN_TEXT("text/plain"),
    HTML("text/html"),
    JSON("application/json");

    private final String type;

    ContentType(String type) {
        this.type = type;
    }

    public String type() {
        return type;
    }
}
