package tApp0;

import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.media.MediaPlayer.Status;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.Duration;
import java.io.File;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;


public class TApp {
	
	private Stage dispStage; //Display Stage: Primary Stage node used to display application to the user from the window...
	private Scene dispScene; //Display Scene: Contains all applications active nodes being shown to and used by the user...
	
	private static CtrlVw ctrlBox;
	private static ClockVw clokStack;
	private static AlarmVw alrmStack;
	private static TimerVw timrStack;
	
	private VBox dispStack; //Display Stack: Root / Parent Node for display nodes (ie: HBoxs and VBoxs etc...)...
	
	private ScrollPane tentPane;
	
	private TLib tLib; //Data && Information Storage Management Class (Also works as a Reader/Writer Class...)...
	
	public static CtrlVw getCtrlVw() {
		return ctrlBox;
	}
	
	public static ClockVw getClockVw() {
		return clokStack;
	}
	
	public void getAlarmTent() {
		this.tentPane.setContent(alrmStack);
		TLog.log.config("TentPane set to Alarms");
	}
	
	public static AlarmVw getAlarmVw() {
		return alrmStack;
	}
	
	public void getTimerTent() {
		this.tentPane.setContent(timrStack);
		TLog.log.config("TentPane set to Timers");
	}
	
	public static TimerVw getTimerVw() {
		return timrStack;
	}
	
	public void getClearTent() {
		this.tentPane.setContent(null);
		TLog.log.config("TentPane Contents Cleared");
	}
	
	public void exitStage(Stage inputStage) {
		inputStage.close();
		TLog.log.config(inputStage.toString() + " has Exited");
	}
	
	public Stage getDispStage() {
		return this.dispStage;
	}
	
	public Scene getDispScene() {
		return this.dispScene;
	}
	
	public TApp() {
		
		if(dispStack != null) {
			dispStack.getChildren().addAll(ctrlBox, clokStack, tentPane);
			dispStage.setScene(dispScene);
		} else {
			tLib = new TLib();
			ctrlBox = new CtrlVw();
			clokStack = new ClockVw();
			alrmStack = new AlarmVw();
			timrStack = new TimerVw();
			dispStack = new VBox();
			tentPane = new ScrollPane();
			
			
			dispStack.getChildren().addAll(ctrlBox, clokStack, tentPane);
			dispScene = new Scene(dispStack, 300, 450);
			dispStage = new Stage();
			
			tentPane.setFitToWidth(true);
			tentPane.setFitToHeight(true);
			
			dispStage.setScene(dispScene);
			
			getClearTent();
			
			TLog.log.config("TApp initilization completed...");
		}
		
	}
	
	public class CtrlVw extends HBox {
		
		private Text aText = new Text("ALARMS");
		private Text cText = new Text("CLOCK");
		private Text tText = new Text("TIMERS");
		
		private Button aAddButt;
		private Button tAddButt;
		private Button aButt;
		private Button cButt;
		private Button tButt;
		
		public CtrlVw() {
			
			aAddButt = new Button("+");
			tAddButt = new Button("+");
			aButt = new Button("ALARMS");
			cButt = new Button("CLOCK");
			tButt = new Button("TIMERS");
			
			aAddButt.setOnAction(press -> {
				alrmStack.aAdd();
				TLog.log.config("New Alarm Dialog Opened");
			});
			
			aButt.setOnAction(press -> {
				getAlarmTent();
				this.getChildren().clear();
				this.getChildren().addAll(aText, aAddButt, cButt, tButt);
				this.setAlignment(Pos.CENTER);
				this.setPadding(new Insets(10.0));
				this.setSpacing(10.0);
				
				TLog.log.config("Controls Configured for Alarms");
			});
			
			cButt.setOnAction(press -> {
				getClearTent();
				this.getChildren().clear();
				this.getChildren().addAll(aButt, cText, tButt);
				this.setAlignment(Pos.CENTER);
				this.setPadding(new Insets(10.0));
				this.setSpacing(10.0);
				
				TLog.log.config("Controls Configured for Clockface");
			});
			
			tAddButt.setOnAction(press -> {
				
				TLog.log.config("New CountDown Timer Dialog Opened");
			});
			
			tButt.setOnAction(press -> {
				getTimerTent();
				this.getChildren().clear();
				this.getChildren().addAll(aButt, cButt, tAddButt, tText);
				this.setAlignment(Pos.CENTER);
				this.setPadding(new Insets(10.0));
				this.setSpacing(10.0);
				
				TLog.log.config("Controls Configured for Timers");
			});
			
			this.getChildren().clear();
			this.getChildren().addAll(aButt, cText, tButt);
			this.setAlignment(Pos.CENTER);
			this.setPadding(new Insets(10.0));
			this.setSpacing(10.0);
			
			TLog.log.config("Application Controls Initilization Completed");
			
		}
		
	}
	
	public class ClockVw extends VBox {

		private String clockString;
		private Text cText;
		private Timeline cLine;
		
		public void refreshClock() {
			if(!Tkpr.getTSplit()) {
				clockString = new String(Tkpr.getTNow().format(Tkpr.getTHForm()) + " : " + Tkpr.getTNow().format(Tkpr.getTmForm()) + " : " + Tkpr.getTNow().format(Tkpr.getTsForm()));
			} else if(Tkpr.getTSplit()) {
				clockString = new String(Tkpr.getTNow().format(Tkpr.getThForm()) + " : " + Tkpr.getTNow().format(Tkpr.getTmForm()) + " : " + Tkpr.getTNow().format(Tkpr.getTsForm()) + " " + Tkpr.getTNow().format(Tkpr.getTaForm()));
			}
		}
		
		private void refreshCText() {
			if(cText != null) {
				cText.setText(clockString);
			} else {
				cText = new Text();
				cText.setText(clockString);
			}
		}
		
		private Timeline cFace() {
			if(cLine != null) {
				return cLine;
			} else {
				
				KeyFrame initKey = new KeyFrame(Duration.ZERO, init -> {
					cText.setStyle("-fx-font-size:21px;");
					refreshClock();
					refreshCText();
				});
				KeyFrame termKey = new KeyFrame(Tkpr.getTTic());
				cLine = new Timeline(initKey, termKey);
				
				return cLine;
			}
		}
		
