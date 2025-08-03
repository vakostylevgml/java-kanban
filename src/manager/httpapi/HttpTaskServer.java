package manager.httpapi;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpServer;
import manager.TaskManager;
import manager.httpapi.adapter.DurationTypeAdapter;
import manager.httpapi.adapter.LocalDateTimeTypeAdapter;
import manager.httpapi.handler.SubtaskHandler;
import manager.httpapi.handler.TaskHandler;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.LocalDateTime;

public class HttpTaskServer {
    private static final int PORT = 8080;
    private static HttpServer httpServer;
    private static Gson gson;
    private final TaskManager manager;

    public HttpTaskServer(TaskManager taskManager) {
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

    public static void stopServer() {
        httpServer.stop(0);
    }

    public void startServer() throws IOException {
        httpServer = HttpServer.create(new InetSocketAddress(PORT), 0);
        httpServer.createContext("/tasks", new TaskHandler(manager, gson));
        httpServer.createContext("/subtasks", new SubtaskHandler(manager, gson));
        httpServer.start();
    }
}
