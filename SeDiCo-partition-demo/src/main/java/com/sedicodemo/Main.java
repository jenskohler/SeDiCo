package com.sedicodemo;

import com.sedicodemo.domain.Customer;

import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.hibernate.jmx.StatisticsService;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.Random;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;

public class Main {
	private static Random rand = new Random();
	private static int NUMBEROFTUPLES = 10;
	private static Customer UPDATECUSTOMER;
	private static List<Customer> customerList = new ArrayList<Customer>();

	public static void main(String[] args) {

		 runQueryBenchmark();

		//runDeleteBenchmark();

		// runUpdateBenchmark();

		// runInsertBenchmark();
		 
		 
		 System.exit(0);

	}

	private static void runInsertBenchmark() {

		long queryStart;
		long queryEnd;
		System.out.println("Start Insert-Benchmark");
		System.out.println("Creating Customers...");
		createCustomers();
		System.out.println("Created " + NUMBEROFTUPLES + " Customers.");

		queryStart = System.currentTimeMillis();

		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		insertNewCustomers(session);

		session.getTransaction().commit();

		HibernateUtil.getSessionFactory().close();
		queryEnd = System.currentTimeMillis();
		long queryTime = queryEnd - queryStart;
		System.out.println("Insertion-Time: " + queryTime);
	}

	private static void runUpdateBenchmark() {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		long queryStart;
		long queryEnd;
		System.out.println("Start Update-Benchmark");
		System.out.println("Creating Update Customers...");
		createUpdateCustomer();
		System.out.println("Created " + NUMBEROFTUPLES + " Update Customers.");

		queryStart = System.currentTimeMillis();
		writeUpdateCustomers(session);

		session.getTransaction().commit();

		HibernateUtil.getSessionFactory().close();
		queryEnd = System.currentTimeMillis();
		long queryTime = queryEnd - queryStart;
		System.out.println("Update-Time: " + queryTime);
	}

	private static void runDeleteBenchmark() {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();

		long queryStart;
		long queryEnd;
		System.out.println("Start Delete-Benchmark");
		queryStart = System.currentTimeMillis();

		for (int i = 1; i <= NUMBEROFTUPLES; i++) {
			deleteCustomer(session, i);
		}

		session.getTransaction().commit();

		HibernateUtil.getSessionFactory().close();

		queryEnd = System.currentTimeMillis();
		long queryTime = queryEnd - queryStart;
		System.out.println("Delete-Time: " + queryTime);
	}

	private static void runQueryBenchmark() {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		long queryStart;
		long queryEnd;
		System.out.println("Start Query-Benchmark");
		queryStart = System.currentTimeMillis();

		List<Customer> customers = session.createCriteria(Customer.class).setMaxResults(NUMBEROFTUPLES).list();

		for (Customer c : customers) {
			System.out.println(c.toString());
		}

		session.getTransaction().commit();

		HibernateUtil.getSessionFactory().close();

		queryEnd = System.currentTimeMillis();
		long queryTime = queryEnd - queryStart;

		System.out.println("No of tuples: " + customers.size());
		System.out.println("Query-Time: " + queryTime + " ms.");

	}

	/**
	 * Diese Methode löscht die Daten eines Kunden
	 * 
	 * @param session
	 *            - Session
	 * @param id
	 *            - int
	 */
	private static void deleteCustomer(Session session, int id) {
		Customer customer = (Customer) session.load(Customer.class, id);
		session.delete(customer);
	}

	/**
	 * Diese Methode erzeugt einen neuen Kunden
	 * 
	 * @param session
	 */
	private static void createNewCustomer(Session session) {
		Customer customer = new Customer();
		customer.setC_ID(288001);
		customer.setC_ADDR_ID(111);
		customer.setC_BALANCE(55.);
		customer.setC_UNAME("test");
		customer.setC_YTD_PMT(9999.);
		customer.setC_FNAME("test");
		customer.setC_LNAME("test");
		customer.setC_FNAME("test");

		session.save(customer);

	}

	private static void writeUpdateCustomers(Session session) {
		// session = HibernateUtil.getSessionFactory().getCurrentSession();
		// Session session = factory.openSession();
		//session.beginTransaction();
		for (int i = 1; i < NUMBEROFTUPLES; i++) {

			// update operation means: load sth. from database change it and
			// write it back
			// load data from database
			Customer c = (Customer) session.load(Customer.class, i);
			// System.out.println("Customer loaded: " +c.toString());

			// change data

			c = UPDATECUSTOMER;
			c.setC_ID(i);
			// System.out.println("Customer changed: " +c.toString());

			// write back data
			session.merge(c);
			// System.out.println("Customer updated: " +c.toString());
		}
		//session.getTransaction().commit();

	}

