package manager.httpapi;

import com.sun.net.httpserver.HttpServer;
import manager.Managers;
import manager.TaskManager;
import manager.httpapi.handler.TaskHandler;

import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpTaskServer {
    private static final int PORT = 8080;
    private static HttpServer httpServer;
    private final TaskManager manager;

    public HttpTaskServer(TaskManager taskManager) {
        manager = taskManager;
    }

    public static void main(String[] args) {
        HttpTaskServer httpTaskServer = new HttpTaskServer(Managers.getDefault());
        try {
            httpTaskServer.startServer();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public void startServer() throws IOException {
        httpServer = HttpServer.create(new InetSocketAddress(PORT), 0);

        httpServer.createContext("/tasks", new TaskHandler(manager));

        httpServer.start(); // запускаем сервер
    }

    public static void stopServer() throws IOException {
        httpServer.stop(0);
    }
}
