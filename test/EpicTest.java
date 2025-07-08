import model.Epic;
import model.Status;
import model.Subtask;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class EpicTest {
    @Test
    void testAddSubtaskIsOk() {
        Epic epic = new Epic("epic", "d");
        Assertions.assertEquals(0, epic.getSubtasks().size());
        Subtask subtask = new Subtask("subtask", "de", Status.DONE, epic.getId());
        epic.addSubtask(subtask);
        Assertions.assertEquals(1, epic.getSubtasks().size());
    }

    @Test
    void testAddDuplicateSubtaskIsNotAllowed() {
        Epic epic = new Epic("epic", "d");
        Subtask subtask = new Subtask("subtask", "de", Status.DONE, epic.getId());
        Subtask subtask2 = new Subtask("subtask", "de", Status.DONE, epic.getId());
        epic.addSubtask(subtask);
        Assertions.assertThrows(IllegalArgumentException.class, () -> epic.addSubtask(subtask2));
    }

    @Test
    void testUpdateIsOk() {
        Epic epic = new Epic("epic", "d");
        Subtask subtask = new Subtask("subtask", "de", Status.DONE, epic.getId());
        Subtask subtask2 = new Subtask("subtask", "de", Status.DONE, epic.getId());
        subtask.setId(1);
        subtask2.setId(2);
        epic.addSubtask(subtask);
        epic.addSubtask(subtask2);
        Assertions.assertEquals(2, epic.getSubtasks().size());
        Subtask subtaskUpdated = new Subtask("subtask", "updated", Status.NEW, epic.getId());
        subtaskUpdated.setId(1);
        epic.updateSubtask(subtaskUpdated);

        List<Subtask> subtasks = epic.getSubtasks();
        Assertions.assertEquals(2, subtasks.size());
        Subtask requested = subtasks.stream().filter(s -> s.getId() == 1).findFirst().get();
        Assertions.assertEquals(subtaskUpdated.getDescription(), requested.getDescription());
        Assertions.assertEquals(subtaskUpdated.getStatus(), requested.getStatus());
    }

    @Test
    void testUpdateUnexistingTaskIsNotAllowed() {
        Epic epic = new Epic("epic", "d");
        Subtask subtask = new Subtask("subtask", "de", Status.DONE, epic.getId());
        Subtask subtask2 = new Subtask("subtask", "de", Status.DONE, epic.getId());
        subtask.setId(1);
        subtask2.setId(2);
        epic.addSubtask(subtask);
        Assertions.assertThrows(IllegalArgumentException.class, () -> epic.updateSubtask(subtask2));

    }

    @Test
    void testEpicStatusIsCorrect() {
        Epic epic = new Epic("epic", "de");
        Assertions.assertEquals(Status.NEW, epic.getStatus());
        Subtask subtask = new Subtask("subtask", "de", Status.DONE, epic.getId());
        subtask.setId(1);
        epic.addSubtask(subtask);
        Assertions.assertEquals(Status.DONE, epic.getStatus());
        Subtask subtaskInProgress = new Subtask("subtask2", "de", Status.IN_PROGRESS, epic.getId());
        subtaskInProgress.setId(2);
        epic.addSubtask(subtaskInProgress);
        Assertions.assertEquals(Status.IN_PROGRESS, epic.getStatus());
    }

    @Test
    void testTimeAndDurationIsAddedOk(){
        LocalDateTime startTask1 = LocalDateTime.of(2025,7,8, 20,0);
        Duration durationTask1 = Duration.ofMinutes(30);
        LocalDateTime startTask2 = LocalDateTime.of(2025,7,9, 21,0);
        Duration durationTask2 = Duration.ofMinutes(60);
        Epic epic = new Epic("epic", "de");
        Assertions.assertTrue(epic.getEndTime().isEmpty());
        Assertions.assertEquals(Duration.ZERO, epic.getDuration());
        Assertions.assertTrue(epic.getStartTime().isEmpty());
        epic.setId(1);

        Subtask subtask1 = new Subtask("subtask1", "de", Status.DONE, epic.getId(),
                startTask1, durationTask1);
        Subtask subtask2 = new Subtask("subtask1", "de", Status.DONE, epic.getId(),
                startTask2, durationTask2);
        subtask1.setId(2);
        subtask2.setId(3);
        epic.addSubtask(subtask1);
        epic.addSubtask(subtask2);

        Assertions.assertEquals(durationTask1.plus(durationTask2), epic.getDuration());
        Assertions.assertEquals(subtask1.getStartTime(), epic.getStartTime());
        Assertions.assertEquals(subtask2.getEndTime(), epic.getEndTime());
    }
}
