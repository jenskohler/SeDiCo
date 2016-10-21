package com.sedicodemo;

import com.sedico.hibernate.SedicoBootstrapper;
import com.sedicodemo.domain.Customer;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.*;

public class HibernateUtil {

    private static final SessionFactory sessionFactory = buildSessionFactory();
/**
 * Diese Methode baut einen SessionFactory.
 * @return factory - SessionFactory
 */
    private static SessionFactory buildSessionFactory() {
        Configuration configuration = configure();
        ServiceRegistry serviceRegistry = new ServiceRegistryBuilder().applySettings(configuration.getProperties()).buildServiceRegistry();
        SessionFactory factory = configuration.buildSessionFactory(serviceRegistry);
        return factory;
    }
/**
 * Diese Methode erzeugt eine Configuration.
 * @return configuration - Configuration
 */
    private static Configuration configure() {
        Configuration configuration = SedicoBootstrapper.getHibernateConfiguration()

            //Generiertes SQL auf Console ausgeben
            //.setProperty("hibernate.show_sql", "true")
            .setProperty("format.sql", "true")
            .setProperty("hibernate.current_session_context_class", "thread")
            //.setProperty("hibernate.generate_statistics", "true")

            //Customer als Entität registrieren
           // .addAnnotatedClass(CustomerTest.class);
            .addAnnotatedClass(Customer.class);
            
        return configuration;
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }

}
