package manager;

import model.Epic;
import model.Subtask;
import model.Task;

import java.util.List;
import java.util.Optional;

public interface TaskManager {
    List<Task> findAllTasks();

    List<Subtask> findAllSubTasks();

    List<Epic> findAllEpics();

    Optional<Task> findTaskById(long id);

    Optional<Subtask> findSubTaskById(long id);

    Optional<Epic> findEpicById(long id);

    List<Subtask> findAllSubtasksByEpic(Epic epic);

    List<Subtask> findAllSubtasksByEpicId(long epicId);

    long createTask(Task task);

    long createSubtask(Subtask subtask);

    long createEpic(Epic epic);

    Optional<Task> updateTask(Task task);

    Optional<Subtask> updateSubtask(Subtask subtask);

    Optional<Epic> updateEpic(Epic epic);

    void deleteTaskById(long id);

    void deleteSubtaskById(long id);

    void deleteEpicById(long id);

    HistoryManager getHistoryManager();

    List<Task> getPrioritizedTasks();

    boolean isOverlapWithExisting(Task task);
}
