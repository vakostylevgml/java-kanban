package manager;

import model.Task;

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
