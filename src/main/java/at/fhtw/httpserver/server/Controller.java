package at.fhtw.httpserver.server;

public interface Controller {
    Response handleRequest(Request request);
}
