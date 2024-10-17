package org.devsyc.controller;

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

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.*;

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

    @Test
    void testDoGetEmployeeTasks() throws Exception {
        List<User> users = Arrays.asList(
                new User(1L, "Johnx", "Doe", "john@example.com", "password", Role.USER),
                new User(2L, "Jane", "Doe", "jane@example.com", "password", Role.USER)
        );
        when(userService.getAllUsers()).thenReturn(users);

        Map<User, List<TaskDTO>> userTasksMap = new HashMap<>();
        for (User user : users) {
            List<TaskDTO> tasks = Arrays.asList(
                    new TaskDTO(1L, "Task 1", "Description 1", null, TaskStatus.TODO, Arrays.asList("tag1", "tag2"), user.getFirstName() + " " + user.getLastName(), null),
                    new TaskDTO(2L, "Task 2", "Description 2", null, TaskStatus.IN_PROGRESS, Arrays.asList("tag2", "tag3"), user.getFirstName() + " " + user.getLastName(), null)
            );
            userTasksMap.put(user, tasks);
            when(taskService.getTasksForUser(user.getId())).thenReturn(tasks);
        }

        when(request.getRequestDispatcher("/employeeTasks.jsp")).thenReturn(requestDispatcher);

        employeeTaskServlet.doGet(request, response);

        verify(request).setAttribute("userTasksMap", userTasksMap);
        verify(requestDispatcher).forward(request, response);
    }

    @Test
    void testDoPostUpdateTaskStatus() throws Exception {
        when(request.getParameter("action")).thenReturn("updateStatus");
        when(request.getParameter("taskId")).thenReturn("1");
        when(request.getParameter("userId")).thenReturn("1");
        when(request.getParameter("status")).thenReturn("IN_PROGRESS");

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);

        employeeTaskServlet.doPost(request, response);

        verify(taskService).updateTaskStatus(1L, TaskStatus.IN_PROGRESS);
        verify(response).setContentType("application/json");
        assert(stringWriter.toString().contains("\"success\":true"));
    }

    @Test
    void testDoPostReplaceTask() throws Exception {
        when(request.getParameter("action")).thenReturn("replaceTask");
        when(request.getParameter("taskId")).thenReturn("1");
        when(request.getParameter("userId")).thenReturn("1");

        User user = new User();
        user.setId(1L);
        user.setReplacementTokens(1);
        when(userService.getUserById(1L)).thenReturn(user);


        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);

        employeeTaskServlet.doPost(request, response);

        verify(taskService).replaceTask(1L, 1L);
        verify(userService).decrementReplacementToken(user);
        verify(response).setContentType("application/json");
        assert(stringWriter.toString().contains("\"success\":true"));
    }
}