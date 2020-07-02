package com.hibernatewithSpringBoot.Utilities;

import java.util.Properties;

import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.service.ServiceRegistry;

import com.hibernatewithSpringBoot.EntityClasses.Customer;
import com.hibernatewithSpringBoot.EntityClasses.CustomerLedger;
import com.hibernatewithSpringBoot.EntityClasses.Schedule;


public class Utility {
	private static SessionFactory sessionFactory;
	public static SessionFactory getSessionFactory() {
		if(sessionFactory == null) {
			try {
				Configuration configuration = new Configuration();
				//Properties settings = new properties();
				Properties settings = new Properties();
				settings.put(Environment.DRIVER, "org.postgresql.Driver");
				settings.put(Environment.URL, "jdbc:postgresql://localhost:5432/postgres");
				settings.put(Environment.USER, "myuser");
				settings.put(Environment.PASS, "myuser");
				settings.put(Environment.DIALECT, "org.hibernate.dialect.PostgreSQLDialect");
				
				settings.put(Environment.SHOW_SQL, "true");
				settings.put(Environment.CURRENT_SESSION_CONTEXT_CLASS, "thread");
				settings.put(Environment.HBM2DDL_AUTO, "update");
				
				configuration.setProperties(settings);
				configuration.addAnnotatedClass(Customer.class);
				configuration.addAnnotatedClass(CustomerLedger.class);
				configuration.addAnnotatedClass(Schedule.class);
				
				ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
	                    .applySettings(configuration.getProperties()).build();
				sessionFactory = configuration.buildSessionFactory(serviceRegistry);
			}catch(Exception ex) {
				System.out.println(ex);
				ex.printStackTrace();
			}
		}
		return sessionFactory;
	}
}
