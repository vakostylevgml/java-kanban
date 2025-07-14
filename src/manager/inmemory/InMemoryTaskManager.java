package manager.inmemory;

import manager.HistoryManager;
import manager.TaskManager;
import model.Epic;
import model.Subtask;
import model.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {
    protected static final int PLANNING_TIME_DAYS = 365;
    protected static final int PLANNING_TIME_INTERVAL_MINUTES = 15;
    protected static final LocalDateTime START_OF_PERIOD = LocalDateTime.of(2025, Month.JANUARY,
            1, 0, 0, 0);


    protected final Map<Long, Task> tasks;
    protected final Map<Long, Subtask> subtasks;
    protected final Map<Long, Epic> epics;
    protected final TreeSet<Task> sortedTasks;
    private final HistoryManager historyManager;
    protected final Map<Integer, Boolean> intervals;
    private long taskId;

    public InMemoryTaskManager(HistoryManager historyManager) {
        tasks = new HashMap<>();
        subtasks = new HashMap<>();
        epics = new HashMap<>();
        this.historyManager = historyManager;
        sortedTasks = new TreeSet<>(Comparator
                .comparing(t -> t.getStartTime().orElseThrow(IllegalArgumentException::new)));
        intervals = new HashMap<>();

        for (int i = 0; i < (PLANNING_TIME_DAYS * 24 * 60) / PLANNING_TIME_INTERVAL_MINUTES; i++) {
            intervals.put(i, false);
        }
    }

    @Override
    public HistoryManager getHistoryManager() {
        return historyManager;
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return sortedTasks.stream().toList();
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

        if (isOverlapWithExisting(task)) {
            throw new OverlapException("Task overlaps with existing task");
        }

        task.setId(getTaskId());
        tasks.put(task.getId(), task);
        if (task.getStartTime().isPresent()) {
            sortedTasks.add(task);
            addIntervals(getTaskTimeIntervals(task));
        }
        return task.getId();
    }

    @Override
    public long createSubtask(Subtask subtask) {
        if (subtask == null || !epics.containsKey(subtask.getEpicId())) {
            return -1;
        }

        if (isOverlapWithExisting(subtask)) {
            throw new OverlapException("Task overlaps with existing task");
        }

        Epic epic = epics.get(subtask.getEpicId());
        subtask.setId(getTaskId());
        subtasks.put(subtask.getId(), subtask);
        epic.addSubtask(subtask);
        if (subtask.getStartTime().isPresent()) {
            sortedTasks.add(subtask);
            addIntervals(getTaskTimeIntervals(subtask));
        }
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

        if (task.getStartTime().isPresent()) {
            Task oldTask = tasks.get(task.getId());
            removeIntervals(getTaskTimeIntervals(oldTask));

            if (isOverlapWithExisting(task)) {
                addIntervals(getTaskTimeIntervals(oldTask)); //roll back
                throw new OverlapException("Task overlaps with existing task");
            }

            sortedTasks.remove(oldTask);
            sortedTasks.add(task);
            addIntervals(getTaskTimeIntervals(task));
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


        if (subtask.getStartTime().isPresent()) {
            removeIntervals(getTaskTimeIntervals(oldSubtask));

            if (isOverlapWithExisting(subtask)) {
                addIntervals(getTaskTimeIntervals(oldSubtask)); //roll back
                throw new OverlapException("Task overlaps with existing task");
            }

            sortedTasks.remove(oldSubtask);
            sortedTasks.add(subtask);
            addIntervals(getTaskTimeIntervals(subtask));
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
        Task task = tasks.get(id);
        removeIntervals(getTaskTimeIntervals(task));
        sortedTasks.remove(task);
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
            removeIntervals(getTaskTimeIntervals(subtask));
            subtasks.remove(id);
            historyManager.remove(id);
            sortedTasks.remove(subtask);
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
                    Subtask subtask = entry.getValue();
                    removeIntervals(getTaskTimeIntervals(subtask));
                    sortedTasks.remove(subtask);
                }
            }

            epics.remove(id);
            historyManager.remove(id);
        }
    }

    @Override
    public boolean isOverlapWithExisting(Task task) {
        Set<Integer> intervalsOfTask = getTaskTimeIntervals(task);
        for (Integer i : intervalsOfTask) {
            if (intervals.get(i).equals(true)) {
                return true;
            }
        }
        return false;
    }

    public boolean isOverlapping(Task task1, Task task2) {
        if ((task1 == null || task2 == null) || task1.getEndTime().isEmpty() || task2.getEndTime().isEmpty()
                || task1.getStartTime().isEmpty() || task2.getStartTime().isEmpty()) {
            return false;
        }
        LocalDateTime t1Start = task1.getStartTime().get();
        LocalDateTime t2Start = task2.getStartTime().get();
        LocalDateTime t1End = task1.getEndTime().get();
        LocalDateTime t2End = task2.getEndTime().get();

        if (t1End.isAfter(t2End) || t1End.isEqual(t2End)) {
            return (t1Start.isBefore(t2End) || t1Start.isEqual(t2End));
        } else {
            return (t2Start.isBefore(t1End) || t2Start.isEqual(t1End));
        }
    }

    private Set<Integer> getTaskTimeIntervals(Task task) {
        if (task != null && task.getEndTime().isPresent()) {
            int startTimeIndex = (int)(Duration.between(START_OF_PERIOD,
                    task.getStartTime().orElseThrow(IllegalArgumentException::new)).toMinutes()
                    / PLANNING_TIME_INTERVAL_MINUTES) + 1;
            int endTimeIndex = (int)(Duration.between(START_OF_PERIOD,
                    task.getEndTime().orElseThrow(IllegalArgumentException::new)).toMinutes()
                    / PLANNING_TIME_INTERVAL_MINUTES);
            Set<Integer> intervals = new HashSet<>();

            for (int i = startTimeIndex; i <= endTimeIndex; i++) {
                intervals.add(i);
            }
            return intervals;
        } else {
            return new HashSet<>();
        }
    }

    private void addIntervals(Set<Integer> intervalsToAdd) {
        if (intervalsToAdd != null && !intervalsToAdd.isEmpty()) {
            intervalsToAdd.forEach(i -> intervals.put(i, true));
        }
    }

    private void removeIntervals(Set<Integer> intervalsToRemove) {
        if (intervalsToRemove != null && !intervalsToRemove.isEmpty()) {
            intervalsToRemove.forEach(i -> intervals.put(i, false));
        }
    }

    private long getTaskId() {
        return taskId++;
    }
}
