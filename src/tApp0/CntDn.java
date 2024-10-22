package tApp0;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class CntDn implements Serializable {
	
	private int cDHr;
	private int cDMn;
	private int cDSc;
	private final int cDInitHr;
	private final int cDInitMn;
	private final int cDInitSc;
	
	private String cDMsgString;
	private String cDRunTimeString;
	private transient String cDSeedString;
	private String cDIdString;
	
	private boolean muted;
	private boolean focused;
	private boolean elapsed;
	
	private StringBuilder cDHexString;
	private MessageDigest cDHashgest;
	
	private CDStat cDStat;
	
	private void genCDId() {
		try {
			this.cDSeedString = new String("CntDn_" + Tkpr.getTNow().format(Tkpr.getTIDForm()));
			this.cDHashgest = MessageDigest.getInstance("SHA-256");
			byte[] cDBitHash = this.cDHashgest.digest(this.cDSeedString.getBytes(StandardCharsets.UTF_8));
			this.cDHexString = new StringBuilder();
			for(byte bite : cDBitHash) {
				this.cDHexString.append(String.format("%02d", bite));
			}
			this.cDIdString = this.cDHexString.toString();
			TLog.log.config("CntDn ID String Calculated");
		} catch(NoSuchAlgorithmException algoError) {
			this.cDIdString = "CntDn_" + System.currentTimeMillis();
			TLog.log.config("CntDn ID String Calculated with Fallback Method");
		}
	}
	
	public void setCDMsgString(String inputString) {
		this.cDMsgString = new String(inputString);
	}
	
	public void setCDStat(CDStat inputCDStat) {
		this.cDStat = inputCDStat;
	}
	
	public void freshCDRunTimeString() {
		if(this.cDStat == CDStat.IDLE) {
			this.cDRunTimeString = new String(this.cDHr + " : " + this.cDMn + " : " + this.cDSc + " [IDLE]");
		} else if(this.cDStat == CDStat.ACTIVE) {
			this.cDRunTimeString = new String(this.cDHr + " : " + this.cDMn + " : " + this.cDSc + " [REMAINING]");
		} else if(this.cDStat == CDStat.SUSPENDED) {
			this.cDRunTimeString = new String(this.cDHr + " : " + this.cDMn + " : " + this.cDSc + " [PAUSED]");
		} else if(this.cDStat == CDStat.COMPLETED) {
			this.cDRunTimeString = new String("Timer Count Down Completed!\n" + this.cDInitHr + " : " + this.cDInitMn + " : " + this.cDInitSc + " [ELAPSED]");
		} else {
			this.cDRunTimeString = new String("[*!Count Down Timer Error!*]");
		}
	}
	
	public CntDn(int inputHr, int inputMn, int inputSc, String inputMsg, boolean inputMuted, boolean inputFocused, CDStat inputCDStat) {
		genCDId();
		
		this.cDInitHr = inputHr;
		this.cDInitMn = inputMn;
		this.cDInitSc = inputSc;
		this.cDHr = this.cDInitHr;
		this.cDMn = this.cDInitMn;
		this.cDSc = this.cDInitSc;
		
		this.cDMsgString = new String(inputMsg);
		this.cDRunTimeString = new String(this.cDHr + " : " + this.cDMn + " : " + this.cDSc);
		
		this.muted = inputMuted;
		this.focused = inputFocused;
		
		setCDStat(CDStat.IDLE);
		
		
	}

}
