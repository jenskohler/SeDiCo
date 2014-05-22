package de.sedico.entities;

import java.io.Serializable;

import javax.faces.bean.ManagedBean;
import javax.persistence.*;
import java.util.Date;


/**
 * The persistent class for the Customer database table.
 * 
 */
@Entity
@ManagedBean
public class Customer implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="Id")
	private int id;

	private String creditCard;

    @Temporal( TemporalType.DATE)
	private Date dateOfBirth;

	private String forename;

	private String insuranceNo;

	private String name;

	private byte regCust;

	private int solScore;

	private String street;

	private String zip;

    public Customer() {
    }

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getCreditCard() {
		return this.creditCard;
	}

	public void setCreditCard(String creditCard) {
		this.creditCard = creditCard;
	}

	public Date getDateOfBirth() {
		return this.dateOfBirth;
	}

	public void setDateOfBirth(Date dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

	public String getForename() {
		return this.forename;
	}

	public void setForename(String forename) {
		this.forename = forename;
	}

	public String getInsuranceNo() {
		return this.insuranceNo;
	}

	public void setInsuranceNo(String insuranceNo) {
		this.insuranceNo = insuranceNo;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public byte getRegCust() {
		return this.regCust;
	}

	public void setRegCust(byte regCust) {
		this.regCust = regCust;
	}

	public int getSolScore() {
		return this.solScore;
	}

	public void setSolScore(int solScore) {
		this.solScore = solScore;
	}

	public String getStreet() {
		return this.street;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	public String getZip() {
		return this.zip;
	}

	public void setZip(String zip) {
		this.zip = zip;
	}
	
	@Override 
	public String toString() {
		String retString = new String("CustomerID: " + id + " Name: " + name + " Birth: " + dateOfBirth 
				+ " CreditCard: " + creditCard + " Forename: " + forename + " InsuranceNo: " + insuranceNo
				+ " RegularCustomer: " + regCust + " SolvencyScore: " + solScore + " Street: " + street 
				+ " Zip: " + zip);
		return retString;
	}

}