package org.devsyc.service;

import org.devsyc.domain.entities.User;
import org.devsyc.domain.enums.Role;
import org.devsyc.repository.UserRepositoryHibernate;
import org.devsyc.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.jupiter.api.*;

import java.time.LocalDate;
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

    @Test
    void testGetUserById() {
        User user = new User(null, "John", "Doe", "john@example.com", "password", Role.USER);
        userService.createUser(user);

        User result = userService.getUserById(user.getId());

        assertNotNull(result);
        assertEquals("John", result.getFirstName());
    }

    @Test
    void testCreateUser() {
        User user = new User(null, "John", "Doe", "john@example.com", "password", Role.USER);

        userService.createUser(user);

        User result = userService.getUserByEmail("john@example.com");
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

    @Test
    void testResetTokensIfNeeded() {
        User user = new User(null, "John", "Doe", "john@example.com", "password", Role.USER);
        user.setLastTokenReset(LocalDate.now().minusDays(1));

        User createdUser = userService.createUser(user);

        User updatedUser = userService.resetTokensIfNeeded(createdUser);

        assertNotNull(updatedUser);
        assertEquals(2, updatedUser.getReplacementTokens(), "Replacement tokens should be reset to 2");
        assertEquals(1, updatedUser.getDeletionTokens(), "Deletion tokens should be reset to 1");
        assertEquals(LocalDate.now(), updatedUser.getLastTokenReset(), "Last token reset should be today");

        // Verify that the changes were actually persisted
        User retrievedUser = userService.getUserById(updatedUser.getId());
        assertNotNull(retrievedUser);
        assertEquals(2, retrievedUser.getReplacementTokens(), "Persisted replacement tokens should be 2");
        assertEquals(1, retrievedUser.getDeletionTokens(), "Persisted deletion tokens should be 1");
        assertEquals(LocalDate.now(), retrievedUser.getLastTokenReset(), "Persisted last token reset should be today");
    }

    @Test
    void testUseReplacementToken() {
        User user = new User(null, "John", "Doe", "john@example.com", "password", Role.USER);
        user.setReplacementTokens(1);
        userService.createUser(user);

        boolean result = userService.useReplacementToken(user.getId());

        assertTrue(result);
        User updatedUser = userService.getUserById(user.getId());
        assertEquals(0, updatedUser.getReplacementTokens());
    }

    @Test
    void testUseDeletionToken() {
        User user = new User(null, "John", "Doe", "john@example.com", "password", Role.USER);
        user.setDeletionTokens(1);
        userService.createUser(user);

        boolean result = userService.useDeletionToken(user.getId());

        assertTrue(result);
        User updatedUser = userService.getUserById(user.getId());
        assertEquals(0, updatedUser.getDeletionTokens());
    }

    @Test
    void testResetAllTokens() {
        User user1 = new User(null, "John", "Doe", "john@example.com", "password", Role.USER);
        user1.setLastTokenReset(LocalDate.now().minusDays(1));
        User user2 = new User(null, "Jane", "Doe", "jane@example.com", "password", Role.MANAGER);
        user2.setLastTokenReset(LocalDate.now().minusDays(1));
        userService.createUser(user1);
        userService.createUser(user2);

        userService.resetAllTokens();

        User updatedUser1 = userService.getUserById(user1.getId());
        User updatedUser2 = userService.getUserById(user2.getId());

        assertEquals(2, updatedUser1.getReplacementTokens());
        assertEquals(1, updatedUser1.getDeletionTokens());
        assertEquals(LocalDate.now(), updatedUser1.getLastTokenReset());

        assertEquals(2, updatedUser2.getReplacementTokens());
        assertEquals(1, updatedUser2.getDeletionTokens());
        assertEquals(LocalDate.now(), updatedUser2.getLastTokenReset());
    }

    private void clearDatabase() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            session.createQuery("DELETE FROM User").executeUpdate();
            transaction.commit();
        }
    }
}
