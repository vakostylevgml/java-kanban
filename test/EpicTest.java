import model.Epic;
import model.Status;
import model.Subtask;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

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
}
