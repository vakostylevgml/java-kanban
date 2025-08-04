package manager.httpapi.handler;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import manager.TaskManager;
import model.Epic;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class EpicHandler extends BaseHttpHandler {
    public EpicHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }

    @Override
    protected void handleGet(HttpExchange exchange) throws IOException {
        String[] pathParts = exchange.getRequestURI().getPath().split("/");
        switch (pathParts.length) {
            case 2:
                String tasks = gson.toJson(taskManager.findAllEpics());
                sendText(exchange, tasks);
                break;
            case 3:
                try {
                    int id = Integer.parseInt(pathParts[2]);
                    Optional<Epic> task = taskManager.findEpicById(id);
                    if (task.isPresent()) {
                        sendText(exchange, gson.toJson(task.get()));
                    } else {
                        sendNotFound(exchange, "Epic with id " + id + " not found");
                    }
                } catch (NumberFormatException e) {
                    sendError(exchange, "Invalid epic id " + pathParts[2]);
                }
                break;
            case 4:
                if (pathParts[3].equals("subtasks")) {
                    int id = Integer.parseInt(pathParts[2]);
                    Optional<Epic> task = taskManager.findEpicById(id);
                    if (task.isPresent()) {
                        sendText(exchange, gson.toJson(taskManager.findAllSubtasksByEpicId(task.get().getId())));
                    } else {
                        sendNotFound(exchange, "Epic with id " + id + " not found");
                    }
                } else {
                    sendError(exchange, "Path not recognized: " + exchange.getRequestURI().getPath());
                }
                break;
            default:
                sendError(exchange, "Path not recognized: " + exchange.getRequestURI().getPath());
        }
    }

    @Override
    protected void handlePost(HttpExchange exchange) throws IOException {
        InputStream inputStream = exchange.getRequestBody();
        String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        try {
            Epic rawTask = gson.fromJson(body, Epic.class);
            Epic epic = new Epic(rawTask.getTitle(), rawTask.getDescription());
            taskManager.createEpic(epic);
            sendCreated(exchange);
        } catch (JsonSyntaxException e) {
            sendError(exchange, "Invalid epic json");
        }
    }

    @Override
    protected void handleDelete(HttpExchange exchange) throws IOException {
        String[] pathParts = exchange.getRequestURI().getPath().split("/");
        if (pathParts.length == 3) {
            try {
                int id = Integer.parseInt(pathParts[2]);
                Optional<Epic> task = taskManager.findEpicById(id);
                task.ifPresent(value -> taskManager.deleteEpicById(value.getId()));
                sendText(exchange, "Epic with id " + id + " deleted");
            } catch (NumberFormatException e) {
                sendError(exchange, "Invalid epic id " + pathParts[2]);
            }
        } else {
            sendError(exchange, "Path not recognized: " + exchange.getRequestURI().getPath());
        }
    }
}
