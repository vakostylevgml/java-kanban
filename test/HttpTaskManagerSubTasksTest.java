import com.google.gson.Gson;
import manager.Managers;
import manager.httpapi.HttpTaskServer;
import manager.inmemory.InMemoryTaskManager;
import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


public class HttpTaskManagerSubTasksTest {
    InMemoryTaskManager manager = new InMemoryTaskManager(Managers.getDefaultHistory());
    HttpTaskServer taskServer = new HttpTaskServer(manager);
    Gson gson = HttpTaskServer.getGson();
    Epic epic;

    public HttpTaskManagerSubTasksTest() {
    }

    @BeforeEach
    public void setUp() throws IOException {
        taskServer.startServer();
        Epic epic1 = new Epic("name", "desc");
        long id = manager.createEpic(epic1);
        Assertions.assertTrue(manager.findEpicById(id).isPresent());
        epic = manager.findEpicById(id).get();
    }

    @AfterEach
    public void shutDown() {
        HttpTaskServer.stopServer();
    }

    @Test
    public void testAddSubTask() throws IOException, InterruptedException {
        Subtask task = new Subtask("Test 2", "Testing task 2",
                Status.NEW, epic.getId(), LocalDateTime.now(), Duration.ofMinutes(5));
        String taskJson = gson.toJson(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());

        List<Subtask> tasksFromManager = manager.findAllSubTasks();

        assertNotNull(tasksFromManager);
        assertEquals(1, tasksFromManager.size());
        assertEquals("Test 2", tasksFromManager.get(0).getTitle());
    }

    @Test
    public void testDeleteSubTask() throws IOException, InterruptedException {
        Subtask task = new Subtask("Test 2", "Testing task 2",
                Status.NEW, epic.getId(), LocalDateTime.now(), Duration.ofMinutes(5));
        String taskJson = gson.toJson(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());

        List<Subtask> tasksFromManager = manager.findAllSubTasks();

        assertNotNull(tasksFromManager);
        assertEquals(1, tasksFromManager.size());
        assertEquals("Test 2", tasksFromManager.get(0).getTitle());

        URI url1 = URI.create("http://localhost:8080/subtasks/" + tasksFromManager.get(0).getId());
        HttpRequest request1 = HttpRequest.newBuilder().uri(url1).DELETE().build();
        HttpResponse<String> response1 = client.send(request1, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response1.statusCode());
        assertEquals(0, manager.findAllTasks().size());
    }

    @Test
    public void testOverlapTask() throws IOException, InterruptedException {
        Subtask task = new Subtask("Test 2", "Testing task 2",
                Status.NEW, epic.getId(), LocalDateTime.now(), Duration.ofMinutes(30));
        Subtask task1 = new Subtask("Test 2", "Testing task 2",
                Status.NEW, epic.getId(), LocalDateTime.now(), Duration.ofMinutes(30));
        String taskJson = gson.toJson(task);
        String taskJson1 = gson.toJson(task1);


        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());

        HttpRequest request1 = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson1)).build();
        HttpResponse<String> response1 = client.send(request1, HttpResponse.BodyHandlers.ofString());
        assertEquals(406, response1.statusCode());

    }

    @Test
    public void testGetTask() throws IOException, InterruptedException {
        Subtask task = new Subtask("Test 2", "Testing task 2",
                Status.NEW, epic.getId(), LocalDateTime.now(), Duration.ofMinutes(30));
        String taskJson = gson.toJson(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());

        List<Subtask> tasksFromManager = manager.findAllSubTasks();
        long id = tasksFromManager.get(0).getId();

        URI url1 = URI.create("http://localhost:8080/subtasks/" + id);
        HttpRequest request1 = HttpRequest.newBuilder().uri(url1).GET().build();
        HttpResponse<String> response1 = client.send(request1, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response1.statusCode());
    }

    @Test
    public void testGetTaskWithInvalidId() throws IOException, InterruptedException {
        Subtask task = new Subtask("Test 2", "Testing task 2",
                Status.NEW, epic.getId(), LocalDateTime.now(), Duration.ofMinutes(30));
        String taskJson = gson.toJson(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());

        List<Subtask> tasksFromManager = manager.findAllSubTasks();
        long id = tasksFromManager.get(0).getId();

        URI url1 = URI.create("http://localhost:8080/subtasks/a");
        HttpRequest request1 = HttpRequest.newBuilder().uri(url1).GET().build();
        HttpResponse<String> response1 = client.send(request1, HttpResponse.BodyHandlers.ofString());
        assertEquals(400, response1.statusCode());
    }

    @Test
    public void testGetUnexistingTask() throws IOException, InterruptedException {
        Subtask task = new Subtask("Test 2", "Testing task 2",
                Status.NEW, epic.getId(), LocalDateTime.now(), Duration.ofMinutes(30));
        String taskJson = gson.toJson(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());

        List<Subtask> tasksFromManager = manager.findAllSubTasks();
        long id = tasksFromManager.get(0).getId();

        URI url1 = URI.create("http://localhost:8080/subtasks/3");
        HttpRequest request1 = HttpRequest.newBuilder().uri(url1).GET().build();
        HttpResponse<String> response1 = client.send(request1, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response1.statusCode());
    }
}