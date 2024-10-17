package org.devsyc.repository;

import org.devsyc.domain.entities.User;
import org.devsyc.domain.enums.Role;
import org.devsyc.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserRepositoryHibernateTest {

    private UserRepositoryHibernate userRepository;
    private Session session;
    private Transaction transaction;

    @BeforeEach
    void setUp() {
        userRepository = new UserRepositoryHibernate();
        session = HibernateUtil.getSessionFactory().openSession();
        transaction = session.beginTransaction();
    }

    @AfterEach
    void tearDown() {
        if (transaction.isActive()) {
            transaction.rollback();
        }
        session.close();
    }

    @Test
    void testSaveAndFindById() {
        User user = new User("John", "Doe", "john@example.com", "password", Role.USER);
        userRepository.save(user);

        User foundUser = userRepository.findById(user.getId());
        assertNotNull(foundUser);
        assertEquals("John", foundUser.getFirstName());
        assertEquals("john@example.com", foundUser.getEmail());
    }

    @Test
    void testFindAll() {
        User user1 = new User("John", "Doe", "john@example.com", "password", Role.USER);
        User user2 = new User("Jane", "Doe", "jane@example.com", "password", Role.MANAGER);

        userRepository.save(user1);
        userRepository.save(user2);

        List<User> users = userRepository.findAll();
        assertFalse(users.isEmpty());
        assertEquals(2, users.size());
        assertTrue(users.stream().anyMatch(u -> u.getEmail().equals("john@example.com")));
        assertTrue(users.stream().anyMatch(u -> u.getEmail().equals("jane@example.com")));
    }

    @Test
    void testUpdate() {
        User user = new User("John", "Doe", "john@example.com", "password", Role.USER);
        userRepository.save(user);

        user.setFirstName("Jonathan");
        user.setEmail("jonathan@example.com");
        userRepository.update(user);

        User updatedUser = userRepository.findById(user.getId());
        assertEquals("Jonathan", updatedUser.getFirstName());
        assertEquals("jonathan@example.com", updatedUser.getEmail());
    }

    @Test
    void testDelete() {
        User user = new User("John", "Doe", "john@example.com", "password", Role.USER);
        userRepository.save(user);

        Long userId = user.getId();
        userRepository.delete(user);

        User deletedUser = userRepository.findById(userId);
        assertNull(deletedUser);
    }

    @Test
    void testFindByEmail() {
        User user = new User("John", "Doe", "john@example.com", "password", Role.USER);
        userRepository.save(user);

        User foundUser = userRepository.findByEmail("john@example.com");
        assertNotNull(foundUser);
        assertEquals("John", foundUser.getFirstName());
        assertEquals("john@example.com", foundUser.getEmail());

        User nonExistentUser = userRepository.findByEmail("nonexistent@example.com");
        assertNull(nonExistentUser);
    }
}
