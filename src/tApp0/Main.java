package tApp0;
	
import javafx.application.Application;
import javafx.stage.Stage;


public class Main extends Application {
	
	//public TApp tApp;
	
	@Override
	public void start(Stage mainStage) {
		
		try {
			
			TApp tApp = new TApp();
			mainStage = tApp.getDispStage();
			mainStage.show();
			
		} catch(Exception startion) {
			startion.printStackTrace();
		}
		
	}
	
	public static void main(String[] args) {
		TLog.log();
		TLog.log.config("Application Launched");
		launch(args);
	}
}