		public ClockVw() {
			clockString = new String();
			cText = new Text();
			this.getChildren().addAll(cText);
			this.setAlignment(Pos.CENTER);
			cFace().setCycleCount(Animation.INDEFINITE);
			cFace().play();
		}
		
	}
	
	public class AlarmVw extends VBox {
		
		private Scene aAScene;
		private Stage aAStage;
		
		private HBox aPBox;
		private VBox aPNozzStack;
		private VBox aPMissStack;
		private VBox aPStatStack;
		private VBox sAStack;
		
		private Button aPNozzButt;
		private Button aPMissButt;
		
		private String aPMsgString;
		private String aPTString;
		private String aPDString;
		private String aPRString;
		
		private Text aPMsgText;
		private Text aPTText;
		private Text aPDText;
		private Text aPRText;
		
		private void aChk() {
			while(true) {
				for(Alarm cAlarm : tLib.getAList()) {
					if(cAlarm.getADateTime().isBefore(Tkpr.getTNow()) && !cAlarm.isActivated()) {
						Platform.runLater(() -> {
							aActivate(cAlarm);
						});
					}
				}
				try {
					Thread.sleep(250);
				} catch(InterruptedException checkingError) {
					TLog.log.info("Alarm Checking Error!");
					checkingError.printStackTrace();
				}
			}
		}
		
		private void aChkr() {
			Thread aChkThread = new Thread(this::aChk);
			aChkThread.setDaemon(true);
			aChkThread.start();
		}
		
		public Alarm queryAP() {
			if(tLib.getAList().isEmpty()) {
				TLog.log.config("Alarm Pending Absent");
				return null;
			} else {
				TLog.log.config("Alarm Pending Found");
				return tLib.getAList().get(0);
			}
			
			
		}
		
		public void aSort() {
			FXCollections.sort(tLib.getAList(), Comparator.comparing(Alarm::getADateTime));
			TLog.log.config("Alarm(s) Sorted");
		}
		
		public void freshAP() {
			Platform.runLater(() -> {
				Alarm pAlarm = queryAP();
				if(pAlarm != null) {
					if(pAlarm.isActivated()) {
						aPMsgString = "ALARM ACTIVATED!\n" + pAlarm.getAMsgString();
						aPTString = pAlarm.getATimeString();
						aPMsgText.setText(aPMsgString);
						aPTText.setText(aPTString);
						aPNozzButt.setDisable(false);
						aPMissButt.setDisable(false);
						aPNozzButt.setVisible(true);
						aPMissButt.setVisible(true);
					} else {
						aPMsgString = pAlarm.getAMsgString();
						aPTString = pAlarm.getATimeString();
						aPMsgText.setText(aPMsgString);
						aPTText.setText(aPTString);
						aPNozzButt.setDisable(true);
						aPMissButt.setDisable(true);
						aPNozzButt.setVisible(false);
						aPMissButt.setVisible(false);
					}
				} else {
					aPMsgString = "No Pending Alarms...";
					aPTString = "@ ## : ## *";
					aPMsgText.setText(aPMsgString);
					aPTText.setText(aPTString);
					aPNozzButt.setDisable(true);
					aPMissButt.setDisable(true);
					aPNozzButt.setVisible(false);
					aPMissButt.setVisible(false);
				}
			});
			
			TLog.log.config("Alarm Pending Refreshed");
		}
		
		public void aActivate(Alarm aAlarm) {
			aAlarm.setIsActivated(true);
			freshAP();
			try {
				if(aAlarm.isFocused()) {
					AAVw aAVw = new AAVw(aAlarm);
				}
				if(!aAlarm.isMuted()) {
					aAlarm.aAudioPlay();
				}				
			} catch(Exception activationException) {
				TLog.log.info("Alarm Activation Error!");
				activationException.printStackTrace();
			}
			
			TLog.log.config("Alarm Activated");
			
		}
		
		public void aAdd() {
			NAVw nAVw = new NAVw();
			TLog.log.config("^||:...");
		}
		
		public void aGen(LocalDate inputDate, LocalTime inputTime, String inputMsg, String inputPath, boolean inputMuted, boolean inputFocused, ReFreq inputFreq) {
			Alarm alarm = new Alarm(inputDate, inputTime, inputMsg, inputPath, inputMuted, inputFocused, inputFreq);
			tLib.getAList().addAll(alarm);
			tLib.getAListView().refresh();
			freshAP();
			tLib.serializeAlarm(alarm);
			
		}
		
		public void aReGen(Alarm rAlarm) {
			if(rAlarm.getAReFreq() != ReFreq.SINGLE) {
				if(rAlarm.getAReFreq() == ReFreq.DAILY) {
					aGen(rAlarm.getADate().plusDays(1), rAlarm.getATime(), rAlarm.getAMsgString(), rAlarm.getAAudioPath(), rAlarm.isMuted(), rAlarm.isFocused(), rAlarm.getAReFreq());
				} else if(rAlarm.getAReFreq() == ReFreq.WEEKLY) {
					aGen(rAlarm.getADate().plusWeeks(1), rAlarm.getATime(), rAlarm.getAMsgString(), rAlarm.getAAudioPath(), rAlarm.isMuted(), rAlarm.isFocused(), rAlarm.getAReFreq());
				} else if(rAlarm.getAReFreq() == ReFreq.BIWEEKLY) {
					aGen(rAlarm.getADate().plusWeeks(2), rAlarm.getATime(), rAlarm.getAMsgString(), rAlarm.getAAudioPath(), rAlarm.isMuted(), rAlarm.isFocused(), rAlarm.getAReFreq());
				} else if(rAlarm.getAReFreq() == ReFreq.MONTHLY) {
					aGen(rAlarm.getADate().plusMonths(1), rAlarm.getATime(), rAlarm.getAMsgString(), rAlarm.getAAudioPath(), rAlarm.isMuted(), rAlarm.isFocused(), rAlarm.getAReFreq());
				} else if(rAlarm.getAReFreq() == ReFreq.QUARTERLY) {
					aGen(rAlarm.getADate().plusMonths(3), rAlarm.getATime(), rAlarm.getAMsgString(), rAlarm.getAAudioPath(), rAlarm.isMuted(), rAlarm.isFocused(), rAlarm.getAReFreq());
				} else if(rAlarm.getAReFreq() == ReFreq.ANNUALLY) {
					aGen(rAlarm.getADate().plusYears(1), rAlarm.getATime(), rAlarm.getAMsgString(), rAlarm.getAAudioPath(), rAlarm.isMuted(), rAlarm.isFocused(), rAlarm.getAReFreq());
				} 
			} else {
				return;
			}
			
			TLog.log.config("Alarm Regenerated if ReFreq is Higher than 'SINGLE'");
			
		}

