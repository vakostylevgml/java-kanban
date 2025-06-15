import manager.Managers;
import manager.TaskManager;
import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

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

    @Test
    void testUserScenario() {
        Task task1 = new Task("task 1", "desc", Status.DONE);
        Task task2 = new Task("task 2", "desc", Status.NEW);
        Epic epic1 = new Epic("epic with subt", "d");
        Epic epic2 = new Epic("epic no subt", "d");
        long taskId1 = manager.createTask(task1);
        long taskId2 = manager.createTask(task2);
        long epicId1 = manager.createEpic(epic1);
        long epicId2 = manager.createEpic(epic2);
        Subtask subtask1 = new Subtask("subtask 1", "de", Status.DONE, epicId1);
        Subtask subtask2 = new Subtask("subtask 2", "de", Status.IN_PROGRESS, epicId1);
        Subtask subtask3 = new Subtask("subtask 3", "de", Status.NEW, epicId1);
        long subtaskId1 = manager.createSubtask(subtask1);
        long subtaskId2 = manager.createSubtask(subtask2);
        long subtaskId3 = manager.createSubtask(subtask3);

        List<Task> history = manager.getHistoryManager().getHistory();
        Assertions.assertEquals(0, history.size());

        manager.findTaskById(taskId1);
        manager.findTaskById(taskId2);
        manager.findSubTaskById(subtaskId1);
        manager.findSubTaskById(subtaskId2);
        manager.findSubTaskById(subtaskId3);
        manager.findEpicById(epicId1);
        manager.findEpicById(epicId2);
        history = manager.getHistoryManager().getHistory();
        Assertions.assertEquals(7, history.size());

        manager.findTaskById(taskId2);
        manager.findSubTaskById(subtaskId3);
        manager.findEpicById(epicId2);
        history = manager.getHistoryManager().getHistory();
        Assertions.assertEquals(7, history.size());

        manager.deleteEpicById(epicId1);
        history = manager.getHistoryManager().getHistory();
        Assertions.assertEquals(3, history.size());


    }
}
