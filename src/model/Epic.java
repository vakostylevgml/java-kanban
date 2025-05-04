package model;

import java.util.HashSet;
import java.util.Set;

public class Epic extends Task {
    private final Set<Subtask> subtasks;

    public Epic(String name, String description) {
        super(name, description, Status.NEW);
        subtasks = new HashSet<>();
    }

    public Epic(String name, String description, Set<Subtask> subtasks) {
        super(name, description, Status.NEW);
        this.subtasks = subtasks;
    }

    public void addSubtask(Subtask subtask) {
        subtasks.add(subtask);
        status = updateStatus();
    }

    public void removeSubtask(Subtask subtask) {
        subtasks.remove(subtask);
        status = updateStatus();
    }

    public void updateSubtask(Subtask subtask) {
        subtasks.remove(subtask);
        subtasks.add(subtask);
        status = updateStatus();
    }

    public Set<Subtask> getSubtasks() {
        return subtasks;
    }

    @Override
    public String toString() {
        return "[TaskId = " + getId() + ", title = " + getTitle() + ", status = " + status
                + ", subtasksSize = " + subtasks.size() + "]";
    }

    private Status updateStatus() {
        boolean hasNew = false;
        boolean hasDone = false;
        boolean hasInProgress = false;

        for (Subtask subtask : subtasks) {
            if (subtask.getStatus() == Status.NEW) {
                hasNew = true;
            }
            if (subtask.getStatus() == Status.DONE) {
                hasDone = true;
            }
            if (subtask.getStatus() == Status.IN_PROGRESS) {
                hasInProgress = true;
            }
        }

        if (hasNew && !hasDone && !hasInProgress) {
            return Status.NEW;
        }

        if (hasDone && !hasNew && !hasInProgress) {
            return Status.DONE;
        }

        return Status.IN_PROGRESS;
    }

}
