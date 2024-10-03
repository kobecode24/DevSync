package org.devsyc.controller;

import org.devsyc.domain.entities.User;
import org.devsyc.domain.enums.Role;
import org.devsyc.repository.UserRepository;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.devsyc.repository.UserRepositoryHibernate;

import java.io.IOException;
import java.util.List;

@WebServlet("/users")
public class UserServlet extends HttpServlet {
    private UserRepositoryHibernate userRepository;

    @Override
    public void init() throws ServletException {
        userRepository = new UserRepositoryHibernate();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        String action = req.getParameter("action");

        try {
            if ("add".equals(action)) {
                String firstName = req.getParameter("firstName");
                String lastName = req.getParameter("lastName");
                String email = req.getParameter("email");
                String password = req.getParameter("password");
                Role role = Role.valueOf(req.getParameter("role"));

                User newUser = new User(null, firstName, lastName, email, password, role);
                userRepository.save(newUser);
            } else if ("update".equals(action)) {
                Long userId = Long.valueOf(req.getParameter("id"));
                String firstName = req.getParameter("firstName");
                String lastName = req.getParameter("lastName");
                String email = req.getParameter("email");
                String password = req.getParameter("password");
                Role role = Role.valueOf(req.getParameter("role"));

                User user = new User(userId, firstName, lastName, email, password, role);
                userRepository.update(user);
            } else if ("delete".equals(action)) {
                Long userId = Long.valueOf(req.getParameter("id"));
                User user = userRepository.findById(userId);
                if (user != null) {
                    userRepository.delete(user);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            req.setAttribute("error", "An error occurred: " + e.getMessage());
            req.getRequestDispatcher("/error.jsp").forward(req, res);
            return;
        }

        res.sendRedirect("users");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
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