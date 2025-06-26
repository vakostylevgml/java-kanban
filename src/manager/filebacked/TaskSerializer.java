package manager.filebacked;

import model.*;

public class TaskSerializer {
    private TaskSerializer() {}

    public static String serrializeToString(Task task) {
        TaskType type = switch (task) {
            case Epic ignored -> TaskType.EPIC;
            case Subtask ignored -> TaskType.SUBTASK;
            case Task ignored -> TaskType.TASK;
        };

        StringBuilder sb = new StringBuilder();
        sb.append(task.getId());
        sb.append(",");
        sb.append(type);
        sb.append(",");
        sb.append(task.getTitle());
        sb.append(",");
        sb.append(task.getStatus());
        sb.append(",");
        sb.append(task.getDescription());

        if (type.equals(TaskType.SUBTASK)) {
            sb.append(",");
            Subtask subtask = (Subtask) task;
            sb.append(subtask.getEpicId());
        }
        return sb.toString();
    }

    public static Task serializeTaskFromString(String stringFromFile) throws IllegalArgumentException, ArithmeticException {
        if (stringFromFile == null || stringFromFile.isBlank()) {
            throw new IllegalArgumentException("Task string is null or blank");
        }

        String[] split = stringFromFile.split(",");
        if (split.length < 5) {
            throw new IllegalArgumentException("Task string is invalid. Required at least 5 words, got " + split.length);
        }

        long id = Long.parseLong(split[0]);
        TaskType type = TaskType.valueOf(split[1]);

        String title = split[2];
        Status status = Status.valueOf(split[3]);
        String description = split[4];

        return switch (type) {
            case TASK -> {
                Task task = new Task(title, description, status);
                task.setId(id);
                yield  task;
            }

            case SUBTASK -> {
                long epicId = Long.parseLong(split[5]);
                Subtask subtask = new Subtask(title, description, status, epicId);
                subtask.setId(id);
                yield  subtask;
            }
            case EPIC -> {
                Epic epic = new Epic(title, description);
                epic.setId(id);
                yield  epic;
            }
        };

    }
}
