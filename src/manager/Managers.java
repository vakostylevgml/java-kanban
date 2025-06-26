package manager;

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
}