		public void aSusp(Alarm dAlarm) {
			if(dAlarm.getAAudioStatus() == Status.PLAYING) {
				dAlarm.aAudioStop();
			}
			dAlarm.aSnozz(5);
			aSort();
			tLib.getAListView().refresh();
			freshAP();
			
			TLog.log.config("Active Alarm Delayed");
			
		}
		
		public void aEdit(Alarm tAlarm) {
			
		}
		
		public void aExit(Alarm eAlarm) {
			if(eAlarm.getAAudioStatus() == Status.PLAYING) {
				eAlarm.aAudioStop();
			}
			
			aReGen(eAlarm);
			
			tLib.removeAlarm(eAlarm);
			aSort();
			tLib.getAListView().refresh();
			freshAP();
			
			TLog.log.config(eAlarm.getAIdString() + " Dismissed and Removed");
			
		}
		
		public void aRemo(Alarm rAlarm) {
			if(rAlarm.getAAudioStatus() == Status.PLAYING) {
				rAlarm.aAudioStop();
			}
			
			tLib.removeAlarm(rAlarm);
			aSort();
			tLib.getAListView().refresh();
			freshAP();
			
			TLog.log.config(rAlarm.getAIdString() + " Removed");
			
		}
		
		public AlarmVw() {
			
			aPMsgString = new String();
			aPTString = new String();
			aPDString = new String();
			aPRString = new String();
			aPMsgText = new Text(aPMsgString);
			aPTText = new Text(aPTString);
			aPDText = new Text(aPDString);
			aPRText = new Text(aPRString);
			
			tLib.getAListView().setCellFactory(new Callback<ListView<Alarm>, ListCell<Alarm>>() {
				@Override
				public ListCell<Alarm> call(ListView<Alarm> sAListView) {
					return new SAListCell();
				}
			});
			
			aPNozzButt = new Button("⏳");
			aPNozzButt.setOnAction(press -> {
				aSusp(queryAP());
			});
			aPMissButt = new Button("🆗");
			aPMissButt.setOnAction(press -> {
				aExit(queryAP());				
			});
			
			sAStack = new VBox();
			aPMissStack = new VBox();
			aPStatStack = new VBox();
			aPNozzStack = new VBox();
			aPBox = new HBox();
			
			sAStack.getChildren().addAll(tLib.getAListView());
			aPMissStack.getChildren().addAll(aPMissButt);
			aPStatStack.getChildren().addAll(aPMsgText, aPTText, aPDText, aPRText);
			aPNozzStack.getChildren().addAll(aPNozzButt);
			aPBox.getChildren().addAll(aPNozzStack, aPStatStack, aPMissStack);
			this.getChildren().addAll(aPBox, sAStack);
			
			sAStack.setAlignment(Pos.TOP_CENTER);
			aPMissStack.setAlignment(Pos.TOP_RIGHT);
			aPStatStack.setAlignment(Pos.TOP_CENTER);
			aPNozzStack.setAlignment(Pos.TOP_LEFT);
			aPBox.setAlignment(Pos.TOP_CENTER);
			this.setAlignment(Pos.TOP_CENTER);
			
			aChkr();
			tLib.deserializeAlarms();
			freshAP();
			tLib.getAListView().refresh();
			
			TLog.log.config("AlarmVw Class Initilization Completed");
			
		}
		
		public class AAVw extends VBox {
			
			private VBox aAStack;
			private HBox aABox;
			
			private String aAMsgString;
			private String aATString;
			private String aADString;
			private String aARString;
			
			private Text aAMsgText;
			private Text aATText;
			private Text aADText;
			private Text aARText;
			
			private Button aANozzButt;
			private Button aAMissButt;
			
			public AAVw(Alarm aAlarm) {
				
				aAMsgString = new String(aAlarm.getAMsgString());
				aATString = new String(aAlarm.getATimeString());
				aADString = new String(aAlarm.getADateString());
				aARString = new String(aAlarm.getAReFreqString());
				
				aAMsgText = new Text(aAMsgString);
				aATText = new Text(aATString);
				aADText = new Text(aADString);
				aARText = new Text(aARString);
				
				aANozzButt = new Button("⏳");
				aAMissButt = new Button("🆗");
				
				aANozzButt.setOnAction(pressed -> {
					aSusp(aAlarm);
					exitStage(aAStage);
				});
				
				aAMissButt.setOnAction(pressed -> {
					aExit(aAlarm);
					exitStage(aAStage);
				});
				
				aAStack = new VBox();
				aABox = new HBox();
				
				aAStack.setPadding(new Insets(10.0));
				aAStack.setSpacing(10.0);
				aABox.setPadding(new Insets(10.0));
				aABox.setSpacing(10.0);
				
				aAStack.getChildren().addAll(aAMsgText, aATText);
				aABox.getChildren().addAll(aANozzButt, aAMissButt);
				this.getChildren().addAll(aAStack, aABox);
				
				aAScene = new Scene(this);
				aAStage = new Stage();
				
				aAStage.setTitle("ACTIVATED ALARM");
				aAStage.initModality(Modality.NONE);
				aAStage.setScene(aAScene);
				aAStage.sizeToScene();
				
				dispStage.toFront();
				dispStage.requestFocus();
				dispStage.requestFocus();
				
				aAStage.show();
				aAStage.toFront();
				aAStage.requestFocus();
				aAStage.requestFocus();
				aAStage.toFront();
				aAStage.show();
				//refresh methods lmao
				//refreshPABox();
				
				TLog.log.info("Focused Alarm Activated");
				
			}
			
		}
		
		public class NAVw extends VBox {

