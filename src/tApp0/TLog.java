package tApp0;

import java.io.IOException;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class TLog {
	
public static final Logger log = Logger.getLogger(TLog.class.getName());
	
	public static void log() {
		
		Logger wLog = Logger.getLogger("");
		Handler[] logHandlers = wLog.getHandlers();
		if(logHandlers[0] instanceof ConsoleHandler) {
			wLog.removeHandler(logHandlers[0]);
		}
		
		ConsoleHandler cHandler = new ConsoleHandler();
		cHandler.setLevel(Level.WARNING);
		cHandler.setFormatter(new SimpleFormatter());
		log.addHandler(cHandler);
		
		try {
			FileHandler fHandler = new FileHandler("tApp0.log", true);
			fHandler.setLevel(Level.ALL);
			fHandler.setFormatter(new SimpleFormatter());
			log.addHandler(fHandler);
		} catch (IOException logException) {
			log.log(Level.SEVERE, "[FATAL_CRASH]", logException);
			log.log(Level.INFO, logException.toString(), logException);
		}
		
		
	}

}
