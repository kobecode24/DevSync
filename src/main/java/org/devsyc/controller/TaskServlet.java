package org.devsyc.controller;

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

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@WebServlet("/tasks/*")
public class TaskServlet extends HttpServlet {

    private TaskService taskService;
    private UserService userService;

    @Override
    public void init() throws ServletException {
        taskService = new TaskService();
        userService = new UserService();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            listTasks(req, resp);
        } else if (pathInfo.equals("/add")) {
            showAddForm(req, resp);
        } else if (pathInfo.startsWith("/edit/")) {
            showEditForm(req, resp);
        } else {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            addTask(req, resp);
        } else if (pathInfo.startsWith("/edit/")) {
            updateTask(req, resp);
        } else if (pathInfo.startsWith("/delete/")) {
            deleteTask(req, resp);
        } else {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    private void listTasks(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<TaskDTO> tasks = taskService.getAllTaskDTOs();
        req.setAttribute("tasks", tasks);
        req.getRequestDispatcher("/tasks.jsp").forward(req, resp);
    }

    private void showAddForm(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<User> users = userService.getAllUsers();
        req.setAttribute("users", users);
        req.getRequestDispatcher("/addTask.jsp").forward(req, resp);
    }

    private void showEditForm(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Long taskId = Long.parseLong(req.getPathInfo().split("/")[2]);
        Task task = taskService.getTaskById(taskId);
        List<User> users = userService.getAllUsers();

        String tagsAsString = String.join(",", task.getTags());

        req.setAttribute("task", task);
        req.setAttribute("tagsAsString", tagsAsString);
        req.setAttribute("users", users);
        req.getRequestDispatcher("/editTask.jsp").forward(req, resp);
    }

    private void addTask(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Task task = createTaskFromRequest(req);
        taskService.createTask(task);
        resp.sendRedirect(req.getContextPath() + "/tasks");
    }

    private void updateTask(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Long taskId = Long.parseLong(req.getPathInfo().split("/")[2]);
        Task task = createTaskFromRequest(req);
        task.setId(taskId);
        taskService.updateTask(task);
        resp.sendRedirect(req.getContextPath() + "/tasks");
    }

    private void deleteTask(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();
        String[] pathParts = pathInfo.split("/");
        if (pathParts.length == 3) {
            try {
                Long taskId = Long.parseLong(pathParts[2]);
                boolean deleted = taskService.deleteTask(taskId);
                if (deleted) {
                    resp.sendRedirect(req.getContextPath() + "/tasks");
                } else {
                    resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Task not found");
                }
            } catch (NumberFormatException e) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid task ID");
            }
        } else {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid delete request");
        }
    }

    private Task createTaskFromRequest(HttpServletRequest req) {
        String title = req.getParameter("title");
        String description = req.getParameter("description");
        LocalDateTime dueDate = LocalDateTime.parse(req.getParameter("dueDate"), DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        Long assignedUserId = Long.parseLong(req.getParameter("assignedUserId"));
        User assignedUser = userService.getUserById(assignedUserId);
        TaskStatus status = TaskStatus.valueOf(req.getParameter("status"));
        List<String> tags = Arrays.asList(req.getParameter("tags").split(","));

        Task task = new Task();
        task.setTitle(title);
        task.setDescription(description);
        task.setDueDate(dueDate);
        task.setCreationDate(LocalDateTime.now());
        task.setAssignedUser(assignedUser);
        task.setStatus(status);
        task.setTags(tags);

        return task;
    }
}
