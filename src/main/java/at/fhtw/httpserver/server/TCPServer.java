package at.fhtw.httpserver.server;

import at.fhtw.app.service.IHttpService;
import at.fhtw.app.model.HttpRequest;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TCPServer {
    private final int port;
    private final IHttpService httpService;
    private static final int BUFFER_SIZE = 1024;
    private final ExecutorService threadPool = Executors.newFixedThreadPool(10);

    public TCPServer(int port, IHttpService httpService) {
        this.port = port;
        this.httpService = httpService;
    }

    public void listen() throws Exception {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("TCPServer listening on port " + port);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                threadPool.submit(() -> acceptClient(clientSocket));
            }
        }
    }

    private void acceptClient(Socket clientSocket) {
        try (clientSocket) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), StandardCharsets.UTF_8));
            StringBuilder completeMessage = new StringBuilder();
            char[] buffer = new char[BUFFER_SIZE];
            int bytesRead;
            while ((bytesRead = reader.read(buffer)) != -1) {
                completeMessage.append(buffer, 0, bytesRead);
                if (clientSocket.getInputStream().available() == 0) break;
            }
            String requestStr = completeMessage.toString();
            System.out.println("Received request:\n" + requestStr);
            HttpRequest httpRequest = httpService.parse(requestStr);
            String responseStr = httpService.route(httpRequest);
            System.out.println("Sending response:\n" + responseStr);
            OutputStream output = clientSocket.getOutputStream();
            output.write(responseStr.getBytes(StandardCharsets.UTF_8));
            output.flush();
            clientSocket.shutdownOutput();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
