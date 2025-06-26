package manager;

import manager.filebacked.FileBackedTaskManager;
import manager.inmemory.InMemoryHistoryManager;
import manager.inmemory.InMemoryTaskManager;

public class Managers {

    private Managers() {
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static TaskManager getDefault() {
        return new InMemoryTaskManager(getDefaultHistory());
    }

    public static FileBackedTaskManager getFileBacked(String fileName) {
        return new FileBackedTaskManager(getDefaultHistory(), fileName);
    }
}
