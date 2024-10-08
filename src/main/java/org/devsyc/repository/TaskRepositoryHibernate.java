package org.devsyc.repository;

import org.devsyc.domain.entities.Task;
import org.devsyc.domain.entities.User;
import org.devsyc.domain.enums.TaskStatus;
import org.devsyc.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.time.LocalDateTime;
import java.util.List;

public class TaskRepositoryHibernate implements TaskRepository {

    @Override
    public void save(Task task) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            session.save(task);
            tx.commit();
        }
    }

    @Override
    public Task findById(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Task.class, id);
        }
    }

    @Override
    public List<Task> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Task", Task.class).list();
        }
    }

    @Override
    public List<Task> findByUser(User user) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Task WHERE assignedUser = :user", Task.class)
                    .setParameter("user", user)
                    .list();
        }
    }

    @Override
    public List<Task> findByStatus(TaskStatus status) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Task WHERE status = :status", Task.class)
                    .setParameter("status", status)
                    .list();
        }
    }

    @Override
    public List<Task> findOverdueTasks(LocalDateTime currentDate) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Task WHERE dueDate < :currentDate AND status != :doneStatus", Task.class)
                    .setParameter("currentDate", currentDate)
                    .setParameter("doneStatus", TaskStatus.DONE)
                    .list();
        }
    }

    @Override
    public void update(Task task) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            session.update(task);
            tx.commit();
        }
    }

    @Override
    public void delete(Task task) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            session.delete(task);
            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}