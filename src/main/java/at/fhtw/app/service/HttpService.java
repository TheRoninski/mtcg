package at.fhtw.app.service;

import at.fhtw.app.controller.AbstractController;
import at.fhtw.app.model.HttpRequest;
import at.fhtw.httpserver.http.HttpMethod;
import java.util.HashMap;
import java.util.Map;

public class HttpService implements IHttpService {

    private final Map<String, AbstractController> controllerMap = new HashMap<>();

    public HttpService(
            AbstractController userController,
            AbstractController packageController,
            AbstractController cardController,
            AbstractController gameController,
            AbstractController tradingController
    ) {
        // Map each route to its controller
        controllerMap.put("/users", userController);
        controllerMap.put("/sessions", userController);
        controllerMap.put("/packages", packageController);
        controllerMap.put("/transactions/packages", packageController);
        controllerMap.put("/cards", cardController);
        controllerMap.put("/deck", cardController);
        controllerMap.put("/scoreboard", gameController);
        controllerMap.put("/stats", gameController);
        controllerMap.put("/battles", gameController);
        controllerMap.put("/tradings", tradingController);
    }

    @Override
    public HttpRequest parse(String requestString) {
        // Minimal parse logic
        if (requestString == null || requestString.isEmpty()) {
            return null;
        }

        // Example logic:
        String[] lines = requestString.split("\r\n");
        if (lines.length < 1) return null;

        // First line: "GET /users HTTP/1.1"
        String[] firstLineParts = lines[0].split(" ");
        if (firstLineParts.length < 2) return null;

        HttpMethod method = getMethod(firstLineParts[0]);
        String route = firstLineParts[1];

        // Extract body if there's a content length
        int contentLength = 0;
        String authToken = null;
        int i = 1;
        for (; i < lines.length; i++) {
            String line = lines[i];
            if (line.isEmpty()) {
                // end of headers
                i++;
                break;
            }
            if (line.toLowerCase().startsWith("content-length:")) {
                String[] parts = line.split(":", 2);
                contentLength = Integer.parseInt(parts[1].trim());
            }
            if (line.toLowerCase().startsWith("authorization:")) {
                String[] parts = line.split(" ", 2);
                if (parts.length == 2) {
                    authToken = parts[1].trim();
                }
            }
        }
        String body = "";
        if (contentLength > 0 && (i < lines.length)) {
            StringBuilder sb = new StringBuilder();
            for (; i < lines.length; i++) {
                sb.append(lines[i]).append("\r\n");
            }
            body = sb.toString().trim();
        }

        // Build a simple HttpRequest record
        // route => e.g. "/users", method => GET, etc.
        return new HttpRequest(method, route, null, authToken, body, false);
    }

    @Override
    public String route(HttpRequest httpRequest) {
        if (httpRequest == null) {
            return "HTTP/1.1 400 Bad Request\r\n\r\nBad Request";
        }
        AbstractController controller = controllerMap.get(httpRequest.route());
        if (controller == null) {
            return "HTTP/1.1 404 Not Found\r\n\r\nNot Found";
        }

        // Convert the HttpRequest to your internal Request object if needed
        // or call handleRequest directly if you have bridging code.

        // Minimal example:
        // We’ll create a minimal bridging “Request” from httpserver.server:
        at.fhtw.httpserver.server.Request req = new at.fhtw.httpserver.server.Request();
        req.setMethod(httpRequest.method());
        req.setUrlContent(httpRequest.route());
        req.setBody(httpRequest.content());
        // If needed, set headers, etc.

        // Now call the controller:
        var resp = controller.handleRequest(req);

        // The controller returns a at.fhtw.httpserver.server.Response,
        // which has a get() method returning the raw HTTP string
        return resp.get();
    }

    @Override
    public HttpMethod getMethod(String methodString) {
        switch (methodString.toUpperCase()) {
            case "GET":    return HttpMethod.Get;
            case "POST":   return HttpMethod.Post;
            case "PUT":    return HttpMethod.Put;
            case "DELETE": return HttpMethod.Delete;
            case "PATCH":  return HttpMethod.Patch;
            default:
                throw new IllegalArgumentException("Invalid method: " + methodString);
        }
    }
}
