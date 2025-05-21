package manager;

import model.Epic;
import model.Subtask;
import model.Task;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface TaskManager {
    Collection<Task> findAllTasks();

    Collection<Subtask> findAllSubTasks();

    Collection<Epic> findAllEpics();

    Task findTaskById(long id);

    Subtask findSubTaskById(long id);

    Epic findEpicById(long id);

    Set<Subtask> findAllSubtasks(Epic epic);

    Set<Subtask> findAllSubtasksByEpicId(long epicId);

    long create(Task task);

    long create(Subtask subtask);

    long create(Epic epic);

    Task update(Task task);

    Subtask update(Subtask subtask);

    Epic update(Epic epic);

    void deleteTaskById(long id);

    void deleteSubtaskById(long id);

    void deleteEpicById(long id);

    List<Task> getHistory();
}
