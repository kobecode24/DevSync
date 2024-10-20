package org.devsyc.service;

import org.devsyc.domain.entities.User;
import org.devsyc.domain.enums.Role;
import org.devsyc.repository.UserRepositoryHibernate;
import org.devsyc.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {

    private static UserService userService;
    private static UserRepositoryHibernate userRepository;

    @BeforeAll
    static void setupClass() {
        userRepository = new UserRepositoryHibernate();
        userService = new UserService(userRepository);
    }

    @AfterAll
    static void tearDownClass() {
        HibernateUtil.shutdown();
    }

    @BeforeEach
    void setUp() {
        clearDatabase();
    }

    @AfterEach
    void tearDown() {
        clearDatabase();
    }

    @Test
    void testGetAllUsers() {
        User user1 = new User(null, "John", "Doe", "john@example.com", "password", Role.USER);
        User user2 = new User(null, "Jane", "Doe", "jane@example.com", "password", Role.MANAGER);
        userService.createUser(user1);
        userService.createUser(user2);

        List<User> users = userService.getAllUsers();

        assertEquals(2, users.size());
        assertTrue(users.stream().anyMatch(u -> "John".equals(u.getFirstName())));
        assertTrue(users.stream().anyMatch(u -> "Jane".equals(u.getFirstName())));
    }

    @DisplayName("Test get all users when there are no users")
    @Test
    void testGetUserById() {
        User user = new User(null, "John", "Doe", "john@example.com", "password", Role.USER);
        userService.createUser(user);

        User result = userService.getUserById(user.getId());

        assertNotNull(result);
        assertEquals("John", result.getFirstName());
    }

    @Test
    void testUpdateUser() {
        User user = new User(null, "John", "Doe", "john@example.com", "password", Role.USER);
        userService.createUser(user);

        user.setFirstName("Johnny");
        userService.updateUser(user);

        User updatedUser = userService.getUserById(user.getId());
        assertEquals("Johnny", updatedUser.getFirstName());
    }

    @Test
    void testDeleteUser() {
        User user = new User(null, "John", "Doe", "john@example.com", "password", Role.USER);
        userService.createUser(user);

        userService.deleteUser(user.getId());

        User deletedUser = userService.getUserById(user.getId());
        assertNull(deletedUser);
    }

    @Test
    void testGetUserByEmail() {
        User user = new User(null, "John", "Doe", "john@example.com", "password", Role.USER);
        userService.createUser(user);

        User result = userService.getUserByEmail("john@example.com");

        assertNotNull(result);
        assertEquals("John", result.getFirstName());
    }

    private void clearDatabase() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            session.createQuery("DELETE FROM User").executeUpdate();
            transaction.commit();
        }
    }
}
