import manager.Managers;
import manager.TaskManager;
import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class InMemoryTaskManagerTest {
    static TaskManager manager;

    @BeforeEach
    void beforeEach() {
        manager = Managers.getDefault();
    }

    @Test
    void testTasksAreAddedAndFoundOk() {
        Task task = new Task("task", "desc", Status.DONE);
        Epic epic = new Epic("epic", "d");
        long taskId = manager.createTask(task);
        long epicId = manager.createEpic(epic);
        Subtask subtask = new Subtask("subtask", "de", Status.DONE, epicId);
        long subtaskId = manager.createSubtask(subtask);

        Assertions.assertEquals(1, manager.findAllTasks().size());
        Assertions.assertEquals(1, manager.findAllSubTasks().size());
        Assertions.assertEquals(1, manager.findAllEpics().size());
        Assertions.assertNotNull(manager.findTaskById(taskId));
        Assertions.assertNotNull(manager.findSubTaskById(subtaskId));
        Assertions.assertNotNull(manager.findEpicById(epicId));
    }
}
