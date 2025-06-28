import manager.filebacked.TaskSerializer;
import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;
import org.junit.Assert;
import org.junit.Test;

public class TaskSerializerTest {

    @Test
    public void testTaskSerializer() {
        Task created =  new Task("title", "desc", Status.NEW);
        created.setId(1);
        String createdString = TaskSerializer.serrializeToString(created);
        Task restored =  TaskSerializer.serializeTaskFromString(createdString);
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
        Subtask restored =  (Subtask) TaskSerializer.serializeTaskFromString(createdString);
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
        Epic restored =  (Epic) TaskSerializer.serializeTaskFromString(createdString);
        Assert.assertEquals(created.getId(), restored.getId());
        Assert.assertEquals(created.getTitle(), restored.getTitle());
        Assert.assertEquals(created.getDescription(), restored.getDescription());
        Assert.assertEquals(created.getStatus(), restored.getStatus());

    }

    @Test
    public void testExceptionIfEmpty() {
        Assert.assertThrows(IllegalArgumentException.class, () -> TaskSerializer.serializeTaskFromString(""));
    }

    @Test
    public void testExceptionIfNotEnough() {
        Assert.assertThrows(IllegalArgumentException.class, () -> TaskSerializer.serializeTaskFromString("a,b"));
    }

    @Test
    public void testExceptionIfNotCorrectId() {
        Assert.assertThrows(IllegalArgumentException.class, () -> TaskSerializer.serializeTaskFromString("a,b,c,d,e"));
    }
}