	private static void createUpdateCustomer() {

		for (int i = 1; i <= NUMBEROFTUPLES; i++) {
			UPDATECUSTOMER = new Customer();
			// System.out.println("Customer loaded: " +c.toString());

			UPDATECUSTOMER.setC_UNAME("AG");

			UPDATECUSTOMER.setC_PASSWD(UPDATECUSTOMER.getC_UNAME().toLowerCase());
			UPDATECUSTOMER.setC_LNAME(getRandomAString(8, 15));
			UPDATECUSTOMER.setC_FNAME(getRandomAString(8, 15));
			UPDATECUSTOMER.setC_ADDR_ID(i);
			UPDATECUSTOMER.setC_PHONE(String.valueOf(getRandomNString(9, 16)));
			UPDATECUSTOMER.setC_EMAIL(UPDATECUSTOMER.getC_UNAME() + "@" + getRandomAString(2, 9) + ".com");

			GregorianCalendar cal = new GregorianCalendar();
			cal.add(Calendar.DAY_OF_YEAR, -1 * getRandomInt(1, 730));
			// UPDATECUSTOMER.setC_SINCE("");
			// UPDATECUSTOMER.setC_LAST_LOGIN(new java.sql.Date(12L));
			cal.add(Calendar.DAY_OF_YEAR, getRandomInt(0, 60));
			if (cal.after(new GregorianCalendar()))
				cal = new GregorianCalendar();

			// UPDATECUSTOMER.setC_LAST_LOGIN(new
			// java.sql.Date(cal.getTime().getTime()));

			// UPDATECUSTOMER.setC_LOGIN(new
			// java.sql.Timestamp(cal.getTime().getTime()));
			cal = new GregorianCalendar();
			cal.add(Calendar.HOUR, 2);
			// UPDATECUSTOMER.setC_EXPIRATION(new
			// java.sql.Timestamp(cal.getTime().getTime()));

			UPDATECUSTOMER.setC_DISCOUNT((double) getRandomInt(0, 50) / 100.0);
			UPDATECUSTOMER.setC_BALANCE(1.0);
			UPDATECUSTOMER.setC_YTD_PMT((double) getRandomInt(0, 99999) / 100.0);
			int year = getRandomInt(1880, 2000);
			int month = getRandomInt(0, 11);
			int maxday = 31;
			int day;
			if (month == 3 | month == 5 | month == 8 | month == 10)
				maxday = 30;
			else if (month == 1)
				maxday = 28;
			day = getRandomInt(1, maxday);
			cal = new GregorianCalendar(year, month, day);

			// UPDATECUSTOMER.setC_BIRTHDATE(Date.valueOf("2010-01-01"));

			UPDATECUSTOMER.setC_DATA(getRandomAString(100, 500));

			customerList.add(UPDATECUSTOMER);
			// System.out.println("Update-Customer created: " +c.toString());

		}

	}

	private static void createCustomers() {

		for (int i = 1; i <= NUMBEROFTUPLES; i++) {
			Customer c = new Customer();
			c.setC_ID(i);
			c.setC_UNAME(DigSyl(i, 0));
			c.setC_PASSWD(c.getC_UNAME().toLowerCase());
			c.setC_LNAME(getRandomAString(8, 15));
			c.setC_FNAME(getRandomAString(8, 15));
			c.setC_ADDR_ID(i);
			c.setC_PHONE(String.valueOf(getRandomNString(9, 16)));
			c.setC_EMAIL(c.getC_UNAME() + "@" + getRandomAString(2, 9) + ".com");

			GregorianCalendar cal = new GregorianCalendar();
			cal.add(Calendar.DAY_OF_YEAR, -1 * getRandomInt(1, 730));
			// c.setC_SINCE(new java.sql.Date(cal.getTime().getTime()));
			cal.add(Calendar.DAY_OF_YEAR, getRandomInt(0, 60));
			if (cal.after(new GregorianCalendar()))
				cal = new GregorianCalendar();

			// c.setC_LAST_LOGIN(new java.sql.Date(cal.getTime().getTime()));
			// c.setC_LOGIN(new java.sql.Timestamp(System.currentTimeMillis()));
			cal = new GregorianCalendar();
			cal.add(Calendar.HOUR, 2);
			// c.setC_EXPIRATION(new
			// java.sql.Timestamp(cal.getTime().getTime()));

			// c.setC_DISCOUNT((double) getRandomInt(0, 50)/100.0);
			// c.setC_BALANCE(1.0);
			// c.setC_YTD_PMT((double) getRandomInt(0, 99999)/100.0);
			int year = getRandomInt(1880, 2000);
			int month = getRandomInt(0, 11);
			int maxday = 31;
			int day;
			if (month == 3 | month == 5 | month == 8 | month == 10)
				maxday = 30;
			else if (month == 1)
				maxday = 28;
			day = getRandomInt(1, maxday);
			cal = new GregorianCalendar(year, month, day);
			// c.setC_BIRTHDATE(Date.valueOf("2010-01-01"));

			c.setC_DATA(getRandomAString(100, 500));

			customerList.add(c);

		}

	}

