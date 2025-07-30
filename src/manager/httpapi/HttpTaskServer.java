package manager.httpapi;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpServer;
import manager.Managers;
import manager.httpapi.adapter.DurationTypeAdapter;
import manager.httpapi.adapter.LocalDateTimeTypeAdapter;
import manager.httpapi.handler.TaskHandler;
import manager.inmemory.InMemoryTaskManager;
import model.Status;
import model.Task;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.LocalDateTime;

public class HttpTaskServer {
    private static final int PORT = 8080;
    private static HttpServer httpServer;
    private static Gson gson;
    private final InMemoryTaskManager manager;

    public HttpTaskServer(InMemoryTaskManager taskManager) {
        manager = taskManager;
        DurationTypeAdapter durationTypeAdapter = new DurationTypeAdapter();
        LocalDateTimeTypeAdapter localDateTimeTypeAdapter = new LocalDateTimeTypeAdapter();
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Duration.class, durationTypeAdapter);
        gsonBuilder.registerTypeAdapter(LocalDateTime.class, localDateTimeTypeAdapter);
        gson = gsonBuilder.create();
    }

    public static Gson getGson() {
        return gson;
    }

    public static void main(String[] args) {
        HttpTaskServer httpTaskServer = new HttpTaskServer(new InMemoryTaskManager(Managers.getDefaultHistory()));

        Task task = new Task("a", "b", Status.NEW);
        Task task2 = new Task("a2", "b2", Status.IN_PROGRESS);
        httpTaskServer.manager.createTask(task);
        httpTaskServer.manager.createTask(task2);
        try {
            httpTaskServer.startServer();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void stopServer() {
        httpServer.stop(0);
    }

    public void startServer() throws IOException {

        httpServer = HttpServer.create(new InetSocketAddress(PORT), 0);
        httpServer.createContext("/tasks", new TaskHandler(manager, gson));
        httpServer.start();
    }
}
