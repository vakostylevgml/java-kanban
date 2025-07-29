package manager.httpapi.handler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import manager.TaskManager;
import model.Task;

import java.io.IOException;
import java.time.Duration;
import java.util.List;

public class TaskHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager taskManager;
    private final Gson gson;

    public TaskHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
        DurationTypeAdapter durationTypeAdapter = new DurationTypeAdapter();
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Duration.class, durationTypeAdapter);
        gson = gsonBuilder.create();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        try {
            switch (method) {
                case "GET":
                    handleGet(exchange);
                    break;
                default:
                    throw new UnsupportedOperationException(method + " method not supported");
            }
        } catch (Exception e) {
            sendError(exchange, e.getMessage());
        }
    }

    private void handleGet(HttpExchange exchange) throws IOException, IllegalArgumentException {
        String[] pathParts = exchange.getRequestURI().getPath().split("/");
        if (pathParts.length == 2) {
            String tasks = gson.toJson(taskManager.findAllTasks());
            sendText(exchange, tasks);
        } else if (pathParts.length == 3) {
            try {
                int id = Integer.parseInt(pathParts[2]);
                sendText(exchange, "get task with id " + id);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid task id " + pathParts[2]);
            }
        } else {
            throw new IllegalArgumentException("Path not recognized: " + exchange.getRequestURI().getPath());
        }
    }

    static class TaskListTypeToken extends TypeToken<List<Task>> {
    }
}
