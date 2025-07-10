import manager.filebacked.TaskSerializer;
import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;
import org.junit.Assert;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;

import java.time.Duration;
import java.time.LocalDateTime;

public class TaskSerializerTest {

    @Test
    public void testTaskSerializer() {
        Task created =  new Task("title", "desc", Status.NEW);
        created.setId(1);
        String createdString = TaskSerializer.serrializeToString(created);
        Task restored =  TaskSerializer.deSerializeTaskFromString(createdString);
        Assert.assertEquals(created.getId(), restored.getId());
        Assert.assertEquals(created.getTitle(), restored.getTitle());
        Assert.assertEquals(created.getDescription(), restored.getDescription());
        Assert.assertEquals(created.getStatus(), restored.getStatus());
    }

    @Test
    public void testSubTaskSerializer() {
        Subtask created =  new Subtask("title", "desc", Status.NEW, 888);
        created.setId(1);
        String createdString = TaskSerializer.serrializeToString(created);
        Subtask restored =  (Subtask) TaskSerializer.deSerializeTaskFromString(createdString);
        Assert.assertEquals(created.getId(), restored.getId());
        Assert.assertEquals(created.getTitle(), restored.getTitle());
        Assert.assertEquals(created.getDescription(), restored.getDescription());
        Assert.assertEquals(created.getStatus(), restored.getStatus());
        Assert.assertEquals(created.getEpicId(), restored.getEpicId());

    }

    @Test
    public void testEpicSerializer() {
        Epic created =  new Epic("title", "desc");
        created.setId(1);
        String createdString = TaskSerializer.serrializeToString(created);
        Epic restored =  (Epic) TaskSerializer.deSerializeTaskFromString(createdString);
        Assert.assertEquals(created.getId(), restored.getId());
        Assert.assertEquals(created.getTitle(), restored.getTitle());
        Assert.assertEquals(created.getDescription(), restored.getDescription());
        Assert.assertEquals(created.getStatus(), restored.getStatus());

    }

    @Test
    public void testExceptionIfEmpty() {
        Assert.assertThrows(IllegalArgumentException.class, () -> TaskSerializer.deSerializeTaskFromString(""));
    }

    @Test
    public void testExceptionIfNotEnough() {
        Assert.assertThrows(IllegalArgumentException.class, () -> TaskSerializer.deSerializeTaskFromString("a,b"));
    }

    @Test
    public void testExceptionIfNotCorrectId() {
        Assert.assertThrows(IllegalArgumentException.class, () -> TaskSerializer.deSerializeTaskFromString("a,b,c,d,e"));
    }

    @Test
    public void testOkWithStartAndDuration() {
        LocalDateTime start = LocalDateTime.of(2025,7,8, 20,0);
        Duration duration = Duration.ofMinutes(30);

        Task created =  new Task("title", "desc", Status.NEW, start, duration);
        created.setId(1);
        String createdString = TaskSerializer.serrializeToString(created);
        Task restored =  TaskSerializer.deSerializeTaskFromString(createdString);
        Assert.assertTrue(restored.getStartTime().isPresent());
        Assert.assertTrue(restored.getEndTime().isPresent());
        Assertions.assertTrue(restored.getDuration().isPositive());

        Assert.assertEquals(created.getStartTime().get(), restored.getStartTime().get());
        Assert.assertEquals(created.getEndTime().get(), restored.getEndTime().get());
        Assert.assertEquals(created.getDuration(), restored.getDuration());

    }
}
