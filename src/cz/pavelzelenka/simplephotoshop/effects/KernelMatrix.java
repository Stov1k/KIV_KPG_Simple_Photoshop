package cz.pavelzelenka.simplephotoshop.effects;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Masky
 * @author Pavel Zelenka
 * @version 2018-04-04
 */
public enum KernelMatrix {
	BLUR("Blur", new double[] {0.1111, 0.1111, 0.1111, 0.1111, 0.1111, 0.1111, 0.1111, 0.1111, 0.1111}),
	GAUSSIAN_BLUR("Gaussian blur", new double[] {0.0625, 0.125, 0.0625, 0.125, 0.25, 0.125, 0.0625, 0.125, 0.0625}),
	SHARPEN("Sharpen", new double[] {0, -1, 0, -1, 5, -1, 0, -1, 0}),
	EDGE_DETECTION_1("Edge Detection 1", new double[] {0, 1, 0, 1, -4, 1, 0, 1, 0}),
	EDGE_DETECTION_2("Edge Detection 2", new double[] {1, 0, -1, 0, 0, 0, -1, 0, 1}),
	EDGE_DETECTION_3("Edge Detection 3", new double[] {-1, -1, -1, -1, 8, -1, -1, -1, -1});
	
	/** nazev */
	public final String name;
	
	/** matice */
    public final double[] matrix;

    /**
     * Konstruktor
     * @param name nazev
     * @param matrix matice
     */
    private KernelMatrix(String name, double[] matrix) {
    	this.name = name;
        this.matrix = matrix;
    }
    
	@Override
	public String toString() {
		return this.name;
	}
	
	/** Vrati vychozi seznam masek */
	public static ObservableList<KernelMatrix> getDefaultList() {
		ObservableList<KernelMatrix> result = FXCollections.observableArrayList(KernelMatrix.values());
		return result;
	}
	
}
