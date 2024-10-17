module tApp_Alpha {
	requires javafx.controls;
	requires javafx.graphics;
	requires java.logging;
	requires javafx.media;
	requires javafx.base;
	
	opens tApp0 to javafx.graphics, javafx.fxml, javafx.controls, javafx.media;
}
