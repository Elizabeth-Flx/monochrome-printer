package _v1_ImageProcessing;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

import javax.imageio.ImageIO;

import net.coobird.thumbnailator.Thumbnails;

public class ImgProc {
	
	
	BufferedImage rescaledImg;
	
	final int MAXHEIGHT = 200;
	final int MAXWIDTH  = 200;
	
	int imgHeight; 	//Number of Rows
	int imgWidth;	//Number of Columns
	
	
	//0 to 255
	//0 = Black
	//255 = White
	int[][] imgValues = null;
	
	//1 = Black
	//0 = White
	int[][] monoImg = null;

	
	
	
	//Implemented dithering algorithms
	private void dither(int kernel) {
		
		int errorMargin;
		int curValue;
		
		for (int row = 0; row < imgHeight; row++) {
			
			for (int col = 0; col < imgWidth; col++) {
				
				curValue = imgValues[row][col];
				
				if (curValue <= 127) {
					monoImg[row][col] = 1;
					errorMargin = curValue; 
				} else {
					monoImg[row][col] = 0;
					errorMargin = curValue - 255;
				} 
				
				
				switch ( kernel ) {
			
				//Does not include case == 1, as this means the user has chosen to use random based threshold
				//This is done in the separate method randomizedThreshold()
				case 0:
					//User has chosen threshold based image processing
					//No dithering is required
					break;
				case 2:
					diffusion1D(row, col, errorMargin);
					break;
				case 3:
					diffusion2D(row, col, errorMargin);
					break;
				case 4:
					floydSteinbergDith(row, col, errorMargin);
					break;
				case 5:
					burkesDith(row, col, errorMargin);
					break;
				case 6:
					jarvisJudiceNinkeDith(row, col, errorMargin);
					break;
	
				}
			}
		}
	}
	
	
	
	private void diffusion1D(int row, int col, int errorMargin) {
		
		// 1  	| # 1 |
		
		if (col+1 < imgWidth) imgValues[row][col+1] = imgValues[row][col+1] + errorMargin;
		
	}
	
	private void diffusion2D(int row, int col, int errorMargin) {
		
		// 1/2	| # 1 |
		//		| 1 0 |
		
		if (col+1 < imgWidth)  imgValues[row][col+1] = (int) (imgValues[row][col+1] + errorMargin*0.5 + 0.5);
		if (row+1 < imgHeight) imgValues[row+1][col] = (int) (imgValues[row+1][col] + errorMargin*0.5 + 0.5);
		
	}
	
	private void floydSteinbergDith(int row, int col, int errorMargin) {
		
		// 1/16	| - # 7 |
		//		| 3 5 1 |
		
		if (col+1 < imgWidth) 				 	   imgValues[row][col+1]   = (int) (imgValues[row][col+1]   + errorMargin*(7.0/16.0) + 0.5);
		if (col-1 >= 0 && row+1 < imgHeight) 	   imgValues[row+1][col-1] = (int) (imgValues[row+1][col-1] + errorMargin*(3.0/16.0) + 0.5);
		if (row+1 < imgHeight) 				       imgValues[row+1][col]   = (int) (imgValues[row+1][col]   + errorMargin*(5.0/16.0) + 0.5);
		if (col+1 < imgWidth && row+1 < imgHeight) imgValues[row+1][col+1] = (int) (imgValues[row+1][col+1] + errorMargin*(1.0/16.0) + 0.5);
		
	}
	
