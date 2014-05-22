package com.sedicodemo;

import com.sedico.hibernate.SedicoBootstrapper;
import org.hibernate.cfg.*;
import org.hibernate.engine.spi.*;
import org.hibernate.metamodel.source.MetadataImplementor;
import org.hibernate.service.spi.*;
/**
 * Diese Klasse implementiert die Klasse MyIntegrator, welche org.hibernate.integrator.spi.Integrator implementiert.
 * @author jens
 *
 */
public class MyIntegrator implements org.hibernate.integrator.spi.Integrator {
	/**
	 * Diese Methode 
	 */
    @Override
    public void integrate(Configuration configuration,
            SessionFactoryImplementor sessionFactory,
            SessionFactoryServiceRegistry serviceRegistry) {

        SedicoBootstrapper.register(serviceRegistry);
    }

    @Override
    public void integrate(MetadataImplementor metadataImplementor, SessionFactoryImplementor sessionFactoryImplementor, SessionFactoryServiceRegistry sessionFactoryServiceRegistry) {

    }

    @Override
    public void disintegrate(SessionFactoryImplementor sessionFactoryImplementor, SessionFactoryServiceRegistry sessionFactoryServiceRegistry) {

    }
}

