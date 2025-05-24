package manager;

import model.Epic;
import model.Subtask;
import model.Task;

import java.util.List;

public interface TaskManager {
    List<Task> findAllTasks();

    List<Subtask> findAllSubTasks();

    List<Epic> findAllEpics();

    Task findTaskById(long id);

    Subtask findSubTaskById(long id);

    Epic findEpicById(long id);

    List<Subtask> findAllSubtasksByEpic(Epic epic);

    List<Subtask> findAllSubtasksByEpicId(long epicId);

    long createTask(Task task);

    long createSubtask(Subtask subtask);

    long createEpic(Epic epic);

    Task updateTask(Task task);

    Subtask updateSubtask(Subtask subtask);

    Epic updateEpic(Epic epic);

    void deleteTaskById(long id);

    void deleteSubtaskById(long id);

    void deleteEpicById(long id);

    HistoryManager getHistoryManager();
}
