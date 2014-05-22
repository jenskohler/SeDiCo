package com.sedicodemo.domain;

import com.sedico.hibernate.SedicoTuplizer;
import org.hibernate.annotations.Tuplizer;
import org.hibernate.annotations.Type;
import javax.persistence.*;

@Entity
@Table(name = "Customer")
@Tuplizer(impl = SedicoTuplizer.class)
/**
 * Die Klasse erzeugt einen neuen Kunden.
 * @author jens
 *
 */
public class Customer {
    @Id
    private int id;
    @Column
    private String name;
    @Column
    private String forename;
    @Column
    private String dateOfBirth;
    @Column
    private String creditCard;
    @Column
    private String insuranceNo;
    @Column
    private String street;
    @Column
    private String zip;
    @Column
    private Boolean regCust;
    @Column
    private int solScore;

    public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getForename() {
		return forename;
	}

	public void setForename(String forename) {
		this.forename = forename;
	}

	public String getDateOfBirth() {
		return dateOfBirth;
	}

	public void setDateOfBirth(String dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

	public String getCreditCard() {
		return creditCard;
	}

	public void setCreditCard(String creditCard) {
		this.creditCard = creditCard;
	}

	public String getInsuranceNo() {
		return insuranceNo;
	}

	public void setInsuranceNo(String insuranceNo) {
		this.insuranceNo = insuranceNo;
	}

	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	public String getZip() {
		return zip;
	}

	public void setZip(String zip) {
		this.zip = zip;
	}

	public Boolean isRegCust() {
		return this.regCust;
		
	}

	public void setRegCust(Boolean regCust) {
		this.regCust = regCust;
		
	}

	public int getSolScore() {
		return solScore;
	}

	public void setSolScore(int solScore) {
		this.solScore = solScore;
	}

	@Override
    public String toString() {
        return new String("ID: " + this.id + " Name: " + this.name + " Forename:" + this.forename + " Birth: " + this.dateOfBirth +
                " Creditcard: " + this.creditCard + " InsuranceNo: " + this.insuranceNo + " Street: " + this.street +
                " Zip: " + this.zip + " regCust: " + this.regCust + " solScore: " + this.solScore );
    }
}