			private Scene nAScene;
			private Stage nAStage;
			
			private HBox nABox; //Contains all of the following..
			private VBox nASetStack; //Contains most of the controls for setting a new alarm..
			private HBox nAMsgBox; //Contains the Text field that the user can write a message in..
			private HBox nATPickBox; //Contains the custom "time picker"..
			private VBox nATHStack; //Contains the "time picker" hour nodes..
			private VBox nATMStack; //Contains the "time picker" minute nodes..
			private HBox nADPickBox; //Contains the DatePicker..
			private HBox nAAudioBox; //Contains the nodes related to Alarm Audio..
			private HBox nAFocusBox; //Contains the nodes related to Alarm Focus..
			private VBox nAStatStack; //Contains the nodes used to show info to user and repetition controls..
			private HBox nATBox; //Shows time Alarm is currently set for..
			private HBox nADBox; //Shows date Alarm is currently set for..
			private HBox nADayBox; //Shows the day the Alarm is currently set on..
			private VBox nARStack; //Contains the repetition control nodes..
			private HBox nALBox; //Contains a separation line of some sort..
			private HBox nAButtBox; //Contains the "CREATE" && "CANCEL" buttons..
			
			private LocalDate nALocalDate;
			private LocalTime nALocalTime;
			
			private ReFreq nAReFreq;
			
			private int nAHrInt;
			private int nAMnInt;
			
			private Boolean toMute;
			private Boolean toFocus;
			
			private String nAHrString; //Stores Hr int for new alarm as a string..
			private String nAMnString; //Stores Mn int for new alarm as a string..
			private String nADPString; //Stores Day Period (AM or PM) as a string..
			private String nADayString; //Stores the weekday as a string for the new alarm..
			private String nADString; //Stores formatted Date as a string for new alarm..
			private String nATString; //Stores formatted Time as a string for new alarm..
			private String nARString; //Stores ReFreq for new alarm post formatting...
			private String nAMsgString; //Stores the Textfield value if any as a string for new alarm..
			private String nAAudioString; //String for nAAudioText..
			private String nAFocusString; //String for nAFocusText..
			private String nAFilePath; //Stores the file path (the URI) for selected Audio to be assigned to the new alarm..
			
			private Text nAHrText;
			private Text nAMnText;
			private Text nADPText;
			private Text nADayText;
			private Text nADText;
			private Text nATText;
			private Text nARText;
			private Text nAMsgText;
			private Text nAAudioText;
			private Text nAFocusText;
			private Text nAHrTopText; //Label text for Hr stack in time picker..
			private Text nAMnTopText; //Label text for Mn stack in time picker..
			private Text nASepText; //Literally just " : " for separating the Hours and Mins's lmao..
			
			private Button nAHrPButt;
			private Button nAHrMButt;
			private Button nAMnPButt;
			private Button nAMnMButt;
			private Button nAAudioButt;
			private Button nACreateButt;
			private Button nACancelButt;
			
			private TextField nAMsgr;
			private DatePicker nADPicker;
			private ComboBox<String> nARCombo;
			private ObservableList<String> nARList;
			private CheckBox nAAudioCBox;
			private CheckBox nAFocusCBox;
			private File nAAudioFile;
			private FileChooser nAAudioFileChooser;
			
			private void nAHrIntPlus() {
				if(nAHrInt <= 22) {
					nAHrInt ++;
					calcNALocalTime();
					refreshNATText();
					TLog.log.info("New Alarm Hour Value Plus One (+1)...");
				} else {
					nAHrInt = 0;
					calcNALocalTime();
					refreshNATText();
					TLog.log.info("New Alarm Hour Value Plus One (+1)...");
				}
			}
			
			private void nAHrIntMinus() {
				if(nAHrInt >= 1) {
					nAHrInt --;
					calcNALocalTime();
					refreshNATText();
					TLog.log.info("New Alarm Hour Value Minus One (-1)...");
				} else {
					nAHrInt = 23;
					calcNALocalTime();
					refreshNATText();
					TLog.log.info("New Alarm Hour Value Minus One (-1)...");
				}
			}
			
			private void nAMnIntPlus() {
				if(nAMnInt <= 58) {
					nAMnInt ++;
					calcNALocalTime();
					refreshNATText();
					TLog.log.info("New Alarm Minute Value Plus One (+1)...");
				} else {
					nAMnInt = 0;
					calcNALocalTime();
					refreshNATText();
					TLog.log.info("New Alarm Minute Value Plus One (+1)...");
				}
			}
			
			private void nAMnIntMinus() {
				if(nAMnInt >= 1) {
					nAMnInt --;
					calcNALocalTime();
					refreshNATText();
					TLog.log.info("New Alarm Minute Value Minus One (-1)...");
				} else {
					nAMnInt = 59;
					calcNALocalTime();
					refreshNATText();
					TLog.log.info("New Alarm Minute Value Minus One (-1)...");
				}
			}
			
			public LocalDate getNALocalDate() {
				return this.nALocalDate;
			}
			
			public LocalTime getNALocalTime() {
				return this.nALocalTime;
			}
			
			public String getNAMsgString() {
				return this.nAMsgString;
			}
			
			public String getNAFilePath() {
				return this.nAFilePath;
			}
			
			public ReFreq getNAReFreq() {
				return this.nAReFreq;
			}
			
			public Boolean getToMute() {
				return this.toMute;
			}
			
			public Boolean getToFocus() {
				return this.toFocus;
			}
			
