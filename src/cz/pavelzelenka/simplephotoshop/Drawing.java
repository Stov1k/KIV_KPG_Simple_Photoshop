package cz.pavelzelenka.simplephotoshop;

import cz.pavelzelenka.simplephotoshop.effects.BasicEffects;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;

/**
 * Kresba
 * @author Pavel Zelenka A16B0176P
 * @version 2018-03-24
 */
public class Drawing {

	/** Vychozi obrazek */
	public static final Image ΙΝΙΤ_IMAGE = new Image("/images/lena.bmp");
	
	/** Sirka pera */
	public static final int MAX_PEN_WIDTH = 1;
	
	/** Ovladaci prvek kresleni */
	private GraphicsContext g;
	
	/** Platno */
	private Canvas activeCanvas;
	
	/** puvodni obrazek */
	private Image original;
	
	/** pracovni obrazek */
	private WritableImage working;
	
	/** krok zpet */
	private WritableImage stepback;
	
	/** moznost vratit o krok zpet */
	private BooleanProperty undoEnable;
	
	/** sirka */
	private DoubleProperty width;
	
	/** vyska */
	private DoubleProperty height;
	
	/**
	 * Vytvoreni instance kresby
	 * @param canvas platno
	 */
	public Drawing(Canvas canvas) {
		this.activeCanvas = canvas;
		g = canvas.getGraphicsContext2D();
		
		original = ΙΝΙΤ_IMAGE;
		undoEnable = new SimpleBooleanProperty(false);
		initializeWritableImage();
		
		canvas.setWidth(getImageWidth());
        canvas.setHeight(getImageHeight());
        
        width = new SimpleDoubleProperty();
        height = new SimpleDoubleProperty();
        
        width.bind(canvas.widthProperty());
        height.bind(canvas.heightProperty());
	}
	
	/**
	 * Inicialize pracovnich obrazku
	 */
	private void initializeWritableImage() {
		working = new WritableImage((int)original.getWidth(),(int)original.getHeight());
		stepback = new WritableImage((int)original.getWidth(),(int)original.getHeight());
	}
	
	/**
	 * Prekresli plochu
	 */
	public void redraw() {
		clear();
		draw();
	}
	
	/**
	 * Vycisti plochu
	 */
	public void clear() {
		g.clearRect(0, 0, activeCanvas.getWidth(), activeCanvas.getHeight());
	}
	
	/**
	 * Vykresleni originalniho obrazu
	 */
	public void draw() {
		g.drawImage(original, 0, 0);
		activeCanvas.snapshot(null, working);
	}
	
	/**
	 * Vrati obraz o krok zpet
	 */
	public void undo() {
		working = stepback;
		stepback = new WritableImage((int)original.getWidth(),(int)original.getHeight());
		g.drawImage(working, 0, 0);
		activeCanvas.snapshot(null, working);
		undoEnable.set(false);
	}
	
	/**
	 * Efekt pouziti smasky
	 * @param weight matice vahy
	 * @return cas algoritmu
	 */
	public long kernelEffect(double[] weight) {
		// ulozeni pracovniho obrazu pro vraceni o krok zpet
		g.drawImage(working, 0, 0);
		activeCanvas.snapshot(null, stepback);
		undoEnable.set(true);
		// uprava pracovniho obrazu
		long start = System.nanoTime();
		BasicEffects.kernelEffect(g, working, (int) working.getWidth(), (int) working.getHeight(), weight);
		long end = System.nanoTime() - start;
		// ulozeni pracovniho obrazu
		activeCanvas.snapshot(null, working);
		return end;
	}
	
	/**
	 * Efekt cernobileho obrazu
	 * @return cas algoritmu
	 */
	public long blackAndWhiteEffect() {
		// ulozeni pracovniho obrazu pro vraceni o krok zpet
		g.drawImage(working, 0, 0);
		activeCanvas.snapshot(null, stepback);
		undoEnable.set(true);
		// uprava pracovniho obrazu
		long start = System.nanoTime();
		BasicEffects.blackAndWhiteEffect(g, working, (int) working.getWidth(), (int) working.getHeight());
		long end = System.nanoTime() - start;
		// ulozeni pracovniho obrazu
		activeCanvas.snapshot(null, working);
		return end;
	}
	
