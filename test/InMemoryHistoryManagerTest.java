import manager.Managers;
import manager.TaskManager;
import model.Status;
import model.Task;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Random;

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
        Assertions.assertArrayEquals(expectedIds, actualIds);
    }

    @Test
    void elevenTasksAreNotAddedToHistory() {
        for (int i = 0; i < 11; i++) {
            Task task = new Task("a" + i, "b", Status.NEW);
            long expectedId = manager.createTask(task);
            manager.findTaskById(expectedId);
        }
        Assertions.assertEquals(10, manager.getHistoryManager().getHistory().size());
    }

    @Test
    void tasksAreOverwrittenInCorrectOrder() {
        Long[] insertedIds = new Long[10];
        for (int i = 0; i < 10; i++) {
            Task task = new Task("a" + i, "b", Status.NEW);
            long expectedId = manager.createTask(task);
            manager.findTaskById(expectedId);
            insertedIds[i] = expectedId;
        }

        Random random = new Random();
        Long[] expectedIds = new Long[10];

        for (int i = 0; i < 10; i++) {
            int randomIndex = random.nextInt(10);
            long randomId = insertedIds[randomIndex];
            manager.findTaskById(randomId);
            expectedIds[i] = randomId;
        }

        Long[] actualIds = manager.getHistoryManager().getHistory()
                .stream()
                .map(Task::getId)
                .toArray(Long[]::new);
        Assertions.assertArrayEquals(expectedIds, actualIds);

        System.out.println("Inserted: " + Arrays.toString(insertedIds));
        System.out.println("Expected: " + Arrays.toString(expectedIds));
        System.out.println("Actual: " + Arrays.toString(actualIds));


    }
}