			private void refreshNATText() {
				if(Tkpr.getTSplit()) {
					if(nAHrInt <= 0) {
						nATText.setText("@ " + String.format("%02d", nAHrInt + 12) + " : " + String.format("%02d", nAMnInt) + " AM");
						nAHrText.setText("" + String.format("%02d", nAHrInt + 12));
						nAMnText.setText("" + String.format("%02d", nAMnInt));
						calcNALocalTime();
						TLog.log.info("New Alarm Time Text Updated! ...");
					} else if(nAHrInt >= 1 && nAHrInt <= 11) {
						nATText.setText("@ " + String.format("%02d", nAHrInt) + " : " + String.format("%02d", nAMnInt) + " AM");
						nAHrText.setText("" + String.format("%02d", nAHrInt));
						nAMnText.setText("" + String.format("%02d", nAMnInt));
						calcNALocalTime();
						TLog.log.info("New Alarm Time Text Updated! ...");
					} else if(nAHrInt == 12) {
						nATText.setText("@ " + String.format("%02d", nAHrInt) + " : " + String.format("%02d", nAMnInt) + " PM");
						nAHrText.setText("" + String.format("%02d", nAHrInt));
						nAMnText.setText("" + String.format("%02d", nAMnInt));
						calcNALocalTime();
						TLog.log.info("New Alarm Time Text Updated! ...");
					} else if(nAHrInt >= 13) {
						nATText.setText("@ " + String.format("%02d", nAHrInt - 12) + " : " + String.format("%02d", nAMnInt) + " PM");
						nAHrText.setText("" + String.format("%02d", nAHrInt - 12));
						nAMnText.setText("" + String.format("%02d", nAMnInt));
						calcNALocalTime();
						TLog.log.info("New Alarm Time Text Updated! ...");
					}
				} else {
					nATText.setText("@ " + String.format("%02d", nAHrInt) + " : " + String.format("%02d", nAMnInt));
					nAHrText.setText("" + String.format("%02d", nAHrInt));
					nAMnText.setText("" + String.format("%02d", nAMnInt));
					calcNALocalTime();
					TLog.log.info("New Alarm Time Text Updated! ...");
				}
			}
			
			private void calcNALocalTime() {
				this.nALocalTime = LocalTime.of(nAHrInt, nAMnInt);
				TLog.log.info("New Alarm Time Calculated! ...");
			}
			
