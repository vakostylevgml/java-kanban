package model;

import manager.Managers;
import manager.TaskManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class HistoryManagerTest {
    static TaskManager manager;

    @BeforeAll
    static void setUpBeforeClass() {
        manager = Managers.getDefault();
    }

    @Test
    void taskIsAddedToHistory() {
        Task task = new Task("a", "b", Status.NEW);
        long expectedId = manager.create(task);
        manager.findTaskById(expectedId);
        Assertions.assertEquals(expectedId, manager.getHistoryManager().getHistory().getFirst().getId());
    }
}
