package com.sedicodemo;

import com.sedicodemo.domain.Customer;

import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
        
        //create a new customer and print it 
        createNewCustomer(session);
        /*
        Customer customer = (Customer)session.load(Customer.class, 8);
        System.out.println(customer.toString());
   
        //update the just created customer and print it
        updateCustomer(session, 8);
        System.out.println(customer.toString());
        
        //delete the just created customer
        deleteCustomer(session, 8);
*/
        //check if customer is really deleted
        List<Customer> customers = session.createCriteria(Customer.class).list();
        for (Customer c : customers) {
        	System.out.println(c.toString());
        }
        System.out.println();
       
        
       
        
        session.getTransaction().commit();

        HibernateUtil.getSessionFactory().close();
        
        /*List customers = session.createCriteria(CustomerTest.class).add(Restrictions.eq("age", 14)).list();

        for(Object cust : customers) {
            System.out.println(cust.toString());
        }

        session.getTransaction().commit();

        HibernateUtil.getSessionFactory().close();
        
        */
    }
    /**
     * Diese Methode aktualisiert den Kunden
     * @param session - Session
     * @param id - int
     */
    private static void updateCustomer(Session session, int id) {
        Customer customer = (Customer)session.load(Customer.class, id);
        
        customer.setCreditCard("999999");
        customer.setDateOfBirth("2009-09-09");
        customer.setForename("forename9");
        customer.setInsuranceNo("999999");
        customer.setName("name9");
        customer.setRegCust(false);
        customer.setSolScore(99);
        customer.setStreet("street9");
        customer.setZip("99999");
        
        session.update(customer);
    }
    /**
     * Diese Methode löscht die Daten eines Kunden
     * @param session - Session
     * @param id - int
     */
    private static void deleteCustomer(Session session, int id) {
        Customer customer = (Customer)session.load(Customer.class, id);
        session.delete(customer);
    }
    /**
     * Diese Methode erzeugt einen neuen Kunden
     * @param session
     */
    private static void createNewCustomer(Session session) {
    	Customer customer = new Customer();
        customer.setCreditCard("9");
       
        customer.setForename("8");
        customer.setId(8);
        customer.setInsuranceNo("9");
        customer.setName("8");
        customer.setRegCust(true);
        customer.setSolScore(88);
        customer.setDateOfBirth("2008-08-08");
        customer.setStreet("8");
        customer.setZip("8");
        
        session.save(customer);
        System.out.println("Customer-ID:" + customer.getId() + " saved.");
    }
}