			public NAVw() {
				TLog.log.info("New Alarm GUI Initilized...");
				nAHrInt = 0;
				nAMnInt = 0;
				
				nABox = new HBox(10.0);
				nASetStack = new VBox(10.0);
				nAMsgBox = new HBox(10.0);
				nATPickBox = new HBox(10.0);
				nATHStack = new VBox(10.0);
				nATMStack = new VBox(10.0);
				nADPickBox = new HBox(10.0);
				nAAudioBox = new HBox(10.0);
				nAFocusBox = new HBox(10.0);
				nAStatStack = new VBox(10.0);
				nATBox = new HBox(10.0);
				nADBox = new HBox(10.0);
				nADayBox = new HBox(10.0);
				nARStack = new VBox(10.0);
				nALBox = new HBox(10.0);
				nAButtBox = new HBox(10.0);
				
				calcNALocalTime();
				
				nAHrString = new String("" + nAHrInt);
				nAMnString = new String("" + nAMnInt);
				if(Tkpr.getTSplit()) {
					nADPString = new String(nALocalTime.format(Tkpr.getTaForm()));					
				} else {
					nADPString = new String();
				}
				nADayString = new String();
				nADString = new String("ON ##.##.####");
				if(Tkpr.getTSplit()) {
					nATString = new String("@ " + nALocalTime.format(Tkpr.getThForm()) + " : " + nALocalTime.format(Tkpr.getTmForm()) + " " + nADPString);					
				} else {
					nATString = new String("@ " + nALocalTime.format(Tkpr.getTHForm()) + " : " + nALocalTime.format(Tkpr.getTmForm()) + " ");
				}
				nAMsgString = new String("Alarm Set For: ");
				nAAudioString = new String("🔊");
				nAFocusString = new String("🗗");
				nAFilePath = new String(tLib.getDFAAudioPath());
				
				nAHrText = new Text(nAHrString);
				nAMnText = new Text(nAMnString);
				nADayText = new Text(nADayString);
				nADText = new Text(nADString);
				nATText = new Text(nATString);
				nARText = new Text("REPEATS");
				nARText.setStyle("-fx-font-size:10px;");
				nAMsgText = new Text(nAMsgString);
				nAAudioText = new Text(nAAudioString);
				nAFocusText = new Text(nAFocusString);
				nAHrTopText = new Text("Hour");
				nAHrTopText.setStyle("-fx-font-size:9px;");
				nAMnTopText = new Text("Mins");
				nAMnTopText.setStyle("-fx-font-size:9px;");
				nASepText = new Text(" : ");
				nASepText.setStyle("-fx-font-size:17px;");
				
				nAHrText.setOnScroll(scroll -> {
					if(scroll.getDeltaY() > 0) {
						nAHrIntPlus();
					} else {
						nAHrIntMinus();
					}
				});
				
				nAMnText.setOnScroll(scroll -> {
					if(scroll.getDeltaY() > 0) {
						nAMnIntPlus();
					} else {
						nAMnIntMinus();
					}
				});
				
				TLog.log.info("New Alarm Writing && Basic Nodes Loaded...");
				
				nAMsgr = new TextField();
				nAMsgr.setPromptText("ALARM MESSAGE... *(Optional)...");
				nAMsgr.textProperty().addListener((observedValue, oldValue, newValue) -> {
					if(newValue != null && !newValue.isEmpty()) {
						nAMsgString = newValue;
						nAMsgText.setText(nAMsgString);
					} else {
						nAMsgString = "Alarm Set For: ";
						nAMsgText.setText(nAMsgString);
					}
				});
				
				nADPicker = new DatePicker();
				nADPicker.valueProperty().addListener((observedValue, oldValue, newValue) -> {
					if(newValue != null) {
						nALocalDate = newValue;
						nADString = "ON " + nALocalDate.format(DateTimeFormatter.ofPattern("MM.dd.yyyy"));
						nADayString = nALocalDate.format(Tkpr.getTEForm());
						nADText.setText(nADString);
						nADayText.setText(nADayString);
					} else {
						nADString = "ON ##.##.####";
						nADayString = "-----";
						nADText.setText(nADString);
						nADayText.setText(nADayString);
					}
				});
				
				nARCombo = new ComboBox<String>();
				ObservableList<String> nARList = nARCombo.getItems();
				nARList.addAll("SINGLE", "DAILY", "WEEKLY", "BIWEEKLY", "MONTHLY", "QUARTERLY", "ANNUALLY");
				nARCombo.setValue("SINGLE");
				nAReFreq = ReFreq.SINGLE;
				nARCombo.setOnAction(select -> {
					if(nARCombo.valueProperty().get() == "SINGLE") {
						nAReFreq = ReFreq.SINGLE;
					} else if(nARCombo.valueProperty().get() == "DAILY") {
						nAReFreq = ReFreq.DAILY;
					} else if(nARCombo.valueProperty().get() == "WEEKLY") {
						nAReFreq = ReFreq.WEEKLY;
					} else if(nARCombo.valueProperty().get() == "BIWEEKLY") {
						nAReFreq = ReFreq.BIWEEKLY;
					} else if(nARCombo.valueProperty().get() == "MONTHLY") {
						nAReFreq = ReFreq.MONTHLY;
					} else if(nARCombo.valueProperty().get() == "QUARTERLY") {
						nAReFreq = ReFreq.QUARTERLY;
					} else if(nARCombo.valueProperty().get() == "ANNUALLY") {
						nAReFreq = ReFreq.ANNUALLY;
					} else {
						nAReFreq = ReFreq.UNSET;
					}
				});
				
				this.toMute = false;
				nAAudioCBox = new CheckBox();
				nAAudioCBox.setSelected(true);
				nAAudioCBox.setOnAction(selected -> {
					if(this.nAAudioCBox.isSelected()) {
						this.toMute = false;
					} else {
						this.toMute = true;
					}
				});
				this.toFocus = true;
				nAFocusCBox = new CheckBox();
				nAFocusCBox.setSelected(true);
				nAFocusCBox.setOnAction(selected -> {
					if(this.nAFocusCBox.isSelected()) {
						this.toFocus = true;
					} else {
						this.toFocus = false;
					}
				});
				
				nAHrPButt = new Button("+");
				nAHrPButt.setOnAction(press -> nAHrIntPlus());
				nAHrMButt = new Button("-");
				nAHrMButt.setOnAction(press -> nAHrIntMinus());
				nAMnPButt = new Button("+");
				nAMnPButt.setOnAction(press -> nAMnIntPlus());
				nAMnMButt = new Button("-");
				nAMnMButt.setOnAction(press -> nAMnIntMinus());
				
				nAAudioButt = new Button("SELECT AUDIO FILE...");
				nAAudioButt.setOnAction(press -> {
					this.nAAudioFileChooser = new FileChooser();
					this.nAAudioFileChooser.setTitle("AUDIO FILE SELECTION...");
					this.nAAudioFile = nAAudioFileChooser.showOpenDialog(this.getScene().getWindow());
					if(this.nAAudioFile != null) {
						nAFilePath = nAAudioFile.toURI().toString();
					} else {
						nAFilePath = new String(tLib.getDFAAudioPath());
					}
				});
				
				nACancelButt = new Button("CANCEL");
				nACancelButt.setOnAction(press -> {
					exitStage(this.nAStage);
				});
				
				nACreateButt = new Button("CREATE");
				nACreateButt.setOnAction(press -> {
					if(getNALocalDate() != null && getNALocalTime() != null && getNAMsgString() != null && getNAFilePath() != null && getNAReFreq() != null && getNAReFreq() != ReFreq.UNSET) {
						aGen(getNALocalDate(), getNALocalTime(), getNAMsgString(), getNAFilePath(), this.toMute, this.toFocus, getNAReFreq());
						exitStage(this.nAStage);
					} else {
						Alert nAErrAlert = new Alert(AlertType.WARNING);
						//The following string will be changed later on to be more concise for the end user...
						String nAErrString = new String("One of the nessasary parts\nneeded to create a new Alarm\nhas been left completely blank\nand therefore in a null state!!!\n\n\nPlease review the Alarm Creation Wizard\nFill in blank node\nOR cancel to close the Wizard!!!\n\n\n***NOTE: The Text Field for the Alarm Msg is optional...\n***NOTE: The Audio and Focus CheckBoxs can be checked or empty...\n\n\n\n\n~(For more help see FAQ from \"Help\" OR \"https://www.Temporalment.com/help/faq\")~");
						nAErrAlert.setContentText(nAErrString);
						nAErrAlert.showAndWait();
					}
					
				});
				
				TLog.log.info("Complex New Alarm Nodes Loaded...");
				
				calcNALocalTime();
				refreshNATText();
				
				TLog.log.info("New Alarm Processed...");
				
				TLog.log.info("Building New Alarm UX/UI...");
				
				nAButtBox.getChildren().addAll(nACancelButt, nACreateButt);
				nALBox.getChildren().addAll();
				nARStack.getChildren().addAll(nARText, nARCombo);
				nADayBox.getChildren().addAll(nADayText);
				nADBox.getChildren().addAll(nADText);
				nATBox.getChildren().addAll(nATText);
				nAStatStack.getChildren().addAll(nAMsgText, nATBox, nADBox, nADayBox, nARStack, nALBox, nAButtBox);
				nAFocusBox.getChildren().addAll(nAFocusCBox, nAFocusText);
				nAAudioBox.getChildren().addAll(nAAudioCBox, nAAudioText, nAAudioButt);
				nADPickBox.getChildren().addAll(nADPicker);
				nATMStack.getChildren().addAll(nAMnTopText, nAMnPButt, nAMnText, nAMnMButt);
				nATHStack.getChildren().addAll(nAHrTopText, nAHrPButt, nAHrText, nAHrMButt);
				nATPickBox.getChildren().addAll(nATHStack, nASepText, nATMStack);
				nAMsgBox.getChildren().addAll(nAMsgr);
				nASetStack.getChildren().addAll(nAMsgBox, nATPickBox, nADPickBox, nAAudioBox, nAFocusBox);
				nABox.getChildren().addAll(nASetStack, nAStatStack);
				
				nAButtBox.setPadding(new Insets(3));
				nARStack.setPadding(new Insets(3));
				nADayBox.setPadding(new Insets(5.0));
				nADBox.setPadding(new Insets(3.0));
				nATBox.setPadding(new Insets(3.0));
				nAStatStack.setPadding(new Insets(1.0));
				nAFocusBox.setPadding(new Insets(5.0));
				nAAudioBox.setPadding(new Insets(5.0));
				nADPickBox.setPadding(new Insets(5.0));
				nATMStack.setPadding(new Insets(3.0));
				nATHStack.setPadding(new Insets(3.0));
				nATPickBox.setPadding(new Insets(5.0));
				nAMsgBox.setPadding(new Insets(3.0));
				nASetStack.setPadding(new Insets(1.0));
				nABox.setPadding(new Insets(3.0));
				this.setSpacing(10.0);
				this.setPadding(new Insets(10.0));
				
				nAScene = new Scene(nABox);
				nAStage = new Stage();
				
				nAStage.setTitle("New Alarm");
				nAStage.initModality(Modality.NONE);
				
				nAStage.setScene(nAScene);
				
				TLog.log.info("New Alarm UX/UI Built, Displaying New Alarm! ...");
				
				nAStage.show();
				
			}
			
		}
		
