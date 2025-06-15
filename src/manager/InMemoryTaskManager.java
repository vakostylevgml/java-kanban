package manager;

import model.*;

import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {
    private long taskId;
    private final Map<Long, Task> tasks;
    private final Map<Long, Subtask> subtasks;
    private final Map<Long, Epic> epics;

    private final HistoryManager historyManager;

    public InMemoryTaskManager(HistoryManager historyManager) {
        tasks = new HashMap<>();
        subtasks = new HashMap<>();
        epics = new HashMap<>();
        this.historyManager = historyManager;
    }

    @Override
    public HistoryManager getHistoryManager() {
        return historyManager;
    }

    @Override
    public List<Task> findAllTasks() {
        return List.copyOf(tasks.values());
    }

    @Override
    public List<Subtask> findAllSubTasks() {
        return List.copyOf(subtasks.values());
    }

    @Override
    public List<Epic> findAllEpics() {
        return List.copyOf(epics.values());
    }

    @Override
    public Task findTaskById(long id) {
        Task task = tasks.get(id);
        if (task != null) {
            historyManager.add(task);
        }
        return task;
    }

    @Override
    public Subtask findSubTaskById(long id) {
        Subtask subtask = subtasks.get(id);
        if (subtask != null) {
            historyManager.add(subtask);
        }
        return subtask;
    }

    @Override
    public Epic findEpicById(long id) {
        Epic epic = epics.get(id);
        if (epic != null) {
            historyManager.add(epic);
        }
        return epic;
    }

    @Override
    public List<Subtask> findAllSubtasksByEpic(Epic epic) {
        if (epic == null) {
            return List.of();
        } else {
            return findAllSubtasksByEpicId(epic.getId());
        }
    }

    @Override
    public List<Subtask> findAllSubtasksByEpicId(long epicId) {
        if (!epics.containsKey(epicId)) {
            return List.of();
        }
        return findEpicById(epicId).getSubtasks();
    }

    @Override
    public long createTask(Task task) {
        if (task == null) {
            return -1;
        }
        task.setId(getTaskId());
        tasks.put(task.getId(), task);
        return task.getId();
    }

    @Override
    public long createSubtask(Subtask subtask) {
        if (subtask == null || !epics.containsKey(subtask.getEpicId())) {
            return -1;
        }

        Epic epic = epics.get(subtask.getEpicId());
        subtask.setId(getTaskId());
        subtasks.put(subtask.getId(), subtask);
        epic.addSubtask(subtask);
        return subtask.getId();
    }

    @Override
    public long createEpic(Epic epic) {
        if (epic == null) {
            return -1;
        }

        epic.setId(getTaskId());
        epics.put(epic.getId(), epic);
        return epic.getId();
    }

    @Override
    public Task updateTask(Task task) {
        if (task == null || !tasks.containsKey(task.getId())) {
            return null;
        }

        return tasks.put(task.getId(), task);
    }

    @Override
    public Subtask updateSubtask(Subtask subtask) {
        if (subtask == null || !subtasks.containsKey(subtask.getId())) {
            return null;
        }

        Subtask oldSubtask = subtasks.get(subtask.getId());
        if (oldSubtask.getEpicId() != subtask.getEpicId()) {
            if (!epics.containsKey(subtask.getEpicId())) {
                return null;
            }
            Epic oldEpic = findEpicById(oldSubtask.getEpicId());
            oldEpic.removeSubtask(subtask);
            Epic newEpic = findEpicById(subtask.getEpicId());
            newEpic.addSubtask(subtask);
        } else {
            Epic epic = findEpicById(subtask.getEpicId());
            epic.updateSubtask(subtask);
        }
        return subtasks.put(subtask.getId(), subtask);
    }

    @Override
    public Epic updateEpic(Epic epic) {
        if (epic == null || !epics.containsKey(epic.getId())) {
            return null;
        }

        Set<Long> newSubtaskIds = epic.getSubtasks().stream()
                .map(Subtask::getId)
                .collect(Collectors.toSet());

        if (!subtasks.keySet().containsAll(newSubtaskIds)) {
            return null;
        }

        epics.put(epic.getId(), epic);
        return epic;
    }

    @Override
    public void deleteTaskById(long id) {
        tasks.remove(id);
        historyManager.remove(id);
    }

    @Override
    public void deleteSubtaskById(long id) {
        if (subtasks.containsKey(id)) {
            Subtask subtask = subtasks.get(id);
            Epic epic = findEpicById(subtask.getEpicId());
            if (epic != null) {
                epic.removeSubtask(subtask);
            }
            subtasks.remove(id);
            historyManager.remove(id);
        }
    }

    @Override
    public void deleteEpicById(long id) {
        if (epics.containsKey(id)) {
            Iterator<Map.Entry<Long, Subtask>> iterator = subtasks.entrySet().iterator();

            while (iterator.hasNext()) {
                Map.Entry<Long, Subtask> entry = iterator.next();
                if (entry.getValue().getEpicId() == id) {
                    iterator.remove();
                    historyManager.remove(entry.getValue().getId());
                }
            }

            epics.remove(id);
            historyManager.remove(id);
        }
    }

    private long getTaskId() {
        return taskId++;
    }
}
