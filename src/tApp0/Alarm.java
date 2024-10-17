package tApp0;

import java.io.*;
import java.nio.charset.*;
import java.security.*;
import java.time.*;

import javafx.scene.media.*;
import javafx.scene.media.MediaPlayer.Status;

public class Alarm implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3043523911927099761L;
	private LocalDateTime aInitDateTime; //Stores the original DateTime calculated at time of creation. This LocalDateTime once assigned a value is unchanging...
	private LocalDateTime aDateTime; //Stores the working DateTime calculated as needed for the next Alarm Activation. This value will change as the situation requires...
	private LocalDate aDate; //The Date part of the LocalDateTime objects
	private LocalTime aTime; //The Time part of the LocalDateTime objects
	private ReFreq aReFreq; //ReFreq = Repeat Frequency. If not == ReFreq.SINGLE then Alarm will repeat. (Acts as a flag for the AlarmVw class)
	
	private boolean muted; //Make sound or no
	private boolean focused; //Tries to pull window to front or no
	private boolean activated; //Has activated or no
	private boolean snozzed; //Did user snooze or no (yes I'm aware of how it's spelled xD)
	
	private String aMsgString; //A user assigned String that can be utilized for many purposes. If not assigned will default to "Alarm Set @: "
	private String aDateString; //The Calendar Date the Alarm is set for in String format
	private String aTimeString; //The Time Alarm is set for in String format; formatted either for 24 hour or 12 hour temporal formats...
	private String aDayString; //Weekday the Alarm will fall on, in String format
	private String aReFreqString; //ReFreq Information, in String format
	private String aDetailString; //Extra details? Maybe works with aReFreqString? IDK :D
	private transient String aSeedString; //The string that will act as a seed for the ID to be calculated from ultimately... made transient for security lmao
	private String aIdString; //The unique ID that is calculated upon the creation of the Alarm initially...
	private String aAudioPath; //File path for the Audio to be used for the Alarm Activation if it's not muted of course. In String format. :D
	
	private transient File aAudioFile; //The Audio in File format
	
	private transient Media aAudio; //The Audio in Media format
	private transient MediaPlayer aAudioPlayer; //The thing that plays the Audio
	
	private transient StringBuilder aHexString; //Uhh.. Tbh not sure but it has something to do with hashing o.o;
	private transient MessageDigest aHashgest; //........>.> see above ;-;
	
	public void setAMsgString(String inputString) {
		if(inputString != null) {
			this.aMsgString = inputString;
		} else {
			this.aMsgString = new String("Alarm set ");
		}
		TLog.log.info("Alarm Msg Set");
	}
	
	public void calcADateString(LocalDate inputDate) {
		if(this.aDate == null) {
			this.aDate = inputDate;
		}
		this.aDateString = new String("ON " + inputDate.format(Tkpr.getTMForm()) + "." + inputDate.format(Tkpr.getTdForm()) + "." + inputDate.format(Tkpr.getTYForm()));
		TLog.log.info("ADateString Calculated From InputDate");
	}
	
	public void calcATimeString(LocalTime inputTime) {
		if(this.aTime == null) {
			this.aTime = inputTime;
		}
		if(Tkpr.getTSplit()) {
			this.aTimeString = new String("@ " + inputTime.format(Tkpr.getThForm()) + " : " + inputTime.format(Tkpr.getTmForm()) + " " + inputTime.format(Tkpr.getTaForm()));
		} else {
			this.aTimeString = new String("@ " + inputTime.format(Tkpr.getTHForm()) + " : " + inputTime.format(Tkpr.getTmForm()));
		}
		TLog.log.info("ATimeString Calculated From InputTime");
	}
	
	public void calcADayString(LocalDate inputDate) {
		if(this.aDate == null) {
			this.aDate = inputDate;
		}
		this.aDayString = new String(inputDate.format(Tkpr.getTEForm()));
		TLog.log.info("ADayString Calculated From InputDate");
	}
	
	public void calcAReFreqString(ReFreq inputReFreq) {
		if(inputReFreq == ReFreq.SINGLE) {
			this.aReFreq = ReFreq.SINGLE;
			this.aReFreqString = new String(aDayString);
		} else if(inputReFreq == ReFreq.DAILY) {
			this.aReFreq = ReFreq.DAILY;
			this.aReFreqString = new String("*Day");
		} else if(inputReFreq == ReFreq.WEEKLY) {
			this.aReFreq = ReFreq.WEEKLY;
			this.aReFreqString = new String("* Wk");
		} else if(inputReFreq == ReFreq.BIWEEKLY) {
			this.aReFreq = ReFreq.BIWEEKLY;
			this.aReFreqString = new String("*BWk");
		} else if(inputReFreq == ReFreq.MONTHLY) {
			this.aReFreq = ReFreq.MONTHLY;
			this.aReFreqString = new String("*Mth");
		} else if(inputReFreq == ReFreq.QUARTERLY) {
			this.aReFreq = ReFreq.QUARTERLY;
			this.aReFreqString = new String("*Qtr");
		} else if(inputReFreq == ReFreq.ANNUALLY) {
			this.aReFreq = ReFreq.ANNUALLY;
			this.aReFreqString = new String("* Yr");
		} else {
			this.aReFreq = ReFreq.UNSET;
			this.aReFreqString = new String("ERR");
		}
	}
	
	public void setAAudioPath(String inputString) {
		this.aAudioPath = inputString;
	}
	
	public void freshAAudio() {
		if(this.aAudioPath == null) {
			this.aAudioPath = "res" + File.separator + "aAudio.wav";
		}
		if(this.aAudioFile == null) {
			this.aAudioFile = new File(this.aAudioPath);
		}
		if(this.aAudio == null) {
			this.aAudio = new Media(aAudioFile.toURI().toString());
		}
		if(this.aAudioPlayer == null) {
			this.aAudioPlayer = new MediaPlayer(this.aAudio);
		} else {
			if(this.aAudioPlayer.getStatus() == Status.PLAYING || this.aAudioPlayer.getStatus() == Status.PAUSED) {
				this.aAudioPlayer.stop();
				this.aAudioPlayer = new MediaPlayer(this.aAudio);
			} else {
				this.aAudioPlayer = new MediaPlayer(this.aAudio);
			}
		}
	}
	
	private void genAId() {
		try {
			this.aSeedString = new String("Alarm_" + Tkpr.getTNow().format(Tkpr.getTIDForm()));
			this.aHashgest = MessageDigest.getInstance("SHA-256");
			byte[] aBitHash = this.aHashgest.digest(this.aSeedString.getBytes(StandardCharsets.UTF_8));
			this.aHexString = new StringBuilder();
			for(byte bite : aBitHash) {
				this.aHexString.append(String.format("%02x", bite));
			}
			this.aIdString = this.aHexString.toString();
			TLog.log.config("Alarm ID String Calculated");
		} catch(NoSuchAlgorithmException algoError) {
			this.aIdString = "Alarm_" + System.currentTimeMillis();
			TLog.log.config("Alarm ID String Calculated with Fallback Method");
		}
	}
	
	public void setIsMuted(boolean inputBoolean) {
		this.muted = inputBoolean;
	}
	
	public void setIsFocused(boolean inputBoolean) {
		this.focused = inputBoolean;
	}
	
	public void setIsActivated(boolean inputBoolean) {
		this.activated = inputBoolean;
	}
	
	public void setIsSnozzed(boolean inputBoolean) {
		this.snozzed = inputBoolean;
	}
	
	public void toggleIsMuted() {
		if(this.muted) {
			this.muted = false;
		} else {
			this.muted = true;
		}
	}
	
	public void toggleIsFocused() {
		if(this.focused) {
			this.focused = false;
		} else {
			this.focused = true;
		}
	}
	
	public void toggleIsActivated() {
		if(this.activated) {
			this.activated = false;
		} else {
			this.activated = true;
		}
	}
	
	public void toggleIsSnozzed() {
		if(this.snozzed) {
			this.snozzed = false;
		} else {
			this.snozzed = true;
		}
	}
	
	public boolean isMuted() {
		return this.muted;
	}
	
	public boolean isFocused() {
		return this.focused;
	}
	
	public boolean isActivated() {
		return this.activated;
	}
	
	public boolean isSnozzed() {
		return this.snozzed;
	}
	
	public void setADate(LocalDate inputDate) {
		this.aDate = inputDate;
	}
	
	public void setATime(LocalTime inputTime) {
		this.aTime = inputTime;
	}
	
	public void calcADateTime() {
		this.aDateTime = LocalDateTime.of(aDate, aTime);
	}
	
	public void deriADateTime() {
		this.aDate = aDateTime.toLocalDate();
		this.aTime = aDateTime.toLocalTime();
	}
	
	public void setADateTime(LocalDateTime inputDateTime) {
		this.aDateTime = inputDateTime;
	}
	
	public void calcAStrings() {
		calcADateString(aDate);
		calcATimeString(aTime);
		calcADayString(aDate);
		calcAReFreqString(aReFreq);
	}
	
	public void setAReFreq(ReFreq inputReFreq) {
		this.aReFreq = inputReFreq;
	}
	
	public void aAudioPlay() {
		this.aAudioPlayer.play();
	}
	
	public void aAudioPause() {
		this.aAudioPlayer.pause();
	}
	
	public void aAudioStop() {
		this.aAudioPlayer.stop();
	}
	
	public Status getAAudioStatus() {
		return this.aAudioPlayer.getStatus();
	}
	
	public String getAIdString() {
		return this.aIdString;
	}
	
	public ReFreq getAReFreq() {
		return this.aReFreq;
	}
	
	public LocalDate getADate() {
		return this.aDate;
	}
	
	public LocalTime getATime() {
		return this.aTime;
	}
	
	public String getAMsgString() {
		return this.aMsgString;
	}
	
	public String getADateString() {
		return this.aDateString;
	}
	
	public String getATimeString() {
		return this.aTimeString;
	}
	
	public String getADayString() {
		return this.aDayString;
	}
	
	public String getAReFreqString() {
		return this.aReFreqString;
	}
	
	public String getADetailString() {
		return this.aDetailString;
	}
	
	public String getAAudioPath() {
		return this.aAudioPath;
	}
	
	public LocalDateTime getADateTime() {
		return this.aDateTime;
	}
	
	public LocalDateTime getAInitDateTime() {
		return this.aInitDateTime;
	}
	
	public void aSnozz(int inputInt) {
		setIsSnozzed(true);
		this.aDateTime = this.aDateTime.plusMinutes(inputInt);
		deriADateTime();
		calcAStrings();
		setIsActivated(false);
		TLog.log.info("Alarm Snoozed");
	}
	
	public Alarm(LocalDate inputDate, LocalTime inputTime, String inputMsg, String inputPath, boolean inputMuted, boolean inputFocused, ReFreq inputFreq) {
		setADate(inputDate);
		setATime(inputTime);
		calcADateTime();
		this.aInitDateTime = aDateTime;
		this.aMsgString = new String(inputMsg);
		setAReFreq(inputFreq);
		setIsMuted(inputMuted);
		setIsFocused(inputFocused);
		setIsActivated(false);
		calcAStrings();
		setAAudioPath(inputPath);
		freshAAudio();
		genAId();
	}
	
}