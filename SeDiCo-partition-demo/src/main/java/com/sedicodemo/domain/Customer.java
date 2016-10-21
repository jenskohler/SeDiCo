package com.sedicodemo.domain;


import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

import com.sedico.hibernate.SedicoTuplizer;

import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Tuplizer;

import javax.persistence.*;

@Entity
@Table(name = "CUSTOMER")
//@Tuplizer(impl = SedicoTuplizer.class)

public class Customer implements Serializable, Comparable<Customer> {
	private static final long serialVersionUID = 1L;
	
	@Id
    private Integer C_ID;
    @Column
    private String C_UNAME;
    @Column
    private String C_PASSWD;
    @Column
    private String C_FNAME;
    @Column
    private String C_LNAME;
    @Column
    private Integer C_ADDR_ID;
    @Column
    private String C_PHONE;
    @Column
    private String C_EMAIL;
    @Column
    @Temporal(TemporalType.DATE)
    private Date C_SINCE;
    @Column
    @Temporal(TemporalType.DATE)
    private Date C_LAST_LOGIN;
    @Column
    private Double C_DISCOUNT;
    @Column
    private Double C_BALANCE;
    @Column
    private Double C_YTD_PMT;
    @Column
    @Temporal(TemporalType.DATE)
    private Date C_BIRTHDATE;
    @Column
    private String C_DATA;

    @Column
    private Timestamp C_EXPIRATION;
    @Column
    private Timestamp C_LOGIN;

	public Timestamp getC_EXPIRATION() {
		return C_EXPIRATION;
	}



	public void setC_EXPIRATION(Timestamp c_EXPIRATION) {
		C_EXPIRATION = c_EXPIRATION;
	}



	public Timestamp getC_LOGIN() {
		return C_LOGIN;
	}



	public void setC_LOGIN(Timestamp c_LOGIN) {
		C_LOGIN = c_LOGIN;
	}



	public Integer getC_ID() {
		return C_ID;
	}



	public void setC_ID(Integer c_ID) {
		C_ID = c_ID;
	}



	public String getC_UNAME() {
		return C_UNAME;
	}



	public void setC_UNAME(String c_UNAME) {
		C_UNAME = c_UNAME;
	}



	public String getC_PASSWD() {
		return C_PASSWD;
	}



	public void setC_PASSWD(String c_PASSWD) {
		C_PASSWD = c_PASSWD;
	}



	public String getC_FNAME() {
		return C_FNAME;
	}



	public void setC_FNAME(String c_FNAME) {
		C_FNAME = c_FNAME;
	}



	public String getC_LNAME() {
		return C_LNAME;
	}



	public void setC_LNAME(String c_LNAME) {
		C_LNAME = c_LNAME;
	}



	public Integer getC_ADDR_ID() {
		return C_ADDR_ID;
	}



	public void setC_ADDR_ID(Integer c_ADDR_ID) {
		C_ADDR_ID = c_ADDR_ID;
	}



	public String getC_PHONE() {
		return C_PHONE;
	}



	public void setC_PHONE(String c_PHONE) {
		C_PHONE = c_PHONE;
	}



	public String getC_EMAIL() {
		return C_EMAIL;
	}



	public void setC_EMAIL(String c_EMAIL) {
		C_EMAIL = c_EMAIL;
	}



	public Date getC_SINCE() {
		return C_SINCE;
	}



	public void setC_SINCE(Date c_SINCE) {
		C_SINCE = c_SINCE;
	}



	public Date getC_LAST_LOGIN() {
		return C_LAST_LOGIN;
	}



	public void setC_LAST_LOGIN(Date c_LAST_LOGIN) {
		C_LAST_LOGIN = c_LAST_LOGIN;
	}



	public Double getC_DISCOUNT() {
		return C_DISCOUNT;
	}



	public void setC_DISCOUNT(Double c_DISCOUNT) {
		C_DISCOUNT = c_DISCOUNT;
	}



	public Double getC_BALANCE() {
		return C_BALANCE;
	}



	public void setC_BALANCE(Double c_BALANCE) {
		C_BALANCE = c_BALANCE;
	}



	public Double getC_YTD_PMT() {
		return C_YTD_PMT;
	}



	public void setC_YTD_PMT(Double c_YTD_PMT) {
		C_YTD_PMT = c_YTD_PMT;
	}



	public Date getC_BIRTHDATE() {
		return C_BIRTHDATE;
	}



	public void setC_BIRTHDATE(Date c_BIRTHDATE) {
		C_BIRTHDATE = c_BIRTHDATE;
	}



	public String getC_DATA() {
		return C_DATA;
	}



	public void setC_DATA(String c_DATA) {
		C_DATA = c_DATA;
	}



	@Override
    public String toString() {
        return new String("ID: " + this.C_ID + " Name: " + this.C_LNAME + " Forename:" + this.C_FNAME + " Birth: " + this.C_BIRTHDATE + 
        		" Revenue: " +this.C_YTD_PMT);
    }



	@Override
	public int compareTo(Customer cust) {
		return this.C_ID.compareTo(cust.C_ID);
	}
}
