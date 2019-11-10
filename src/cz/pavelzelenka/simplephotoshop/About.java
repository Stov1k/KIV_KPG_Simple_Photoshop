package cz.pavelzelenka.simplephotoshop;
import javafx.application.Application;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;

/**
 * O aplikaci
 * @author Pavel Zelenka
 */

public class About {

	/** Titulek okna */
	public static final String TITLE = "About Simple Photoshop";

	/** Aplikace */
	private Application application;
	
	private TabPane tabPane = new TabPane();
	private Parent parent;
	
	public About() {
		this.parent = initialize();
	}
	
	/** Vrati instanci tridy Parrent */
	public Parent initialize() {
		BorderPane borderPane = new BorderPane();
		borderPane.setTop(getTopPane());
		borderPane.setCenter(getCenterPane());
		addAuthorTab();
		return borderPane;
	}

	/** Vrati horni panel */
	private Node getTopPane() {
		AnchorPane anchorPane = new AnchorPane();
		anchorPane.setStyle("-fx-background-color: #00A6CC;");
		HBox hbox = new HBox();
		ImageView logo = new ImageView(IconType.APPLICATION_128.get());
		TextFlow title = getTitleTextFlow();
		hbox.getChildren().addAll(title, logo);
		anchorPane.getChildren().add(hbox);
		HBox.setHgrow(title, Priority.ALWAYS);
		HBox.setHgrow(logo, Priority.NEVER);
		AnchorPane.setTopAnchor(hbox, 5D);
		AnchorPane.setLeftAnchor(hbox, 5D);
		AnchorPane.setRightAnchor(hbox, 5D);
		AnchorPane.setBottomAnchor(hbox, 5D);
		return anchorPane;
	}
	
	/** Vrati stredni panel */
	private Node getCenterPane() {
		AnchorPane anchorPane = new AnchorPane();
		anchorPane.getChildren().add(tabPane);
		AnchorPane.setTopAnchor(tabPane, 0D);
		AnchorPane.setLeftAnchor(tabPane, 0D);
		AnchorPane.setRightAnchor(tabPane, 0D);
		AnchorPane.setBottomAnchor(tabPane, 0D);
		return anchorPane;
	}
	
	/** Prida zalozku autora */
	private void addAuthorTab() {
		Tab tab = new Tab("Author");
		tab.setClosable(false);
		AnchorPane anchorPane = new AnchorPane();
		VBox vbox = new VBox();
		vbox.getChildren().add(getAuthotTextFlow());
		anchorPane.getChildren().add(vbox);
		AnchorPane.setTopAnchor(vbox, 10D);
		AnchorPane.setLeftAnchor(vbox, 10D);
		AnchorPane.setRightAnchor(vbox, 10D);
		AnchorPane.setBottomAnchor(vbox, 10D);
		tab.setContent(anchorPane);
		tabPane.getTabs().add(tab);
	}
	
	/** Vrati formatovany text autora */
	private TextFlow getAuthotTextFlow() {
		TextFlow textFlow = new TextFlow();
		Text i = new Text("Pavel Zelenka");
		i.setStyle("-fx-font-weight: bold;");
		
		Text descriptionText = new Text("\nA16B0176P");
		descriptionText.setStyle("-fx-font-style: italic");
		
		Text mailText = new Text("\nE-mail:");
		mailText.setStyle("-fx-font-weight: normal");
		
		Text wwwText = new Text("\nWWW:");
		wwwText.setStyle("-fx-font-weight: normal");
		
		Hyperlink mail = new Hyperlink();
		mail.setText("pavel.zelenka@gmail.com");
		mail.setOnAction(event -> {
			application.getHostServices().showDocument("mailto:pavel.zelenka@gmail.com");
		});

		Hyperlink www = new Hyperlink();
		www.setText("http://www.pavelzelenka.cz/");
		www.setOnAction(event -> {
			application.getHostServices().showDocument("http://www.pavelzelenka.cz/");
		});
		
		textFlow.getChildren().addAll(i, descriptionText, mailText, mail, wwwText, www);
		textFlow.setTextAlignment(TextAlignment.LEFT);
		return textFlow;
	}
	
	/** Vrati formatovany text hlavicky */
	private TextFlow getTitleTextFlow() {
		TextFlow textFlow = new TextFlow();
		Text title = new Text("About\n");
		title.setFont(Font.font ("Verdana", FontWeight.BOLD, 42));
		Text appName = new Text("Simple Photoshop\n");
		appName.setFont(Font.font ("Verdana", FontWeight.BOLD, 32));
		Text subject = new Text("KIV/KPG\n");
		subject.setFont(Font.font ("Verdana", FontWeight.BOLD, 16));
		textFlow.getChildren().addAll(title, appName, subject);
		textFlow.setTextAlignment(TextAlignment.CENTER);
		return textFlow;
	}

	public Parent getParent() {
		return parent;
	}

	public Application getApplication() {
		return application;
	}

	public void setApplication(Application application) {
		this.application = application;
	}
	
}
