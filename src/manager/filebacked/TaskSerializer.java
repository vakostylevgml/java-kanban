package manager.filebacked;

import model.*;

import java.time.DateTimeException;
import java.time.Duration;
import java.time.LocalDateTime;

public class TaskSerializer {
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

        if (!type.equals(TaskType.EPIC)) {
            sb.append(",");
            sb.append(task.getStartTime().toString());
            sb.append(",");
            sb.append(task.getDuration().toMinutes());
        }
        return sb.toString();
    }

    public static Task deSerializeTaskFromString(String stringFromFile) throws TaskFileSerizalizationException {
        if (stringFromFile == null || stringFromFile.isBlank()) {
            throw new IllegalArgumentException("Task string is null or blank");
        }

        String[] split = stringFromFile.split(",");
        if (split.length < 5) {
            throw new TaskFileSerizalizationException("Task string is invalid. Required at least 5 words, got " + split.length);
        }

        long id = Long.parseLong(split[0]);
        TaskType type = TaskType.valueOf(split[1]);

        String title = split[2];
        Status status = Status.valueOf(split[3]);
        String description = split[4];

        if (type.equals(TaskType.TASK)) {
            LocalDateTime startTime;
            Duration duration = Duration.ZERO;

            try {
                startTime = LocalDateTime.parse(split[5]);
                duration = Duration.parse(split[6]);
            } catch (DateTimeException e) {
                startTime = null;
            }

            Task task;
            if (startTime == null) {
                task = new Task(title, description, status);
            } else {
                task = new Task(title, description, status, startTime, duration);
            }
            task.setId(id);
            return task;
        } else if (type.equals(TaskType.SUBTASK)) {
            LocalDateTime startTime;
            Duration duration = Duration.ZERO;

            try {
                startTime = LocalDateTime.parse(split[6]);
                duration = Duration.parse(split[7]);
            } catch (DateTimeException e) {
                startTime = null;
            }

            long epicId;
            try {
                epicId = Long.parseLong(split[5]);
            } catch (NumberFormatException e) {
                throw new TaskFileSerizalizationException("Invalid epic id: " + split[5]);
            }
            Subtask subtask;
            if (startTime == null) {
                subtask = new Subtask(title, description, status, epicId);
            } else {
                subtask = new Subtask(title, description, status, epicId, startTime, duration);
            }
            subtask.setId(id);
            return subtask;
        } else {
            Epic epic = new Epic(title, description);
            epic.setId(id);
            return epic;
        }
    }
}
