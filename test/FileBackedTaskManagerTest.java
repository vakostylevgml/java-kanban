import manager.Managers;
import manager.filebacked.FileBackedTaskManager;
import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Order;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileBackedTaskManagerTest {
    static FileBackedTaskManager manager;
    static Path tempFile;

    static {
        try {
            tempFile = Files.createTempFile("temp", "csv");
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    @Test
    @Order(1)
    public void testUserScenarioInitOk() {
        manager = Managers.getFileBacked(tempFile.toString());
        Assertions.assertTrue(manager.findAllTasks().isEmpty());
        Assertions.assertTrue(manager.findAllSubTasks().isEmpty());
        Assertions.assertTrue(manager.findAllEpics().isEmpty());

        Task task = new Task("task", "desc", Status.DONE);
        Epic epic = new Epic("epic", "d");
        manager.createTask(task);
        long epicId = manager.createEpic(epic);
        Subtask subtask = new Subtask("subtask", "de", Status.IN_PROGRESS, epicId);
        manager.createSubtask(subtask);

    }

    @Test
    @Order(2)
    public void testUserScenarioLoadingOk() {
        manager = Managers.getFileBacked(tempFile.toString());

        Assertions.assertTrue(manager.findAllTasks().isEmpty());
        Assertions.assertTrue(manager.findAllSubTasks().isEmpty());
        Assertions.assertTrue(manager.findAllEpics().isEmpty());

        manager.loadFromFile(tempFile.toFile());

        Assertions.assertEquals(1, manager.findAllTasks().size());
        Assertions.assertEquals(1, manager.findAllEpics().size());
        Assertions.assertEquals(1, manager.findAllSubTasks().size());

    }
}
