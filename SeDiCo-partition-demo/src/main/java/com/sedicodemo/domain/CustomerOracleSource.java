package com.sedicodemo.domain;

import com.sedico.hibernate.SedicoTuplizer;
import org.hibernate.annotations.Tuplizer;
import org.hibernate.annotations.Type;
import javax.persistence.*;

@Entity
@Table(name = "Customer")
@Tuplizer(impl = SedicoTuplizer.class)
/**
 * Diese Klasse implementiert die Oracle-Partition.
 * @author jens
 *
 */
public class CustomerOracleSource {
    @Id
    private Integer ID;
    @Column
    private String NAME;
    @Column
    private String FORENAME;
    @Column
    private String DATEOFBIRTH;
    @Column
    private String CREDITCARD;
    @Column
    private String INSURANCENO;
    @Column
    private String STREET;
    @Column
    private String ZIP;
    @Column
    private Boolean REGCUST;
    @Column
    private Integer SOLSCORE;

    

	public Integer getID() {
		return ID;
	}



	public void setID(Integer iD) {
		ID = iD;
	}



	public String getNAME() {
		return NAME;
	}



	public void setNAME(String nAME) {
		NAME = nAME;
	}



	public String getFORENAME() {
		return FORENAME;
	}



	public void setFORENAME(String fORENAME) {
		FORENAME = fORENAME;
	}



	public String getDATEOFBIRTH() {
		return DATEOFBIRTH;
	}



	public void setDATEOFBIRTH(String dATEOFBIRTH) {
		DATEOFBIRTH = dATEOFBIRTH;
	}



	public String getCREDITCARD() {
		return CREDITCARD;
	}



	public void setCREDITCARD(String cREDITCARD) {
		CREDITCARD = cREDITCARD;
	}



	public String getINSURANCENO() {
		return INSURANCENO;
	}



	public void setINSURANCENO(String iNSURANCENO) {
		INSURANCENO = iNSURANCENO;
	}



	public String getSTREET() {
		return STREET;
	}



	public void setSTREET(String sTREET) {
		STREET = sTREET;
	}



	public String getZIP() {
		return ZIP;
	}



	public void setZIP(String zIP) {
		ZIP = zIP;
	}



	public Boolean getREGCUST() {
		return REGCUST;
	}



	public void setREGCUST(Boolean rEGCUST) {
		REGCUST = rEGCUST;
	}



	public Integer getSOLSCORE() {
		return SOLSCORE;
	}



	public void setSOLSCORE(Integer sOLSCORE) {
		SOLSCORE = sOLSCORE;
	}



	@Override
    public String toString() {
        return new String("ID: " + this.ID + " Name: " + this.NAME + " Forename:" + this.FORENAME);
    }
}
