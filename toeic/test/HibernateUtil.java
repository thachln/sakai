/*
 * Filename: HibernateUtil.java
 *
 * Version: v1.0
 *
 * Date: Oct, 22 2008
 *
 * Copyright notice
 *
 * Modification Logs:
 *      DATE        AUTHOR      DESCRIPTION
 *  ------------------------------------------------------------------
 *  22-Oct-2008     TranNTB     Create file.
 */
/*===================================================================*/
package m.k.s.sakaiapp.survey.logic;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

/**
 * HibernateUtil class to create sessionFactory.
 */
public final class HibernateUtil {

    /**
     * Declare private constructor.
     */
    private HibernateUtil() {
    }

    /**
     * Create SessionFactory sessionFactory.
     */
    private static final SessionFactory SESSION_FACTORY =
        new Configuration().configure().buildSessionFactory();

    /**
     * Get SessionFactory sessionFactory.
     * @return sessionFactory.
     */
    public static SessionFactory getSessionFactory() {
        return SESSION_FACTORY;
    }

    /**
     * Get Session session.
     * @return session.
     */
    public static Session getSession() {
        return SESSION_FACTORY.openSession();
    }
}
