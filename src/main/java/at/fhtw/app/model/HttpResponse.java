package at.fhtw.app.model;

public record HttpResponse(StatusCode statusCode, String payload) {
    public HttpResponse(StatusCode statusCode) {
        this(statusCode, null);
    }
}
