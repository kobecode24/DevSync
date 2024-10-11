package org.devsyc.controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import jakarta.servlet.annotation.MultipartConfig;
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

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import jakarta.servlet.http.Part;

@WebServlet("/employee-tasks")
@MultipartConfig
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
                                    taskMap.put("pendingRequests", task.getPendingRequests());
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
        Map<String, String> params = new HashMap<>();
        String contentType = req.getContentType();

        if (contentType != null && contentType.startsWith("multipart/form-data")) {
            Collection<Part> parts = req.getParts();
            for (Part part : parts) {
                String name = part.getName();
                String value = new String(part.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
                params.put(name, value);
            }
        }
        System.out.println("Parsed parameters: " + params);

        String action = params.get("action");
        String taskIdStr = params.get("taskId");
        String userIdStr = params.get("userId");

        System.out.println("Received parameters: action=" + action + ", taskId=" + taskIdStr + ", userId=" + userIdStr);

        JsonObject jsonResponse = new JsonObject();

        try {
            // Check for missing parameters
            if (action == null || taskIdStr == null || userIdStr == null) {
                throw new IllegalArgumentException("Missing required parameters");
            }

            // Attempt to parse taskId and userId as Longs
            Long taskId = Long.parseLong(taskIdStr);
            Long userId = Long.parseLong(userIdStr);

            // Retrieve the user based on the provided userId
            User user = userService.getUserById(userId);

            // Handle the action parameter
            switch (action) {
                case "updateStatus":
                    String newStatus = params.get("status");
                    taskService.updateTaskStatus(taskId, TaskStatus.valueOf(newStatus));
                    break;
                case "requestEdit":
                    taskService.requestEditTask(taskId, userId);
                    break;
                case "requestDelete":
                    taskService.requestDeleteTask(taskId, userId);
                    break;
                default:
                    throw new IllegalArgumentException("Invalid action: " + action);
            }

            jsonResponse.addProperty("success", true);

        } catch (NumberFormatException e) {
            jsonResponse.addProperty("success", false);
            jsonResponse.addProperty("error", "Invalid taskId or userId");
        } catch (IllegalArgumentException | IllegalStateException e) {
            jsonResponse.addProperty("success", false);
            jsonResponse.addProperty("error", e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            jsonResponse.addProperty("success", false);
            jsonResponse.addProperty("error", "An unexpected error occurred: " + e.getMessage());
        }

        // Set response content type and send the JSON response
        resp.setContentType("application/json");
        resp.getWriter().write(new Gson().toJson(jsonResponse));
    }

}
