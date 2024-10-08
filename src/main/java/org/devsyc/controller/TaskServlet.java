package org.devsyc.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.devsyc.domain.entities.Task;
import org.devsyc.domain.entities.User;
import org.devsyc.domain.enums.TaskStatus;
import org.devsyc.repository.TaskRepositoryHibernate;
import org.devsyc.repository.UserRepositoryHibernate;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

@WebServlet("/tasks/*")
public class TaskServlet extends HttpServlet {

    private TaskRepositoryHibernate taskRepository;
    private UserRepositoryHibernate userRepository;

    @Override
    public void init() throws ServletException {
        taskRepository = new TaskRepositoryHibernate();
        userRepository = new UserRepositoryHibernate();
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
        List<Task> tasks = taskRepository.findAll();
        req.setAttribute("tasks", tasks);
        req.getRequestDispatcher("/tasks.jsp").forward(req, resp);
    }

    private void showAddForm(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<User> users = userRepository.findAll();
        req.setAttribute("users", users);
        req.getRequestDispatcher("/addTask.jsp").forward(req, resp);
    }

    private void showEditForm(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Long taskId = Long.parseLong(req.getPathInfo().split("/")[2]);
        Task task = taskRepository.findById(taskId);
        List<User> users = userRepository.findAll();
        req.setAttribute("task", task);
        req.setAttribute("users", users);
        req.getRequestDispatcher("/editTask.jsp").forward(req, resp);
    }

    private void addTask(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Task task = createTaskFromRequest(req);
        taskRepository.save(task);
        resp.sendRedirect(req.getContextPath() + "/tasks");
    }

    private void updateTask(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Long taskId = Long.parseLong(req.getPathInfo().split("/")[2]);
        Task task = createTaskFromRequest(req);
        task.setId(taskId);
        taskRepository.update(task);
        resp.sendRedirect(req.getContextPath() + "/tasks");
    }

    private void deleteTask(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Long taskId = Long.parseLong(req.getPathInfo().split("/")[2]);
        Task task = taskRepository.findById(taskId);
        taskRepository.delete(task);
        resp.sendRedirect(req.getContextPath() + "/tasks");
    }

    private Task createTaskFromRequest(HttpServletRequest req) {
        String title = req.getParameter("title");
        String description = req.getParameter("description");
        LocalDateTime dueDate = LocalDateTime.parse(req.getParameter("dueDate"), DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        Long assignedUserId = Long.parseLong(req.getParameter("assignedUserId"));
        User assignedUser = userRepository.findById(assignedUserId);
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
