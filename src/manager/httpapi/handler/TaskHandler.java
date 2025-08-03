package manager.httpapi.handler;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import manager.TaskManager;
import manager.inmemory.OverlapException;
import model.Task;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

public class TaskHandler extends BaseHttpHandler {
    public TaskHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
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


    protected void handleGet(HttpExchange exchange) throws IOException {
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
                sendError(exchange, "Invalid task id " + pathParts[2]);
            }
        } else {
            sendError(exchange, "Path not recognized: " + exchange.getRequestURI().getPath());
        }
    }

    protected void handlePost(HttpExchange exchange) throws IOException {
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
        } catch (OverlapException e) {
            sendHasOverlaps(exchange);
        } catch (JsonSyntaxException e) {
            sendError(exchange, "Invalid task json");
        }
    }

    protected void handleDelete(HttpExchange exchange) throws IOException {
        String[] pathParts = exchange.getRequestURI().getPath().split("/");
        if (pathParts.length == 3) {
            try {
                int id = Integer.parseInt(pathParts[2]);
                Optional<Task> task = taskManager.findTaskById(id);
                task.ifPresent(value -> taskManager.deleteTaskById(value.getId()));
                sendText(exchange, "Task with id " + id + " deleted");
            } catch (NumberFormatException e) {
                sendError(exchange, "Invalid task id " + pathParts[2]);
            }
        } else {
            sendError(exchange, "Path not recognized: " + exchange.getRequestURI().getPath());
        }
    }

}
