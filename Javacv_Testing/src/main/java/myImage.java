//Class to simplify pairing the filename of a chosen image with the image itself 

import java.awt.Image;

public class myImage 
{
	//Ctor(s)
	public myImage(Image theImage, String theFile)
	{
		img = theImage;
		filename = theFile;
	}
	
	//Methods
	public Image getImage() { return img; }
	public String getFileName() { return filename; }
	
	//Properties
	private Image img;
	private String filename;
}
