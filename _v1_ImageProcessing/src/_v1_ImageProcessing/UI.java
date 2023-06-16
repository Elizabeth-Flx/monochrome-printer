package _v1_ImageProcessing;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
//import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;

public class UI {
	
	//Image Processing & Data Transfer
	ImgProc imgProc = new ImgProc();
	JschConnection jschConnection = new JschConnection();
		
		
	//UI segments
	JFrame frame;
	
	JLabel dithLabel;	
	JComboBox<Object> dithChoser;
	
	JLabel fileLabel;
	JFileChooser 	  fileChoser;
	
	JLabel     brigtLabel;
	JSlider    brigtSlider;
	JTextField brigtValue;
	
	JButton	btnPreview;
	JButton btnPrint;
		

	
	public UI() {
		
		frame = new JFrame("Monochrome LEJOS Printer");
		
		//Choose Dithering Algorithm
		String[] dithTypes = {	"Threshhold", 
								"Randomized Threshold", 
								"1D Diffusion", 
								"2D Diffusion", 
								"Floyd Steinberg", 
								"Burkes", 
								"Jarvis, Judith & Ninke"};
		
		dithChoser = new JComboBox<Object>(dithTypes);
		dithLabel  = new JLabel("Select Dithering Algorithm");
		
		//File Chooser
		fileLabel  = new JLabel("Select Image to Print");
		fileChoser = new JFileChooser();
		
		//Brightness Slider
		brigtLabel  = new JLabel    ("Change Brightness");
		brigtSlider = new JSlider   (0, 100, 50);
		brigtValue  = new JTextField("50");
		
		//Preview & Print Buttons
		btnPreview = new JButton("Preview");
		btnPrint   = new JButton("Print");

		
		//File Chooser 
		fileLabel.setFont(new Font("Tahoma", Font.BOLD, 16));

		fileChoser.setFileSelectionMode      (JFileChooser.FILES_ONLY);
		fileChoser.setAcceptAllFileFilterUsed(false);
		fileChoser.addChoosableFileFilter    (new FileNameExtensionFilter("PNG, JPEG, JPG", "png", "jpg", "jpeg"));
		fileChoser.setControlButtonsAreShown (false);
		
		
		//Brightness Slider
		brigtValue.setHorizontalAlignment(JTextField.CENTER);
		
		brigtSlider.setMajorTickSpacing(10);
		brigtSlider.setMinorTickSpacing(1);
		brigtSlider.setPaintTicks (true);
		brigtSlider.setSnapToTicks(true);
		brigtSlider.setPaintLabels(true);
		
		brigtSlider.addChangeListener(
		new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent ce) {
				brigtValue.setText("" + brigtSlider.getValue());
			}
		});
		

		//Preview Button
		btnPreview.addActionListener(
		new ActionListener() {
			@Override
			//Preview image when button is pressed
			public void actionPerformed(ActionEvent e) {
				previewImage();
			}
		});
		
		
		//Print Button
		btnPrint.addActionListener(
		new ActionListener() {
			@Override
			//Print image when button is pressed
			public void actionPerformed(ActionEvent e) {
				printImage();
			}
		});
		
		
		
		//Add Items
		dithLabel.  setBounds (30,  15,  200, 20);
		dithChoser. setBounds (215, 15,  200, 20);
		fileLabel.  setBounds (150, 60,  450, 20);
		fileChoser. setBounds (50,  80,  400, 300);
		brigtLabel. setBounds (195, 395, 200, 20);
		brigtSlider.setBounds (50,  420, 400, 50);
		brigtValue. setBounds (15,  425, 30,  25);
		btnPreview. setBounds (15,  505, 100, 20);
		btnPrint.   setBounds (380, 505, 100, 20);
		
		frame.add(dithLabel);
		frame.add(dithChoser);
		frame.add(fileChoser);
		frame.add(fileLabel);
		frame.add(brigtLabel);
		frame.add(brigtSlider);
		frame.add(brigtValue);
		frame.add(btnPreview);
		frame.add(btnPrint);

		frame.setSize(500, 570);
		frame.setResizable(false);
		frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		
		frame.setLayout(null);
		frame.setVisible(true);
		
	}
	
	
	
	//Back-end
	
	private boolean isImageInvalid(File f) {
		
		//Error pop-up if user has not selected a file.
		if (f == null) {
			JOptionPane.showMessageDialog(frame,
				"Please choose a valid file.",
				"Error",
				JOptionPane.WARNING_MESSAGE);
			return true;
		}
				
				
		//Gets the file-type of the selected file
		String extension = "";
		int i = f.getAbsolutePath().lastIndexOf('.');
		if (i > 0) extension = f.getAbsolutePath().substring(i+1);
				
				
		//Error pop-up if user has not chosen a valid image file-type.
		if (!extension.equals("png")  && 
			!extension.equals("jpg")  && 
			!extension.equals("jpeg") && 
			!extension.equals("PNG")  && 
			!extension.equals("JPG")  && 
			!extension.equals("JPEG") ) 
		{
			JOptionPane.showMessageDialog(frame,
				"The filetype you chose is invalid. Please choose a png, jpg or jpeg file.",
				"Error",
				JOptionPane.WARNING_MESSAGE);
			return true;
		}
		return false;
	}
	
	
	
	private void previewImage() {
		
		int  index = dithChoser.getSelectedIndex();
		int  brightness = brigtSlider.getValue();
		
		File file = fileChoser.getSelectedFile();
		
		//Stops the preview process if the chosen image is invalid
		if (isImageInvalid(file)) return;
		
		imgProc.generateRescaledImage(file);
		imgProc.generatePreviewImage (index, brightness);
		
		
		//Pop-up with print preview
		JFrame imageFrame = new JFrame("Print Preview - " + dithChoser.getSelectedItem() + " Dithering");
		
		ImageIcon icon = new ImageIcon("./previewImg.png");
		icon.getImage().flush();
		JLabel label = new JLabel(icon);
		
		imageFrame.add(label);
		imageFrame.pack();
		imageFrame.setResizable(false);
		imageFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		imageFrame.setVisible(true);
		
	}
	
	private void printImage() {
		
		int  index = dithChoser.getSelectedIndex();
		int  brightness = brigtSlider.getValue();
		File file = fileChoser.getSelectedFile();
		
		//Stops the printing process if the chosen image is invalid
		if (isImageInvalid(file)) return;
		
		imgProc.generateRescaledImage(file);
		imgProc.generateImgDataFile  (index, brightness);
		
		System.out.println("Connecting...");
		
		//Send Data to Printer
		jschConnection.transferData();
		
		System.out.println("Printing...");
		
		//Run Printer
		jschConnection.runProgram();
		
	}
	
	
	
	
	
	

}
