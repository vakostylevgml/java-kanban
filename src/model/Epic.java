package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Epic extends Task {
    private final Map<Long, Subtask> subtasks;

    public Epic(String name, String description) {
        super(name, description, Status.NEW);
        subtasks = new HashMap<>();
    }

    public Epic(String name, String description, Map<Long, Subtask> subtasks) {
        super(name, description, Status.NEW);
        this.subtasks = subtasks;
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