	private void burkesDith(int row, int col, int errorMargin) {
		
		// 1/32	| - - # 8 4 |
		//		| 2 4 8 4 2 |
		
		if (col+1 < imgWidth) 				 	   imgValues[row][col+1]   = (int) (imgValues[row][col+1]   + errorMargin*(8.0/32.0) + 0.5);
		if (col+2 < imgWidth) 				 	   imgValues[row][col+2]   = (int) (imgValues[row][col+2]   + errorMargin*(4.0/32.0) + 0.5);
		if (col-2 >= 0 && row+1 < imgHeight) 	   imgValues[row+1][col-2] = (int) (imgValues[row+1][col-2] + errorMargin*(2.0/32.0) + 0.5);
		if (col-1 >= 0 && row+1 < imgHeight) 	   imgValues[row+1][col-1] = (int) (imgValues[row+1][col-1] + errorMargin*(4.0/32.0) + 0.5);
		if (row+1 < imgHeight) 				       imgValues[row+1][col]   = (int) (imgValues[row+1][col]   + errorMargin*(8.0/32.0) + 0.5);
		if (col+1 < imgWidth && row+1 < imgHeight) imgValues[row+1][col+1] = (int) (imgValues[row+1][col+1] + errorMargin*(4.0/32.0) + 0.5);
		if (col+2 < imgWidth && row+2 < imgHeight) imgValues[row+1][col+2] = (int) (imgValues[row+1][col+2] + errorMargin*(2.0/32.0) + 0.5);
		
	}
	
	private void jarvisJudiceNinkeDith(int row, int col, int errorMargin) {
		
		// 1/48	| - - # 7 5 |
		//		| 3 5 7 5 3 |
		//		| 1 3 5 3 1 |
		
		if (col+1 < imgWidth) 				 	   imgValues[row][col+1]   = (int) (imgValues[row][col+1]   + errorMargin*(7.0/48.0) + 0.5);
		if (col+2 < imgWidth) 				 	   imgValues[row][col+2]   = (int) (imgValues[row][col+2]   + errorMargin*(5.0/48.0) + 0.5);
		if (col-2 >= 0 && row+1 < imgHeight) 	   imgValues[row+1][col-2] = (int) (imgValues[row+1][col-2] + errorMargin*(3.0/48.0) + 0.5);
		if (col-1 >= 0 && row+1 < imgHeight) 	   imgValues[row+1][col-1] = (int) (imgValues[row+1][col-1] + errorMargin*(5.0/48.0) + 0.5);
		if (row+1 < imgHeight) 				       imgValues[row+1][col]   = (int) (imgValues[row+1][col]   + errorMargin*(7.0/48.0) + 0.5);
		if (col+1 < imgWidth && row+1 < imgHeight) imgValues[row+1][col+1] = (int) (imgValues[row+1][col+1] + errorMargin*(5.0/48.0) + 0.5);
		if (col+2 < imgWidth && row+2 < imgHeight) imgValues[row+1][col+2] = (int) (imgValues[row+1][col+2] + errorMargin*(3.0/48.0) + 0.5);
		if (col-2 >= 0 && row+2 < imgHeight) 	   imgValues[row+2][col-2] = (int) (imgValues[row+2][col-2] + errorMargin*(1.0/48.0) + 0.5);
		if (col-1 >= 0 && row+2 < imgHeight) 	   imgValues[row+2][col-1] = (int) (imgValues[row+2][col-1] + errorMargin*(3.0/48.0) + 0.5);
		if (row+2 < imgHeight) 				       imgValues[row+2][col]   = (int) (imgValues[row+2][col]   + errorMargin*(5.0/48.0) + 0.5);
		if (col+1 < imgWidth && row+2 < imgHeight) imgValues[row+2][col+1] = (int) (imgValues[row+2][col+1] + errorMargin*(3.0/48.0) + 0.5);
		if (col+2 < imgWidth && row+2 < imgHeight) imgValues[row+2][col+2] = (int) (imgValues[row+2][col+2] + errorMargin*(1.0/48.0) + 0.5);
		
	}
	
	private void randomizedThreshold() {
		
		Random rand = new Random();
		
		int curTotal;
		int randomThreshold;
		
		for (int row = 0; row < imgHeight; row++) {
			
			for (int col = 0; col < imgWidth; col++) {
				
				curTotal = imgValues[row][col];
				
				randomThreshold = rand.nextInt(255);
				
				if (curTotal <= randomThreshold) monoImg[row][col] = 1;
				else 							 monoImg[row][col] = 0;
				
			}
		}
	}
	
	
	
	

