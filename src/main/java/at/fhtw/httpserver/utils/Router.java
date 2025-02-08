package at.fhtw.httpserver.utils;

import at.fhtw.httpserver.server.Controller;

import java.util.HashMap;
import java.util.Map;

public class Router {
    private final Map<String, Controller> controllerRegistry = new HashMap<>();

    public void addController(String route, Controller controller) {
        this.controllerRegistry.put(route, controller);
    }

    public void removeController(String route) {
        this.controllerRegistry.remove(route);
    }

    public Controller resolve(String route) {
        return this.controllerRegistry.get(route);
    }
}
