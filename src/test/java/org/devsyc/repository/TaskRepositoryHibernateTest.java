package org.devsyc.repository;

import org.devsyc.domain.entities.Task;
import org.devsyc.domain.entities.User;
import org.devsyc.domain.enums.TaskStatus;
import org.devsyc.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TaskRepositoryHibernateTest {

    private TaskRepositoryHibernate taskRepository;
    private Session session;
    private Transaction transaction;

    @BeforeEach
    void setUp() {
        taskRepository = new TaskRepositoryHibernate();
        session = HibernateUtil.getSessionFactory().openSession();
        transaction = session.beginTransaction();
    }

    @AfterEach
    void tearDown() {
        if (transaction != null && transaction.isActive()) {
            transaction.rollback();
        }
        if (session != null && session.isOpen()) {
            session.close();
        }
    }

    @Test
    void testSaveAndFindById() {
        User user = new User("John", "Doe", "john@example.com", "password", null);
        session.save(user);
        session.flush();
        transaction.commit(); // Ensure data is committed before continuing

        transaction = session.beginTransaction(); // Start a new transaction for further operations
        Task task = new Task();
        task.setTitle("Test Task");
        task.setDescription("Test Description");
        task.setStatus(TaskStatus.TODO);
        task.setCreationDate(LocalDateTime.now());
        task.setDueDate(LocalDateTime.now().plusDays(1));
        task.setAssignedUser(user);
        task.setCreatedBy(user);

        session.save(task);
        session.flush();
        transaction.commit();

        Task foundTask = taskRepository.findById(task.getId());
        assertNotNull(foundTask);
        assertEquals("Test Task", foundTask.getTitle());
        assertEquals(user.getId(), foundTask.getAssignedUser().getId());
    }

    @Test
    void testFindAll() {
        User user = new User("John", "Doe", "john@example.com", "password", null);
        session.save(user);
        session.flush();
        transaction.commit();

        transaction = session.beginTransaction();
        Task task1 = new Task("Task 1", "Description 1", LocalDateTime.now().plusDays(1), user, user, null);
        task1.setCreationDate(LocalDateTime.now());
        Task task2 = new Task("Task 2", "Description 2", LocalDateTime.now().plusDays(2), user, user, null);
        task2.setCreationDate(LocalDateTime.now());

        session.save(task1);
        session.save(task2);
        session.flush();
        transaction.commit();

        List<Task> tasks = taskRepository.findAll();
        assertFalse(tasks.isEmpty());
        assertTrue(tasks.stream().anyMatch(t -> t.getTitle().equals("Task 1")));
        assertTrue(tasks.stream().anyMatch(t -> t.getTitle().equals("Task 2")));
    }

    @Test
    void testFindByUser() {
        User user1 = new User("John", "Doe", "john@example.com", "password", null);
        User user2 = new User("Jane", "Doe", "jane@example.com", "password", null);
        session.save(user1);
        session.save(user2);
        session.flush();
        transaction.commit();

        transaction = session.beginTransaction();
        Task task1 = new Task("Task 1", "Description 1", LocalDateTime.now().plusDays(1), user1, user1, null);
        task1.setCreationDate(LocalDateTime.now());
        Task task2 = new Task("Task 2", "Description 2", LocalDateTime.now().plusDays(2), user1, user1, null);
        task2.setCreationDate(LocalDateTime.now());
        Task task3 = new Task("Task 3", "Description 3", LocalDateTime.now().plusDays(3), user2, user2, null);
        task3.setCreationDate(LocalDateTime.now());

        session.save(task1);
        session.save(task2);
        session.save(task3);
        session.flush();
        transaction.commit();

        List<Task> user1Tasks = taskRepository.findByUser(user1);
        assertEquals(2, user1Tasks.size());
        assertTrue(user1Tasks.stream().allMatch(t -> t.getAssignedUser().getId().equals(user1.getId())));

        List<Task> user2Tasks = taskRepository.findByUser(user2);
        assertEquals(1, user2Tasks.size());
        assertEquals("Task 3", user2Tasks.get(0).getTitle());
    }

    @Test
    void testFindByStatus() {
        User user = new User("John", "Doe", "john@example.com", "password", null);
        session.save(user);
        session.flush();
        transaction.commit();

        transaction = session.beginTransaction();
        Task task1 = new Task("Task 1", "Description 1", LocalDateTime.now().plusDays(1), user, user, null);
        task1.setStatus(TaskStatus.TODO);
        task1.setCreationDate(LocalDateTime.now());
        Task task2 = new Task("Task 2", "Description 2", LocalDateTime.now().plusDays(2), user, user, null);
        task2.setStatus(TaskStatus.IN_PROGRESS);
        task2.setCreationDate(LocalDateTime.now());
        Task task3 = new Task("Task 3", "Description 3", LocalDateTime.now().plusDays(3), user, user, null);
        task3.setStatus(TaskStatus.DONE);
        task3.setCreationDate(LocalDateTime.now());

        session.save(task1);
        session.save(task2);
        session.save(task3);
        session.flush();
        transaction.commit();

        List<Task> todoTasks = taskRepository.findByStatus(TaskStatus.TODO);
        assertEquals(1, todoTasks.size());
        assertEquals("Task 1", todoTasks.get(0).getTitle());

        List<Task> inProgressTasks = taskRepository.findByStatus(TaskStatus.IN_PROGRESS);
        assertEquals(1, inProgressTasks.size());
        assertEquals("Task 2", inProgressTasks.get(0).getTitle());
    }

    @Test
    void testUpdate() {
        User user = new User("John", "Doe", "john@example.com", "password", null);
        session.save(user);
        session.flush();
        transaction.commit();

        transaction = session.beginTransaction();
        Task task = new Task("Original Task", "Original Description", LocalDateTime.now().plusDays(1), user, user, null);
        task.setCreationDate(LocalDateTime.now());
        session.save(task);
        session.flush();
        transaction.commit();

        transaction = session.beginTransaction();
        task.setTitle("Updated Task");
        task.setDescription("Updated Description");
        taskRepository.update(task);
        transaction.commit();

        Task updatedTask = taskRepository.findById(task.getId());
        assertEquals("Updated Task", updatedTask.getTitle());
        assertEquals("Updated Description", updatedTask.getDescription());
    }

    @Test
    void testDelete() {
        User user = new User("John", "Doe", "john@example.com", "password", null);
        session.save(user);
        session.flush();
        transaction.commit();

        transaction = session.beginTransaction();
        Task task = new Task("Task to Delete", "Description", LocalDateTime.now().plusDays(1), user, user, null);
        task.setCreationDate(LocalDateTime.now());
        session.save(task);
        session.flush();
        transaction.commit();

        transaction = session.beginTransaction();
        taskRepository.delete(task);
        transaction.commit();

        Task deletedTask = taskRepository.findById(task.getId());
        assertNull(deletedTask);
    }
}
