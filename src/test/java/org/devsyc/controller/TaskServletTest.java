package org.devsyc.controller;

import org.devsyc.service.TaskService;
import org.devsyc.service.UserService;
import org.devsyc.domain.entities.Task;
import org.devsyc.domain.entities.User;
import org.devsyc.dto.TaskDTO;
import org.devsyc.domain.enums.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;

class TaskServletTest {

    @Mock private TaskService taskService;
    @Mock private UserService userService;
    @Mock private HttpServletRequest request;
    @Mock private HttpServletResponse response;
    @Mock private RequestDispatcher requestDispatcher;

    private TaskServlet taskServlet;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        taskServlet = new TaskServlet();
        taskServlet.taskService = taskService;
        taskServlet.userService = userService;
    }

    @Test
    void testDoGetListTasks() throws Exception {
        List<TaskDTO> tasks = Arrays.asList(
                new TaskDTO(1L, "Task 1", "Description 1", null, TaskStatus.TODO, Arrays.asList("tag1", "tag2"), "User 1" , null),
                new TaskDTO(2L, "Task 2", "Description 2", null, TaskStatus.IN_PROGRESS, Arrays.asList("tag2", "tag3"), "User 2" , null)
        );
        when(taskService.getAllTaskDTOs()).thenReturn(tasks);
        when(request.getRequestDispatcher("/tasks.jsp")).thenReturn(requestDispatcher);

        taskServlet.doGet(request, response);

        verify(request).setAttribute("tasks", tasks);
        verify(requestDispatcher).forward(request, response);
    }

    @Test
    void testDoPostCreateTask() throws Exception {
        when(request.getParameter("title")).thenReturn("New Task");
        when(request.getParameter("description")).thenReturn("Task Description");
        when(request.getParameter("dueDate")).thenReturn("2023-06-01T10:00");
        when(request.getParameter("assignedUserId")).thenReturn("1");
        when(request.getParameter("status")).thenReturn("TODO");
        when(request.getParameter("tags")).thenReturn("tag1,tag2");

        User user = new User();
        user.setId(1L);
        when(userService.getUserById(1L)).thenReturn(user);

        taskServlet.doPost(request, response);

        verify(taskService).createTask(any(Task.class));
        verify(response).sendRedirect(request.getContextPath() + "/tasks");
    }

    @Test
    void testDoPostUpdateTask() throws Exception {
        when(request.getPathInfo()).thenReturn("/edit/1");
        when(request.getParameter("title")).thenReturn("Updated Task");
        when(request.getParameter("description")).thenReturn("Updated Description");
        when(request.getParameter("dueDate")).thenReturn("2023-06-01T10:00");
        when(request.getParameter("assignedUserId")).thenReturn("1");
        when(request.getParameter("status")).thenReturn("IN_PROGRESS");
        when(request.getParameter("tags")).thenReturn("tag1,tag2,tag3");

        User user = new User();
        user.setId(1L);
        when(userService.getUserById(1L)).thenReturn(user);

        Task existingTask = new Task();
        existingTask.setId(1L);
        when(taskService.getTaskById(1L)).thenReturn(existingTask);

        taskServlet.doPost(request, response);

        verify(taskService).updateTask(any(Task.class));
        verify(response).sendRedirect(request.getContextPath() + "/tasks");
    }

    @Test
    void testDoPostDeleteTask() throws Exception {
        when(request.getPathInfo()).thenReturn("/delete/1");
        when(request.getParameter("userId")).thenReturn("1");

        when(taskService.deleteTask(1L, 1L)).thenReturn(true);

        taskServlet.doPost(request, response);

        verify(taskService).deleteTask(1L, 1L);
        verify(response).sendRedirect(request.getContextPath() + "/tasks");
    }
}