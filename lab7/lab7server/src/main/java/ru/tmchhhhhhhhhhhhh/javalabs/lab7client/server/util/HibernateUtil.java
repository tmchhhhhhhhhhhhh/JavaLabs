package ru.tmchhhhhhhhhhhhh.javalabs.lab7client.server.util;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class HibernateUtil {
    private static SessionFactory sessionFactory;
    
    static {
        try {
            System.out.println("[HIBERNATE] Инициализация SessionFactory...");
            
            Configuration configuration = new Configuration();
            configuration.configure("hibernate.cfg.xml");
            
            sessionFactory = configuration.buildSessionFactory();
            
            System.out.println("[HIBERNATE] ✓ SessionFactory успешно создана!");
            
        } catch (Throwable ex) {
            System.err.println("[HIBERNATE] ✗ Ошибка создания SessionFactory: " + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }
    
    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }
    
    public static void shutdown() {
        if (sessionFactory != null) {
            System.out.println("[HIBERNATE] Закрытие SessionFactory...");
            sessionFactory.close();
        }
    }
}
