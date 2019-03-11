package cz.pavelzelenka.simplephotoshop;

import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;

import javax.imageio.ImageIO;

import cz.pavelzelenka.simplephotoshop.effects.KernelMatrix;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.control.Separator;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.ToolBar;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
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
	private ToolBar operationsPane = new ToolBar();
	
	/** Panel moznosti operace */
	private VBox operationBox = new VBox();
	
	/** Skupina prepinacu operaci */
	private ToggleGroup operationsGroup = new ToggleGroup();
	
	/** Operace vytvoreni mozaiky */
	private ToggleButton mosaicTB = new ToggleButton("Mosaic");
	
	/** Ruzne operace */
	private ToggleButton sandboxTB = new ToggleButton("Sandbox");
	
	/** Operace prevodu na YCbCr / RGB barevny model */ 
	private ToggleButton ycbcrTB = new ToggleButton("YCbCr");
	
	/** Operace pragovani */ 
	private ToggleButton thresholdingTB = new ToggleButton("Thresholding");
	
	/** Morfologicke operace */
	private ToggleButton morphologyTB = new ToggleButton("Morphology");
	
	/** Operace vytvoreni sepie */
	private ToggleButton sepiaTB = new ToggleButton("Sepia");
	
	/** Operace masky */
	private ToggleButton kernelTB = new ToggleButton("Kernel");
	
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
		openMI.setOnAction(action -> handleOpenPrimaryImg());
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
		
		kernelTB.setTooltip(new Tooltip("Kernel"));
		kernelTB.setToggleGroup(operationsGroup);
		kernelTB.setMaxWidth(200);
		
		mosaicTB.setTooltip(new Tooltip("Mosaic"));
		mosaicTB.setToggleGroup(operationsGroup);
		mosaicTB.setMaxWidth(200);
		
		sandboxTB.setTooltip(new Tooltip("Sandbox"));
		sandboxTB.setToggleGroup(operationsGroup);
		sandboxTB.setMaxWidth(200);
		
		ycbcrTB.setTooltip(new Tooltip("YCbCr"));
		ycbcrTB.setToggleGroup(operationsGroup);
		ycbcrTB.setMaxWidth(200);
		
		thresholdingTB.setTooltip(new Tooltip("Thresholding"));
		thresholdingTB.setToggleGroup(operationsGroup);
		thresholdingTB.setMaxWidth(200);
		
		morphologyTB.setTooltip(new Tooltip("Morphology"));
		morphologyTB.setToggleGroup(operationsGroup);
		morphologyTB.setMaxWidth(200);
		
		sepiaTB.setTooltip(new Tooltip("Sepia"));
		sepiaTB.setToggleGroup(operationsGroup);
		sepiaTB.setMaxWidth(200);
		
		operationsPane.getItems().addAll(kernelTB, mosaicTB, sandboxTB, ycbcrTB, thresholdingTB, morphologyTB, sepiaTB);
		operationsPane.setOrientation(Orientation.VERTICAL);
		operationsPane.setMaxWidth(5000D);
		operationsPane.setMaxHeight(5000D);
		operationsPane.setPadding(new Insets(5D,5D,5D,5D));
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
				if(newValue.equals(kernelTB)) {
					kernelEffectOptions();
				} else if(newValue.equals(mosaicTB)) {
					mosaicEffectOptions();
				} else if(newValue.equals(sandboxTB)) {
					sandboxEffectOptions();
				} else if(newValue.equals(ycbcrTB)) {
					ycbcrEffectOptions();
				} else if(newValue.equals(morphologyTB)) {
					morphologyEffectOptions();
				} else if(newValue.equals(thresholdingTB)) {
					thresholdingEffectOptions();
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
	 * Obsluha efektu mazaika
	 */
	public void mosaicEffectOptions() {
		operationBox.getChildren().clear();
		Label precentLabel = new Label("Precent");
		Slider precent = new Slider(); 
		precent.setMin(0.01);
		precent.setMax(0.50);
		precent.setValue(0.05);
		precent.setBlockIncrement(0.01);
		CheckBox doublePass = new CheckBox("Double Pass");
		doublePass.setMaxWidth(200);
		Separator separator = new Separator();
		separator.setMaxWidth(200);
		CheckBox dotted = new CheckBox("Dotted");
		dotted.setMaxWidth(200);
		CheckBox rounded = new CheckBox("Rounded");
		rounded.setMaxWidth(200);
		Label distributionLabel = new Label("Distribution");
		Slider distribution = new Slider(); 
		distribution.setMin(0.25);
		distribution.setMax(10);
		distribution.setValue(1);
		Button apply = new Button("Apply");
		apply.setMaxWidth(200);
		apply.setOnAction(action -> {
			long time = drawing.mosaicEffect(precent.getValue(), doublePass.isSelected(), dotted.isSelected(), rounded.isSelected(), distribution.getValue());
			timeLabel.setText("Mosaic Effect Time: " + toMs(time) + " ms");
		});

		operationBox.getChildren().addAll(precentLabel, precent, doublePass, separator, dotted, rounded, distributionLabel, distribution, apply);
	}
	
	/**
	 * Obsluha ruznych efektu
	 */
	public void sandboxEffectOptions() {
		operationBox.getChildren().clear();
		Button applyNeg = new Button("Apply Negative");
		Button applySubstraction = new Button("Apply Substraction");
		Button applyBW = new Button("Apply B&W");
		Button applyEmboss = new Button("Apply Emboss");
		applyNeg.setMaxWidth(200);
		applyNeg.setOnAction(action -> {
			long time = drawing.negativeEffect();
			timeLabel.setText("Negative Effect Time: " + toMs(time) + " ms");
		});
		applySubstraction.setMaxWidth(200);
		applySubstraction.setOnAction(action -> {
			File file = handleOpen();
	        if (file != null) {
	        	try {
	        		BufferedImage bufferedImage = ImageIO.read(file);
	                Image image = SwingFXUtils.toFXImage(bufferedImage, null);
	    			long time = drawing.substractionEffect(image);
	    			timeLabel.setText("Substraction Effect Time: " + toMs(time) + " ms");
	        	} catch (Exception e) {
	        		timeLabel.setText("Error!");
	        	}
	        } else {
	        	timeLabel.setText("Invalid image!");
	        }
		});
		applyBW.setMaxWidth(200);
		applyBW.setOnAction(action -> {
			long time = drawing.blackAndWhiteEffect();
			timeLabel.setText("B&W Effect Time: " + toMs(time) + " ms");
		});
		applyEmboss.setMaxWidth(200);
		applyEmboss.setOnAction(action -> {
			long time = drawing.embossEffect();
			timeLabel.setText("Emboss Effect Time: " + toMs(time) + " ms");
		});
		operationBox.getChildren().addAll(applyNeg, applyBW, applyEmboss, applySubstraction);
	}
	
	/**
	 * Obsluha efektu YCbCr / RGB
	 */
	public void ycbcrEffectOptions() {
		operationBox.getChildren().clear();
		Button applyYCbCr = new Button("RGB to YCbCr");
		Button applyRGB = new Button("YCbCr to RGB");
		applyYCbCr.setMaxWidth(200);
		applyYCbCr.setOnAction(action -> {
			long time = drawing.ycbcrEffect();
			timeLabel.setText("YCbCr Effect Time: " + toMs(time) + " ms");
		});
		applyRGB.setMaxWidth(200);
		applyRGB.setOnAction(action -> {
			long time = drawing.rgbEffect();
			timeLabel.setText("RGB Effect Time: " + toMs(time) + " ms");
		});
		operationBox.getChildren().addAll(applyYCbCr, applyRGB);
	}
	
	/**
	 * Obsluha efektu prahovani
	 */
	public void thresholdingEffectOptions() {
		operationBox.getChildren().clear();
		int defaultValue = 125;
		Label thresholdLabel = new Label("Threshold (" + String.format("%3d", defaultValue) + ")");
		Slider threshold = new Slider(); 
		threshold.setMin(0);
		threshold.setMax(255);
		threshold.setValue(defaultValue);
		threshold.setShowTickLabels(false);
		threshold.setShowTickMarks(false);
		threshold.setMajorTickUnit(50);
		threshold.setMinorTickCount(2);
		threshold.setBlockIncrement(1);
		threshold.valueProperty().addListener(new ChangeListener<Object>() {
            @Override
            public void changed(ObservableValue<?> arg0, Object arg1, Object arg2) {
            	thresholdLabel.textProperty().setValue("Threshold (" + String.format("%3d", (int) threshold.getValue()) + ")");

            }
        });
		Button applyThresholding = new Button("Apply Thresholding");
		Button applyMultiThresholding = new Button("Apply MLT");
		applyThresholding.setMaxWidth(200);
		applyThresholding.setOnAction(action -> {
			long time = drawing.thresholdingEffect((int)(threshold.getValue()), true);
			timeLabel.setText("Thresholding Effect Time: " + toMs(time) + " ms");
		});
		applyMultiThresholding.setMaxWidth(200);
		applyMultiThresholding.setOnAction(action -> {
			long time = drawing.multilevelThresholdingEffect((int)(threshold.getValue()));
			timeLabel.setText("Multilevel Thresholding Effect Time: " + toMs(time) + " ms");
		});
		operationBox.getChildren().addAll(thresholdLabel, threshold, applyThresholding, applyMultiThresholding);
	}
	
	/**
	 * Obsluha morfologickych operaci
	 */
	public void morphologyEffectOptions() {
		operationBox.getChildren().clear();
		Button applyDilation = new Button("Apply Dilation");
		Button applyErosion = new Button("Apply Erosion");
		applyDilation.setMaxWidth(200);
		applyDilation.setOnAction(action -> {
			long time = drawing.dilationEffect();
			timeLabel.setText("Dilation Effect Time: " + toMs(time) + " ms");
		});
		applyErosion.setMaxWidth(200);
		applyErosion.setOnAction(action -> {
			long time = drawing.erosionEffect();
			timeLabel.setText("Erosion Effect Time: " + toMs(time) + " ms");
		});
		operationBox.getChildren().addAll(applyDilation, applyErosion);
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
	 * Obsluha efektu pouziti masky
	 */
	public void kernelEffectOptions() {
		operationBox.getChildren().clear();
		Label weightLabel = new Label("Kernel");
		ChoiceBox<KernelMatrix> kernelSelection = new ChoiceBox<KernelMatrix>();
		kernelSelection.getItems().addAll(KernelMatrix.getDefaultList());
		kernelSelection.getSelectionModel().select(0);
		kernelSelection.setMaxWidth(200);
		Button apply = new Button("Apply");
		apply.setMaxWidth(200);
		apply.setOnAction(action -> {
			long time = 0L;
			if(kernelSelection.getSelectionModel().getSelectedItem().equals(KernelMatrix.SOBEL)) {
				time = drawing.sobelEffect(false);	// specialni pripad pro sobeluv filtr
			} else if(kernelSelection.getSelectionModel().getSelectedItem().equals(KernelMatrix.NEGATIVE_SOBEL)) {
				time = drawing.sobelEffect(true);	// specialni pripad pro sobeluv filtr
			} else {
				double[] weight = kernelSelection.getSelectionModel().getSelectedItem().matrix;
				time = drawing.kernelEffect(weight);
			}
			timeLabel.setText("Kernel Effect Time: " + toMs(time) + " ms");
		});

		operationBox.getChildren().addAll(weightLabel, kernelSelection, apply);
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
    protected File handleOpen() {
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
        return file;
    }
	
	/** Otevre FileChooser pro vyber obrazku */
    private void handleOpenPrimaryImg() {
    	File file = handleOpen();
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
