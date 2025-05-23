import manager.Managers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ManagersTest {

    @Test
    void testManagerInitIsOk() {
        Assertions.assertNotNull(Managers.getDefault());
        Assertions.assertNotNull(Managers.getDefault().getHistoryManager());
        Assertions.assertNotNull(Managers.getDefault().findAllTasks());
    }
}
