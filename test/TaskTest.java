import model.Status;
import model.Task;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

class TaskTest {

    @Test
    void testEqualsIsCorrect() {
        Task task1 = new Task("1", "2", Status.NEW);
        Task task2 = new Task("2", "5", Status.DONE);
        task1.setId(1);
        task2.setId(1);
        Assertions.assertEquals(task1, task2);
    }

    @Test
    void testTimeAndDurationIsAddedOk() {
        LocalDateTime start = LocalDateTime.of(2025,7,8, 20,0);
        Duration duration = Duration.ofHours(7);
        Task task1 = new Task("task 1", "desc", Status.DONE,
                start, duration);
        Assertions.assertTrue(task1.getEndTime().isPresent());
        Assertions.assertEquals(task1.getEndTime().get(), start.plus(duration));
    }

    @Test
    void testTimeAndDurationIsNotAddedAndWorksAsRequired() {
        Task task1 = new Task("task 1", "desc", Status.DONE);
        Assertions.assertTrue(task1.getEndTime().isEmpty());
        Assertions.assertTrue(task1.getStartTime().isEmpty());
        Assertions.assertEquals(Duration.ZERO, task1.getDuration());
    }
}