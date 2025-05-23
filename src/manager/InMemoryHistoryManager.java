package manager;

import model.Task;
import java.util.LinkedList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private static final int HISTORY_SIZE = 10;
    private final List<Task> vivewHistoryList;

    public InMemoryHistoryManager() {
        vivewHistoryList = new LinkedList<>();
    }

    @Override
    public void add(Task task) {
        if (vivewHistoryList.size() == HISTORY_SIZE) {
            vivewHistoryList.removeFirst();
        }
        vivewHistoryList.add(task);
    }

    @Override
    public List<Task> getHistory() {
        return vivewHistoryList;
    }

}

