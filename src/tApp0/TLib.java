package tApp0;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Optional;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;

public class TLib {
	
	private HashMap<Alarm, String> alarmFileMap;
	
	private ObservableList<Alarm> aListing; //Alarm List
	private ObservableList<CntUp> cUListing; //Count Up List
	private ObservableList<CntDn> aCDListing; //Active Count Down List
	private ObservableList<CntDn> iCDListing; //Idle Count Down List
	
	private ListView<Alarm> aListView; //Alarm List View
	private ListView<CntUp> cUListView; //Count Up List View
	private ListView<CntDn> aCDListView; //Active Count Down List View
	private ListView<CntDn> iCDListView; //Idle Count Down List View
	
	private TSet tSet; //Temporalment Settings *Custom Class...*
	
	private String dFAAudioPath = new String("res" + File.separator + "aAudio.wav"); //Alarm Activation Audio File Path (Application Default Value)
	private String dFCDAudioPath = new String(); //Count Down Completion Audio File Path (Application Default Value)
	private String dFClickAudioPath = new String(); //Click Sound Audio File Path (Application Default Value)
	private String dFAcceptAudioPath = new String(); //Accept aka Positive Audio File Path (Application Default Value)
	private String dFRejectAudioPath = new String(); //Reject aka Negative Audio File Path (Application Default Value)
	private String dFADirPath = new String("libs" + File.separator + "alarm");
	
	public void aList(Alarm inputAlarm) {
	    // Ensure no duplicate alarms are added based on their ID
	    Optional<Alarm> existingAlarm = aListing.stream()
	        .filter(alarm -> alarm.getAIdString().equals(inputAlarm.getAIdString()))
	        .findFirst();
	    
	    if (existingAlarm.isEmpty()) {
	        aListing.add(inputAlarm);
	        aListView.refresh();
	    } else {
	        // Alarm with this ID already exists, so do not add it again
	        System.out.println("Duplicate alarm detected, skipping: " + inputAlarm.getAIdString());
	    }
	}

	
	public void libAGen(LocalDate inputDate, LocalTime inputTime, String inputMsg, String inputPath, boolean inputMuted, boolean inputFocused, ReFreq inputFreq) {
		Alarm alarm = new Alarm(inputDate, inputTime, inputMsg, inputPath, inputMuted, inputFocused, inputFreq);
		aList(alarm);
		aSerialize(alarm);
		
	}
	
	public void aRemove(Alarm inputAlarm) {
		String aFilePath = new String(this.dFADirPath + File.separator + inputAlarm.getAIdString() + ".alrm");
		File aFile = new File(aFilePath);
		if(aFile.exists() && aFile.delete()) {
			TLog.log.info(aFilePath + " Deletion Sucessful");
		} else {
			TLog.log.info(aFilePath + " Deletion Failed!");
		}
		this.aListing.remove(inputAlarm);
	}
	
	private void aDirChk() {
		boolean dirCreated = false;
		File dir = new File(this.dFADirPath);
		if(!dir.exists()) {
			dirCreated = dir.mkdirs();
			if(dirCreated) {
				TLog.log.info("Alarm Serialization Directory was Absent but Created Sucsessfully");
				return;
			} else {
				TLog.log.info("Alarm Serialization Directory is Absent and couldn't be Created!");
				return;
			}
		} else {
			TLog.log.info("Alarm Serialization Directory Verified to Exist");
			return;
		}
	}
	
	private Boolean aFileChk(File inputFile) {
		if(inputFile.isFile() && inputFile.getName().endsWith(".alrm")) {
			return true;
		} else {
			return false;
		}
	}
	
