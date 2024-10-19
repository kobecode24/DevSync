package org.devsyc.repository;

import org.devsyc.domain.entities.Task;
import org.devsyc.domain.entities.User;
import org.devsyc.domain.enums.TaskStatus;
import org.devsyc.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.time.LocalDateTime;
import java.util.List;

public class TaskRepositoryHibernate {

    public void save(Task task) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            session.save(task);
            tx.commit();
        }
    }

    public Task findById(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Task.class, id);
        }
    }

    public List<Task> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Task", Task.class).list();
        }
    }

    public List<Task> findByUser(User user) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Task WHERE assignedUser = :user", Task.class)
                    .setParameter("user", user)
                    .list();
        }
    }

    public List<Task> findByStatus(TaskStatus status) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Task WHERE status = :status", Task.class)
                    .setParameter("status", status)
                    .list();
        }
    }

    public List<Task> findOverdueTasks(LocalDateTime currentDate) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Task WHERE dueDate < :currentDate AND status != :doneStatus", Task.class)
                    .setParameter("currentDate", currentDate)
                    .setParameter("doneStatus", TaskStatus.DONE)
                    .list();
        }
    }

    public void update(Task task) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            session.update(task);
            tx.commit();
        }
    }

    public void delete(Task task) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            session.delete(task);
            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Task> findByAssignedUserId(Long userId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Task WHERE assignedUser.id = :userId", Task.class)
                    .setParameter("userId", userId)
                    .list();
        }
    }
}