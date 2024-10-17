package org.devsyc.service;

import org.devsyc.domain.entities.Task;
import org.devsyc.domain.entities.User;
import org.devsyc.domain.enums.Role;
import org.devsyc.domain.enums.TaskStatus;
import org.devsyc.dto.TaskDTO;
import org.devsyc.repository.TaskRepositoryHibernate;
import org.devsyc.repository.UserRepositoryHibernate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TaskServiceTest {

    @Mock
    private TaskRepositoryHibernate taskRepository;

    @Mock
    private UserRepositoryHibernate userRepository;

    @InjectMocks
    private TaskService taskService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllTaskDTOs() {
        User user = new User(1L, "John", "Doe", "john@example.com", "password", Role.USER);
        Task task1 = new Task(1L, "Task 1", "Description 1", LocalDateTime.now(), LocalDateTime.now().plusDays(1), TaskStatus.TODO, user, user, Arrays.asList("tag1", "tag2"));
        Task task2 = new Task(2L, "Task 2", "Description 2", LocalDateTime.now(), LocalDateTime.now().plusDays(2), TaskStatus.IN_PROGRESS, user, user, Arrays.asList("tag2", "tag3"));

        when(taskRepository.findAll()).thenReturn(Arrays.asList(task1, task2));

        List<TaskDTO> taskDTOs = taskService.getAllTaskDTOs();

        assertEquals(2, taskDTOs.size());
        assertEquals("Task 1", taskDTOs.get(0).getTitle());
        assertEquals("Task 2", taskDTOs.get(1).getTitle());
    }

    @Test
    void testGetTaskById() {
        User user = new User(1L, "John", "Doe", "john@example.com", "password", Role.USER);
        Task task = new Task(1L, "Task 1", "Description 1", LocalDateTime.now(), LocalDateTime.now().plusDays(1), TaskStatus.TODO, user, user, Arrays.asList("tag1", "tag2"));

        when(taskRepository.findById(1L)).thenReturn(task);

        Task result = taskService.getTaskById(1L);

        assertNotNull(result);
        assertEquals("Task 1", result.getTitle());
    }

    @Test
    void testCreateTask() {
        User user = new User(1L, "John", "Doe", "john@example.com", "password", Role.USER);
        Task task = new Task(null, "New Task", "Description", LocalDateTime.now(), LocalDateTime.now().plusDays(1), TaskStatus.TODO, user, user, Arrays.asList("tag1", "tag2"));

        taskService.createTask(task);

        verify(taskRepository).save(task);
    }

    @Test
    void testUpdateTask() {
        User user = new User(1L, "John", "Doe", "john@example.com", "password", Role.USER);
        Task task = new Task(1L, "Updated Task", "Updated Description", LocalDateTime.now(), LocalDateTime.now().plusDays(1), TaskStatus.IN_PROGRESS, user, user, Arrays.asList("tag1", "tag2", "tag3"));

        taskService.updateTask(task);

        verify(taskRepository).update(task);
    }

    @Test
    void testDeleteTask() {
        User user = new User(1L, "John", "Doe", "john@example.com", "password", Role.USER);
        Task task = new Task(1L, "Task to Delete", "Description", LocalDateTime.now(), LocalDateTime.now().plusDays(1), TaskStatus.TODO, user, user, Arrays.asList("tag1"));

        when(taskRepository.findById(1L)).thenReturn(task);
        when(userRepository.findById(1L)).thenReturn(user);

        boolean result = taskService.deleteTask(1L, 1L);

        assertTrue(result);
        verify(taskRepository).delete(task);
    }

    @Test
    void testGetTasksForUser() {
        User user = new User(1L, "John", "Doe", "john@example.com", "password", Role.USER);
        Task task1 = new Task(1L, "Task 1", "Description 1", LocalDateTime.now(), LocalDateTime.now().plusDays(1), TaskStatus.TODO, user, user, Arrays.asList("tag1", "tag2"));
        Task task2 = new Task(2L, "Task 2", "Description 2", LocalDateTime.now(), LocalDateTime.now().plusDays(2), TaskStatus.IN_PROGRESS, user, user, Arrays.asList("tag2", "tag3"));

        when(taskRepository.findByAssignedUserId(1L)).thenReturn(Arrays.asList(task1, task2));

        List<TaskDTO> taskDTOs = taskService.getTasksForUser(1L);

        assertEquals(2, taskDTOs.size());
        assertEquals("Task 1", taskDTOs.get(0).getTitle());
        assertEquals("Task 2", taskDTOs.get(1).getTitle());
    }

    @Test
    void testUpdateTaskStatus() {
        User user = new User(1L, "John", "Doe", "john@example.com", "password", Role.USER);
        Task task = new Task(1L, "Task 1", "Description 1", LocalDateTime.now(), LocalDateTime.now().plusDays(1), TaskStatus.TODO, user, user, Arrays.asList("tag1", "tag2"));

        when(taskRepository.findById(1L)).thenReturn(task);

        taskService.updateTaskStatus(1L, TaskStatus.IN_PROGRESS);

        assertEquals(TaskStatus.IN_PROGRESS, task.getStatus());
        verify(taskRepository).update(task);
    }

    @Test
    void testAssignTaskToSelf() {
        User user = new User(1L, "John", "Doe", "john@example.com", "password", Role.USER);
        Task task = new Task(1L, "Task 1", "Description 1", LocalDateTime.now(), LocalDateTime.now().plusDays(1), TaskStatus.TODO, null, user, Arrays.asList("tag1", "tag2"));

        when(taskRepository.findById(1L)).thenReturn(task);
        when(userRepository.findById(1L)).thenReturn(user);

        taskService.assignTaskToSelf(1L, 1L);

        assertEquals(user, task.getAssignedUser());
        verify(taskRepository).update(task);
    }
}