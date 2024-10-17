package tApp0;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class CntUp implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5957561053464751389L;
	private int cUHr;
	private int cUMn; //lmao..... I'm three years old xD GD. Is funny tho :)
	private int cUSc;
	
	private String cUStartDateTimeString;
	private String cUEndDateTimeString;
	private String cURunTimeString;
	private transient String cUSeedString;
	private String cUIdString;
	
	private CUStat cUStat;
	
	private transient StringBuilder cUHexString;
	private transient MessageDigest cUHashgest;
	
	public void assignStartDateTimeString() {
		this.cUStartDateTimeString = new String(Tkpr.getTNow().format(Tkpr.getTMForm()) + "." + Tkpr.getTNow().format(Tkpr.getTdForm()) + "." + Tkpr.getTNow().format(Tkpr.getTYForm()) + "@" + Tkpr.getTNow().format(Tkpr.getTHForm()) + "." + Tkpr.getTNow().format(Tkpr.getTmForm()));
		TLog.log.info("CntUp " + cUIdString + " Start Date Time Assigned");
	}
	
	public void assignEndDateTimeString() {
		this.cUEndDateTimeString = new String(Tkpr.getTNow().format(Tkpr.getTMForm()) + "." + Tkpr.getTNow().format(Tkpr.getTdForm()) + "." + Tkpr.getTNow().format(Tkpr.getTYForm()) + "@" + Tkpr.getTNow().format(Tkpr.getTHForm()) + "." + Tkpr.getTNow().format(Tkpr.getTmForm()));
		TLog.log.info("CntUp " + cUIdString + " End Date Time Assigned");
	}
	
	public void setCUStat(CUStat inputCUStat) {
		this.cUStat = inputCUStat;
		TLog.log.info("CntUp " + cUIdString + " Status Set");
	}
	
	public void cUTick() {
		if(this.cUSc <= 59) {
			this.cUSc ++;
		} else if(this.cUSc >= 60) {
			if(this.cUMn <= 59) {
				this.cUMn ++;
				this.cUSc = 0;
			} else if(cUMn >= 60) {
				this.cUHr ++;
				this.cUMn = 0;
				this.cUSc = 0;
			}
		}
		
		TLog.log.info("CntUp Timer Tick! [CntUp " + cUIdString + "]");
		
	}
	
	public String getCUStartDateTimeString() {
		return this.cUStartDateTimeString;
	}
	
	public String getCUEndDateTimeString() {
		return this.cUEndDateTimeString;
	}
	
	public String getCURunTimeString() {
		return this.cURunTimeString;
	}
	
	public String getCUIdString() {
		return this.cUIdString;
	}
	
	public int getCUHr() {
		return this.cUHr;
	}
	
	public int getCUMn() {
		return this.cUMn;
	}
	
	public int getCUSc() {
		return this.cUSc;
	}
	
	public void freshCURunTimeString() {
		if(this.cUStat == CUStat.READY) {
			this.cURunTimeString = new String("-- : -- : -- [READY]");
		} else if(this.cUStat == CUStat.ACTIVE) {
			this.cURunTimeString = new String(String.format("%02d", this.cUHr) + " : " + String.format("%02d", this.cUMn) + " : " + String.format("%02d", this.cUSc) + " [RUNNING]");
		} else if(this.cUStat == CUStat.SUSPENDED) {
			this.cURunTimeString = new String(String.format("%02d", this.cUHr) + " : " + String.format("%02d", this.cUMn) + " : " + String.format("%02d", this.cUSc) + " [PAUSED]");
		} else if(this.cUStat == CUStat.SAVED) {
			this.cURunTimeString = new String("[WATCH SAVED @: " + String.format("%02d", this.cUHr) + " : " + String.format("%02d", this.cUMn) + " : " + String.format("%02d", this.cUSc) + " ]");
		} else {
			this.cURunTimeString = new String("[*!Count Up Timer Run Time Error!*]");
		}
		
		TLog.log.info("CntUp " + cUIdString + " Run Time String Refreshed");
	}
	
	private void genCUId() {
		try {
			this.cUSeedString = new String("CntUp_" + Tkpr.getTNow().format(Tkpr.getTIDForm()));
			this.cUHashgest = MessageDigest.getInstance("SHA-256");
			byte[] cUBitHash = this.cUHashgest.digest(this.cUSeedString.getBytes(StandardCharsets.UTF_8));
			this.cUHexString = new StringBuilder();
			for(byte bite : cUBitHash) {
				this.cUHexString.append(String.format("%02x", bite));
			}
			this.cUIdString = this.cUHexString.toString();
			TLog.log.config("CntUp ID String Calculated");
		} catch(NoSuchAlgorithmException algoError) {
			this.cUIdString = "CntUp_" + System.currentTimeMillis();
			TLog.log.config("CntUp ID String Calculated with Fallback Method");
		}
	}
	
	public CntUp() {
		genCUId();
		this.cUHr = 0;
		this.cUMn = 0;
		this.cUSc = 0;
		assignStartDateTimeString();
		this.cUStat = CUStat.READY;
		freshCURunTimeString();
		TLog.log.config("New CntUp Timer Initialized [CntUp " + cUIdString + "]");		
	}
	
	

}
