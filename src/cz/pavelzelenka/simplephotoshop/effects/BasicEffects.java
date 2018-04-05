package cz.pavelzelenka.simplephotoshop.effects;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

/**
 * Efekty
 * @author Pavel Zelenka A16B0176P
 * @version 2018-03-24
 */
public class BasicEffects {

	/**
	 * Cernobily obraz
	 * @param g ovladaci prvek kresleni
	 * @param working pracovni obrazek
	 * @param width sirka obrazku
	 * @param height vyska obrazku
	 */
	public static void blackAndWhiteEffect(GraphicsContext g, WritableImage working, int width, int height) {
		PixelReader reader = working.getPixelReader();
		PixelWriter writer = g.getPixelWriter();
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				Color color = reader.getColor(x, y);
				int grayTone = (int) ((color.getRed()*255 + color.getGreen()*255 + color.getBlue()*255) / 3D);
				Color newColor = Color.rgb(grayTone, grayTone, grayTone);
				writer.setColor(x, y, newColor);
			}
		}
	}
	
	/**
	 * Vytepat obraz
	 * @param g ovladaci prvek kresleni
	 * @param working pracovni obrazek
	 * @param width sirka obrazku
	 * @param height vyska obrazku
	 */
	public static void embossEffect(GraphicsContext g, WritableImage working, int width, int height) {
		PixelReader reader = working.getPixelReader();
		PixelWriter writer = g.getPixelWriter();
		for (int x = 0; x < width-1; x++) {
			for (int y = 0; y < height-1; y++) {
				Color color = reader.getColor(x, y);
				Color neighborn = reader.getColor(x+1, y+1);
				int red = (int) ((color.getRed() - neighborn.getRed() + 0.5) * 255);
				int gre = (int) ((color.getGreen( ) - neighborn.getGreen( ) + 0.5) * 255);
				int blu = (int) ((color.getBlue( ) - neighborn.getBlue( ) + 0.5) * 255);
				if(red > 255) red = 255;
				if(gre > 255) gre = 255;
				if(blu > 255) blu = 255;
				if(red < 0) red = 0;
				if(gre < 0) gre = 0;
			    if(blu < 0) blu = 0;	
				Color newColor = Color.rgb(red, gre, blu);
				writer.setColor(x, y, newColor);
			}
		}
	}
	
	/**
	 * Mozaika
	 * @param g ovladaci prvek kresleni
	 * @param working pracovni obrazek
	 * @param width sirka obrazku
	 * @param height vyska obrazku
	 * @param precent procento deleni
	 */
	public static void mosaicEffect(GraphicsContext g, WritableImage working, int width, int height, double precent) {
		if(precent > 1D) return;
		if(precent < 0.01) precent = 0.01;
		PixelReader reader = working.getPixelReader();
		PixelWriter writer = g.getPixelWriter();
		int raw = (int) ((double)width * precent);
		int unit = (raw > 0 ? width/raw : width);
		if (unit >= width || unit >= height) return;
		for (int x = 0; x < width; x+=unit) {
			for (int y = 0; y < height; y+=unit) {
				Color color = reader.getColor(x, y);
				for (int k = 0; k < unit; k++) {
					for (int m = 0; m < unit; m++) {
						if((x + k) < width && (y + m) < height) {
							writer.setColor(x + k, y + m, color);
						}
					}
				}
			}
		}
	}
	
	/**
	 * Mozaika (teckovana)
	 * @param g ovladaci prvek kresleni
	 * @param working pracovni obrazek
	 * @param width sirka obrazku
	 * @param height vyska obrazku
	 * @param precent procento deleni
	 * @param rounded zakulaceni
	 * @param distribution exponent rozmisteni barvy pro teckovani
	 */
	public static void mosaicDottedEffect(GraphicsContext g, WritableImage working, int width, int height, double precent, boolean rounded, double distribution) {
		if(precent > 1D) return;
		if(precent < 0.01) precent = 0.01;
		PixelReader reader = working.getPixelReader();
		PixelWriter writer = g.getPixelWriter();
		int raw = (int) ((double)width * precent);
		int unit = (raw > 0 ? width/raw : width);
		if (unit >= width || unit >= height) return;
		for (int x = 0; x < width; x+=unit) {
			for (int y = 0; y < height; y+=unit) {
				Color color = reader.getColor(x, y);
				for (int k = 0; k < unit; k++) {
					for (int m = 0; m < unit; m++) {
						if((x + k) < width && (y + m) < height) {
							double h = (unit/2D);								// polovicni velikost dilku
							double n = 1;
							if(rounded) {
								double sx = (double)h - (double)k;		// smerovy vektor
								double sy = (double)h - (double)m;		// smerovy vektor
								double d = Math.sqrt(sx*sx + sy*sy);	// delka smeroveho vektoru
								n = 1 - Math.pow((d / h), distribution);
							} else {
								double hk = Math.abs(h-k);							// vzdalenost od stredu (na sirku k)
								double ck = 1 - Math.pow((hk / h), distribution);	// hodnota od 0 - 1 (pro sirku)
								double hm = Math.abs(h-m);							// vzdalenost od stredu (na vysku m)
								double cm = 1 - Math.pow((hm / h), distribution);	// hodnota od 0 - 1 (pro vysku)
								n = ck * cm;
							}
							int red = (int) (color.getRed()*n*255);
							int gre = (int) (color.getGreen()*n*255);
							int blu = (int) (color.getBlue()*n*255);
							if(red > 255) red = 255;
							if(gre > 255) gre = 255;
							if(blu > 255) blu = 255;
							if(red < 0) red = 0;
							if(gre < 0) gre = 0;
						    if(blu < 0) blu = 0;
							Color newColor = Color.rgb(red, gre, blu);
							writer.setColor(x + k, y + m, newColor);
						}
					}
				}
			}
		}
	}
	
	/**
	 * Mozaika s prumerovanim barvy ctverce
	 * @param g ovladaci prvek kresleni
	 * @param working pracovni obrazek
	 * @param width sirka obrazku
	 * @param height vyska obrazku
	 * @param precent procento deleni
	 */
	public static void mosaicDoublePassEffect(GraphicsContext g, WritableImage working, int width, int height, double precent) {
		if(precent > 1D) return;
		if(precent < 0.01) precent = 0.01;
		PixelReader reader = working.getPixelReader();
		PixelWriter writer = g.getPixelWriter();
		int raw = (int) ((double)width * precent);
		int unit = (raw > 0 ? width/raw : width);
		if (unit >= width || unit >= height) return;
		for (int x = 0; x < width; x+=unit) {
			for (int y = 0; y < height; y+=unit) {
				int num = 0;
				int red = 0;
				int gre = 0;
				int blu = 0;
				for (int k = 0; k < unit; k++) {
					for (int m = 0; m < unit; m++) {
						if((x + k) < width && (y + m) < height) {
							Color color = reader.getColor(x + k, y + m);
							num++;
							red += color.getRed() * 255;
							gre += color.getGreen() * 255;
							blu += color.getBlue() * 255;
						}
					}
				}
				red = red / num;
				gre = gre / num;
				blu = blu / num;
				if(red > 255) red = 255;
				if(gre > 255) gre = 255;
				if(blu > 255) blu = 255;
				if(red < 0) red = 0;
				if(gre < 0) gre = 0;
			    if(blu < 0) blu = 0;
			    Color newColor = Color.rgb(red, gre, blu);
				for (int k = 0; k < unit; k++) {
					for (int m = 0; m < unit; m++) {
						if((x + k) < width && (y + m) < height) {
							writer.setColor(x + k, y + m, newColor);
						}
					}
				}
			}
		}
	}
	
	/**
	 * Mozaika s prumerovanim barvy ctverce (teckovana)
	 * @param g ovladaci prvek kresleni
	 * @param working pracovni obrazek
	 * @param width sirka obrazku
	 * @param height vyska obrazku
	 * @param precent procento deleni
	 * @param rounded zakulaceni
	 * @param distribution exponent rozmisteni barvy pro teckovani
	 */
	public static void mosaicDoublePassDottedEffect(GraphicsContext g, WritableImage working, int width, int height, double precent, boolean rounded, double distribution) {
		if(precent > 1D) return;
		if(precent < 0.01) precent = 0.01;
		PixelReader reader = working.getPixelReader();
		PixelWriter writer = g.getPixelWriter();
		int raw = (int) ((double)width * precent);
		int unit = (raw > 0 ? width/raw : width);
		if (unit >= width || unit >= height) return;
		for (int x = 0; x < width; x+=unit) {
			for (int y = 0; y < height; y+=unit) {
				int num = 0;
				int red = 0;
				int gre = 0;
				int blu = 0;
				for (int k = 0; k < unit; k++) {
					for (int m = 0; m < unit; m++) {
						if((x + k) < width && (y + m) < height) {
							Color color = reader.getColor(x + k, y + m);
							num++;
							red += color.getRed() * 255;
							gre += color.getGreen() * 255;
							blu += color.getBlue() * 255;
						}
					}
				}
				red = red / num;
				gre = gre / num;
				blu = blu / num;
				if(red > 255) red = 255;
				if(gre > 255) gre = 255;
				if(blu > 255) blu = 255;
				if(red < 0) red = 0;
				if(gre < 0) gre = 0;
			    if(blu < 0) blu = 0;
			    Color tempColor = Color.rgb(red, gre, blu);
				for (int k = 0; k < unit; k++) {
					for (int m = 0; m < unit; m++) {
						if((x + k) < width && (y + m) < height) {
							double h = (unit/2D);								// polovicni velikost dilku
							double n = 1;
							if(rounded) {
								double sx = (double)h - (double)k;		// smerovy vektor
								double sy = (double)h - (double)m;		// smerovy vektor
								double d = Math.sqrt(sx*sx + sy*sy);	// delka smeroveho vektoru
								n = 1 - Math.pow((d / h), distribution);
							} else {
								double hk = Math.abs(h-k);							// vzdalenost od stredu (na sirku k)
								double ck = 1 - Math.pow((hk / h), distribution);	// hodnota od 0 - 1 (pro sirku)
								double hm = Math.abs(h-m);							// vzdalenost od stredu (na vysku m)
								double cm = 1 - Math.pow((hm / h), distribution);	// hodnota od 0 - 1 (pro vysku)
								n = ck * cm;
							}
							int newR = (int) (tempColor.getRed()*n*255);
							int newG = (int) (tempColor.getGreen()*n*255);
							int newB = (int) (tempColor.getBlue()*n*255);
							if(newR > 255) newR = 255;
							if(newG > 255) newG = 255;
							if(newB > 255) newB = 255;
							if(newR < 0) newR = 0;
							if(newG < 0) newG = 0;
						    if(newB < 0) newB = 0;
							Color newColor = Color.rgb(newR, newG, newB);
							writer.setColor(x + k, y + m, newColor);
						}
					}
				}
			}
		}
	}
	
	/**
	 * Negativ
	 * @param g ovladaci prvek kresleni
	 * @param working pracovni obrazek
	 * @param width sirka obrazku
	 * @param height vyska obrazku
	 */
	public static void negativeEffect(GraphicsContext g, WritableImage working, int width, int height) {
		PixelReader reader = working.getPixelReader();
		PixelWriter writer = g.getPixelWriter();
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				Color color = reader.getColor(x, y);
				int red = (int) Math.abs(color.getRed() * 255 - 255);
				int gre = (int) Math.abs(color.getGreen( ) * 255 - 255);
				int blu = (int) Math.abs(color.getBlue( ) * 255 - 255);
				Color newColor = Color.rgb(red, gre, blu);
				writer.setColor(x, y, newColor);
			}
		}
	}
	
	/**
	 * Sepia
	 * @param g ovladaci prvek kresleni
	 * @param working pracovni obrazek
	 * @param width sirka obrazku
	 * @param height vyska obrazku
	 * @param depth hloubka
	 * @param intensity intenzita
	 */
	public static void sepiaEffect(GraphicsContext g, WritableImage working, int width, int height, int depth, int intensity) {
		PixelReader reader = working.getPixelReader();
		PixelWriter writer = g.getPixelWriter();
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				Color color = reader.getColor(x, y);
				int red = (int) (color.getRed() * 255);
				int gre = (int) (color.getGreen( ) * 255);
				int blu = (int) (color.getBlue( ) * 255);
				int gry = (red + gre + blu) / 3;
				red = gry;
				gre = gry;
				blu = gry;
				red = red + (depth * 2);
				gre = gre + depth;
				if(red > 255) red = 255;
				if(gre > 255) gre = 255;
				if(blu > 255) blu = 255;
				blu -= intensity;
				if(blu > 255) blu = 255;
				if(red < 0) red = 0;
				if(gre < 0) gre = 0;
			    if(blu < 0) blu = 0;	
				Color newColor = Color.rgb(red, gre, blu);
				writer.setColor(x, y, newColor);
			}
		}
	}
	
	/**
	 * Maska
	 * @param g ovladaci prvek kresleni
	 * @param working pracovni obrazek
	 * @param width sirka obrazku
	 * @param height vyska obrazku
	 * @param weight vaha
	 */
	public static void kernelEffect(GraphicsContext g, WritableImage working, int width, int height, double[] weight) {
		PixelReader reader = working.getPixelReader();
		PixelWriter writer = g.getPixelWriter();
		for (int x = 1; x < width-1; x++) {
			for (int y = 1; y < height-1; y++) {
				Color[][] color = new Color[3][3];
				for(int m = -1; m <= 1; m++) {
					for(int n = -1; n <= 1; n++) {
						color[m+1][n+1] = reader.getColor(x+m, y+n);
					}
				}
				int wei = 0;
				int red = 0;
				int gre = 0;
				int blu = 0;
				for(int m = 0; m < 3; m++) {
					for(int n = 0; n < 3; n++) {
						red += (int) (color[m][n].getRed() * 255 * weight[wei]);
						gre += (int) (color[m][n].getGreen() * 255 * weight[wei]);
						blu += (int) (color[m][n].getBlue() * 255 * weight[wei]);
						wei++;
					}
				}
				if(red > 255) red = 255;
				if(gre > 255) gre = 255;
				if(blu > 255) blu = 255;
				if(red < 0) red = 0;
				if(gre < 0) gre = 0;
			    if(blu < 0) blu = 0;
				Color newColor = Color.rgb(red, gre, blu);
				writer.setColor(x, y, newColor);
			}
		}
	}
	
}
