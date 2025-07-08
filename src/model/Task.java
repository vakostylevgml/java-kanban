package model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

public class Task {
    private final String title;
    private final String description;
    protected Status status;
    private long id;
    private Duration durationInMinutes = Duration.ZERO;
    private LocalDateTime startTime;

    public Task(String title, String description, Status status) {
        this.title = title;
        this.description = description;
        this.status = status;
        this.id = -1;
    }

    public Task(String title, String description, Status status, LocalDateTime startTime, Duration durationInMinutes) {
        this(title, description, status);

        if (durationInMinutes == null || startTime == null) {
            this.durationInMinutes = Duration.ZERO;
            this.startTime = null;
        } else {
            this.startTime = startTime;
            this.durationInMinutes = durationInMinutes;
        }
    }

    @Override
    public final boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return getId() == task.getId();
    }

    @Override
    public final int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "[TaskId = " + getId() + ", title = " + getTitle() + ", status = " + status + "]";
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Status getStatus() {
        return status;
    }

    public Optional<LocalDateTime> getStartTime() {
        return Optional.ofNullable(startTime);
    }

    public Duration getDuration() {
        return Optional.ofNullable(durationInMinutes).orElse(Duration.ZERO);
    }

    public Optional<LocalDateTime> getEndTime() {
        if (getStartTime().isPresent()) {
            return Optional.of(getStartTime().get().plus(getDuration()));
        } else {
            return Optional.empty();
        }
    }
}
