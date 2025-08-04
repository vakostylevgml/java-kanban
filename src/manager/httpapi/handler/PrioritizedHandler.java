package manager.httpapi.handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import manager.TaskManager;

import java.io.IOException;

public class PrioritizedHandler extends BaseHttpHandler {

    public PrioritizedHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }

    @Override
    protected void handleGet(HttpExchange exchange) throws IOException {
        sendText(exchange, gson.toJson(taskManager.getPrioritizedTasks()));
    }

    @Override
    protected void handlePost(HttpExchange exchange) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected void handleDelete(HttpExchange exchange) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
