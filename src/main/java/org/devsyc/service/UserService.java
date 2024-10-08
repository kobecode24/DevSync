package org.devsyc.service;

import org.devsyc.domain.entities.User;
import org.devsyc.repository.UserRepositoryHibernate;

import java.util.List;

public class UserService {
    private UserRepositoryHibernate userRepository;

    public UserService() {
        this.userRepository = new UserRepositoryHibernate();
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(Long id) {
        return userRepository.findById(id);
    }

    public void createUser(User user) {
        userRepository.save(user);
    }

    public void updateUser(User user) {
        userRepository.update(user);
    }

    public void deleteUser(Long id) {
        User user = userRepository.findById(id);
        if (user != null) {
            userRepository.delete(user);
        }
    }

    // Additional methods for user management

    public User getUserByEmail(String email) {
        // Implement this method in UserRepositoryHibernate
        return userRepository.findByEmail(email);
    }

    public boolean isEmailUnique(String email) {
        User user = getUserByEmail(email);
        return user == null;
    }

    public void resetPassword(Long userId, String newPassword) {
        User user = getUserById(userId);
        if (user != null) {
            user.setPassword(newPassword);
            updateUser(user);
        }
    }

    public User authenticateUser(String email, String password) {
        User user = getUserByEmail(email);
        if (user != null && user.getPassword().equals(password)) {
            return user;
        }
        return null;
    }
}