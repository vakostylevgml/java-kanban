package manager.filebacked;

import manager.HistoryManager;
import manager.inmemory.InMemoryTaskManager;
import manager.TaskManager;

import java.io.File;

public class FileBackedTaskManager extends InMemoryTaskManager implements TaskManager {
    private static String FILENAME;

    public FileBackedTaskManager(HistoryManager historyManager, String fileName) {
        super(historyManager);
        FILENAME = fileName;
    }

    public static void loadFromFile(File file) {

    }

    private void save() {

    }


}
