package cz.pavelzelenka.simplephotoshop;

import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;

import javax.imageio.ImageIO;

import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.Slider;
import javafx.scene.control.Spinner;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 * Rozvrzeni okna aplikace
 * @author Pavel Zelenka A16B0176P
 * @version 2018-03-24
 */
public class WindowLayout {

	/** Hlavni stage aplikace */
	private final Stage stage;
	
	/** Zakladni rozvrzeni okna */
	private BorderPane borderPane;

	/** Kresba */
	private Drawing drawing;
	
	/** Panel operaci */
	private VBox operationsPane = new VBox();
	
	/** Panel moznosti operace */
	private VBox operationBox = new VBox();
	
	/** Skupina prepinacu operaci */
	private ToggleGroup operationsGroup = new ToggleGroup();
	
	/** Operace prevodu na cernobily obraz */
	private ToggleButton bwTB = new ToggleButton("Black & White");
	
	/** Operace prevodu na tepany obraz */
	private ToggleButton embossTB = new ToggleButton("Emboss");
	
	/** Operace vytvoreni mozaiky */
	private ToggleButton mosaicTB = new ToggleButton("Mosaic");
	
	/** Operace vytvoreni negativu */
	private ToggleButton negativeTB = new ToggleButton("Negative");
	
	/** Operace vytvoreni sepie */
	private ToggleButton sepiaTB = new ToggleButton("Sepia");
	
	/** Operace rozmazani */
	private ToggleButton blurTB = new ToggleButton("Blur");
	
	/** Cas operace */
	private Label timeLabel = new Label("");
	
	/**
	 * Konstruktor
	 * @param stage (hlavni) stage aplikace
	 */
	public WindowLayout(Stage stage) {
		this.stage = stage;
	}
	
	/**
	 * Vrati zakladni rozvrzeni okna aplikace
	 * @return zakladni rozvrzeni okna aplikace
	 */
	public Parent get() {
		borderPane = new BorderPane();
		borderPane.setTop(getMenuBar());
		borderPane.setCenter(getCanvasPane());
		borderPane.setBottom(getBottomPane());
		borderPane.setRight(getRightPane());
		return borderPane;
	}
	
	/**
	 * Vrati horni menu
	 * @return horni menu
	 */
	public Parent getMenuBar() {
		MenuBar menuBar = new MenuBar();
		Menu fileMenu = new Menu("File");
		MenuItem openMI = new MenuItem("Open");
		openMI.setOnAction(action -> handleOpen());
		MenuItem saveAsMI = new MenuItem("Save As...");
		saveAsMI.setOnAction(action -> handleSaveAs());
		MenuItem closeMI = new MenuItem("Close");
		closeMI.setOnAction(action -> handleClose());
		fileMenu.getItems().addAll(openMI, saveAsMI, new SeparatorMenuItem(), closeMI);
		menuBar.getMenus().add(fileMenu);
		return menuBar;
	}
	
	/**
	 * Vrati pravy panel
	 * @return pravy panel
	 */
	public Parent getRightPane() {
		VBox vBox = new VBox();
		vBox.setAlignment(Pos.TOP_CENTER);
		vBox.setPadding(new Insets(5D,5D,5D,5D));
		vBox.setSpacing(5D);
		vBox.setPrefWidth(180D);
		
		operationBox.setAlignment(Pos.CENTER);
		operationBox.setSpacing(5D);
		operationBox.setPadding(new Insets(5D,5D,5D,5D));
		
		blurTB.setTooltip(new Tooltip("Blur"));
		blurTB.setToggleGroup(operationsGroup);
		blurTB.setMaxWidth(200);
		
		bwTB.setTooltip(new Tooltip("Black and White"));
		bwTB.setToggleGroup(operationsGroup);
		bwTB.setMaxWidth(200);
		
		embossTB.setTooltip(new Tooltip("Emboss"));
		embossTB.setToggleGroup(operationsGroup);
		embossTB.setMaxWidth(200);
		
		mosaicTB.setTooltip(new Tooltip("Mosaic"));
		mosaicTB.setToggleGroup(operationsGroup);
		mosaicTB.setMaxWidth(200);
		
		negativeTB.setTooltip(new Tooltip("Negative"));
		negativeTB.setToggleGroup(operationsGroup);
		negativeTB.setMaxWidth(200);
		
		sepiaTB.setTooltip(new Tooltip("Sepia"));
		sepiaTB.setToggleGroup(operationsGroup);
		sepiaTB.setMaxWidth(200);
		
		operationsPane.getChildren().addAll(blurTB, bwTB, embossTB, mosaicTB, negativeTB, sepiaTB);
		operationsPane.setSpacing(5D);
		operationsPane.setPadding(new Insets(5D,5D,5D,5D));
		operationsPane.setAlignment(Pos.TOP_CENTER);
		operationsPane.setStyle("-fx-base: #9BE8BC; -fx-background-color: #D1F5E0; -fx-border-color: #9BE8BC;");
		
		vBox.getChildren().addAll(operationsPane, operationBox, getUndoPane());
		observeOperationsGroup();
		return vBox;
	}
	
