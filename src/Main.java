import manager.TaskManager;
import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;

public class Main {

    public static void main(String[] args) {
        Task task1 = new Task("test task 1", "desc", Status.NEW);
        Task task2 = new Task("test task 2", "desc", Status.NEW);
        Task task3 = new Task("test task 3", "desc", Status.NEW);
        Epic epic1 = new Epic("test epic 1", "desc");
        Epic epic2 = new Epic("test epic 2", "desc");

        TaskManager taskManager = new TaskManager();

        System.out.println("Adding tasks and epics");
        long task1id = taskManager.create(task1);
        long task2id = taskManager.create(task2);
        long task3id = taskManager.create(task3);

        long epic1id = taskManager.create(epic1);
        long epic2id = taskManager.create(epic2);

        System.out.println("task1id : " + task1id);
        System.out.println("task2id : " + task2id);
        System.out.println("task3id : " + task3id);
        System.out.println("epic1id : " + epic1id);
        System.out.println("epic2id : " + epic2id);

        printAll(taskManager);

        System.out.println("There are 3 tasks as expected: " + (taskManager.findAllTasks().size() == 3));
        System.out.println("There are 2 epics as expected: " + (taskManager.findAllEpics().size() == 2));

        Subtask subtask = new Subtask("Subtask 0 which should fail", "st0 desc", Status.NEW, task1id);

        Subtask subtask1 = new Subtask("Subtask 1", "st1 desc", Status.NEW, epic1id);
        Subtask subtask2 = new Subtask("Subtask 2", "st1 desc", Status.NEW, epic1id);
        Subtask subtask3 = new Subtask("Subtask 3", "st1 desc", Status.NEW, epic1id);
        Subtask subtask4 = new Subtask("Subtask 4", "st1 desc", Status.NEW, epic2id);
        Subtask subtask5 = new Subtask("Subtask 5", "st1 desc", Status.NEW, epic2id);

        System.out.println("Add subtask with wrong epic id:");
        taskManager.create(subtask);

        System.out.println("Add correct subtasks");
        long subtask1id = taskManager.create(subtask1);
        taskManager.create(subtask2);
        taskManager.create(subtask3);
        long subtask4id = taskManager.create(subtask4);
        long subtask5id = taskManager.create(subtask5);

        System.out.println("There are 3 subtasks for epic 1 as expected: "
                + (taskManager.findAllSubtasksByEpicId(epic1id).size() == 3));
        System.out.println("There are 2 subtasks for epic 2 as expected: "
                + (taskManager.findAllSubtasksByEpicId(epic2id).size() == 2));
        System.out.println("There are 5 subtasks total as expected: "
                + (taskManager.findAllSubTasks().size() == 5));

        printAll(taskManager);

        System.out.println("Check status change");
        Subtask updatedSubtask1 = new Subtask("upd subt1", "desc", Status.DONE, epic1id);
        updatedSubtask1.setId(subtask1id);
        taskManager.update(updatedSubtask1);
        System.out.println("Updated subtask = " + taskManager.findSubTaskById(subtask1id));
        System.out.println("Updated epic 1 status = " + taskManager.findEpicById(epic1id).getStatus());

        Subtask updatedSubtask4 = new Subtask("upd subt4", "desc", Status.DONE, epic2id);
        updatedSubtask4.setId(subtask4id);
        Subtask updatedSubtask5 = new Subtask("upd subt5", "desc", Status.DONE, epic2id);
        updatedSubtask5.setId(subtask5id);
        taskManager.update(updatedSubtask4);
        taskManager.update(updatedSubtask5);
        System.out.println("Updated epic 2 status = " + taskManager.findEpicById(epic2id).getStatus());

        taskManager.deleteSubtaskById(subtask1id);
        System.out.println("Updated epic 1 status = " + taskManager.findEpicById(epic1id).getStatus());

        printAll(taskManager);

        System.out.println("Delete epic");
        taskManager.deleteEpicById(epic2id);

        printAll(taskManager);
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
