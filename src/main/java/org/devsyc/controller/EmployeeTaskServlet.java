package org.devsyc.controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import jakarta.servlet.annotation.MultipartConfig;
import org.devsyc.domain.entities.Task;
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
import jakarta.servlet.http.Part;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@WebServlet("/employee-tasks")
@MultipartConfig
public class EmployeeTaskServlet extends HttpServlet {
    TaskService taskService;
    UserService userService;

    @Override
    public void init() throws ServletException {
        taskService = new TaskService();
        userService = new UserService();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
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
                                    taskMap.put("dueDate", task.getDueDate() != null ? task.getDueDate().format(formatter) : "N/A");
                                    taskMap.put("status", task.getStatus());
                                    taskMap.put("tags", task.getTags());
                                    taskMap.put("assignedUserName", task.getAssignedUserName());
                                    taskMap.put("pendingRequests", task.getPendingRequests());
                                    return taskMap;
                                })
                                .collect(Collectors.toList())
                ));

        System.out.println("Formatted tasks map size: " + formattedTasksMap.size());

        req.setAttribute("formattedTasksMap", formattedTasksMap);
        req.getRequestDispatcher("/employeeTasks.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Map<String, String> params = new HashMap<>();
        String contentType = req.getContentType();

        if (contentType != null && contentType.startsWith("multipart/form-data")) {
            Collection<Part> parts = req.getParts();
            if (!parts.isEmpty()) {
                for (Part part : parts) {
                    String name = part.getName();
                    String value = new String(part.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
                    params.put(name, value);
                }
            } else {
                // Handle the case when parts are empty (for testing purposes)
                Enumeration<String> parameterNames = req.getParameterNames();
                while (parameterNames.hasMoreElements()) {
                    String paramName = parameterNames.nextElement();
                    params.put(paramName, req.getParameter(paramName));
                }
            }
        } else {
            Enumeration<String> parameterNames = req.getParameterNames();
            while (parameterNames.hasMoreElements()) {
                String paramName = parameterNames.nextElement();
                params.put(paramName, req.getParameter(paramName));
            }
        }

        System.out.println("Parsed parameters: " + params);

        String action = params.get("action");
        String taskIdStr = params.get("taskId");
        String userIdStr = params.get("userId");

        System.out.println("Received parameters: action=" + action + ", taskId=" + taskIdStr + ", userId=" + userIdStr);

        JsonObject jsonResponse = new JsonObject();

        try {
            if (action == null) {
                throw new IllegalArgumentException("Missing action parameter");
            }

            switch (action) {
                case "updateStatus":
                    Long taskId = Long.parseLong(taskIdStr);
                    String newStatus = params.get("status");
                    taskService.updateTaskStatus(taskId, TaskStatus.valueOf(newStatus));
                    break;
                case "requestEdit":
                    taskId = Long.parseLong(taskIdStr);
                    Long userId = Long.parseLong(userIdStr);
                    taskService.requestEditTask(taskId, userId);
                    break;
                case "requestDelete":
                    taskId = Long.parseLong(taskIdStr);
                    userId = Long.parseLong(userIdStr);
                    taskService.requestDeleteTask(taskId, userId);
                    break;
                case "addTask":
                    userId = Long.parseLong(userIdStr);
                    String title = params.get("title");
                    String description = params.get("description");
                    LocalDateTime dueDate = LocalDateTime.parse(params.get("dueDate"));
                    TaskStatus status = TaskStatus.valueOf(params.get("status"));
                    List<String> tags = Arrays.asList(params.get("tags").split(","));

                    User user = userService.getUserById(userId);
                    Task newTask = new Task(title, description, dueDate, user, user, tags);
                    newTask.setStatus(status);
                    taskService.createTask(newTask);
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

        resp.setContentType("application/json");
        resp.getWriter().write(new Gson().toJson(jsonResponse));
    }
}
