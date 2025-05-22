package model;

import manager.Managers;
import manager.TaskManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class HistoryManagerTest {
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
        long expectedId = manager.create(task);
        manager.findTaskById(expectedId);
        Assertions.assertEquals(expectedId, manager.getHistoryManager().getHistory().getFirst().getId());
    }

    @Test
    void tenTasksAreAddedToHistory() {
        Long[] expectedIds = new Long[10];
        for (int i = 0; i < 10; i++) {
            Task task = new Task("a" + i, "b", Status.NEW);
            long expectedId = manager.create(task);
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
            long expectedId = manager.create(task);
            manager.findTaskById(expectedId);
        }
        Assertions.assertEquals(10, manager.getHistoryManager().getHistory().size());
    }
}
