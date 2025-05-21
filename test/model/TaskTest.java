package model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class TaskTest {

    @Test
    void testEquals() {
        Task task1 = new Task("1", "2", Status.NEW);
        Task task2 = new Task("2", "5", Status.NEW);
        task1.setId(1);
        task2.setId(1);
        Assertions.assertEquals(task1, task2);
    }
}