	/**
	 * Efekt tepaneho obrazu
	 * @return cas algoritmu
	 */
	public long embossEffect() {
		// ulozeni pracovniho obrazu pro vraceni o krok zpet
		g.drawImage(working, 0, 0);
		activeCanvas.snapshot(null, stepback);
		undoEnable.set(true);
		// uprava pracovniho obrazu
		long start = System.nanoTime();
		BasicEffects.embossEffect(g, working, (int) working.getWidth(), (int) working.getHeight());
		long end = System.nanoTime() - start;
		// ulozeni pracovniho obrazu
		activeCanvas.snapshot(null, working);
		return end;
	}
	
	/**
	 * Efekt negativu
	 * @return cas algoritmu
	 */
	public long negativeEffect() {
		// ulozeni pracovniho obrazu pro vraceni o krok zpet
		g.drawImage(working, 0, 0);
		activeCanvas.snapshot(null, stepback);
		undoEnable.set(true);
		// uprava pracovniho obrazu
		long start = System.nanoTime();
		BasicEffects.negativeEffect(g, working, (int) working.getWidth(), (int) working.getHeight());
		long end = System.nanoTime() - start;
		// ulozeni pracovniho obrazu
		activeCanvas.snapshot(null, working);
		return end;
	}
	
	/**
	 * Efekt sepie
	 * @param depth hloubka
	 * @param intesity intenzita
	 * @return cas algoritmu
	 */
	public long sepiaEffect(int depth, int intensity) {
		// ulozeni pracovniho obrazu pro vraceni o krok zpet
		g.drawImage(working, 0, 0);
		activeCanvas.snapshot(null, stepback);
		undoEnable.set(true);
		// uprava pracovniho obrazu
		long start = System.nanoTime();
		BasicEffects.sepiaEffect(g, working, (int) working.getWidth(), (int) working.getHeight(), depth, intensity);
		long end = System.nanoTime() - start;
		// ulozeni pracovniho obrazu
		activeCanvas.snapshot(null, working);
		return end;
	}
	
	/**
	 * Efekt mozaiky
	 * @param precent koeficient urcujici pocet ctvercu
	 * @param doublePass dvojity pruchod
	 * @return cas algoritmu
	 */
	public long mosaicEffect(double precent, boolean doublePass) {
		// ulozeni pracovniho obrazu pro vraceni o krok zpet
		g.drawImage(working, 0, 0);
		activeCanvas.snapshot(null, stepback);
		undoEnable.set(true);
		// uprava pracovniho obrazu
		long start = System.nanoTime();
		if(doublePass) {
			BasicEffects.mosaicDoublePassEffect(g, working, (int) working.getWidth(), (int) working.getHeight(), precent);
		} else {
			BasicEffects.mosaicEffect(g, working, (int) working.getWidth(), (int) working.getHeight(), precent);
		}
		long end = System.nanoTime() - start;
		// ulozeni pracovniho obrazu
		activeCanvas.snapshot(null, working);
		return end;
	}
	
	/**
	 * Vrati sirku obrazku
	 * @return sirka obrazku
	 */
	public double getImageWidth() {
		return this.original.getWidth();
	}
	
	/**
	 * Vrati vysku obrazku
	 * @return vysku obrazku
	 */
	public double getImageHeight() {
		return this.original.getHeight();
	}

	/**
	 * Vrati, zdali je mozne se vratit o krok zpet
	 * @return true, kdyz je mozne se vratit o krok zpet
	 */
	public boolean isUndoEnable() {
		return undoEnable.get();
	}
	
	/**
	 * Moznost vraceni o krok zpet
	 * @return vlastnost vraceni o krok zpet
	 */
	public BooleanProperty undoEnableProperty() {
		return undoEnable;
	}
	
	/**
	 * Obnovi puvodni obrazek
	 */
	public void reset() {
		clear();
		this.undoEnable.set(false);
		this.activeCanvas.setWidth(original.getWidth());
		this.activeCanvas.setHeight(original.getHeight());
		initializeWritableImage();
		draw();
	}
	
	/**
	 * Nastaveni obrazku
	 * @param image obrazek
	 */
	public void setImage(Image image) {
		clear();
		this.original = image;
		this.activeCanvas.setWidth(image.getWidth());
		this.activeCanvas.setHeight(image.getHeight());
		initializeWritableImage();
		undoEnable.set(false);
		draw();
	}

	/** Sirka */
	public DoubleProperty widthProperty() {
		return width;
	}

	/** Vyska */
	public DoubleProperty heightProperty() {
		return height;
	}

	/**
	 * Vrati pracovni obrazek
	 * @return pracovni obrazek
	 */
	public WritableImage getWorking() {
		return working;
	}
	
}