		public class SAVw extends VBox {
			
			private HBox sABox;
			private HBox sARBox;
			private HBox sAABox;
			private HBox sAFBox;
			private HBox sAIBox;
			private HBox sAEdBox;
			private HBox sAExBox;
			private HBox sARmBox;
			private HBox sAIDBox;
			private VBox sAStack;
			private VBox sACStack;
			private VBox sATStack;
			private VBox sASStack;
			
			private CheckBox sAAudioCBox;
			private CheckBox sAFocusCBox;
			private Button sAEditButt;
			private Button sANozzButt;
			private Button sAExitButt;
			private Button sAIdntButt;
			private Button sARemoButt;
			
			private String sAMsgString;
			private String sATString;
			private String sADString;
			private String sARString;
			private String sAIDString;
			private Text sAMsgText;
			private Text sATText;
			private Text sADText;
			private Text sARText;
			private Text sAAudioText;
			private Text sAAText;
			private Text sAFocusText;
			private Text sAFText;
			private Text sAIDText;
			
			public SAVw(Alarm inputAlarm) {
				
				if(inputAlarm != null) {

					sABox = new HBox(10.0);
					sARBox = new HBox();
					sAABox = new HBox();
					sAFBox = new HBox();
					sAIBox = new HBox();
					sAEdBox = new HBox();
					sAExBox = new HBox();
					sARmBox = new HBox();
					sAIDBox = new HBox();
					sAStack = new VBox();
					sACStack = new VBox();
					sATStack = new VBox();
					sASStack = new VBox();
					
					sAAudioCBox = new CheckBox();
					sAFocusCBox = new CheckBox();
					sAEditButt = new Button("🖉");
					sAEditButt.setStyle("-fx-font-size:10px;");
					sANozzButt = new Button("⏳");
					sANozzButt.setStyle("-fx-font-size:10px;");
					sAExitButt = new Button("🆗");
					sAExitButt.setStyle("-fx-font-size:10px;");
					sAIdntButt = new Button("I");
					sAIdntButt.setStyle("-fx-font-size:10px;");
					sARemoButt = new Button("R");
					sARemoButt.setStyle("-fx-font-size:10px;");
					
					sAMsgString = new String();
					sATString = new String();
					sADString = new String();
					sARString = new String();
					sAIDString = new String();
					sAMsgText = new Text();
					sATText = new Text();
					sADText = new Text();
					sARText = new Text();
					sAAudioText = new Text();
					sAAText = new Text();
					sAFocusText = new Text();
					sAFText = new Text();
					sAIDText = new Text();
					
					sAIDBox.getChildren().addAll(sAIDText);
					sARBox.getChildren().addAll(sARText, sAAText, sAFText);
					sAABox.getChildren().addAll(sAAudioCBox, sAAudioText);
					sAFBox.getChildren().addAll(sAFocusCBox, sAFocusText);
					sAIBox.getChildren().addAll(sAIdntButt);
					sAEdBox.getChildren().addAll(sAEditButt);
					sAExBox.getChildren().addAll(sAExitButt);
					sARmBox.getChildren().addAll(sARemoButt);
					sATStack.getChildren().addAll(sAABox, sAFBox, sAIBox);
					sACStack.getChildren().addAll(sAEdBox, sAExBox, sARmBox);
					sASStack.getChildren().addAll(sAMsgText, sATText, sADText, sARBox);
					sABox.getChildren().addAll(sATStack, sASStack, sACStack);
					sAStack.getChildren().addAll(sABox, sAIDBox);
					
					
					
					sAStack.setAlignment(Pos.CENTER);
					sABox.setAlignment(Pos.TOP_CENTER);
					sAIDBox.setAlignment(Pos.BASELINE_CENTER);
					sACStack.setAlignment(Pos.CENTER_RIGHT);
					sATStack.setAlignment(Pos.BASELINE_LEFT);
					
					if(inputAlarm.isMuted()) {
						sAAText.setText("🔇");
						sAAText.setStyle("-fx-font-size:8px;");
						sAAudioCBox.setSelected(false);
						sAAudioText.setText("🔈");
					} else if(!inputAlarm.isMuted()) {
						sAAText.setText("🔊");
						sAAText.setStyle("-fx-font-size:8px;");
						sAAudioCBox.setSelected(true);
						sAAudioText.setText("🔊");
					}
					
					if(inputAlarm.isFocused()) {
						sAFText.setText("🗗");
						sAFText.setStyle("-fx-font-size:8px;");
						sAFocusCBox.setSelected(true);
						sAFocusText.setText("🗗");
					} else if(!inputAlarm.isFocused()) {
						sAFText.setText("🗕");
						sAFText.setStyle("-fx-font-size:8px;");
						sAFocusCBox.setSelected(false);
						sAFocusText.setText("🗔");
					}
					
					sAMsgString = inputAlarm.getAMsgString();
					sAMsgText.setText(sAMsgString);
					sAMsgText.setStyle("-fx-font-size:11px;");
					sATString = inputAlarm.getATimeString();
					sATText.setText(sATString);
					sATText.setStyle("-fx-font-size:11px;");
					sADString = inputAlarm.getADateString();
					sADText.setText(sADString);
					sADText.setStyle("-fx-font-size:10px;");
					sARString = inputAlarm.getAReFreqString();
					sARText.setText(sARString);
					sARText.setStyle("-fx-font-size:8px;");
					sAIDString = inputAlarm.getAIdString();
					sAIDText.setText("🆔||: " + sAIDString);
					sAIDText.setStyle("-fx-font-size:7px;");
					sAIDText.setVisible(false);
					
					sAIdntButt.setOnAction(press -> {
						sAIdToggle();
					});
					
					sAAudioCBox.setOnAction(selected -> {
						if(sAAudioCBox.isSelected()) {
							inputAlarm.setIsMuted(false);
							sAAText.setText("🔊");
							sAAText.setStyle("-fx-font-size:8px;");
							sAAudioText.setText("🔊");
							sAAudioText.setStyle("-fx-font-size:10px;");
						} else {
							inputAlarm.setIsMuted(true);
							sAAText.setText("🔇");
							sAAText.setStyle("-fx-font-size:8px;");
							sAAudioText.setText("🔈");
							sAAudioText.setStyle("-fx-font-size:10px;");
						}
					});
					
					sAFocusCBox.setOnAction(selected -> {
						if(sAFocusCBox.isSelected()) {
							inputAlarm.setIsFocused(true);
							sAFText.setText("🗗");
							sAFText.setStyle("-fx-font-size:8px;");
							sAFocusText.setText("🗗");
							sAFocusText.setStyle("-fx-font-size:11px;");
						} else {
							inputAlarm.setIsFocused(false);
							sAFText.setText("🗕");
							sAFText.setStyle("-fx-font-size:8px;");
							sAFocusText.setText("🗔");
							sAFocusText.setStyle("-fx-font-size:11px;");
						}
					});
					
					sAEditButt.setOnAction(press -> aEdit(inputAlarm));
					
					sANozzButt.setOnAction(press -> aSusp(inputAlarm));
					
					sAExitButt.setOnAction(press -> aExit(inputAlarm));
					
					sARemoButt.setOnAction(press -> aRemo(inputAlarm));
					
					if(!inputAlarm.isActivated()) {
						sAEditButt.setVisible(true);
						sAEditButt.setDisable(false);
						sANozzButt.setVisible(false);
						sANozzButt.setDisable(true);
						
					} else {
						sAEditButt.setVisible(false);
						sAEditButt.setDisable(true);
						sANozzButt.setVisible(true);
						sANozzButt.setDisable(false);
						
					}
					
					this.sASetVisable(true);
			
				} else {
					
					sAAudioCBox = new CheckBox();
					sAFocusCBox = new CheckBox();
					sAEditButt = new Button("🖉");
					sAEditButt.setStyle("-fx-font-size:10px;");
					sANozzButt = new Button("⏳");
					sANozzButt.setStyle("-fx-font-size:10px;");
					sAExitButt = new Button("🆗");
					sAExitButt.setStyle("-fx-font-size:10px;");
					sAIdntButt = new Button("I");
					sAIdntButt.setStyle("-fx-font-size:10px;");
					
					sAMsgString = new String();
					sATString = new String();
					sADString = new String();
					sARString = new String();
					sAIDString = new String();
					sAMsgText = new Text();
					sATText = new Text();
					sADText = new Text();
					sARText = new Text();
					sAAudioText = new Text();
					sAAText = new Text();
					sAFocusText = new Text();
					sAFText = new Text();
					sAIDText = new Text();
					
					sAStack = new VBox();
					
					this.sASetVisable(false);
				}
					
			}
			
