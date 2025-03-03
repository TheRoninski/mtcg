package at.fhtw.httpserver.server;

import at.fhtw.app.controller.AbstractController;
import java.util.HashMap;
import java.util.Map;

public class Router {
    private final Map<String, AbstractController> controllerRegistry = new HashMap<>();

    public void addController(String route, AbstractController controller) {
        controllerRegistry.put(route, controller);
    }

    public AbstractController resolve(String route) {
        return controllerRegistry.get(route);
    }
}
