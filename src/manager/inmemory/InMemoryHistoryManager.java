package manager.inmemory;

import manager.HistoryManager;
import model.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {
    private Node head;
    private Node tail;
    private final Map<Long, Node> tasksMap;

    public InMemoryHistoryManager() {
        head = null;
        tail = null;
        tasksMap = new HashMap<>();
    }

    @Override
    public void add(Task task) {
        if (tasksMap.containsKey(task.getId())) {
            removeNode(tasksMap.get(task.getId()));
        }

        Node newNode = new Node(task);
        if (tail == null) {
            tail = newNode;
            head = newNode;
            tasksMap.put(task.getId(), newNode);
        } else if (head == tail) {
            Node currentHead = head;
            currentHead.next = newNode;
            tail = newNode;
            newNode.prev = currentHead;
            tasksMap.put(task.getId(), newNode);
            tasksMap.put(currentHead.t.getId(), currentHead);
        } else {
            Node currentTail = tail;
            currentTail.next = newNode;
            tail = newNode;
            newNode.prev = currentTail;
            tasksMap.put(task.getId(), newNode);
            tasksMap.put(currentTail.t.getId(), currentTail);
        }
    }

    @Override
    public void remove(long id) {
        if (tasksMap.containsKey(id)) {
            Node toRemove = tasksMap.get(id);
            removeNode(toRemove);
        }
    }

    @Override
    public List<Task> getHistory() {
        if (head == null) {
            return new ArrayList<>();
        } else if (head == tail) {
            List<Task> result = new ArrayList<>();
            result.add(head.t);
            return result;
        } else {
            List<Task> result = new ArrayList<>();
            Node current = head;
            while (current != null) {
                result.add(current.t);
                current = current.next;
            }
            return result;
        }
    }

    private void removeNode(Node toRemove) {
        if (toRemove != null) {
            tasksMap.remove(toRemove);

            if (toRemove == head && toRemove == tail) {
                head = null;
                tail = null;
            } else if (toRemove == head) {
                Node next = toRemove.next;
                next.prev = null;
                tasksMap.put(next.t.getId(), next);
                head = next;
            } else if (toRemove == tail) {
                Node prev = toRemove.prev;
                prev.next = null;
                tasksMap.put(prev.t.getId(), prev);
                tail = prev;
            } else {
                Node next = toRemove.next;
                Node prev = toRemove.prev;
                prev.next = next;
                next.prev = prev;
                tasksMap.put(next.t.getId(), next);
                tasksMap.put(prev.t.getId(), prev);
            }

        }
    }

    static class Node {
        Task t;
        Node next;
        Node prev;

        public Node(Task t) {
            this.t = t;
        }
    }
}

