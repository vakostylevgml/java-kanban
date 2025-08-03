package manager.httpapi.handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import manager.TaskManager;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class HistoryHandler extends BaseHttpHandler {
    public HistoryHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }

    @Override
    protected void handleGet(HttpExchange exchange) throws IOException {
        sendText(exchange, gson.toJson(taskManager.getHistoryManager().getHistory()));
    }

    @Override
    protected void handlePost(HttpExchange exchange) throws IOException {
        throw new UnsupportedEncodingException("Post method not supported");
    }

    @Override
    protected void handleDelete(HttpExchange exchange) throws IOException {
        throw new UnsupportedEncodingException("Delete method not supported");
    }
}