	private Boolean aInitChk(Alarm inputAlarm) {
		boolean awsr = false;
		if(inputAlarm.getADateTime().isBefore(Tkpr.getTNow()) && !(inputAlarm.getAReFreq() == ReFreq.SINGLE)) {
			switch (inputAlarm.getAReFreq()) {
				case ReFreq.DAILY:
					libAGen(inputAlarm.getADate().plusDays(1), inputAlarm.getATime(), inputAlarm.getAMsgString(), inputAlarm.getAAudioPath(), inputAlarm.isMuted(), inputAlarm.isFocused(), inputAlarm.getAReFreq());
					aRemove(inputAlarm);
					awsr = false;
					break;
				case ReFreq.WEEKLY:
					libAGen(inputAlarm.getADate().plusWeeks(1), inputAlarm.getATime(), inputAlarm.getAMsgString(), inputAlarm.getAAudioPath(), inputAlarm.isMuted(), inputAlarm.isFocused(), inputAlarm.getAReFreq());
					aRemove(inputAlarm);
					awsr = false;
					break;
				case ReFreq.BIWEEKLY:
					libAGen(inputAlarm.getADate().plusWeeks(2), inputAlarm.getATime(), inputAlarm.getAMsgString(), inputAlarm.getAAudioPath(), inputAlarm.isMuted(), inputAlarm.isFocused(), inputAlarm.getAReFreq());
					aRemove(inputAlarm);
					awsr = false;
					break;
				case ReFreq.MONTHLY:
					libAGen(inputAlarm.getADate().plusMonths(1), inputAlarm.getATime(), inputAlarm.getAMsgString(), inputAlarm.getAAudioPath(), inputAlarm.isMuted(), inputAlarm.isFocused(), inputAlarm.getAReFreq());
					aRemove(inputAlarm);
					awsr = false;
					break;
				case ReFreq.QUARTERLY:
					libAGen(inputAlarm.getADate().plusMonths(3), inputAlarm.getATime(), inputAlarm.getAMsgString(), inputAlarm.getAAudioPath(), inputAlarm.isMuted(), inputAlarm.isFocused(), inputAlarm.getAReFreq());
					aRemove(inputAlarm);
					awsr = false;
					break;
				case ReFreq.ANNUALLY:
					libAGen(inputAlarm.getADate().plusYears(1), inputAlarm.getATime(), inputAlarm.getAMsgString(), inputAlarm.getAAudioPath(), inputAlarm.isMuted(), inputAlarm.isFocused(), inputAlarm.getAReFreq());
					aRemove(inputAlarm);
					awsr = false;
					break;
				default:
					aRemove(inputAlarm);
					awsr = false;
					break;
			}
		} else if(inputAlarm.getADateTime().isBefore(Tkpr.getTNow()) && (inputAlarm.getAReFreq() == ReFreq.SINGLE)) {
			aRemove(inputAlarm);
			awsr = false;
		} else {
			
			awsr = true;
		}
		
		return awsr;
	}
	
	public void aSerialize(Alarm inputAlarm) {
		aDirChk();
		String aFilePath = new String(this.dFADirPath + File.separator + inputAlarm.getAIdString() + ".alrm");
		try(FileOutputStream alrmFile = new FileOutputStream(aFilePath);
			ObjectOutputStream fileOutput = new ObjectOutputStream(alrmFile)) {
			fileOutput.writeObject(inputAlarm);
			TLog.log.info("Alarm with ID:" + inputAlarm.getAIdString() + " Serialized to File System");
		} catch(IOException aSerError) {
			TLog.log.info("Alarm Serilization Error Encountered!\n Alarm ID:" + inputAlarm.getAIdString() + "\n---------------\n" + aSerError.getStackTrace());
		}
	}
	
	public void aSerializeAll(ObservableList<Alarm> inputList) {
		aDirChk();
		for(int i = 0; i < inputList.size(); i++) {
			Alarm iAlarm = inputList.get(i);
			String iFilePath = new String(this.dFADirPath + File.separator + iAlarm.getAIdString() + ".alrm");
			File iFile = new File(iFilePath);
			try(FileOutputStream alrmFile = new FileOutputStream(iFilePath);
				ObjectOutputStream fileOutput = new ObjectOutputStream(alrmFile)) {
				if(!iFile.exists()) {
					fileOutput.writeObject(iAlarm);
					TLog.log.info("Alarm with ID:" + iAlarm.getAIdString() + " Serialized to File System");
				} else {
					TLog.log.info("Alarm with ID:" + iAlarm.getAIdString() + " already Serialized to File System");
				}
			} catch(IOException aSerErrors) {
				TLog.log.info("Alarm Serialization Error Encountered!\n Alarm ID:" + iAlarm.getAIdString() + "\n---------------\n" + aSerErrors.getStackTrace());
			}
		}
	}
	
