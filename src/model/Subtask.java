package model;

public class Subtask extends Task {
    private long epicId;

    public Subtask(String name, String description, Status status, long epicId) {
        super(name, description, status);
        this.epicId = epicId;
    }

    public long getEpicId() {
        return epicId;
    }

    public void setEpicId(long epicId) {
        this.epicId = epicId;
    }

    @Override
    public String toString() {
        return "[TaskId = " + getId() + ", title = " + getTitle() + ", status = " + status
                + ", epicId = " + epicId + "]";
    }
}
