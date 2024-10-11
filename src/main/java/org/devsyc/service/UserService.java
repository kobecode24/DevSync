package org.devsyc.service;

import jakarta.ejb.Stateless;
import jakarta.enterprise.context.ApplicationScoped;
import org.devsyc.domain.entities.User;
import org.devsyc.repository.UserRepositoryHibernate;
import org.devsyc.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.time.LocalDate;
import java.util.List;

@ApplicationScoped
public class UserService {
    private UserRepositoryHibernate userRepository = new UserRepositoryHibernate();

    public List<User> getAllUsers() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM User", User.class).list();
        }
    }

    public User getUserById(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(User.class, id);
        }
    }

    public void createUser(User user) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.save(user);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    public void updateUser(User user) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.update(user);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    public void deleteUser(Long id) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            User user = session.get(User.class, id);
            if (user != null) {
                transaction = session.beginTransaction();
                session.delete(user);
                transaction.commit();
            }
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    public User getUserByEmail(String email) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM User WHERE email = :email", User.class)
                    .setParameter("email", email)
                    .uniqueResult();
        }
    }

    public void resetTokensIfNeeded(User user) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            LocalDate now = LocalDate.now();
            if (!now.equals(user.getLastTokenReset())) {
                user.resetDailyTokens();
                if (now.getMonth() != user.getLastTokenReset().getMonth()) {
                    user.resetMonthlyTokens();
                }
                user.setLastTokenReset(now);

                transaction = session.beginTransaction();
                session.update(user);
                transaction.commit();
            }
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    public boolean useReplacementToken(Long userId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            User user = session.get(User.class, userId);
            resetTokensIfNeeded(user);
            if (user.useReplacementToken()) {
                Transaction transaction = session.beginTransaction();
                session.update(user);
                transaction.commit();
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean useDeletionToken(Long userId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            User user = session.get(User.class, userId);
            resetTokensIfNeeded(user);
            if (user.useDeletionToken()) {
                Transaction transaction = session.beginTransaction();
                session.update(user);
                transaction.commit();
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public void resetAllTokens() {
        List<User> users = userRepository.findAll();
        LocalDate today = LocalDate.now();

        for (User user : users) {
            if (!user.getLastTokenReset().isEqual(today)) {
                user.resetDailyTokens();
                if (today.getMonthValue() != user.getLastTokenReset().getMonthValue()) {
                    user.resetMonthlyTokens();
                }
                user.setLastTokenReset(today);
                userRepository.update(user);
            }
        }
    }

    public void decrementReplacementToken(User user) {
        if (user.getReplacementTokens() > 0) {
            user.setReplacementTokens(user.getReplacementTokens() - 1);
            // In real application, update the database
        }
    }

    public void decrementDeletionToken(User user) {
        if (user.getDeletionTokens() > 0) {
            user.setDeletionTokens(user.getDeletionTokens() - 1);
            // In real application, update the database
        }
    }

    public void resetDailyTokens() {
        List<User> users = userRepository.findAll();
        for (User user : users) {
            user.setReplacementTokens(2);
            userRepository.update(user);
        }
    }

    public void resetMonthlyTokens() {
        List<User> users = userRepository.findAll();
        for (User user : users) {
            user.setDeletionTokens(1);
            userRepository.update(user);
        }
    }

}
