package model;

import java.util.Objects;

public class Task implements SerializableToFile<Task> {
    private final String title;
    private final String description;
    protected Status status;
    private long id;

    public Task(String title, String description, Status status) {
        this.title = title;
        this.description = description;
        this.status = status;
        this.id = -1;
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

    @Override
    public String serrializeToString() {
        return id +
                "," +
                TaskType.TASK +
                "," +
                title +
                "," +
                status +
                "," +
                description;
    }

    @Override
    public Task serializeFromString(String stringFromFile) throws IllegalArgumentException, ArithmeticException {
        String[] split = stringFromFile.split(",");
        if (split.length == 5) {
            long id = Long.parseLong(split[0]);
            TaskType type = TaskType.valueOf(split[1]);
            String title = split[2];
            Status status = Status.valueOf(split[3]);
            String description = split[4];
            Task task = new Task(title, description, status);
            task.setId(id);
            return task;
        }
        return null;
    }
}
