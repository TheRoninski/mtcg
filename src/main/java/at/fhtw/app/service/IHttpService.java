package at.fhtw.app.service;

import at.fhtw.app.model.HttpRequest;

public interface IHttpService {
    HttpRequest parse(String requestString);
    String route(HttpRequest httpRequest);
    at.fhtw.httpserver.http.HttpMethod getMethod(String method);
}
