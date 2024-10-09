package org.devsyc.service;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.devsyc.domain.entities.User;

import java.time.LocalDate;
import java.util.List;

@Stateless
public class UserService {
    @PersistenceContext
    private EntityManager em;

    public List<User> getAllUsers() {
        return em.createQuery("SELECT u FROM User u", User.class).getResultList();
    }

    public User getUserById(Long id) {
        return em.find(User.class, id);
    }

    public void createUser(User user) {
        em.persist(user);
    }

    public void updateUser(User user) {
        em.merge(user);
    }

    public void deleteUser(Long id) {
        User user = getUserById(id);
        if (user != null) {
            em.remove(user);
        }
    }

    public User getUserByEmail(String email) {
        return em.createQuery("SELECT u FROM User u WHERE u.email = :email", User.class)
                .setParameter("email", email)
                .getSingleResult();
    }

    public void resetTokensIfNeeded(User user) {
        LocalDate now = LocalDate.now();
        if (!now.equals(user.getLastTokenReset())) {
            user.resetDailyTokens();
            if (now.getMonth() != user.getLastTokenReset().getMonth()) {
                user.resetMonthlyTokens();
            }
            user.setLastTokenReset(now);
            em.merge(user);
        }
    }

    public boolean useReplacementToken(Long userId) {
        User user = getUserById(userId);
        resetTokensIfNeeded(user);
        if (user.useReplacementToken()) {
            em.merge(user);
            return true;
        }
        return false;
    }

    public boolean useDeletionToken(Long userId) {
        User user = getUserById(userId);
        resetTokensIfNeeded(user);
        if (user.useDeletionToken()) {
            em.merge(user);
            return true;
        }
        return false;
    }
}