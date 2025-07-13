import manager.Managers;
import manager.inmemory.InMemoryTaskManager;
import model.Status;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

public class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    @Override
    protected InMemoryTaskManager createTaskManager() {
        return new InMemoryTaskManager(Managers.getDefaultHistory());
    }

    @Test
    public void testOverlappingTasksNotOverlapping() {
        LocalDateTime startTask1 = LocalDateTime.of(2025,7,8, 20,0);
        Duration durationTask1 = Duration.ofMinutes(30);
        LocalDateTime startTask2 = LocalDateTime.of(2025,7,9, 21,0);
        Duration durationTask2 = Duration.ofMinutes(60);
        Task task1 = new Task("task1", "de", Status.DONE, startTask1, durationTask1);
        Subtask subtask2 = new Subtask("subtask1", "de", Status.DONE, 888L,
                startTask2, durationTask2);
        Assertions.assertFalse(manager.isOverlapping(task1, subtask2));
    }

    @Test
    public void testOverlappingTasksAreOverlapping() {
        LocalDateTime startTask1 = LocalDateTime.of(2025,7,8, 20,0);
        Duration durationTask1 = Duration.ofMinutes(60);
        LocalDateTime startTask2 = LocalDateTime.of(2025,7,8, 21,0);
        Duration durationTask2 = Duration.ofMinutes(60);
        Task task1 = new Task("task1", "de", Status.DONE, startTask1, durationTask1);
        Subtask subtask2 = new Subtask("subtask1", "de", Status.DONE, 888L,
                startTask2, durationTask2);
        Assertions.assertTrue(manager.isOverlapping(task1, subtask2));
    }
}
