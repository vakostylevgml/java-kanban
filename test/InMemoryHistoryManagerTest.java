import manager.Managers;
import manager.TaskManager;
import model.Status;
import model.Task;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

public class InMemoryHistoryManagerTest {
    static TaskManager manager;

    @BeforeEach
    void beforeEach() {
        manager = Managers.getDefault();
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
        System.out.println("Actual" + Arrays.toString(actualIds));
        System.out.println("Expected" + Arrays.toString(expectedIds));
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
        System.out.println("Actual" + Arrays.toString(history.toArray()));
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


    }


}
