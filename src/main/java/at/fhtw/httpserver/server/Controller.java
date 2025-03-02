package at.fhtw.httpserver.server;

import at.fhtw.app.controller.AbstractController;

public interface Controller {
    Response handleRequest(Request request);
}
