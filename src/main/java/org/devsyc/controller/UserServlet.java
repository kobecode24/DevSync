package org.devsyc.controller;

import org.devsyc.domain.entities.User;
import org.devsyc.domain.enums.Role;
import org.devsyc.repository.UserRepositoryHibernate;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet(urlPatterns = {"/users", "/users/add"})
public class UserServlet extends HttpServlet {
    private static final String USER_LIST_PATH = "/users";
    private static final String ADD_USER_PATH = "/users/add";

    private UserRepositoryHibernate userRepository;

    @Override
    public void init() throws ServletException {
        userRepository = new UserRepositoryHibernate();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        String path = req.getServletPath();

        if (ADD_USER_PATH.equals(path)) {
            req.getRequestDispatcher("/addUser.jsp").forward(req, res);
        } else if (USER_LIST_PATH.equals(path)) {
            String action = req.getParameter("action");
            if ("edit".equals(action)) {
                Long userId = Long.valueOf(req.getParameter("id"));
                User user = userRepository.findById(userId);
                req.setAttribute("user", user);
                req.getRequestDispatcher("/editUser.jsp").forward(req, res);
            } else {
                List<User> users = userRepository.findAll();
                req.setAttribute("users", users);
                req.getRequestDispatcher("/users.jsp").forward(req, res);
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        String action = req.getParameter("action");

        try {
            if ("add".equals(action)) {
                addUser(req);
            } else if ("update".equals(action)) {
                updateUser(req);
            } else if ("delete".equals(action)) {
                deleteUser(req);
            }
            res.sendRedirect(req.getContextPath() + USER_LIST_PATH);
        } catch (Exception e) {
            e.printStackTrace();
            req.setAttribute("error", "An error occurred: " + e.getMessage());
            req.getRequestDispatcher("/error.jsp").forward(req, res);
        }
    }

    private void addUser(HttpServletRequest req) {
        String firstName = req.getParameter("firstName");
        String lastName = req.getParameter("lastName");
        String email = req.getParameter("email");
        String password = req.getParameter("password");
        Role role = Role.valueOf(req.getParameter("role"));

        User newUser = new User(firstName, lastName, email, password, role);
        userRepository.save(newUser);
    }

    private void updateUser(HttpServletRequest req) {
        Long userId = Long.valueOf(req.getParameter("id"));
        String firstName = req.getParameter("firstName");
        String lastName = req.getParameter("lastName");
        String email = req.getParameter("email");
        String password = req.getParameter("password");
        Role role = Role.valueOf(req.getParameter("role"));

        User user = userRepository.findById(userId);
        if (user != null) {
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setEmail(email);
            user.setPassword(password);
            user.setRole(role);
            userRepository.update(user);
        } else {
            throw new IllegalArgumentException("User not found with id: " + userId);
        }
    }

    private void deleteUser(HttpServletRequest req) {
        Long userId = Long.valueOf(req.getParameter("id"));
        User user = userRepository.findById(userId);
        if (user != null) {
            userRepository.delete(user);
        } else {
            throw new IllegalArgumentException("User not found with id: " + userId);
        }
    }
}