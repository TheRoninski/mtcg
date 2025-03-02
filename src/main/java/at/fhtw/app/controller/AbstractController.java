package at.fhtw.app.controller;

import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import com.fasterxml.jackson.databind.ObjectMapper;

public abstract class AbstractController implements at.fhtw.httpserver.server.Controller {

    protected final ObjectMapper mapper = new ObjectMapper();

    protected <T> T parseContent(String content, Class<T> clazz) {
        if (content == null || content.isEmpty()) {
            throw new IllegalArgumentException("Content is null or empty");
        }
        try {
            T parsed = mapper.readValue(content, clazz);
            if (parsed == null) {
                throw new IllegalArgumentException("Parsed content is null");
            }
            return parsed;
        } catch (Exception e) {
            throw new IllegalArgumentException("Error parsing content: " + e.getMessage(), e);
        }
    }

    @Override
    public abstract Response handleRequest(Request request);
}
