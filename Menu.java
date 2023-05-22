import java.util.Scanner;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.io.IOException;
import java.io.File;

import java.beans.Transient;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.BoxLayout;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.AbstractAction;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.JFileChooser;
import javax.imageio.ImageIO;

public class Menu extends JPanel{

	// instance variables
	private JPanel button_panel;
	private static JFrame thisFrame;

	// set up the starting menu
	public Menu() throws IOException{
		JButton play = new JButton("Play");
		button_panel = new JPanel();
		button_panel.add(play);
		button_panel.add(Box.createVerticalStrut(200));

		// If they click play have them enter a size of side
		play.addActionListener(new AbstractAction("play") {
			@Override
			public void actionPerformed(ActionEvent e) {
				button_panel.removeAll();
				button_panel.repaint();
				TextField newText = new TextField("Input size of side:");
				JButton enter = new JButton("Enter");
				button_panel.add(newText);
				button_panel.add(enter);

				// If they click enter get their size and then have them enter a file, setting up the CGOL based off of it
				enter.addActionListener(new AbstractAction("enter") {
					@Override
					public void actionPerformed(ActionEvent e){

						int size = Integer.parseInt(newText.getText().substring(19))*4;
						ConwayGameOfLife_Paris cgol;
						try {
						  	File imgFile = chooseFile(size);
						  	cgol = new ConwayGameOfLife_Paris(size, imgFile);
							thisFrame.dispose();
							JFrame frame = new JFrame();
							frame.getContentPane().add(cgol);
							frame.pack();
							frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
							frame.setLocationByPlatform(true);
							frame.setVisible(true);
							new Timer(100, new ActionListener() {
								@Override
								public void actionPerformed(ActionEvent e) {
									cgol.nextGeneration();
									cgol.repaint();
								}
							}).start();
						}
						catch(IOException v) {
						  v.printStackTrace();
						}
					}

				});
				button_panel.revalidate();
				button_panel.repaint();
			}
		});
		play.setAlignmentX(Component.CENTER_ALIGNMENT);
		button_panel.setLayout(new BoxLayout(button_panel, BoxLayout.Y_AXIS));
		setLayout(new BorderLayout());
		add(button_panel, BorderLayout.PAGE_END);
	}



	// Set how big the box should be for the starting menu
	@Override
	@Transient
	public Dimension getPreferredSize() {
		return new Dimension(1366, 625);
    }

	// Change how its painted for a simple title
	@Override
	protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setFont(new Font("TimesRoman", Font.PLAIN, 50));
		g.drawString("Welcome To", 568, 100);
		g.drawString("Conway's Game Of Life", 446, 200);

	}

	// Have them choose a file and not let it accept it until it is the right side
	public File chooseFile(int size) throws IOException{
		int imgwidth, imgheight;
		JFileChooser chooser = new JFileChooser();
		FileNameExtensionFilter filter = new FileNameExtensionFilter(
			"JPG & PNG Images", "jpg", "png");
		chooser.setFileFilter(filter);
		int returnVal;
		do
		{
			returnVal = chooser.showOpenDialog(chooser.getParent());
			if(returnVal == JFileChooser.APPROVE_OPTION) {
			System.out.println("You chose to open this file: " +
				chooser.getSelectedFile().getName());
			}
			BufferedImage bimg = ImageIO.read(new File(chooser.getSelectedFile().getAbsolutePath()));
			imgwidth = bimg.getWidth();
			imgheight = bimg.getHeight();
		}
		while ((imgwidth != size/4 || imgheight != size/4) && returnVal != JFileChooser.CANCEL_OPTION);
		return chooser.getSelectedFile();
	}

	// Start up the menu
	public static void main(String[] args) throws IOException{
		Menu menu = new Menu();
		thisFrame = new JFrame();
		thisFrame.getContentPane().add(menu, BorderLayout.PAGE_START);
		thisFrame.pack();
		thisFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		thisFrame.setLocationByPlatform(true);
		thisFrame.setVisible(true);
		menu.repaint();

	}

}