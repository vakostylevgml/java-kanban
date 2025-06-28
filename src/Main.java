import manager.Managers;
import manager.filebacked.FileBackedTaskManager;

import java.io.File;

public class Main {
    private static final String PATH = "C:\\Users\\kosty\\IdeaProjects\\java-kanban1\\data.csv";

    public static void main(String[] args) {

        FileBackedTaskManager fileBacked = Managers.getFileBacked(PATH);
/*
        Task task = new Task("task", "desc", Status.DONE);
        Epic epic = new Epic("epic", "d");
        long taskId = fileBacked.createTask(task);
        long epicId = fileBacked.createEpic(epic);
        Subtask subtask = new Subtask("subtask", "de", Status.IN_PROGRESS, epicId);
        long subtaskId = fileBacked.createSubtask(subtask);
*/

        fileBacked.loadFromFile(new File(PATH));

        System.out.println(fileBacked.findAllTasks());
        System.out.println(fileBacked.findAllSubTasks());
        System.out.println(fileBacked.findAllEpics());


    }
}
