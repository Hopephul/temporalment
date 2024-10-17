package tApp0;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;

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
	
	public void removeAlarm(Alarm inputAlarm) {
		String aFilePath = alarmFileMap.get(inputAlarm);
		if(aFilePath != null) {
			File aFile = new File(aFilePath);
			if(aFile.exists() && aFile.delete()) {
				TLog.log.config(aFilePath + " Deletion Sucessful");
			} else {
				TLog.log.config(aFilePath + " Deletion Failed!");
			}
			alarmFileMap.remove(inputAlarm);
			aList.remove(inputAlarm);
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
			
			String mFilePath = dFADirPath + File.separator + "aMapping.hmap";
			
			File aMap = new File(mFilePath);
			
			if(aMap.exists() && aMap.delete()) {
				
				TLog.log.config("Alarm Mapping Data Cleared");
				
			} else {
				
				TLog.log.config("Alarm Mapping Data Absent");
				
			}
			
			HashMap<Alarm, String> aFileMap = alarmFileMap;
			
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
			FileOutputStream alrmFile = new FileOutputStream(dFADirPath + File.separator + inputAlarm.getAIdString() + ".alrm");
			ObjectOutputStream fileOutput = new ObjectOutputStream(alrmFile);
			fileOutput.writeObject(inputAlarm);
			fileOutput.close();
			alrmFile.close();
			mapAlarm(inputAlarm);
			serializeAFileMap(alarmFileMap);
			
			TLog.log.config("Single Alarm Data Saved to File, Mapped, and Map Saved to File");
		} catch(Exception serError) {
			TLog.log.info("Serilization Error Encountered!");
			serError.printStackTrace();
		}
	}
	
	public void serializeAlarms(ObservableList<Alarm> inputList) {
		File dir = new File(dFADirPath);
		
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
					
					File sAPath = new File(dFADirPath + File.separator + alarm.getAIdString() + ".alrm");
					
					if(!sAPath.exists()) {
						
						FileOutputStream alrmFile = new FileOutputStream(dFADirPath + File.separator + alarm.getAIdString() + ".alrm");
						
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
	
	public void deserializeAlarms() {
		try {
			File aDir = new File(dFADirPath);
			if(aDir.exists() && aDir.isDirectory()) {
				File[] aDats = aDir.listFiles();
				if(aDats != null) {
					for(File aDat : aDats) {
						if(aDat.isFile() && aDat.getName().endsWith(".alrm")) {
							FileInputStream aDatIn = new FileInputStream(aDat);
							ObjectInputStream aIn = new ObjectInputStream(aDatIn);
							Alarm alarm = (Alarm) aIn.readObject();
							aIn.close();
							aDatIn.close();
							mapAlarm(alarm);
							if(!aList.contains(alarm)) {
								aList.addAll(alarm);
							} else {
								continue;
							}
							if(alarm.getADateTime().isBefore(Tkpr.getTNow())) {
								if(alarm.getAReFreq() != ReFreq.SINGLE) {
									TApp.getAlarmVw().aReGen(alarm);
								} else {
									continue;
								}
								removeAlarm(alarm);
							}
							alarm.freshAAudio();
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
		return dFAAudioPath;
	}
	
	public String getDFCDAudioPath() {
		return dFCDAudioPath;
	}
	
	public String getDFClickAudioPath() {
		return dFClickAudioPath;
	}
	
	public String getDFAcceptAudioPath() {
		return dFAcceptAudioPath;
	}
	
	public String getDFRejectAudioPath() {
		return dFRejectAudioPath;
	}
	
	public TLib() {
		
		try {
			File aMap = new File(dFADirPath + File.separator + "aMapping.hmap");
			if(aMap.exists() && aMap.isFile()) {
				FileInputStream aMapDatIn = new FileInputStream(aMap);
				ObjectInputStream aMapIn = new ObjectInputStream(aMapDatIn);
				alarmFileMap = (HashMap<Alarm, String>) aMapIn.readObject();
				aMapIn.close();
				aMapDatIn.close();
			} else {
				alarmFileMap = new HashMap<>();
			}
		} catch(Exception loadError) {
			TLog.log.info("Failed AlarmFileMap Initilization Loading");
			loadError.printStackTrace();
		}
		
		aList = FXCollections.observableArrayList();
		cUList = FXCollections.observableArrayList();
		aCDList = FXCollections.observableArrayList();
		iCDList = FXCollections.observableArrayList();
		
		aListView = new ListView<>(aList);
		cUListView = new ListView<>(cUList);
		aCDListView = new ListView<>(aCDList);
		iCDListView = new ListView<>(iCDList);
		
		tSet = new TSet();
	}

}