	/**
	 * Sledovani zvolene operace
	 */
	public void observeOperationsGroup() {
		operationsGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
			if(newValue != null) {
				operationBox.setStyle("-fx-base: #AAB8ED; -fx-background-color: #DAE8F0; -fx-border-color: #AAB8ED;");
				if(newValue.equals(blurTB)) {
					blurEffectOptions();
				} else if(newValue.equals(bwTB)) {
						blackWhiteEffectOptions();
				} else if(newValue.equals(embossTB)) {
					embossEffectOptions();
				} else if(newValue.equals(mosaicTB)) {
					mosaicEffectOptions();
				} else if(newValue.equals(negativeTB)) {
					negativeEffectOptions();
				} else if(newValue.equals(sepiaTB)) {
					sepiaEffectOptions();
				} else {
					operationBox.setStyle(null);
					operationBox.getChildren().clear();
				}
			} else {
				operationBox.setStyle(null);
				operationBox.getChildren().clear();
			}
		});
	}
	
	/**
	 * Vrati panel s tlacitkem zpet
	 * @return panel s tlacitkem zpet
	 */
	public Parent getUndoPane() {
		VBox vBox = new VBox();
		vBox.setStyle("-fx-base:  #F8C291; -fx-background-color: #FAE8C1; -fx-border-color: #F8C291;");
		vBox.setPadding(new Insets(5D,5D,5D,5D));
		vBox.setSpacing(5D);
		vBox.setAlignment(Pos.CENTER);
		Button undo = new Button("Undo");
		undo.setDisable(true);
		undo.setMaxWidth(200);
		drawing.undoEnableProperty().addListener((observable, oldValue, newValue) -> {
			if(newValue.booleanValue()) {
				undo.setDisable(false);
			} else {
				undo.setDisable(true);
			}
		});
		undo.setOnAction(action -> {
			drawing.undo();
		});
		Button reset = new Button("Reset");
		reset.setMaxWidth(200);
		reset.setOnAction(action -> {
			drawing.reset();
		});
		vBox.getChildren().addAll(undo, reset);
		return vBox;
	}
	
	/**
	 * Obsluha efektu cernobily
	 */
	public void blackWhiteEffectOptions() {
		operationBox.getChildren().clear();
		Button apply = new Button("Apply");
		apply.setMaxWidth(200);
		apply.setOnAction(action -> {
			long time = drawing.blackAndWhiteEffect();
			timeLabel.setText("B&W Effect Time: " + toMs(time) + " ms");
		});

		operationBox.getChildren().addAll(apply);
	}
	
	/**
	 * Obsluha efektu tepani
	 */
	public void embossEffectOptions() {
		operationBox.getChildren().clear();
		Button apply = new Button("Apply");
		apply.setMaxWidth(200);
		apply.setOnAction(action -> {
			long time = drawing.embossEffect();
			timeLabel.setText("Emboss Effect Time: " + toMs(time) + " ms");
		});

		operationBox.getChildren().addAll(apply);
	}
	
	/**
	 * Obsluha efektu mazaika
	 */
	public void mosaicEffectOptions() {
		operationBox.getChildren().clear();
		Label precentLabel = new Label("Precent");
		Slider precent = new Slider(); 
		precent.setMin(0.01);
		precent.setMax(0.50);
		precent.setValue(0.05);
		precent.setShowTickLabels(false);
		precent.setShowTickMarks(false);
		precent.setMajorTickUnit(0.1);
		precent.setMinorTickCount(2);
		precent.setBlockIncrement(0.01);
		CheckBox doublePass = new CheckBox("Double Pass");
		doublePass.setMaxWidth(200);
		Button apply = new Button("Apply");
		apply.setMaxWidth(200);
		apply.setOnAction(action -> {
			long time = drawing.mosaicEffect(precent.getValue(), doublePass.isSelected());
			timeLabel.setText("Mosaic Effect Time: " + toMs(time) + " ms");
		});

		operationBox.getChildren().addAll(precentLabel, precent, doublePass, apply);
	}
	
	/**
	 * Obsluha efektu negativ
	 */
	public void negativeEffectOptions() {
		operationBox.getChildren().clear();
		Button apply = new Button("Apply");
		apply.setMaxWidth(200);
		apply.setOnAction(action -> {
			long time = drawing.negativeEffect();
			timeLabel.setText("Negative Effect Time: " + toMs(time) + " ms");
		});

		operationBox.getChildren().addAll(apply);
	}
	
	/**
	 * Obsluha efektu sepie
	 */
	public void sepiaEffectOptions() {
		operationBox.getChildren().clear();
		Label depthLabel = new Label("Depth");
		Slider depth = new Slider(); 
		depth.setMin(-255);
		depth.setMax(255);
		depth.setValue(25);
		depth.setShowTickLabels(false);
		depth.setShowTickMarks(false);
		depth.setMajorTickUnit(50);
		depth.setMinorTickCount(2);
		depth.setBlockIncrement(1);
		Label intensityLabel = new Label("Intensity");
		Slider intensity = new Slider(); 
		intensity.setMin(-255);
		intensity.setMax(255);
		intensity.setValue(25);
		intensity.setShowTickLabels(false);
		intensity.setShowTickMarks(false);
		intensity.setMajorTickUnit(50);
		intensity.setMinorTickCount(2);
		intensity.setBlockIncrement(1);
		Button apply = new Button("Apply");
		apply.setMaxWidth(200);
		apply.setOnAction(action -> {
			long time = drawing.sepiaEffect((int)depth.getValue(), (int)intensity.getValue());
			timeLabel.setText("Sepia Effect Time: " + toMs(time) + " ms");
		});

		operationBox.getChildren().addAll(depthLabel, depth, intensityLabel, intensity, apply);
	}
	
	/**
	 * Obsluha efektu rozmazani
	 */
	public void blurEffectOptions() {
		operationBox.getChildren().clear();
		Label weightLabel = new Label("Weight");
		GridPane weightGrid = new GridPane();
		weightGrid.setHgap(2D);
		weightGrid.setVgap(2D);
		weightGrid.setStyle("-fx-font-size: 11px;");
		Spinner<Integer> weight1 = new Spinner<Integer>(0, 255, 9);
		Spinner<Integer> weight2 = new Spinner<Integer>(0, 255, 9);
		Spinner<Integer> weight3 = new Spinner<Integer>(0, 255, 9);
		Spinner<Integer> weight4 = new Spinner<Integer>(0, 255, 9);
		Spinner<Integer> weight5 = new Spinner<Integer>(0, 255, 9);
		Spinner<Integer> weight6 = new Spinner<Integer>(0, 255, 9);
		Spinner<Integer> weight7 = new Spinner<Integer>(0, 255, 9);
		Spinner<Integer> weight8 = new Spinner<Integer>(0, 255, 9);
		Spinner<Integer> weight9 = new Spinner<Integer>(0, 255, 9);
		weightGrid.add(weight1, 0, 0);
		weightGrid.add(weight2, 1, 0);
		weightGrid.add(weight3, 2, 0);
		weightGrid.add(weight4, 0, 1);
		weightGrid.add(weight5, 1, 1);
		weightGrid.add(weight6, 2, 1);
		weightGrid.add(weight7, 0, 2);
		weightGrid.add(weight8, 1, 2);
		weightGrid.add(weight9, 2, 2);
		weight1.getStyleClass().add(Spinner.STYLE_CLASS_SPLIT_ARROWS_VERTICAL);
		weight2.getStyleClass().add(Spinner.STYLE_CLASS_SPLIT_ARROWS_VERTICAL);
		weight3.getStyleClass().add(Spinner.STYLE_CLASS_SPLIT_ARROWS_VERTICAL);
		weight4.getStyleClass().add(Spinner.STYLE_CLASS_SPLIT_ARROWS_VERTICAL);
		weight5.getStyleClass().add(Spinner.STYLE_CLASS_SPLIT_ARROWS_VERTICAL);
		weight6.getStyleClass().add(Spinner.STYLE_CLASS_SPLIT_ARROWS_VERTICAL);
		weight7.getStyleClass().add(Spinner.STYLE_CLASS_SPLIT_ARROWS_VERTICAL);
		weight8.getStyleClass().add(Spinner.STYLE_CLASS_SPLIT_ARROWS_VERTICAL);
		weight9.getStyleClass().add(Spinner.STYLE_CLASS_SPLIT_ARROWS_VERTICAL);
		Button apply = new Button("Apply");
		apply.setMaxWidth(200);
		apply.setOnAction(action -> {
			long time = drawing.blurEffect(new double[]{
					weight1.getValue(),weight4.getValue(),weight7.getValue(),
					weight2.getValue(),weight5.getValue(),weight8.getValue(),
					weight3.getValue(),weight6.getValue(),weight9.getValue()});
			timeLabel.setText("Blur Effect Time: " + toMs(time) + " ms");
		});

		operationBox.getChildren().addAll(weightLabel, weightGrid, apply);
	}
	
	/**
	 * Prevod nanosekund na milisekundy
	 * @param ns nanosekundy
	 * @return milisekundy
	 */
	public long toMs(long ns) {
		return ns / 1000000;
	}
	
	/**
	 * Vrati spodni panel
	 * @return spodni panel
	 */
	public Parent getBottomPane() {	
		HBox hBox = new HBox();
		hBox.setAlignment(Pos.CENTER_LEFT);
		hBox.setPadding(new Insets(5D, 5D, 5D, 5D));
		hBox.setSpacing(5D);
		Label resolutionLabel = new Label("Image resolution: " + (int) drawing.getImageWidth() + "×" + (int) drawing.getImageHeight());
		drawing.widthProperty().addListener((observable, oldValue, newValue) -> resolutionLabel.setText("Image resolution: " + (int) drawing.getImageWidth() + "×" + (int) drawing.getImageHeight()));
		drawing.heightProperty().addListener((observable, oldValue, newValue) -> resolutionLabel.setText("Image resolution: " + (int) drawing.getImageWidth() + "×" + (int) drawing.getImageHeight()));
		hBox.getChildren().addAll(resolutionLabel, timeLabel);
		return hBox;
	}
	
	/**
	 * Vrati panel kresby
	 * @return panel kresby
	 */
	public Parent getCanvasPane() {
		ScrollPane scrollPane = new ScrollPane();
		scrollPane.setStyle("-fx-font-size: 11px;");
		
		BorderPane pane = new BorderPane();
		
        Canvas canvas = new Canvas(pane.getWidth(), pane.getHeight());
        pane.setStyle("-fx-background-color: #FFFFFF;");
        
        drawing = new Drawing(canvas);
        drawing.draw();
         
        pane.getChildren().add(canvas);
        
        scrollPane.widthProperty().addListener(resize ->  {
        		double maxWidth = Math.max(drawing.getImageWidth(), scrollPane.getWidth()-14);
        		pane.setPrefWidth(maxWidth);
        	});
        
        scrollPane.heightProperty().addListener(resize -> {
        		double maxHeight = Math.max(drawing.getImageHeight(), scrollPane.getHeight()-14);
        		pane.setPrefHeight(maxHeight);
        	});
        
        scrollPane.setContent(pane);
        scrollPane.setHbarPolicy(ScrollBarPolicy.ALWAYS);
        scrollPane.setVbarPolicy(ScrollBarPolicy.ALWAYS);
        
		return scrollPane;
	}
	
	/** Ukonci aplikaci */
	private void handleClose() {
		Platform.exit();
	}
	
	/** Otevre FileChooser pro vyber obrazku */
    private void handleOpen() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Image");
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        
        // Nastaveni filtru pripony
        FileChooser.ExtensionFilter pngExtFilter = new FileChooser.ExtensionFilter("PNG file (.png)", "*.png");
        FileChooser.ExtensionFilter jpgExtFilter = new FileChooser.ExtensionFilter("JPG file (.jpg)", "*.jpg");
        FileChooser.ExtensionFilter bmpExtFilter = new FileChooser.ExtensionFilter("BMP file (.bmp)", "*.bmp");
        fileChooser.getExtensionFilters().addAll(pngExtFilter, jpgExtFilter, bmpExtFilter);

        // Zobrazeni oteviraciho dialogu
        File file = fileChooser.showOpenDialog(stage);

        if (file != null) {
        	try {
        		BufferedImage bufferedImage = ImageIO.read(file);
                Image image = SwingFXUtils.toFXImage(bufferedImage, null);
        		drawing.setImage(image);
        	} catch (Exception e) {
        		e.printStackTrace();
        	}
        }
        drawing.redraw();
    }
    
    /** Otevre FileChooser pro ulozeni obrazku */
    private void handleSaveAs() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Image");
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        fileChooser.setInitialFileName("my_image.png");
        
        // Nastaveni filtru pripony
        FileChooser.ExtensionFilter pngExtFilter = new FileChooser.ExtensionFilter("PNG file (.png)", "*.png");
        fileChooser.getExtensionFilters().add(pngExtFilter);

        // Zobrazeni ukladaciho dialogu
        File file = fileChooser.showSaveDialog(stage);

        if (file != null) {
        	try {
        		if (!file.getPath().endsWith(".png")) {
        			file = new File(file.getPath() + ".png");
        		}
        		RenderedImage renderedImage = SwingFXUtils.fromFXImage(drawing.getWorking(), null);
        		ImageIO.write(renderedImage, "png", file);
        	} catch(Exception e) {
        		e.printStackTrace();
        	}
        }
    }
	
}
