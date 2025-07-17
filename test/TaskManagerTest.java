import manager.inmemory.OverlapException;
import manager.TaskManager;
import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class TaskManagerTest<T extends TaskManager> {
    protected T manager;

    @BeforeEach
    void setUp() {
        manager = createTaskManager();
    }

    protected abstract T createTaskManager();

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

    @Test
    void historyIsEmptyByDefault() {
        Assertions.assertEquals(0, manager.getHistoryManager().getHistory().size());
    }

    @Test
    void taskIsAddedToHistory() {
        Task task = new Task("a", "b", Status.NEW);
        long expectedId = manager.createTask(task);
        manager.findTaskById(expectedId);
        Assertions.assertEquals(expectedId, manager.getHistoryManager().getHistory().getFirst().getId());
    }

    @Test
    void tenTasksAreAddedToHistory() {
        Long[] expectedIds = new Long[10];
        for (int i = 0; i < 10; i++) {
            Task task = new Task("a" + i, "b", Status.NEW);
            long expectedId = manager.createTask(task);
            manager.findTaskById(expectedId);
            expectedIds[i] = expectedId;
        }
        Long[] actualIds = manager.getHistoryManager().getHistory()
                .stream()
                .map(Task::getId)
                .toArray(Long[]::new);
        Assertions.assertArrayEquals(expectedIds, actualIds);
    }

    @Test
    void oneTaskIsAddedToHistoryNoDuplicatesUpdatedOk() {
        Task task = new Task("a", "b", Status.NEW);
        long expectedId = manager.createTask(task);

        List<Task> history = manager.getHistoryManager().getHistory();
        Assertions.assertEquals(0, history.size());
        manager.findTaskById(expectedId);
        history = manager.getHistoryManager().getHistory();
        Assertions.assertEquals(1, history.size());
        Assertions.assertEquals(expectedId, manager.getHistoryManager().getHistory().getFirst().getId());

        Task updated = new Task("a upd", "b", Status.DONE);
        updated.setId(expectedId);
        manager.updateTask(updated);
        manager.findTaskById(expectedId);
        history = manager.getHistoryManager().getHistory();
        Assertions.assertEquals(1, history.size());
        Assertions.assertEquals(expectedId, manager.getHistoryManager().getHistory().getFirst().getId());
        Assertions.assertEquals(updated.getTitle(), manager.getHistoryManager().getHistory().getFirst().getTitle());
    }

    @Test
    void oneTaskInHistoryDeleted() {
        Task task = new Task("a", "b", Status.NEW);
        long expectedId = manager.createTask(task);
        manager.findTaskById(expectedId);
        List<Task> history = manager.getHistoryManager().getHistory();
        Assertions.assertEquals(1, history.size());
        manager.deleteTaskById(expectedId);
        history = manager.getHistoryManager().getHistory();
        Assertions.assertEquals(0, history.size());
    }

    @Test
    void twoTasksInHistoryOneDeletedFromHead() {
        Task task1 = new Task("a", "b", Status.NEW);
        Task task2 = new Task("a", "b", Status.NEW);

        long expectedId1 = manager.createTask(task1);
        manager.findTaskById(expectedId1);
        long expectedId2 = manager.createTask(task2);
        manager.findTaskById(expectedId2);

        List<Task> history = manager.getHistoryManager().getHistory();
        Assertions.assertEquals(2, history.size());

        manager.deleteTaskById(expectedId1);
        history = manager.getHistoryManager().getHistory();
        Assertions.assertEquals(1, history.size());
        Assertions.assertEquals(expectedId2, manager.getHistoryManager().getHistory().getFirst().getId());
        manager.deleteTaskById(expectedId2);
        history = manager.getHistoryManager().getHistory();
        Assertions.assertEquals(0, history.size());
    }

    @Test
    void twoTasksInHistoryDeletedFromTail() {
        Task task1 = new Task("a", "b", Status.NEW);
        Task task2 = new Task("a3", "b3", Status.NEW);

        long expectedId1 = manager.createTask(task1);
        manager.findTaskById(expectedId1);
        long expectedId2 = manager.createTask(task2);
        manager.findTaskById(expectedId2);

        List<Task> history = manager.getHistoryManager().getHistory();
        Assertions.assertEquals(2, history.size());
        manager.deleteTaskById(expectedId2);
        history = manager.getHistoryManager().getHistory();
        Assertions.assertEquals(1, history.size());

        Assertions.assertEquals(expectedId1, manager.getHistoryManager().getHistory().getFirst().getId());
        manager.deleteTaskById(expectedId1);
        history = manager.getHistoryManager().getHistory();
        Assertions.assertEquals(0, history.size());
    }

    @Test
    void threeTasksInHistoryDeletedFromMiddle() {
        Task task1 = new Task("a", "b", Status.NEW);
        Task task2 = new Task("a2", "b2", Status.NEW);
        Task task3 = new Task("a3", "b3", Status.NEW);

        long expectedId1 = manager.createTask(task1);
        manager.findTaskById(expectedId1);
        long expectedId2 = manager.createTask(task2);
        manager.findTaskById(expectedId2);
        long expectedId3 = manager.createTask(task3);
        manager.findTaskById(expectedId3);

        List<Task> history = manager.getHistoryManager().getHistory();
        Assertions.assertEquals(3, history.size());
        manager.deleteTaskById(expectedId2);
        history = manager.getHistoryManager().getHistory();
        Assertions.assertEquals(2, history.size());

        manager.deleteTaskById(expectedId1);
        history = manager.getHistoryManager().getHistory();
        Assertions.assertEquals(1, history.size());

    }

    @Test
    void priorityTestOk() {
        LocalDateTime startTime1 = LocalDateTime.of(2025,7,8, 20,0);
        LocalDateTime startTime2 = LocalDateTime.of(2025,7,9, 21,0);
        LocalDateTime startTime3 = LocalDateTime.of(2025,9,9, 21,0);
        Epic epic = new Epic("epic", "de");
        long epicId = manager.createEpic(epic);
        Task task1 = new Task("a", "b", Status.NEW, startTime3, Duration.ofMinutes(30));
        Subtask task2 = new Subtask("a", "b", Status.NEW, epicId, startTime1, Duration.ofMinutes(30));
        Task task3 = new Task("a", "b", Status.NEW, startTime2, Duration.ofMinutes(30));
        manager.createTask(task1);
        manager.createTask(task2);
        manager.createTask(task3);
        Set<Long> tasksExpectedIds = new HashSet<>(Arrays.asList(task2.getId(), task3.getId(), task1.getId()));
        Set<Long> tasksActualIds = manager.getPrioritizedTasks().stream().map(Task::getId).collect(Collectors.toSet());
        Assertions.assertEquals(tasksExpectedIds, tasksActualIds);
        LocalDateTime startTime3changed = LocalDateTime.of(2025,1,8, 20,0);
        Task task1changed = new Task("a", "b", Status.NEW, startTime3changed, Duration.ofMinutes(30));
        task1changed.setId(task1.getId());
        manager.updateTask(task1changed);
        Set<Long> tasksExpectedIds2 = new HashSet<>(Arrays.asList(task1.getId(), task3.getId(), task2.getId()));
        Set<Long> tasksActualIds2 = manager.getPrioritizedTasks().stream().map(Task::getId).collect(Collectors.toSet());
        Assertions.assertEquals(tasksExpectedIds2, tasksActualIds2);
    }

    @Test
    public void overlappingTasksTestOk() {
        LocalDateTime startTask1 = LocalDateTime.of(2025,7,8, 20,0);
        Duration durationTask1 = Duration.ofMinutes(30);
        LocalDateTime startTask2 = LocalDateTime.of(2025,7,8, 21,0);
        Duration durationTask2 = Duration.ofMinutes(60);
        Task task1 = new Task("task1", "de", Status.DONE, startTask1, durationTask1);
        Epic epic = new Epic("e", "d");
        long epicid = manager.createEpic(epic);
        Subtask subtask2 = new Subtask("subtask1", "de", Status.DONE, epicid,
                startTask2, durationTask2);
        long taskId = manager.createTask(task1);
        long subtaskId = manager.createSubtask(subtask2);
        Assertions.assertEquals(taskId, manager.findTaskById(taskId).get().getId());
        Assertions.assertEquals(subtaskId, manager.findSubTaskById(subtaskId).get().getId());
    }

    @Test
    public void overlappingTasksTestThrowsException() {
        LocalDateTime startTask1 = LocalDateTime.of(2025,12,8, 20,0);
        Duration durationTask1 = Duration.ofMinutes(120);
        LocalDateTime startTask2 = LocalDateTime.of(2025,12,8, 21,0);
        Duration durationTask2 = Duration.ofMinutes(60);
        Task task1 = new Task("task1", "de", Status.DONE, startTask1, durationTask1);
        Epic epic = new Epic("e", "d");
        long epicid = manager.createEpic(epic);
        Subtask subtask2 = new Subtask("subtask1", "de", Status.DONE, epicid,
                startTask2, durationTask2);
        long taskId = manager.createTask(task1);
        Assertions.assertEquals(taskId, manager.findTaskById(taskId).get().getId());
        Assertions.assertThrows(OverlapException.class, () -> manager.createSubtask(subtask2));
    }

}