			public void sASetVisable(boolean inputValue) {
				if(inputValue) {
					sAAText.setVisible(true);
					sAAudioCBox.setVisible(true);
					sAAudioText.setVisible(true);
					sAFText.setVisible(true);
					sAFocusCBox.setVisible(true);
					sAFocusText.setVisible(true);
					sAEditButt.setVisible(true);
					sANozzButt.setVisible(true);
					sAExitButt.setVisible(true);
					sAMsgText.setVisible(true);
					sATText.setVisible(true);
					sADText.setVisible(true);
					sARText.setVisible(true);
					sAIDText.setVisible(true);
				} else {
					sAAText.setVisible(false);
					sAAudioCBox.setVisible(false);
					sAAudioText.setVisible(false);
					sAFText.setVisible(false);
					sAFocusCBox.setVisible(false);
					sAFocusText.setVisible(false);
					sAEditButt.setVisible(false);
					sANozzButt.setVisible(false);
					sAExitButt.setVisible(false);
					sAMsgText.setVisible(false);
					sATText.setVisible(false);
					sADText.setVisible(false);
					sARText.setVisible(false);
					sAIDText.setVisible(false);
				}
			}
			
			public void sAIdToggle() {
				if(sAIDBox.isVisible()) {
					sAIDBox.setVisible(false);
				} else {
					sAIDBox.setVisible(true);
				}
			}
			
			public void sAExitVw() {
				sAExitButt.setVisible(true);
				sAExitButt.setDisable(false);
				sARemoButt.setVisible(false);
				sARemoButt.setDisable(true);
			}
			
			public void sAPendVw() {
				sAExitButt.setVisible(false);
				sAExitButt.setDisable(true);
				sARemoButt.setVisible(true);
				sARemoButt.setDisable(false);
			}
			
			public VBox getSAStack() {
				return this.sAStack;
			}
			
		}
		
		public class SAListCell extends ListCell<Alarm> {
			
			private SAVw sAVw;
			
			public SAListCell() {
				
			}
			
			@Override
			protected void updateItem(Alarm sAlarm, boolean empty) {
				super.updateItem(sAlarm, empty);
				
				sAVw = new SAVw(sAlarm);
				
				if(empty || sAlarm == null) {
					sAVw.sASetVisable(false);
					setGraphic(null);
				} else {
					sAVw.sASetVisable(true);
					
					if(sAlarm.isActivated()) {
						sAVw.sAExitVw();
					} else {
						sAVw.sAPendVw();
					}
					
					setGraphic(sAVw.getSAStack());
				}
			}
			
		}
		
	}

		
		
	public class TimerVw extends VBox {
		
		
		
		
		public class CUVw extends VBox {
			
		}
		
		public class SCUVw extends VBox {
			
		}
		
		public class NCDVw extends VBox {
			
		}
		
		public class SCDAListCell extends ListCell<CntDn> {
			
		}
		
		public class SCDIListCell extends ListCell<CntDn> {
			
		}
		
		public class SCUListCell extends ListCell<CntUp> {
			
		}
		
	}

}
