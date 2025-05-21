import manager.TaskManager;
import model.Epic;
import model.Subtask;
import model.Task;

public class Main {

    public static void main(String[] args) {

    }

    static void printAll(TaskManager manager) {
        System.out.println("Tasks");
        for (Task task : manager.findAllTasks()) {
            System.out.println(task);
        }

        System.out.println("Subtasks");
        for (Subtask subtask : manager.findAllSubTasks()) {
            System.out.println(subtask);
        }

        System.out.println("Epics");
        for (Epic epic : manager.findAllEpics()) {
            System.out.println(epic);
        }
    }
}
