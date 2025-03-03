package at.fhtw.httpserver.server;

import at.fhtw.httpserver.utils.RequestHandler;
import at.fhtw.httpserver.server.Router;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private final int port;
    private final Router router;

    public Server(int port, Router router) {
        this.port = port;
        this.router = router;
    }

    public void start() throws IOException {
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        System.out.println("Server started on port " + port);
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            while (true) {
                Socket clientConnection = serverSocket.accept();
                RequestHandler handler = new RequestHandler(clientConnection, router);
                executorService.submit(handler);
            }
        }
    }
}
