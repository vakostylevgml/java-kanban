package manager.httpapi.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import manager.TaskManager;

import java.io.IOException;

public class TaskHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager taskManager;

    public TaskHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws UnsupportedOperationException,  IOException {
        String method = exchange.getRequestMethod();
        System.out.println("Method: " + method);
        switch (method) {
            case "GET":
                handleGet(exchange);
                break;
            default:
                throw new UnsupportedOperationException(method + " not supported");
        }
    }

    private void handleGet(HttpExchange exchange) throws IOException {
        String[] pathParts = exchange.getRequestURI().getPath().split("/");
        System.out.println(pathParts.length);
        if (pathParts.length == 2) {
            sendText(exchange, "get all tasks");
        } else {
            int id = Integer.parseInt(pathParts[2]);
            sendText(exchange, "get task with id " + id);
        }
    }
}