	private static void insertNewCustomers(Session session) {
		session.beginTransaction();
		for (Customer customer : customerList) {
			// System.out.println("Customer insert: " + customer.getC_ID());

			session.save(customer);
		}
		session.getTransaction().commit();
	}

	private static String DigSyl(int D, int N) {
		int i;
		String resultString = new String();
		String Dstr = Integer.toString(D);

		if (N > Dstr.length()) {
			int padding = N - Dstr.length();
			for (i = 0; i < padding; i++)
				resultString = resultString.concat("BA");
		}

		for (i = 0; i < Dstr.length(); i++) {
			if (Dstr.charAt(i) == '0')
				resultString = resultString.concat("BA");
			else if (Dstr.charAt(i) == '1')
				resultString = resultString.concat("OG");
			else if (Dstr.charAt(i) == '2')
				resultString = resultString.concat("AL");
			else if (Dstr.charAt(i) == '3')
				resultString = resultString.concat("RI");
			else if (Dstr.charAt(i) == '4')
				resultString = resultString.concat("RE");
			else if (Dstr.charAt(i) == '5')
				resultString = resultString.concat("SE");
			else if (Dstr.charAt(i) == '6')
				resultString = resultString.concat("AT");
			else if (Dstr.charAt(i) == '7')
				resultString = resultString.concat("UL");
			else if (Dstr.charAt(i) == '8')
				resultString = resultString.concat("IN");
			else if (Dstr.charAt(i) == '9')
				resultString = resultString.concat("NG");
		}

		return resultString;
	}

	private static String getRandomAString(int length) {
		String newstring = new String();
		int i;
		final char[] chars = { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r',
				's', 't', 'u', 'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
				'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '!', '@', '#', '$', '%', '^', '&', '*',
				'(', ')', '_', '-', '=', '+', '{', '}', '[', ']', '|', ':', ';', ',', '.', '?', '/', '~', ' ' }; // 79
																													// characters
		for (i = 0; i < length; i++) {
			char c = chars[(int) Math.floor(rand.nextDouble() * 79)];
			newstring = newstring.concat(String.valueOf(c));
		}
		return newstring;
	}

	private static String getRandomAString(int min, int max) {
		String newstring = new String();
		int i;
		final char[] chars = { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r',
				's', 't', 'u', 'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
				'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '!', '@', '#', '$', '%', '^', '&', '*',
				'(', ')', '_', '-', '=', '+', '{', '}', '[', ']', '|', ':', ';', ',', '.', '?', '/', '~', ' ' }; // 79
																													// characters
		int strlen = (int) Math.floor(rand.nextDouble() * ((max - min) + 1));
		strlen += min;
		for (i = 0; i < strlen; i++) {
			char c = chars[(int) Math.floor(rand.nextDouble() * 79)];
			newstring = newstring.concat(String.valueOf(c));
		}
		return newstring;
	}

	private static int getRandomInt(int lower, int upper) {

		int num = (int) Math.floor(rand.nextDouble() * ((upper + 1) - lower));
		if (num + lower > upper || num + lower < lower) {
			System.out.println("ERROR: Random returned value of of range!");
			System.exit(1);
		}
		return num + lower;
	}

	private static int getRandomNString(int min, int max) {
		int strlen = (int) Math.floor(rand.nextDouble() * ((max - min) + 1)) + min;
		return getRandomNString(strlen);
	}

	private static int getRandomNString(int num_digits) {
		int return_num = 0;
		for (int i = 0; i < num_digits; i++) {
			return_num += getRandomInt(0, 9) * (int) java.lang.Math.pow(10.0, (double) i);
		}
		return return_num;
	}

}
