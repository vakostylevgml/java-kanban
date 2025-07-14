package manager.filebacked;

import manager.HistoryManager;
import manager.Managers;
import manager.TaskManager;
import manager.inmemory.InMemoryTaskManager;
import model.Epic;
import model.Subtask;
import model.Task;

import java.io.*;
import java.util.Optional;

public class FileBackedTaskManager extends InMemoryTaskManager implements TaskManager {
    private static String FILENAME;

    public FileBackedTaskManager(HistoryManager historyManager, String fileName) {
        super(historyManager);
        FILENAME = fileName;
    }

    @Override
    public long createTask(Task task) {
        super.createTask(task);
        save();
        return task.getId();
    }

    @Override
    public long createSubtask(Subtask subtask) {
        super.createSubtask(subtask);
        save();
        return subtask.getId();
    }

    @Override
    public long createEpic(Epic epic) {
        super.createEpic(epic);
        save();
        return epic.getId();
    }

    @Override
    public Optional<Task> updateTask(Task task) {
        Task updatedTask = super.updateTask(task).orElseThrow(IllegalArgumentException::new);
        save();
        return Optional.of(updatedTask);
    }

    @Override
    public Optional<Subtask> updateSubtask(Subtask subtask) {
        Subtask updatedSubtask = super.updateSubtask(subtask).orElseThrow(IllegalArgumentException::new);
        save();
        return Optional.of(updatedSubtask);
    }

    @Override
    public Optional<Epic> updateEpic(Epic epic) {
        Epic updatedEpic = super.updateEpic(epic).orElseThrow(IllegalArgumentException::new);
        save();
        return Optional.of(updatedEpic);
    }

    @Override
    public void deleteTaskById(long id) {
        super.deleteTaskById(id);
        save();
    }

    @Override
    public void deleteSubtaskById(long id) {
        super.deleteSubtaskById(id);
        save();
    }

    @Override
    public void deleteEpicById(long id) {
        super.deleteEpicById(id);
        save();
    }

    public static FileBackedTaskManager loadFromFile(File file) {
            FileBackedTaskManager manager = new FileBackedTaskManager(Managers.getDefaultHistory(),
                    file.getAbsolutePath());

            int epicsCount = 0;
            int subtasksCount = 0;
            int tasksCount = 0;

            try (BufferedReader fileReader = new BufferedReader(new FileReader(file))) {
                while (fileReader.ready()) {
                    String line = fileReader.readLine();
                    Task task = TaskSerializer.deSerializeTaskFromString(line);

                    if (task instanceof Epic epic) {
                        manager.epics.put(epic.getId(), epic);
                        epicsCount++;
                    } else if (task instanceof Subtask subtask) {
                        if (manager.epics.containsKey(subtask.getEpicId())) {
                            manager.subtasks.put(subtask.getId(), subtask);
                            Epic epic = manager.epics.get(subtask.getEpicId());
                            epic.addSubtask(subtask);
                            subtasksCount++;
                        } else {
                            throw new ManagerSaveException("Can't add subtask with unexistent epic");
                        }
                    } else {
                        manager.tasks.put(task.getId(), task);
                        tasksCount++;
                    }
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            } finally {
                if (manager.epics.isEmpty() && manager.subtasks.isEmpty() && manager.tasks.isEmpty()) {
                    System.out.println("No tasks found");
                } else {
                    System.out.printf("Added %d tasks, %d subtasks, %d epics \n",  tasksCount, subtasksCount, epicsCount);
                }
            }
            return manager;
    }

    private void save() {
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(FILENAME))) {
            for (Task task : tasks.values()) {
                bufferedWriter.write(TaskSerializer.serrializeToString(task));
                bufferedWriter.newLine();
            }

            for (Epic epic : epics.values()) {
                bufferedWriter.write(TaskSerializer.serrializeToString(epic));
                bufferedWriter.newLine();
            }

            for (Subtask subtask : subtasks.values()) {
                bufferedWriter.write(TaskSerializer.serrializeToString(subtask));
                bufferedWriter.newLine();
            }


        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }


}
