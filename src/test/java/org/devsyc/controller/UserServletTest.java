package org.devsyc.controller;

import org.devsyc.domain.entities.User;
import org.devsyc.domain.enums.Role;
import org.devsyc.repository.UserRepositoryHibernate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;

class UserServletTest {

    @Mock private UserRepositoryHibernate userRepository;
    @Mock private HttpServletRequest request;
    @Mock private HttpServletResponse response;
    @Mock private RequestDispatcher requestDispatcher;

    private UserServlet userServlet;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userServlet = new UserServlet();
        userServlet.userRepository = userRepository;
    }

    @Test
    void testDoGetListUsers() throws Exception {
        List<User> users = Arrays.asList(
                new User("John", "Doe", "john@example.com", "password", Role.USER),
                new User("Jane", "Doe", "jane@example.com", "password", Role.MANAGER)
        );
        when(userRepository.findAll()).thenReturn(users);
        when(request.getServletPath()).thenReturn("/users");
        when(request.getRequestDispatcher("/users.jsp")).thenReturn(requestDispatcher);

        userServlet.doGet(request, response);

        verify(request).setAttribute("users", users);
        verify(requestDispatcher).forward(request, response);
    }

    @Test
    void testDoPostAddUser() throws Exception {
        when(request.getParameter("action")).thenReturn("add");
        when(request.getParameter("firstName")).thenReturn("New");
        when(request.getParameter("lastName")).thenReturn("User");
        when(request.getParameter("email")).thenReturn("new@example.com");
        when(request.getParameter("password")).thenReturn("password");
        when(request.getParameter("role")).thenReturn("USER");

        userServlet.doPost(request, response);

        verify(userRepository).save(any(User.class));
        verify(response).sendRedirect(request.getContextPath() + "/users");
    }

    @Test
    void testDoPostUpdateUser() throws Exception {
        when(request.getParameter("action")).thenReturn("update");
        when(request.getParameter("id")).thenReturn("1");
        when(request.getParameter("firstName")).thenReturn("Updated");
        when(request.getParameter("lastName")).thenReturn("User");
        when(request.getParameter("email")).thenReturn("updated@example.com");
        when(request.getParameter("password")).thenReturn("newpassword");
        when(request.getParameter("role")).thenReturn("MANAGER");

        User existingUser = new User();
        existingUser.setId(1L);
        when(userRepository.findById(1L)).thenReturn(existingUser);

        userServlet.doPost(request, response);

        verify(userRepository).update(any(User.class));
        verify(response).sendRedirect(request.getContextPath() + "/users");
    }

    @Test
    void testDoPostDeleteUser() throws Exception {
        when(request.getParameter("action")).thenReturn("delete");
        when(request.getParameter("id")).thenReturn("1");

        User userToDelete = new User();
        userToDelete.setId(1L);
        when(userRepository.findById(1L)).thenReturn(userToDelete);

        userServlet.doPost(request, response);

        verify(userRepository).delete(userToDelete);
        verify(response).sendRedirect(request.getContextPath() + "/users");
    }
}