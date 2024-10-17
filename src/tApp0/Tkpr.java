package tApp0;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import javafx.util.Duration;

public class Tkpr {

	//Tkpr == TimeKeeper
	
	//TLDR; This is the TimeKeeper Class >.>;...           lmao :P
	
	public static LocalDateTime tNow;
	public static LocalDate tDate;
	public static LocalTime tTime;
	
	public static Duration tNano;
	public static Duration tMilli;
	public static Duration tTic;
	public static Duration tToc;
	
	public static boolean tSplit = true;
	
	public static DateTimeFormatter tYForm = DateTimeFormatter.ofPattern("YYYY"); //Week-Based-Year #
	public static DateTimeFormatter tdForm = DateTimeFormatter.ofPattern("dd"); //Day-Of-Month #
	public static DateTimeFormatter tDForm = DateTimeFormatter.ofPattern("DDD"); //Day-Of-Year #
	public static DateTimeFormatter tmForm = DateTimeFormatter.ofPattern("mm"); //Minute-Of-Hour #
	public static DateTimeFormatter tMForm = DateTimeFormatter.ofPattern("MM"); //Month-Of-Year #
	public static DateTimeFormatter tLForm = DateTimeFormatter.ofPattern("LLLL"); //Month-Of-Year Txt
	public static DateTimeFormatter tQForm = DateTimeFormatter.ofPattern("Q"); //Quarter-Of-Year #
	public static DateTimeFormatter tEForm = DateTimeFormatter.ofPattern("EEEE"); //Day-Of-Week Txt
	public static DateTimeFormatter taForm = DateTimeFormatter.ofPattern("a"); //Am-Pm-Of-Day Txt
	public static DateTimeFormatter tBForm = DateTimeFormatter.ofPattern("B"); //Period-Of-Day Txt
	public static DateTimeFormatter thForm = DateTimeFormatter.ofPattern("hh"); //Hour-Of-Day # (12 Hour Formatting)
	public static DateTimeFormatter tHForm = DateTimeFormatter.ofPattern("HH"); //Hour-Of-Day # (24 Hour Formatting)
	public static DateTimeFormatter tsForm = DateTimeFormatter.ofPattern("ss"); //Second-Of-Minute #
	public static DateTimeFormatter tSForm = DateTimeFormatter.ofPattern("SSS"); //Fraction-Of-Second #
	public static DateTimeFormatter tnForm = DateTimeFormatter.ofPattern("nnnnn"); //Nano-Second-Of-Second #
	public static DateTimeFormatter tzForm = DateTimeFormatter.ofPattern("zzz"); //Time-Zone-Name Txt
	
	public static DateTimeFormatter tIDForm = DateTimeFormatter.ofPattern("MMddYYYYDDDHHmmssSSSnnnnn");
	
	public static LocalDateTime getTNow() {
		tNow = LocalDateTime.now();
		return tNow;
	}
	
	public static Duration getTNano() {
		tNano = Duration.millis(0.000001);
		return tNano;
	}
	
	public static Duration getTMilli() {
		tMilli = Duration.millis(1.0);
		return tMilli;
	}
	
	public static Duration getTTic() {
		tTic = Duration.millis(333.39);
		return tTic;
	}
	
	public static Duration getTToc() {
		tToc = Duration.seconds(1.0);
		return tToc;
	}
	
	public static boolean getTSplit() {
		return tSplit;
	}
	
	public static void setTSplit(boolean inputValue) {
		tSplit = inputValue;
	}
	
	public static DateTimeFormatter getTYForm() {
		return tYForm;
	}
	
	public static DateTimeFormatter getTdForm() {
		return tdForm;
	}
	
	public static DateTimeFormatter getTDForm() {
		return tDForm;
	}
	
	public static DateTimeFormatter getTmForm() {
		return tmForm;
	}
	
	public static DateTimeFormatter getTMForm() {
		return tMForm;
	}
	
	public static DateTimeFormatter getTLForm() {
		return tLForm;
	}
	
	public static DateTimeFormatter getTQForm() {
		return tQForm;
	}
	
	public static DateTimeFormatter getTEForm() {
		return tEForm;
	}
	
	public static DateTimeFormatter getTaForm() {
		return taForm;
	}
	
	public static DateTimeFormatter getTBForm() {
		return tBForm;
	}
	
	public static DateTimeFormatter getThForm() {
		return thForm;
	}
	
	public static DateTimeFormatter getTHForm() {
		return tHForm;
	}
	
	public static DateTimeFormatter getTsForm() {
		return tsForm;
	}
	
	public static DateTimeFormatter getTSForm() {
		return tSForm;
	}
	
	public static DateTimeFormatter getTnForm() {
		return tnForm;
	}
	
	public static DateTimeFormatter getTzForm() {
		return tzForm;
	}
	
	public static DateTimeFormatter getTIDForm() {
		return tIDForm;
	}

}
