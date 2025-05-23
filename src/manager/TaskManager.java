package manager;

import model.Epic;
import model.Subtask;
import model.Task;

import java.util.Map;

public interface TaskManager {
    Map<Long, Task> findAllTasks();

    Map<Long, Subtask> findAllSubTasks();

    Map<Long, Epic> findAllEpics();

    Task findTaskById(long id);

    Subtask findSubTaskById(long id);

    Epic findEpicById(long id);

    Map<Long, Subtask> findAllSubtasksByEpic(Epic epic);

    Map<Long, Subtask> findAllSubtasksByEpicId(long epicId);

    long create(Task task);

    long create(Subtask subtask);

    long create(Epic epic);

    Task update(Task task);

    Subtask update(Subtask subtask);

    Epic update(Epic epic);

    void deleteTaskById(long id);

    void deleteSubtaskById(long id);

    void deleteEpicById(long id);

    HistoryManager getHistoryManager();
}
