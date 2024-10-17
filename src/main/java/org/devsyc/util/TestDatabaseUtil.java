package org.devsyc.util;

import org.hibernate.Session;
import org.hibernate.Transaction;

public class TestDatabaseUtil {
    public static void resetDatabase() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = session.beginTransaction();
        try {
            // Disable foreign key checks
            session.createNativeQuery("SET CONSTRAINTS ALL DEFERRED").executeUpdate();

            // Truncate tables
            session.createNativeQuery("TRUNCATE TABLE tasks, users, task_requests RESTART IDENTITY CASCADE").executeUpdate();

            // Re-enable foreign key checks
            session.createNativeQuery("SET CONSTRAINTS ALL IMMEDIATE").executeUpdate();

            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
    }
}