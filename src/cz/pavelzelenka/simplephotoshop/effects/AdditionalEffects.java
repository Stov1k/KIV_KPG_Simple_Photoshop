package cz.pavelzelenka.simplephotoshop.effects;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

public class AdditionalEffects {
	
	public static void subtraction(GraphicsContext g, WritableImage working, int width, int height, Image image) {
		PixelReader newReader = image.getPixelReader();
		PixelReader reader = working.getPixelReader();
		PixelWriter writer = g.getPixelWriter();
		
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				Color oldColor = reader.getColor(x, y);
				Color newColor = newReader.getColor(x, y);
				int r_diff = Math.abs((int)(oldColor.getRed()*255) - (int)(newColor.getRed()*255));
				int g_diff = Math.abs((int)(oldColor.getGreen()*255) - (int)(newColor.getGreen()*255));
				int b_diff = Math.abs((int)(oldColor.getBlue()*255) - (int)(newColor.getBlue()*255));
				writer.setColor(x, y, Color.rgb(r_diff, g_diff, b_diff));
			}
		}

	}
	
	/**
	 * Prevod RGB na YCbCr
	 * @param g ovladaci prvek kresleni
	 * @param working pracovni obrazek
	 * @param width sirka obrazku
	 * @param height vyska obrazku
	 */
	public static void ycbcrEffect(GraphicsContext g, WritableImage working, int width, int height) {
		PixelReader reader = working.getPixelReader();
		PixelWriter writer = g.getPixelWriter();
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				Color color = reader.getColor(x, y);
				int[] yCbCr = fromRGB(new double[]{color.getRed(),color.getGreen(),color.getBlue()});
				Color newColor = Color.rgb(yCbCr[0], yCbCr[1], yCbCr[2]);
				writer.setColor(x, y, newColor);
			}
		}
	}
	
	/**
	 * Prevod YCbCr na RGB
	 * @param g ovladaci prvek kresleni
	 * @param working pracovni obrazek
	 * @param width sirka obrazku
	 * @param height vyska obrazku
	 */
	public static void rgbEffect(GraphicsContext g, WritableImage working, int width, int height) {
		PixelReader reader = working.getPixelReader();
		PixelWriter writer = g.getPixelWriter();
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				Color color = reader.getColor(x, y);
				int[] rgb = fromYCbCr(new double[]{color.getRed(),color.getGreen(),color.getBlue()});
				Color newColor = Color.rgb(rgb[0], rgb[1], rgb[2]);
				writer.setColor(x, y, newColor);
			}
		}
	}
	
	/**
	 * Prevod RGB na YCbCr
	 * @param rgb barva v RGB
	 * @return barva v YCbCr
	 */
	private static int[] fromRGB(double[] rgb) {
		double r = rgb[0]*255D; double g = rgb[1]*255D; double b = rgb[2]*255D;
		int[] ycbcr_tmp = new int[3];
		int y =  (int)(16  + (double) (+0.25675 * r + 0.50412 * g + 0.09790 * b));	// slozka luminance
		int cb = (int)(128 + (double) (-0.14822 * r - 0.29099 * g + 0.43921 * b));	// modra chrominancni slozka
		int cr = (int)(128 + (double) (+0.43921 * r - 0.36778 * g - 0.07142 * b));	// cervena chrominancni slozka
		ycbcr_tmp[0] = y; ycbcr_tmp[1] = cb; ycbcr_tmp[2] = cr;
		
		for (int i=0; i<3; i++) {
			if (ycbcr_tmp[i] > 255)
				ycbcr_tmp[i] = 255;
			else if (ycbcr_tmp[i] < 0)
				ycbcr_tmp[i] = 0;
		}
		return ycbcr_tmp;
	}
	
	/**
	 * Prevod YCbCr na RGB
	 * @param ycbcr barva v YCbCr
	 * @return barva v RGB
	 */
	private static int[] fromYCbCr(double[] ycbcr) {
		double y = ycbcr[0]*255; double cb = ycbcr[1]*255; double cr = ycbcr[2]*255;
		
		int[] rgb_tmp = new int[3];
		int r_int = (int)((double) (1.16443 * (y-16.00) - 0.00000 * (cb-128.0) + 1.59611 * (cr-128.0)));	// slozka cervena
		int g_int = (int)((double) (1.16446 * (y-16.00) - 0.39174 * (cb-128.0) - 0.81291 * (cr-128.0)));	// slozka zelena
		int b_int = (int)((double) (1.16445 * (y-16.00) + 2.01726 * (cb-128.0) + 0.00005 * (cr-128.0)));	// slozka modra
		rgb_tmp[0] = r_int; rgb_tmp[1] = g_int; rgb_tmp[2] = b_int;
		
		for (int i=0; i<3; i++) {
			if (rgb_tmp[i] > 255)
				rgb_tmp[i] = 255;
			else if (rgb_tmp[i] < 0)
				rgb_tmp[i] = 0;
		}
		
		return rgb_tmp;
	}
	
	/**
	 * Dilatace
	 * @param g ovladaci prvek kresleni
	 * @param input pracovni obrazek na vstupu
	 * @param output pracovni obrazek na vystupu
	 * @param width sirka obrazku
	 * @param height vyska obrazku
	 */
	public static void dilationEffect(GraphicsContext g, WritableImage input, WritableImage output, int width, int height) {
		PixelReader reader_in = input.getPixelReader();
		PixelReader reader_out = output.getPixelReader();
		PixelWriter writer_out = g.getPixelWriter();
		for (int x = 0; x < width-1; x++) {
			for (int y = 0; y < height-1; y++) {
				Color color_out = reader_out.getColor(x, y);
				
				Color left = null;
				if(x > 0) {
					left = reader_in.getColor(x-1, y);
				}  else {
					left = Color.rgb(0, 0, 0);
				}
				
				Color right = null;
				if(x < width-1) {
					right = reader_in.getColor(x+1, y);
				} else {
					right = Color.rgb(0, 0, 0);
				}
				
				double red, gre, blu = 0D;
				red = color_out.getRed();
				gre = color_out.getGreen();
				blu = color_out.getBlue();

				double red_left, gre_left, blu_left = 0D;
				red_left = left.getRed();
				gre_left = left.getGreen();
				blu_left = left.getBlue();

				double red_right, gre_right, blu_right = 0D;
				red_right = right.getRed();
				gre_right = right.getGreen();
				blu_right = right.getBlue();
				
				if(red_left > red) {
					red = red_left;
				}
				if(gre_left > gre) {
					gre = gre_left;
				}
				if(blu_left > blu) {
					blu = blu_left;
				}
				
				if(red_right > red) {
					red = red_right;
				}
				if(gre_right > gre) {
					gre = gre_right;
				}
				if(blu_right > blu) {
					blu = blu_right;
				}
				
				red = red * 255; gre = gre * 255; blu = blu * 255;
				if(red > 255) red = 255;
				if(gre > 255) gre = 255;
				if(blu > 255) blu = 255;
				
				Color newColor = Color.rgb((int)red, (int)gre, (int)blu);
				writer_out.setColor(x, y, newColor);
			}
		}
	}
	
	/**
	 * Eroze
	 * @param g ovladaci prvek kresleni
	 * @param input pracovni obrazek na vstupu
	 * @param output pracovni obrazek na vystupu
	 * @param width sirka obrazku
	 * @param height vyska obrazku
	 */
	public static void erosionEffect(GraphicsContext g, WritableImage input, WritableImage output, int width, int height) {
		PixelReader reader_in = input.getPixelReader();
		PixelReader reader_out = output.getPixelReader();
		PixelWriter writer_out = g.getPixelWriter();
		for (int x = 0; x < width-1; x++) {
			for (int y = 0; y < height-1; y++) {
				Color color_out = reader_out.getColor(x, y);
				
				Color left = null;
				if(x > 0) {
					left = reader_in.getColor(x-1, y);
				}  else {
					left = Color.rgb(255, 255, 255);
				}
				
				Color right = null;
				if(x < width-1) {
					right = reader_in.getColor(x+1, y);
				} else {
					right = Color.rgb(255, 255, 255);
				}
				
				double red, gre, blu = 1D;
				red = color_out.getRed();
				gre = color_out.getGreen();
				blu = color_out.getBlue();

				double red_left, gre_left, blu_left = 1D;
				red_left = left.getRed();
				gre_left = left.getGreen();
				blu_left = left.getBlue();

				double red_right, gre_right, blu_right = 1D;
				red_right = right.getRed();
				gre_right = right.getGreen();
				blu_right = right.getBlue();
				
				if(red_left < red) {
					red = red_left;
				}
				if(gre_left < gre) {
					gre = gre_left;
				}
				if(blu_left < blu) {
					blu = blu_left;
				}
				
				if(red_right < red) {
					red = red_right;
				}
				if(gre_right < gre) {
					gre = gre_right;
				}
				if(blu_right < blu) {
					blu = blu_right;
				}
				
				red = red * 255; gre = gre * 255; blu = blu * 255;
				if(red > 255) red = 255;
				if(gre > 255) gre = 255;
				if(blu > 255) blu = 255;
				
				Color newColor = Color.rgb((int)red, (int)gre, (int)blu);
				writer_out.setColor(x, y, newColor);
			}
		}
	}

	/**
	 * Prahovani
	 * @param g ovladaci prvek kresleni
	 * @param working pracovni obrazek
	 * @param width sirka obrazku
	 * @param height vyska obrazku
	 * @param setlevel uroven prahu
	 * @param luminance podle luminance (predpoklada obraz v YCbCr, tzn. red = luminance)
	 */
	public static void thresholdingEffect(GraphicsContext g, WritableImage working, int width, int height, int setlevel, boolean luminance) {
		PixelReader reader = working.getPixelReader();
		PixelWriter writer = g.getPixelWriter();
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				Color color = reader.getColor(x, y);
				int grayTone = 0;
				if(luminance) {
					grayTone = (int) ((color.getRed()*255));
				} else {
					grayTone = (int) ((color.getRed()*255 + color.getGreen()*255 + color.getBlue()*255) / 3D);
				}
				if(grayTone < setlevel) {
					grayTone = 0;
				} else {
					grayTone = 255;
				}
				Color newColor = Color.rgb(grayTone, grayTone, grayTone);
				writer.setColor(x, y, newColor);
			}
		}
	}
	
	/**
	 * Prahovani
	 * @param g ovladaci prvek kresleni
	 * @param working pracovni obrazek
	 * @param width sirka obrazku
	 * @param height vyska obrazku
	 * @param levels pocet urovni
	 * @param luminance podle luminance (predpoklada obraz v YCbCr, tzn. red = luminance)
	 */
	public static void multilevelThresholdingEffect(GraphicsContext g, WritableImage working, int width, int height, int levels,  boolean luminance) {
		int lvl = levels;
		if(lvl > 255) {
			lvl = 255;
		}
		if(lvl < 1) {
			lvl = 1;
		}
		PixelReader reader = working.getPixelReader();
		PixelWriter writer = g.getPixelWriter();
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				Color color = reader.getColor(x, y);
				int grayTone = 0;
				if(luminance) {
					grayTone = (int) ((color.getRed()*255));
				} else {
					grayTone = (int) ((color.getRed()*255 + color.getGreen()*255 + color.getBlue()*255) / 3D);
				}
				Color newColor = Color.rgb(0, 0, 0);
				int sectionWidth = 255/lvl;
				for(int i = 0; i < lvl; i++) {
					if(grayTone <= i*sectionWidth) {
						newColor = getRainbowColor(i, lvl+1, 1D, 0.8D);
						break;
					}
				}
				writer.setColor(x, y, newColor);
			}
		}
	}
	
	/**
	 * Vrati barvu
	 * @param position aktualni pozice
	 * @param total celkovy pocet pozic
	 * @param saturation sytost
	 * @param brightness svetlost
	 * @return barva
	 */
	private static Color getRainbowColor(int position, int total, double saturation, double brightness) {
    	double hue = Math.floor((double)position * 360D/(double)(total));
    	Color color = Color.hsb(hue, saturation, brightness);
    	return color;
	}
	
}