	private Alarm readInAlarm(File inputFile) {
		Alarm alarmIn = null;
		if(aFileChk(inputFile)) {
			try(FileInputStream fileIn = new FileInputStream(inputFile);
				ObjectInputStream objectIn = new ObjectInputStream(fileIn)) {
				Object aObj = objectIn.readObject();
				if(aObj instanceof Alarm) {
					alarmIn = (Alarm) aObj;
				} else {
					System.err.println("Error: The object being read is not of type 'Alarm'!");	
				}
			} catch(Exception e) {
				
			}
		}
		return alarmIn;
	}
	
	public void dSerializeAlarms() {
		File aDir = new File(this.dFADirPath); //Set up the Alarm Directory File Path
		if(aDir.exists() && aDir.isDirectory()) { //Make sure it exists and is a Directory
			File[] aFiles = aDir.listFiles(); //Make an array of type File and populate it with the Files within the Directory we just defined if there are any
			if(aFiles != null) { //If the directory was empty it'd be null but if the array isn't null then...
				for(File aFile : aFiles) { //For every File (each file to be referenced as "aFile") of the array we've just defined then we will do the following...
					Alarm alarm = readInAlarm(aFile); //Creates an Alarm instance by passing the current aFile to the readInAlarm() method
					if(alarm != null) { //If the readInAlarm() method did not return null
						if(aInitChk(alarm)) { //Check if the Alarm instance is programmed to have already gone off and if it was not then:
							aList(alarm); //Add the Alarm instance to the aListing ObservableList<Alarm>
							alarm.freshAAudio();
						} else { //If the Alarm instance was programmed to have already gone off then just restart this loop with the next file. (The method used to check will remove the Alarm instance, the Alarm mapping, the Alarm listing, and the Alarm file from the device so that it wont be encountered again so no need to worry about this...)
							continue; 
						}
					} else { //Similarly to the aInitChk() method; from the readInAlarm() method - if it returns a null value due to the file not being a .alrm file or something of the like then just start with the next file in the array and ignore any problem that this might be hinting at. (This accounts for user medaling in the file system or if it was trying to read the hmapping file etc...)
						continue;
					}
				}
			}
		}
	}
	
	public ObservableList<Alarm> getAList() {
		return this.aListing;
	}
	
	public ObservableList<CntUp> getCUList() {
		return this.cUListing;
	}
	
	public ObservableList<CntDn> getACDList() {
		return this.aCDListing;
	}
	
	public ObservableList<CntDn> getICDList() {
		return this.iCDListing;
	}
	
	public ListView<Alarm> getAListView() {
		return this.aListView;
	}
	
	public ListView<CntUp> getCUListView() {
		return this.cUListView;
	}
	
	public ListView<CntDn> getACDListView() {
		return this.aCDListView;
	}
	
	public ListView<CntDn> getICDListView() {
		return this.iCDListView;
	}
	
	public String getDFAAudioPath() {
		return this.dFAAudioPath;
	}
	
	public String getDFCDAudioPath() {
		return this.dFCDAudioPath;
	}
	
	public String getDFClickAudioPath() {
		return this.dFClickAudioPath;
	}
	
	public String getDFAcceptAudioPath() {
		return this.dFAcceptAudioPath;
	}
	
	public String getDFRejectAudioPath() {
		return this.dFRejectAudioPath;
	}
	
	public TLib() {
		
		this.aListing = FXCollections.observableArrayList();
		this.cUListing = FXCollections.observableArrayList();
		this.aCDListing = FXCollections.observableArrayList();
		this.iCDListing = FXCollections.observableArrayList();

		this.aListView = new ListView<>(this.aListing);
		this.cUListView = new ListView<>(this.cUListing);
		this.aCDListView = new ListView<>(this.aCDListing);
		this.iCDListView = new ListView<>(this.iCDListing);

		dSerializeAlarms();
		
		this.tSet = new TSet();
	}

}
