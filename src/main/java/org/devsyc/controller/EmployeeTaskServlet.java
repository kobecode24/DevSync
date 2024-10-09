package org.devsyc.controller;

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
        List<TaskDTO> tasks = taskService.getAllTaskDTOs();
        req.setAttribute("tasks", tasks);
        req.getRequestDispatcher("/employeeTasks.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        long taskId = Long.parseLong(req.getParameter("taskId"));
        String newStatus = req.getParameter("status");
        taskService.updateTaskStatus(taskId, TaskStatus.valueOf(newStatus));
        resp.sendRedirect(req.getContextPath() + "/employee-tasks");
    }
}