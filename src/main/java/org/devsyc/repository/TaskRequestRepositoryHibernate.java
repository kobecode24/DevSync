package org.devsyc.repository;

import org.devsyc.domain.entities.TaskRequest;
import org.devsyc.domain.enums.RequestStatus;
import org.devsyc.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

public class TaskRequestRepositoryHibernate {

    public void save(TaskRequest taskRequest) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.save(taskRequest);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    public List<TaskRequest> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM TaskRequest", TaskRequest.class).list();
        }
    }

    public List<TaskRequest> findByStatus(RequestStatus status) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM TaskRequest WHERE status = :status", TaskRequest.class)
                    .setParameter("status", status)
                    .list();
        }
    }

    public void update(TaskRequest taskRequest) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.update(taskRequest);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    public void delete(TaskRequest taskRequest) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.delete(taskRequest);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    public List<TaskRequest> findPendingRequests() {
        return findByStatus(RequestStatus.PENDING);
    }

    public List<TaskRequest> findPendingRequestsForTask(Long taskId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                            "FROM TaskRequest WHERE task.id = :taskId AND status = :status",
                            TaskRequest.class)
                    .setParameter("taskId", taskId)
                    .setParameter("status", RequestStatus.PENDING)
                    .list();
        }
    }
}
