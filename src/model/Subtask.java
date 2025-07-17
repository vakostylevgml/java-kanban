package model;

import java.time.Duration;
import java.time.LocalDateTime;

public class Subtask extends Task {
    private final long epicId;

    public Subtask(String name, String description, Status status, long epicId) {
        super(name, description, status);
        this.epicId = epicId;
    }

    public Subtask(String name, String description, Status status, long epicId, LocalDateTime startTime,
                   Duration duration) {
        super(name, description, status, startTime, duration);
        this.epicId = epicId;
    }

    public long getEpicId() {
        return epicId;
    }

    @Override
    public String toString() {
        return "[TaskId = " + getId() + ", title = " + getTitle() + ", status = " + status
                + ", epicId = " + epicId + "]";
    }
}
