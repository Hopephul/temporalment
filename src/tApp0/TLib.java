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
	
	private ObservableList<Alarm> aList; //Alarm List
	private ObservableList<CntUp> cUList; //Count Up List
	private ObservableList<CntDn> aCDList; //Active Count Down List
	private ObservableList<CntDn> iCDList; //Idle Count Down List
	
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
	
	public void mapAlarm(Alarm inputAlarm) {
		String aFilePath = dFADirPath + File.separator + inputAlarm.getAIdString() + ".alrm";
		
		alarmFileMap.putIfAbsent(inputAlarm, aFilePath);
	}
	
	public void libAGen(LocalDate inputDate, LocalTime inputTime, String inputMsg, String inputPath, boolean inputMuted, boolean inputFocused, ReFreq inputFreq) {
		Alarm alarm = new Alarm(inputDate, inputTime, inputMsg, inputPath, inputMuted, inputFocused, inputFreq);
		getAList().addAll(alarm);
		getAListView().refresh();
		
		serializeAlarm(alarm);
		
	}
	
	public void removeAlarm(Alarm inputAlarm) {
		String aFilePath = this.alarmFileMap.get(inputAlarm);
		if(aFilePath != null) {
			File aFile = new File(aFilePath);
			if(aFile.exists() && aFile.delete()) {
				TLog.log.config(aFilePath + " Deletion Sucessful");
			} else {
				TLog.log.config(aFilePath + " Deletion Failed!");
			}
			this.alarmFileMap.remove(inputAlarm);
			this.aList.remove(inputAlarm);
		} else {
			TLog.log.info("No Such Saved Alarm To Remove?*");
		}
	}
	
	public void serializeAFileMap(HashMap<Alarm, String> inputMap) {
		File dir = new File(dFADirPath);
		
		if(!dir.exists()) {
			boolean dirCreated = dir.mkdirs();
			if(dirCreated) {
				TLog.log.config("Alarm Serialization Directory Created");
			} else {
				TLog.log.config("Application couldn't create the Directory @: " + dFADirPath);
				return;
			}
		}
		try {
			
			String mFilePath = this.dFADirPath + File.separator + "aMapping.hmap";
			
			File aMap = new File(mFilePath);
			
			if(aMap.exists() && aMap.delete()) {
				
				TLog.log.config("Alarm Mapping Data Cleared");
				
			} else {
				
				TLog.log.config("Alarm Mapping Data Absent");
				
			}
			
			HashMap<Alarm, String> aFileMap = this.alarmFileMap;
			
			FileOutputStream aMapFile = new FileOutputStream(mFilePath);
			
			ObjectOutputStream fileOutput = new ObjectOutputStream(aMapFile);
			
			fileOutput.writeObject(aFileMap);
			
			fileOutput.close();
			aMapFile.close();
			
			TLog.log.config("Alarm Mapping Data Serialized");
			
		} catch(Exception serError) {
			TLog.log.info("Serilization Error Encountered!");
			serError.printStackTrace();
		}
		
		
		
	}
	
	public void serializeAlarm(Alarm inputAlarm) {
		File dir = new File(this.dFADirPath);
		
		if(!dir.exists()) {
			boolean dirCreated = dir.mkdirs();
			if(dirCreated) {
				TLog.log.config("Alarm Serialization Directory Created");
			} else {
				TLog.log.config("Application couldn't create the Directory @: " + dFADirPath);
				return;
			}
		}
		try {
			FileOutputStream alrmFile = new FileOutputStream(this.dFADirPath + File.separator + inputAlarm.getAIdString() + ".alrm");
			ObjectOutputStream fileOutput = new ObjectOutputStream(alrmFile);
			fileOutput.writeObject(inputAlarm);
			fileOutput.close();
			alrmFile.close();
			mapAlarm(inputAlarm);
			serializeAFileMap(this.alarmFileMap);
			
			TLog.log.config("Single Alarm Data Saved to File, Mapped, and Map Saved to File");
		} catch(Exception serError) {
			TLog.log.info("Serilization Error Encountered!");
			serError.printStackTrace();
		}
	}
	
	public void serializeAlarms(ObservableList<Alarm> inputList) {
		File dir = new File(this.dFADirPath);
		
		if(!dir.exists()) {
			boolean dirCreated = dir.mkdirs();
			if(dirCreated) {
				TLog.log.config("Alarm Serialization Directory Created");
			} else {
				TLog.log.config("Application couldn't create the Directory @: " + dFADirPath);
				return;
			}
		} else {
			try {
				for(int i = 0; i < inputList.size(); i++) {
					Alarm alarm = inputList.get(i);
					
					File sAPath = new File(this.dFADirPath + File.separator + alarm.getAIdString() + ".alrm");
					
					if(!sAPath.exists()) {
						
						FileOutputStream alrmFile = new FileOutputStream(this.dFADirPath + File.separator + alarm.getAIdString() + ".alrm");
						
						ObjectOutputStream fileOutput = new ObjectOutputStream(alrmFile);
						
						fileOutput.writeObject(alarm);
						
						fileOutput.close();
						alrmFile.close();
						
						TLog.log.info("Alarm(s) Data Saved");
						
					} else {
						
						TLog.log.info("Alarm Data Already Saved");
						
					}
					
				}
			} catch(Exception serError) {
				TLog.log.info("Serilization Error Encountered!");
				serError.printStackTrace();
			}
		}
	}
	
	private boolean aInitChk(Alarm inputAlarm) {
		boolean awsr;
		if(inputAlarm.getADateTime().isBefore(Tkpr.getTNow()) && !(inputAlarm.getAReFreq() == ReFreq.SINGLE)) {
			switch (inputAlarm.getAReFreq()) {
				case ReFreq.DAILY:
					libAGen(inputAlarm.getADate().plusDays(1), inputAlarm.getATime(), inputAlarm.getAMsgString(), inputAlarm.getAAudioPath(), inputAlarm.isMuted(), inputAlarm.isFocused(), inputAlarm.getAReFreq());
					break;
				case ReFreq.WEEKLY:
					libAGen(inputAlarm.getADate().plusWeeks(1), inputAlarm.getATime(), inputAlarm.getAMsgString(), inputAlarm.getAAudioPath(), inputAlarm.isMuted(), inputAlarm.isFocused(), inputAlarm.getAReFreq());
					break;
				case ReFreq.BIWEEKLY:
					libAGen(inputAlarm.getADate().plusWeeks(2), inputAlarm.getATime(), inputAlarm.getAMsgString(), inputAlarm.getAAudioPath(), inputAlarm.isMuted(), inputAlarm.isFocused(), inputAlarm.getAReFreq());
					break;
				case ReFreq.MONTHLY:
					libAGen(inputAlarm.getADate().plusMonths(1), inputAlarm.getATime(), inputAlarm.getAMsgString(), inputAlarm.getAAudioPath(), inputAlarm.isMuted(), inputAlarm.isFocused(), inputAlarm.getAReFreq());
					break;
				case ReFreq.QUARTERLY:
					libAGen(inputAlarm.getADate().plusMonths(3), inputAlarm.getATime(), inputAlarm.getAMsgString(), inputAlarm.getAAudioPath(), inputAlarm.isMuted(), inputAlarm.isFocused(), inputAlarm.getAReFreq());
					break;
				case ReFreq.ANNUALLY:
					libAGen(inputAlarm.getADate().plusYears(1), inputAlarm.getATime(), inputAlarm.getAMsgString(), inputAlarm.getAAudioPath(), inputAlarm.isMuted(), inputAlarm.isFocused(), inputAlarm.getAReFreq());
					break;
				default:
					
			}
			
			
			else if(inputAlarm.getAReFreq() == ReFreq.MONTHLY) {
				libAGen(inputAlarm.getADate().plusMonths(1), inputAlarm.getATime(), inputAlarm.getAMsgString(), inputAlarm.getAAudioPath(), inputAlarm.isMuted(), inputAlarm.isFocused(), inputAlarm.getAReFreq());
			} else if(inputAlarm.getAReFreq() == ReFreq.QUARTERLY) {
				libAGen(inputAlarm.getADate().plusMonths(3), inputAlarm.getATime(), inputAlarm.getAMsgString(), inputAlarm.getAAudioPath(), inputAlarm.isMuted(), inputAlarm.isFocused(), inputAlarm.getAReFreq());
			} else if(inputAlarm.getAReFreq() == ReFreq.ANNUALLY) {
				libAGen(inputAlarm.getADate().plusYears(1), inputAlarm.getATime(), inputAlarm.getAMsgString(), inputAlarm.getAAudioPath(), inputAlarm.isMuted(), inputAlarm.isFocused(), inputAlarm.getAReFreq());
			}
		}
	}
	
	public void dSerializeAlarm() {
		File aDir = new File(this.dFADirPath);
		if(aDir.exists() && aDir.isDirectory()) {
			File[] aFiles = aDir.listFiles();
			if(aFiles != null) {
				for(File aFile : aFiles) {
					Optional<Alarm> alarm = readInAlarm(aFile);
				}
			}
		}
	}
	
	private Optional<Alarm> readInAlarm(File inputFile) {
		Alarm alarmIn;
		try {
			if(inputFile.isFile() && inputFile.getName().endsWith(".alrm")) {
				FileInputStream fileIn = new FileInputStream(inputFile);
				ObjectInputStream objectIn = new ObjectInputStream(fileIn);
				try{
					alarmIn = (Alarm) objectIn.readObject();
				} catch(ClassNotFoundException classCastError) {
					alarmIn = null;
				}
				objectIn.close();
				fileIn.close();
				
				return Optional.of(alarmIn);
			} else {
				return Optional.empty();
			}
		} catch(IOException readInError) {
			System.err.println("Unable to Read Input File at " + inputFile.getPath() + "\n" + readInError.getMessage());
			return Optional.empty();
		}
	}
	
	
	public void deserializeAlarms() {
		try {
			File aDir = new File(this.dFADirPath);
			if(aDir.exists() && aDir.isDirectory()) {
				File[] aDats = aDir.listFiles();
				if(aDats != null) {
					for(File aDat : aDats) {
						if(aDat.isFile() && aDat.getName().endsWith(".alrm")) {
							FileInputStream aDatIn = new FileInputStream(aDat);
							ObjectInputStream aIn = new ObjectInputStream(aDatIn);
							Alarm inputAlarm = (Alarm) aIn.readObject();
							aIn.close();
							aDatIn.close();
							mapAlarm(inputAlarm);
							if(!this.aList.contains(inputAlarm)) {
								this.aList.addAll(inputAlarm);
							} else {
								continue;
							}
							if(inputAlarm.getADateTime().isBefore(Tkpr.getTNow())) {
								if(inputAlarm.getAReFreq() != ReFreq.SINGLE) {
									if(inputAlarm.getAReFreq() == ReFreq.DAILY) {
										libAGen(inputAlarm.getADate().plusDays(1), inputAlarm.getATime(), inputAlarm.getAMsgString(), inputAlarm.getAAudioPath(), inputAlarm.isMuted(), inputAlarm.isFocused(), inputAlarm.getAReFreq());
									} else if(inputAlarm.getAReFreq() == ReFreq.WEEKLY) {
										libAGen(inputAlarm.getADate().plusWeeks(1), inputAlarm.getATime(), inputAlarm.getAMsgString(), inputAlarm.getAAudioPath(), inputAlarm.isMuted(), inputAlarm.isFocused(), inputAlarm.getAReFreq());
									} else if(inputAlarm.getAReFreq() == ReFreq.BIWEEKLY) {
										libAGen(inputAlarm.getADate().plusWeeks(2), inputAlarm.getATime(), inputAlarm.getAMsgString(), inputAlarm.getAAudioPath(), inputAlarm.isMuted(), inputAlarm.isFocused(), inputAlarm.getAReFreq());
									} else if(inputAlarm.getAReFreq() == ReFreq.MONTHLY) {
										libAGen(inputAlarm.getADate().plusMonths(1), inputAlarm.getATime(), inputAlarm.getAMsgString(), inputAlarm.getAAudioPath(), inputAlarm.isMuted(), inputAlarm.isFocused(), inputAlarm.getAReFreq());
									} else if(inputAlarm.getAReFreq() == ReFreq.QUARTERLY) {
										libAGen(inputAlarm.getADate().plusMonths(3), inputAlarm.getATime(), inputAlarm.getAMsgString(), inputAlarm.getAAudioPath(), inputAlarm.isMuted(), inputAlarm.isFocused(), inputAlarm.getAReFreq());
									} else if(inputAlarm.getAReFreq() == ReFreq.ANNUALLY) {
										libAGen(inputAlarm.getADate().plusYears(1), inputAlarm.getATime(), inputAlarm.getAMsgString(), inputAlarm.getAAudioPath(), inputAlarm.isMuted(), inputAlarm.isFocused(), inputAlarm.getAReFreq());
									} 
								} else {
									continue;
								}
								removeAlarm(inputAlarm);
							}
							inputAlarm.freshAAudio();
						} else {
							continue;
						}
						
					}
				}
			}
		} catch(Exception deserError) {
			TLog.log.info("Serilization Error Encountered!");
			deserError.printStackTrace();
		}
	}
	
	public ObservableList<Alarm> getAList() {
		return this.aList;
	}
	
	public ObservableList<CntUp> getCUList() {
		return this.cUList;
	}
	
	public ObservableList<CntDn> getACDList() {
		return this.aCDList;
	}
	
	public ObservableList<CntDn> getICDList() {
		return this.iCDList;
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
		
		try {
			File aMap = new File(this.dFADirPath + File.separator + "aMapping.hmap");
			if(aMap.exists() && aMap.isFile()) {
				FileInputStream aMapDatIn = new FileInputStream(aMap);
				ObjectInputStream aMapIn = new ObjectInputStream(aMapDatIn);
				this.alarmFileMap = (HashMap<Alarm, String>) aMapIn.readObject();
				aMapIn.close();
				aMapDatIn.close();
			} else {
				this.alarmFileMap = new HashMap<>();
			}
		} catch(Exception loadError) {
			TLog.log.info("Failed AlarmFileMap Initilization Loading");
			loadError.printStackTrace();
		}
		
		this.aList = FXCollections.observableArrayList();
		this.cUList = FXCollections.observableArrayList();
		this.aCDList = FXCollections.observableArrayList();
		this.iCDList = FXCollections.observableArrayList();
		
		this.aListView = new ListView<>(this.aList);
		this.cUListView = new ListView<>(this.cUList);
		this.aCDListView = new ListView<>(this.aCDList);
		this.iCDListView = new ListView<>(this.iCDList);
		
		this.tSet = new TSet();
	}

}
