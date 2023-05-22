import java.util.Scanner;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.io.IOException;
import java.io.File;
import java.awt.image.BufferedImage;

import java.beans.Transient;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.imageio.ImageIO;

@SuppressWarnings("serial")
public class ConwayGameOfLife_Paris extends JPanel{

	// Instance variables
	private ArrayList<boolean[][]> allGrids = new ArrayList<boolean[][]>();
	private boolean[][] grid;
	private boolean[][] temp;
	private boolean pastCheck = false;
	private int generationCounter = 0;

	// Set up the grid and starting image
	public ConwayGameOfLife_Paris(int size, File imgFile) throws IOException{
		this.grid = new boolean[size/4][size/4];
		this.temp = new boolean[size/4][size/4];
		startingPosition(grid, imgFile);
	}

	// Set up where the grid has starts based off the image uploaded
	public void startingPosition(boolean[][] grid, File imgFile) throws IOException{
		BufferedImage image = ImageIO.read(new File(imgFile.getAbsolutePath()));
		for(int i = 0; i < grid.length; i++)
			for(int j = 0; j < grid[i].length; j++) {
				if (image.getRGB(i, j) ==0xFF000000){
					grid[i][j] = true;
					temp[i][j] = true;
				}
				else {
					grid[i][j] = false;
					temp[i][j] = false;
				}
			}
		allGrids.add(temp);
	}

	// Continue to the next generation
	public void nextGeneration() {
		boolean[][] future = new boolean[grid.length][grid[0].length];
		temp = new boolean[grid.length][grid[0].length];
		for (int i=0;i<grid.length;i++) {
			for (int j=0;j<grid[i].length;j++) {


				// Find number of alive neighbors
				int aliveNeighbors = 0;
				for (int lr = -1; lr <= 1; lr++)
					for (int ud = -1; ud <= 1; ud++)
						if ((lr+i>=0 && lr+i<grid.length) && (ud+j>=0 && ud+j<grid[0].length) && grid[i+lr][j+ud])
							aliveNeighbors++;
				if (grid[i][j])
					aliveNeighbors--;


				// Implement rules for changing the current cell
				if ((grid[i][j]) && (aliveNeighbors < 2))
					future[i][j] = false;
				else if ((grid[i][j]) && (aliveNeighbors > 3))
					future[i][j] = false;
				else if ((!grid[i][j]) && (aliveNeighbors == 3))
					future[i][j] = true;
				else
					future[i][j] = grid[i][j];

				temp[i][j] = future[i][j];
			}
		}
		grid = future;
		allGrids.add(temp);
	}


	// Get how big the screen should be
	@Override
    @Transient
    public Dimension getPreferredSize() {
        return new Dimension(grid.length * 4, grid[0].length * 4);
    }


	// change how the screen is painted
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Color gColor = g.getColor();

		// for each alive cell make it blue
		for (int i = 0; i < grid.length; i++)
			for (int j = 0; j < grid[i].length; j++)
				if (grid[i][j]) {
					g.setColor(Color.blue);
					g.fillRect(j * 4, i * 4, 4, 4);
				}

		// Change the patterns based off rules to find patterns
		paintPatterns(g);

		// for each dead cell make it black
		for (int i = 0; i < grid.length; i++) {
			for (int j = 0; j < grid[i].length; j++) {
				if (!grid[i][j]){
					g.setColor(Color.black);
					g.fillRect(j * 4, i * 4, 4, 4);
				}
			}
		}

		// add the generation count
		g.setColor(new Color(204, 204, 204));
        g.drawString("Generation: " + generationCounter++, 0, 10);
        g.setColor(gColor);

    }

	// paint specific patterns
	public void paintPatterns(Graphics g){

		for (int i=0;i<grid.length;i++) {
			for (int j=0;j<grid[i].length;j++) {

				// Find alive neighbors
				int aliveNeighbors = 0;
				for (int lr = -1; lr <= 1; lr++)
					for (int ud = -1; ud <= 1; ud++)
						if ((lr+i>=0 && lr+i<grid.length) && (ud+j>=0 && ud+j<grid[0].length) && grid[i+lr][j+ud])
							aliveNeighbors++;
				if (grid[i][j])
					aliveNeighbors--;

				// if the cell hasnt really changed has the same amount of neighbors (still-life) make it red
				if (generationCounter >= 4) {
					if (((allGrids.get(generationCounter)[i][j] && (aliveNeighbors == 2)) || (allGrids.get(generationCounter)[i][j] && (aliveNeighbors == 3))) && allGrids.get(generationCounter)[i][j] == allGrids.get(generationCounter-1)[i][j] == allGrids.get(generationCounter-2)[i][j] == allGrids.get(generationCounter-3)[i][j] == allGrids.get(generationCounter-4)[i][j]) {
						g.setColor(Color.red);
						g.fillRect(j * 4, i * 4, 4, 4);
					}


					// if the neighbors have changed set the color back to blue
					boolean check = true;
					for (int lr = -1; lr <= 1; lr++)
						for (int ud = -1; ud <= 1; ud++)
							if ((lr+i>=0 && lr+i<grid.length) && (ud+j>=0 && ud+j<grid[0].length) && grid[i+lr][j+ud])
								if (grid[i+lr][j+ud] != allGrids.get(generationCounter-1)[i+lr][j+ud])
									check = false;
					if (!check) {
						g.setColor(Color.blue);
						g.fillRect(j * 4, i * 4, 4, 4);
					}
				}

				// if the cell is part of an alternating pattern, paint it green
				if (generationCounter >= 3) {
					for (int lr = -1; lr <= 1; lr++)
						for (int ud = -1; ud <= 1; ud++)
							if ((lr+i>=0 && lr+i<grid.length) && (ud+j>=0 && ud+j<grid[0].length))
								if ((allGrids.get(generationCounter)[i+lr][j+ud] == allGrids.get(generationCounter-2)[i+lr][j+ud]) &&
									(allGrids.get(generationCounter)[i+lr][j+ud] != allGrids.get(generationCounter-1)[i+lr][j+ud]) &&
									((aliveNeighbors < 3 && allGrids.get(generationCounter)[i][j]) || (aliveNeighbors == 3 && !allGrids.get(generationCounter)[i][j])) &&
									((allGrids.get(generationCounter)[i][j]) && (allGrids.get(generationCounter)[i][j] == allGrids.get(generationCounter-1)[i][j] == allGrids.get(generationCounter-2)[i][j] == allGrids.get(generationCounter-3)[i][j]))){
										g.setColor(Color.green);
										g.fillRect(j * 4, i * 4, 4, 4);
										continue;
								}

					if ((j-1>=0 && j+1<grid.length) && grid[i][j]) {
						if (((allGrids.get(generationCounter)[i][j] == allGrids.get(generationCounter-2)[i][j]) &&
						   (allGrids.get(generationCounter)[i][j] != allGrids.get(generationCounter-1)[i][j])) &&
						   ((allGrids.get(generationCounter)[i][j] == allGrids.get(generationCounter-2)[i][j]) &&
						   (allGrids.get(generationCounter)[i][j] != allGrids.get(generationCounter-1)[i][j])))
							{

								g.setColor(Color.green);
								g.fillRect(j * 4, i * 4, 4, 4);
						}
					}
				}
			}

		}
	}

}