	public void generateRescaledImage(File file) {
		
		try {
			
			BufferedImage tmpBfImg = ImageIO.read(file);
			
			int tmpHeight = tmpBfImg.getHeight();
			int tmpWidth  = tmpBfImg.getWidth ();
			
			if (tmpHeight > tmpWidth) {
				
				rescaledImg = Thumbnails.of(tmpBfImg)
	            .size(MAXWIDTH, MAXHEIGHT)
	            .rotate(90)
	            .asBufferedImage();
				
			} else {
				
				rescaledImg = Thumbnails.of(tmpBfImg)
	            .size(MAXWIDTH, MAXHEIGHT)
	            .asBufferedImage();
				
			}
					
		} catch (IOException e) {
			e.printStackTrace();
		}
	
	}
	
	
	
	private void generateImgValues(int brightness) {
		
		imgHeight = rescaledImg.getHeight();
		imgWidth  = rescaledImg.getWidth();
		
		imgValues = new int [imgHeight][imgWidth];
		monoImg = 	new int [imgHeight][imgWidth];
		
		
		for (int row = 0; row < imgHeight; row++) {
			
			for (int col = 0; col < imgWidth; col++) {
				
				Color curColor = new Color(rescaledImg.getRGB(col, row)); 
				
				//Calculates greyscale pixel value (0-255) with brightness taken into account
				imgValues[row][col] = (int) (  (curColor.getRed() + curColor.getGreen() + curColor.getBlue()) / 3.0  + (765.0/50.0)*(brightness-50) + 0.5);
				
				if      (imgValues[row][col] > 255) imgValues[row][col] = 255;
				else if (imgValues[row][col] < 0)   imgValues[row][col] = 0;
				
			}
		}
	}
	
	private void monoToImg() {
		
		BufferedImage previewBfImg = new BufferedImage(imgWidth, imgHeight, BufferedImage.TYPE_INT_RGB);
		
		for (int row = 0; row < imgHeight; row++) {
			
			for (int col = 0; col < imgWidth; col++) { 
				
				//System.out.println("Row:" + row + " | " + "Col:" + col);
				
				if (monoImg[row][col] == 1) previewBfImg.setRGB(col, row, Color.BLACK.getRGB());
				else 					    previewBfImg.setRGB(col, row, Color.WHITE.getRGB());
				
			}
		}
		
		try {
			ImageIO.write(previewBfImg, "png", new File("./previewImg.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}
	
	private void monoToTxt() {
		
		BufferedWriter bw = null;
		
		try {
			
			File imgDataFile = new File("./imgData.txt");
			
			if (!imgDataFile.exists()) {
				imgDataFile.createNewFile();
			}
			
			bw = new BufferedWriter(new FileWriter(imgDataFile));
			
			for (int row = 0; row < imgHeight; row++) {
				
				for (int col = 0; col < imgWidth; col++) { 	
					bw.write(String.valueOf(monoImg[row][col]));
				}
				bw.newLine();
				
			}
			
		} catch (IOException e) {
			
			e.printStackTrace();
			
		} finally {
			
			if(bw!=null) {
				
				try {
					bw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				
			}
		}
		
		
		
		
	}
	
	
	
	public void generatePreviewImage(int dith, int brightness) {
		
		generateImgValues(brightness);
		
		if (dith == 0 || dith > 1) dither(dith);
		else randomizedThreshold();

		monoToImg();
		
	}
	
	public void generateImgDataFile(int dith, int brightness) {
		
		generateImgValues(brightness);
		
		if (dith == 0 || dith > 1) dither(dith);
		else randomizedThreshold();
		
		monoToTxt();
		
	}
	
	
	
	
	
	
	
	
	

}
