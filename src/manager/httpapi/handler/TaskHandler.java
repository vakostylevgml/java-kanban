package manager.httpapi.handler;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import manager.inmemory.InMemoryTaskManager;
import manager.inmemory.OverlapException;
import model.Task;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class TaskHandler extends BaseHttpHandler implements HttpHandler {
    public TaskHandler(InMemoryTaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        try {
            switch (method) {
                case "GET":
                    handleGet(exchange);
                    break;
                case "POST":
                    handlePost(exchange);
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
                Optional<Task> task = taskManager.findTaskById(id);
                if (task.isPresent()) {
                    sendText(exchange, gson.toJson(task.get()));
                } else {
                    sendNotFound(exchange, "Task with id " + id + " not found");
                }
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid task id " + pathParts[2]);
            }
        } else {
            throw new IllegalArgumentException("Path not recognized: " + exchange.getRequestURI().getPath());
        }
    }

    private void handlePost(HttpExchange exchange) throws IOException, IllegalArgumentException {
        InputStream inputStream = exchange.getRequestBody();
        String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        try {
            Task rawTask = gson.fromJson(body, Task.class);
            Task task = getTask(rawTask);
            if (rawTask.getId() <= 0) {
                taskManager.createTask(task);    
            } else {
                task.setId(rawTask.getId());
                taskManager.updateTask(task);
            }
            sendCreated(exchange);
        } catch (JsonSyntaxException e) {
            sendError(exchange, "Invalid task json");
        } catch (OverlapException e) {
            sendHasOverlaps(exchange, "Task overlaps with existing task");
        }

    }

    private static Task getTask(Task rawTask) {
        Duration duration = rawTask.getDuration() != null ? rawTask.getDuration() : null;
        LocalDateTime start = null;
        if (duration != null && rawTask.getStartTime().isPresent()) {
            start = rawTask.getStartTime().get();
        }
        Task task;
        if (duration != null && start != null) {
            task = new Task(rawTask.getTitle(), rawTask.getDescription(), rawTask.getStatus(), start, duration);
        } else {
            task = new Task(rawTask.getTitle(), rawTask.getDescription(), rawTask.getStatus());
        }
        return task;
    }

    static class TaskListTypeToken extends TypeToken<List<Task>> {
    }
}
