package manager.httpapi.handler;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import manager.inmemory.InMemoryTaskManager;
import manager.inmemory.OverlapException;
import model.Subtask;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

public class SubtaskHandler extends BaseHttpHandler implements HttpHandler {
    public SubtaskHandler(InMemoryTaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }

    private static Subtask getSubTask(Subtask rawTask) {
        Duration duration = rawTask.getDuration() != null ? rawTask.getDuration() : null;
        LocalDateTime start = null;
        if (duration != null && rawTask.getStartTime().isPresent()) {
            start = rawTask.getStartTime().get();
        }
        Subtask task;
        if (duration != null && start != null) {
            task = new Subtask(rawTask.getTitle(), rawTask.getDescription(), rawTask.getStatus(),
                    rawTask.getEpicId(), start, duration);
        } else {
            task = new Subtask(rawTask.getTitle(), rawTask.getDescription(), rawTask.getStatus(),
                    rawTask.getEpicId());
        }
        return task;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException, UnsupportedOperationException {
        String method = exchange.getRequestMethod();
        switch (method) {
            case "GET":
                handleGet(exchange);
                break;
            case "POST":
                handlePost(exchange);
                break;
            case "DELETE":
                handleDelete(exchange);
            default:
                throw new UnsupportedOperationException(method + " method not supported");
        }

    }

    private void handleGet(HttpExchange exchange) throws IOException {
        String[] pathParts = exchange.getRequestURI().getPath().split("/");
        if (pathParts.length == 2) {
            String tasks = gson.toJson(taskManager.findAllSubTasks());
            sendText(exchange, tasks);
        } else if (pathParts.length == 3) {
            try {
                int id = Integer.parseInt(pathParts[2]);
                Optional<Subtask> task = taskManager.findSubTaskById(id);
                if (task.isPresent()) {
                    sendText(exchange, gson.toJson(task.get()));
                } else {
                    sendNotFound(exchange, "Subtask with id " + id + " not found");
                }
            } catch (NumberFormatException e) {
                sendError(exchange, "Invalid subtask id " + pathParts[2]);
            }
        } else {
            sendError(exchange, "Path not recognized: " + exchange.getRequestURI().getPath());
        }
    }

    private void handlePost(HttpExchange exchange) throws IOException {
        InputStream inputStream = exchange.getRequestBody();
        String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);

        try {
            Subtask rawTask = gson.fromJson(body, Subtask.class);
            Subtask task = getSubTask(rawTask);
            if (rawTask.getId() <= 0) {
                taskManager.createSubtask(task);
            } else {
                task.setId(rawTask.getId());
                taskManager.updateSubtask(task);
            }
            sendCreated(exchange);
        } catch (OverlapException e) {
            sendHasOverlaps(exchange);
        } catch (JsonSyntaxException e) {
            sendError(exchange, "Invalid subtask json");
        }
    }

    private void handleDelete(HttpExchange exchange) throws IOException {
        String[] pathParts = exchange.getRequestURI().getPath().split("/");
        if (pathParts.length == 3) {
            try {
                int id = Integer.parseInt(pathParts[2]);
                Optional<Subtask> task = taskManager.findSubTaskById(id);
                task.ifPresent(value -> taskManager.deleteSubtaskById(value.getId()));
                sendText(exchange, "SubTask with id " + id + " deleted");
            } catch (NumberFormatException e) {
                sendError(exchange, "Invalid subtask id " + pathParts[2]);
            }
        } else {
            sendError(exchange, "Path not recognized: " + exchange.getRequestURI().getPath());
        }
    }

}

