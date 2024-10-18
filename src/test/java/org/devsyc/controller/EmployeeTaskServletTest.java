package org.devsyc.controller;

import org.devsyc.domain.entities.Task;
import org.devsyc.domain.entities.User;
import org.devsyc.domain.enums.Role;
import org.devsyc.dto.TaskDTO;
import org.devsyc.service.TaskService;
import org.devsyc.service.UserService;
import org.devsyc.domain.enums.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

import java.io.ByteArrayInputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class EmployeeTaskServletTest {

    @Mock private TaskService taskService;
    @Mock private UserService userService;
    @Mock private HttpServletRequest request;
    @Mock private HttpServletResponse response;
    @Mock private RequestDispatcher requestDispatcher;

    private EmployeeTaskServlet employeeTaskServlet;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        employeeTaskServlet = new EmployeeTaskServlet();
        employeeTaskServlet.taskService = taskService;
        employeeTaskServlet.userService = userService;
    }

    private void mockMultipartRequest(Map<String, String> params) throws Exception {
        when(request.getContentType()).thenReturn("multipart/form-data");
        List<Part> parts = new ArrayList<>();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            Part part = mock(Part.class);
            when(part.getName()).thenReturn(entry.getKey());
            when(part.getInputStream()).thenReturn(new ByteArrayInputStream(entry.getValue().getBytes()));
            parts.add(part);
        }
        when(request.getParts()).thenReturn(parts);
    }

    @Test
    void testDoGetEmployeeTasks() throws Exception {
        List<User> users = Arrays.asList(
                new User(1L, "John", "Doe", "john@example.com", "password", Role.USER),
                new User(2L, "Jane", "Doe", "jane@example.com", "password", Role.USER)
        );
        when(userService.getAllUsers()).thenReturn(users);

        Map<User, List<TaskDTO>> userTasksMap = new HashMap<>();
        for (User user : users) {
            List<TaskDTO> tasks = Arrays.asList(
                    new TaskDTO(1L, "Task 1", "Description 1", LocalDateTime.now().plusDays(1), TaskStatus.TODO, Arrays.asList("tag1", "tag2"), user.getFirstName() + " " + user.getLastName(), null),
                    new TaskDTO(2L, "Task 2", "Description 2", LocalDateTime.now().plusDays(2), TaskStatus.IN_PROGRESS, Arrays.asList("tag2", "tag3"), user.getFirstName() + " " + user.getLastName(), null)
            );
            userTasksMap.put(user, tasks);
            when(taskService.getTasksForUser(user.getId())).thenReturn(tasks);
        }

        when(request.getRequestDispatcher("/employeeTasks.jsp")).thenReturn(requestDispatcher);

        employeeTaskServlet.doGet(request, response);

        verify(request).setAttribute(eq("formattedTasksMap"), any());
        verify(requestDispatcher).forward(request, response);
    }

    @Test
    void testDoPostUpdateTaskStatus() throws Exception {
        Map<String, String> params = new HashMap<>();
        params.put("action", "updateStatus");
        params.put("taskId", "1");
        params.put("userId", "1");
        params.put("status", "IN_PROGRESS");

        mockMultipartRequest(params);

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);

        employeeTaskServlet.doPost(request, response);

        verify(taskService).updateTaskStatus(1L, TaskStatus.IN_PROGRESS);
        verify(response).setContentType("application/json");
        assertTrue(stringWriter.toString().contains("\"success\":true"));
    }

    @Test
    void testDoPostRequestEdit() throws Exception {
        Map<String, String> params = new HashMap<>();
        params.put("action", "requestEdit");
        params.put("taskId", "1");
        params.put("userId", "1");

        mockMultipartRequest(params);

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);

        employeeTaskServlet.doPost(request, response);

        verify(taskService).requestEditTask(1L, 1L);
        verify(response).setContentType("application/json");
        assertTrue(stringWriter.toString().contains("\"success\":true"));
    }

    @Test
    void testDoPostAddTask() throws Exception {
        Map<String, String> params = new HashMap<>();
        params.put("action", "addTask");
        params.put("userId", "1");
        params.put("title", "New Task");
        params.put("description", "New Description");
        params.put("dueDate", LocalDateTime.now().plusDays(1).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        params.put("status", "TODO");
        params.put("tags", "tag1,tag2");

        mockMultipartRequest(params);

        User user = new User(1L, "John", "Doe", "john@example.com", "password", Role.USER);
        when(userService.getUserById(1L)).thenReturn(user);

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);

        employeeTaskServlet.doPost(request, response);

        verify(taskService).createTask(any(Task.class));
        verify(response).setContentType("application/json");
        assertTrue(stringWriter.toString().contains("\"success\":true"));
    }

    @Test
    void testDoPostInvalidAction() throws Exception {
        Map<String, String> params = new HashMap<>();
        params.put("action", "invalidAction");

        mockMultipartRequest(params);

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);

        employeeTaskServlet.doPost(request, response);

        verify(response).setContentType("application/json");
        assertTrue(stringWriter.toString().contains("\"success\":false"));
        assertTrue(stringWriter.toString().contains("Invalid action"));
    }
}
