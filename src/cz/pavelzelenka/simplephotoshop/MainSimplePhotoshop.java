package cz.pavelzelenka.simplephotoshop;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * Hlavni trida aplikace
 * @author Pavel Zelenka A16B0176P
 * @version 2018-03-24
 */
public class MainSimplePhotoshop extends Application {

	/** Popisek okna aplikace */
	public static final String WINDOW_TITLE = "Simple Photoshop";
	/** Minimalni sirka okna aplikace */
	public static final double MIN_WINDOW_WIDTH = 400D;
	/** Minimalni vyska okna aplikace */
	public static final double MIN_WINDOW_HEIGHT = 620D;
	
	/** Scena */
	public static Scene scene;
	
	/** Stage */
	protected Stage primaryStage;
	
	/** Vychozi rozvrzeni okna */
	private WindowLayout wl;
	
	/** 
	 * Hlavni metoda aplikace
	 * @param args argumenty pri spusteni
	 */
	public static void main(String[] args) {
		launch(args);
	}
	
	/**
	 * Spusteni GUI aplikace
	 */
	@Override
	public void start(Stage primaryStage) throws Exception {
		primaryStage.setTitle(WINDOW_TITLE);
		primaryStage.setMinWidth(MIN_WINDOW_WIDTH);
		primaryStage.setMinHeight(MIN_WINDOW_HEIGHT);
		primaryStage.getIcons().add(IconType.APPLICATION_128.get());
		wl = new WindowLayout(primaryStage);
		wl.setApplication(this);
		scene = new Scene(wl.get());
		primaryStage.setScene(scene);
		primaryStage.setWidth(800D);
		primaryStage.setHeight(640D);
		primaryStage.show();
	}
	
}
