package manager;

import model.*;

import java.util.*;

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
    public Map<Long, Task> findAllTasks() {
        return tasks;
    }

    @Override
    public Map<Long, Subtask> findAllSubTasks() {
        return subtasks;
    }

    @Override
    public Map<Long, Epic> findAllEpics() {
        return epics;
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
    public Map<Long, Subtask> findAllSubtasksByEpic(Epic epic) {
        if (epic == null) {
            System.out.println("Can't get subtasks because epic is null");
            return new HashMap<>();
        } else {
            return findAllSubtasksByEpicId(epic.getId());
        }
    }

    @Override
    public Map<Long, Subtask> findAllSubtasksByEpicId(long epicId) {
        if (!epics.containsKey(epicId)) {
            System.out.println("Can't get subtasks because epic does not exist");
            return new HashMap<>();
        }
        return findEpicById(epicId).getSubtasks();
    }

    @Override
    public long create(Task task) {
        if (task == null) {
            System.out.println("Can't create. Task is null");
            return -1;
        }
        task.setId(getTaskId());
        tasks.put(task.getId(), task);
        return task.getId();
    }

    @Override
    public long create(Subtask subtask) {
        if (subtask == null) {
            System.out.println("Can't create. Subtask is null");
            return -1;
        }
        if (!epics.containsKey(subtask.getEpicId())) {
            System.out.printf("Can't create subtask because epic with id %d does not exist \n",
                    subtask.getEpicId());
            return -1;
        }

        Epic epic = findEpicById(subtask.getEpicId());
        subtask.setId(getTaskId());
        subtasks.put(subtask.getId(), subtask);
        epic.addSubtask(subtask);
        return subtask.getId();
    }

    @Override
    public long create(Epic epic) {
        if (epic == null) {
            System.out.println("Can't create. Epic is null");
            return -1;
        }
        if (epic.getSubtasks() == null) {
            System.out.println("Can't create epic because epic with subtasks is null");
            return -1;
        }

        epic.setId(getTaskId());
        epics.put(epic.getId(), epic);
        return epic.getId();
    }

    @Override
    public Task update(Task task) {
        if (task == null) {
            System.out.println("Can't update. Task is null");
            return null;
        }
        if (!tasks.containsKey(task.getId())) {
            System.out.println("Can't update because task with id " + task.getId() + " does not exist");
            return null;
        }

        return tasks.put(task.getId(), task);
    }

    @Override
    public Subtask update(Subtask subtask) {
        if (subtask == null) {
            System.out.println("Can't update. Subtask is null");
            return null;
        }
        if (!subtasks.containsKey(subtask.getId())) {
            System.out.println("Can't update because subtask with id " + subtask.getId() + " does not exist");
            return null;
        }

        Subtask oldSubtask = subtasks.get(subtask.getId());
        if (oldSubtask.getEpicId() != subtask.getEpicId()) {
            if (!epics.containsKey(subtask.getEpicId())) {
                System.out.println("Can't update because epic with id " + subtask.getEpicId() + " does not exist");
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
    public Epic update(Epic epic) {
        if (epic == null) {
            System.out.println("Can't update epic. Epic is null");
            return null;
        }

        if (!epics.containsKey(epic.getId())) {
            System.out.println("Can't update epic because epic with id " + epic.getId() + " does not exist");
            return null;
        }

        if (epic.getSubtasks() == null) {
            System.out.println("Can't update epic because epic's subtask list is null");
            return null;
        }

        Set<Long> newSubtaskIds = epic.getSubtasks().keySet();
        if (!subtasks.keySet().containsAll(newSubtaskIds)) {
            System.out.println("Can't update because updated epic contains subtasks which do not exist");
            return null;
        }

        subtasks.putAll(epic.getSubtasks());
        epics.put(epic.getId(), epic);
        return epic;
    }

    @Override
    public void deleteTaskById(long id) {
        tasks.remove(id);
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
        }
    }

    @Override
    public void deleteEpicById(long id) {
        if (epics.containsKey(id)) {
            Epic epic = epics.get(id);
            for (Subtask subtask : subtasks.values()) {
                if (subtask.getEpicId() == epic.getId()) {
                    subtasks.remove(subtask.getId());
                }
            }
            epics.remove(id);
        }
    }

    private long getTaskId() {
        return taskId++;
    }
}
