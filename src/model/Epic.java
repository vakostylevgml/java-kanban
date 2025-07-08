package model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class Epic extends Task {
    private final Map<Long, Subtask> subtasks;

    public Epic(String name, String description) {
        super(name, description, Status.NEW);
        subtasks = new HashMap<>();
    }

    public void addSubtask(Subtask subtask) throws IllegalArgumentException {
        if (subtasks.containsKey(subtask.getId())) {
            throw new IllegalArgumentException("Subtask " + subtask + " already exists, cannot add subtask");
        }
        subtasks.put(subtask.getId(), subtask);
        status = updateStatus();
    }

    public void removeSubtask(Subtask subtask) {
        subtasks.remove(subtask.getId());
        status = updateStatus();
    }

    public void updateSubtask(Subtask subtask) throws IllegalArgumentException {
        if (!subtasks.containsKey(subtask.getId())) {
            throw new IllegalArgumentException("Subtask " + subtask + " does not exist, can't update it");
        }
        subtasks.remove(subtask.getId());
        subtasks.put(subtask.getId(), subtask);
        status = updateStatus();
    }

    public List<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public String toString() {
        return "[TaskId = " + getId() + ", title = " + getTitle() + ", status = " + status
                + ", subtasksSize = " + subtasks.size() + "]";
    }

    @Override
    public Optional<LocalDateTime> getEndTime() {
        return subtasks.values().stream()
                .filter(s -> s.getEndTime().isPresent())
                .sorted(Comparator.comparing(s -> s.getEndTime().get(), Comparator.reverseOrder()))
                        .map(s -> s.getEndTime().get())
                .findFirst();
    }

    @Override
    public Duration getDuration() {
        return subtasks.values().stream()
                .map(Task::getDuration)
                .reduce(Duration.ZERO, Duration::plus);
    }

    @Override
    public Optional<LocalDateTime> getStartTime() {
        return subtasks.values().stream()
                .filter(s -> s.getStartTime().isPresent())
                .sorted(Comparator.comparing(s -> s.getStartTime().get()))
                .map(s -> s.getStartTime().get())
                .findFirst();
    }

    private Status updateStatus() {
        boolean hasNew = false;
        boolean hasDone = false;

        for (Subtask subtask : subtasks.values()) {
            if (subtask.getStatus() == Status.IN_PROGRESS) {
                return Status.IN_PROGRESS;
            }
            if (subtask.getStatus() == Status.NEW) {
                hasNew = true;
            }
            if (subtask.getStatus() == Status.DONE) {
                hasDone = true;
            }
        }

        if (hasNew && !hasDone) {
            return Status.NEW;
        }
        if (hasDone && !hasNew) {
            return Status.DONE;
        }

        return Status.IN_PROGRESS;
    }

}
