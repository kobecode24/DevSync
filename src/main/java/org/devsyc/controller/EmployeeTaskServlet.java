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
import java.util.List;

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
        Long userId = (Long) req.getSession().getAttribute("userId");
        User user = userService.getUserById(userId);
        userService.resetTokensIfNeeded(user);

        List<TaskDTO> userTasks = taskService.getTasksForUser(userId);
        List<TaskDTO> availableTasks = taskService.getAvailableTasks();

        req.setAttribute("userTasks", userTasks);
        req.setAttribute("availableTasks", availableTasks);
        req.setAttribute("replacementTokens", user.getReplacementTokens());
        req.setAttribute("deletionTokens", user.getDeletionTokens());

        req.getRequestDispatcher("/employeeTasks.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");
        Long userId = (Long) req.getSession().getAttribute("userId");
        Long taskId = Long.parseLong(req.getParameter("taskId"));

        JsonObject jsonResponse = new JsonObject();

        try {
            switch (action) {
                case "updateStatus":
                    String newStatus = req.getParameter("status");
                    taskService.updateTaskStatus(taskId, TaskStatus.valueOf(newStatus));
                    break;
                case "replaceTask":
                    taskService.replaceTask(taskId, userId);
                    break;
                case "deleteTask":
                    taskService.deleteTask(taskId, userId);
                    break;
                case "assignToSelf":
                    taskService.assignTaskToSelf(taskId, userId);
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
