package com.sedico.hibernate;


import org.hibernate.cfg.Configuration;
import org.hibernate.event.service.spi.*;
import org.hibernate.event.spi.*;
import org.hibernate.service.spi.*;

import com.sedico.partition.PartitionDescriptor;

/**
 * Diese Klasse implementiert die Klasse SedicoBootstrapper.
 * @author jens
 *
 */

public class SedicoBootstrapper {
/**
 * Diese Methode implementiert die Registierung eines neuen Benutzers.
 * @param serviceRegistry - SessionFactoryServiceRegistry(Objekt welches die Registrierung ermöglicht)
 */
    public static void register(SessionFactoryServiceRegistry serviceRegistry) {
        //System.out.println("Registriere Listener");
    	
        final EventListenerRegistry eventListenerRegistry = serviceRegistry.getService(EventListenerRegistry.class);

        eventListenerRegistry.appendListeners(EventType.PRE_INSERT, new SedicoWriteInterceptor());
        eventListenerRegistry.appendListeners(EventType.PRE_UPDATE, new SedicoWriteInterceptor());
        eventListenerRegistry.appendListeners(EventType.PRE_DELETE, new SedicoWriteInterceptor());
        eventListenerRegistry.appendListeners(EventType.PRE_LOAD, new SedicoReadInterceptor());
    }
/**
 * Diese Methode liefert eine HibernateConfiguration mit gesetzten Werten zurück
 * @return
 */
    public static Configuration getHibernateConfiguration() {
        PartitionDescriptor partition = com.sedico.Configuration.getPrimaryPartition();
        Configuration configuration = new Configuration()
                .setProperty("hibernate.connection.driver_class", partition.getJdbcDriverName())
                .setProperty("hibernate.connection.url", partition.getConnectionString())
                .setProperty("hibernate.connection.username", partition.getUser())
                .setProperty("hibernate.connection.password", partition.getPassword())
                .setProperty("hibernate.dialect", partition.getDialectName());

        return configuration;
    }
}
