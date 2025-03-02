package at.fhtw.app.model;

import at.fhtw.httpserver.http.HttpMethod;

public record HttpRequest(
        HttpMethod method,
        String route,
        String subRouteParameter,
        String authentication,
        String content,
        boolean plain
) {
    public HttpRequest(HttpMethod method, String route) {
        this(method, route, null, null, null, false);
    }
}
