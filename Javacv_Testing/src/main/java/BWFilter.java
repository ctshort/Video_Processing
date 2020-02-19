//Cameron Short
//CS451 Senior Project - Image/Video Filter

import java.awt.image.BufferedImage;
import java.awt.Color;

/**
 * The BWFilter class is meant to take an input image and convert it to Black and White using 
 * 	the weighted average method.
 * @author Cameron Short
 */
public class BWFilter implements ImageFilter 
{
	//Default Ctor- sets filterName to default "BW"
	/**
	 * Default constructor that sets filterName to default - "BW"
	 */
	public BWFilter()
	{
		filterName = "BW";
	}
	//Naming Ctor- set filterName to whatever user passes in for *String name* parameter 
	/**
	 * Constructor that sets filterName to new value instead of leaving the default
	 * @param name the custom name for the filter
	 */
	public BWFilter(String name)
	{
		filterName = name;
	}
	
	/**
	 * {@inheritDoc} Converts the BufferedImage parameter "imageToFilter" to Black and White 
	 * using the Weighted average method and returns a BufferedImage as the newly filtered image
	 */
	@Override
	public BufferedImage filter(BufferedImage imageToFilter) 
	{
		final int height = imageToFilter.getHeight();
		final int width = imageToFilter.getWidth();
		
		Color c = null;
		double r = 0;	//red
		double g = 0; 	//green
		double b = 0;	//blue
		
		for (int h = 0; h < height; ++h)
			for (int w = 0; w < width; ++w)
			{
				//Saves the r,g,b values of each pixel separately
				c = new Color(imageToFilter.getRGB(w, h));
				r = (c.getRed() * RED_WEIGHT);			//Also weights the colors 
				g = (c.getGreen() * GREEN_WEIGHT);			//	according to the wave lengths as described in 
				b = (c.getBlue() * BLUE_WEIGHT);			//	https://www.tutorialspoint.com/dip/grayscale_to_rgb_conversion.htm
													// and https://www.tutorialspoint.com/java_dip/grayscale_conversion.htm
				
				//Calculates the average of the color
				double sum = r+g+b;
				Color newC = new Color((int)sum,(int)sum,(int)sum);
				imageToFilter.setRGB(w,h,newC.getRGB());
			}
		
		return imageToFilter;
	}
	//Returns the value of filterName property 
	@Override
	public String getFilterName()
	{
		return filterName;
	}
	
	//Value for ImageModifier so it can name outputs with abbreviations for each filter used
	//	this is the name for the Black&White filter->it is fetched from ImageModifier when used
	//	and appended to the output file's name
	/**
	 * The name of the filter, whether it be custom set or the default name 
	 */
	private String filterName;
	
	//Weights for the colors provided by https://www.tutorialspoint.com/dip/grayscale_to_rgb_conversion.htm
	//	values used in the "filter" method
	private final double RED_WEIGHT = 0.299; 
	private final double GREEN_WEIGHT = 0.587;
	private final double BLUE_WEIGHT = 0.114;
}