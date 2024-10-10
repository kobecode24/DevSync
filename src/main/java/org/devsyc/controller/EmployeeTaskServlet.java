package org.devsyc.controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.devsyc.domain.entities.User;
import org.devsyc.domain.enums.TaskStatus;
import org.devsyc.dto.TaskDTO;
import org.devsyc.service.TaskService;
import org.devsyc.service.UserService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@WebServlet("/employee-tasks")
public class EmployeeTaskServlet extends HttpServlet {
    private TaskService taskService;
    private UserService userService;

    @Override
    public void init() throws ServletException {
        taskService = new TaskService();
        userService = new UserService();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Retrieve all users and their tasks
        List<User> allUsers = userService.getAllUsers();
        System.out.println("Number of users: " + allUsers.size());

        Map<User, List<TaskDTO>> userTasksMap = allUsers.stream()
                .collect(Collectors.toMap(
                        user -> user,
                        user -> {
                            List<TaskDTO> tasks = taskService.getTasksForUser(user.getId());
                            System.out.println("User " + user.getId() + " has " + tasks.size() + " tasks");
                            return tasks;
                        }
                ));

        // Format due dates as strings to avoid EL conversion issues in JSP
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        Map<User, List<Map<String, Object>>> formattedTasksMap = userTasksMap.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().stream()
                                .map(task -> {
                                    Map<String, Object> taskMap = new HashMap<>();
                                    taskMap.put("id", task.getId());
                                    taskMap.put("title", task.getTitle());
                                    taskMap.put("description", task.getDescription());
                                    taskMap.put("dueDate", task.getDueDate().format(formatter));
                                    taskMap.put("status", task.getStatus());
                                    taskMap.put("tags", task.getTags());
                                    taskMap.put("assignedUserName", task.getAssignedUserName());
                                    return taskMap;
                                })
                                .collect(Collectors.toList())
                ));

        System.out.println("Formatted tasks map size: " + formattedTasksMap.size());

        // Set attributes for JSP page
        req.setAttribute("formattedTasksMap", formattedTasksMap);

        req.getRequestDispatcher("/employeeTasks.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");
        Long taskId = Long.parseLong(req.getParameter("taskId"));
        Long userId = Long.parseLong(req.getParameter("userId"));

        JsonObject jsonResponse = new JsonObject();

        try {
            // Retrieve the user
            User user = userService.getUserById(userId);

            switch (action) {
                case "updateStatus":
                    String newStatus = req.getParameter("status");
                    taskService.updateTaskStatus(taskId, TaskStatus.valueOf(newStatus));
                    break;
                case "requestEdit":
                    taskService.requestEditTask(taskId, userId);
                    break;
                case "requestDelete":
                    taskService.requestDeleteTask(taskId, userId);
                    break;
                case "replaceTask":
                    if (user.getReplacementTokens() > 0) {
                        taskService.replaceTask(taskId, userId);
                        userService.decrementReplacementToken(user);
                    } else {
                        throw new IllegalStateException("Not enough replacement tokens available.");
                    }
                    break;
                case "deleteTask":
                    if (user.getDeletionTokens() > 0) {
                        taskService.deleteTask(taskId, userId);
                        userService.decrementDeletionToken(user);
                    } else {
                        throw new IllegalStateException("Not enough deletion tokens available.");
                    }
                    break;
                default:
                    throw new IllegalArgumentException("Invalid action: " + action);
            }

            jsonResponse.addProperty("success", true);
        } catch (IllegalStateException | IllegalArgumentException e) {
            jsonResponse.addProperty("success", false);
            jsonResponse.addProperty("error", e.getMessage());
        }

        resp.setContentType("application/json");
        resp.getWriter().write(new Gson().toJson(jsonResponse));
    }
}
