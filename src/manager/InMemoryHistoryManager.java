package manager;

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
            System.out.println("contains key");
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
            tasksMap.put(task.getId(), currentHead);
        } else {
            Node currentTail = tail;
            currentTail.next = newNode;
            tail = newNode;
            newNode.prev = currentTail;
            tasksMap.put(task.getId(), newNode);
            tasksMap.put(task.getId(), currentTail);
        }
    }

    @Override
    public void remove(long id) {
        removeNode(tasksMap.get(id));
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

    private void removeNode(Node node) {
        Node toRemove = tasksMap.get(node.t.getId());
        if (toRemove != null) {
            tasksMap.remove(node.t.getId());

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
                tail = toRemove.prev;
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

