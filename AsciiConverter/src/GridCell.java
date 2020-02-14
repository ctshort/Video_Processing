//Cameron Short
//CS451 2019F 10/7/19
//Simple class that is meant to just provdie a link to
//	what cell of the imagined grid the score goes with
//	Essentially this is a key value pairing data structure

import java.awt.Image;


public class GridCell 
{	
	//Ctor
	GridCell(int pixelCount, int x, int y)					
	{
		score = pixelCount;
		pixelStartX = x;
		pixelStartY = y;
	}
	
	private int score, pixelStartX, pixelStartY;	
	
	//Simple getter functions
	public Integer GetScore()
	{
		return score;									//Score is the count of black pixels in the grid cell
	}
	public Integer GetX()
	{
		return pixelStartX;								//pixelStartX is the value of where the x coordinate of the origin of the grid cell is (top left corner of cell)
	}
	public Integer GetY()
	{
		return pixelStartY;								//pixelStartY is tandem with pixelX and gives the y coordinate of origin
	}
}
