//Cameron Short
//CS451 2019F Senior Project - Image/Video Filter

import java.awt.image.BufferedImage;

//JavaDoc
/**
 * The ImageFilter interface provides functions implemented in different filter 
 * classes. Each filter implements this interface and is used in conjunction with
 * the ImageModifier class 
 * @author Cameron Short
 */

public interface ImageFilter 
{
	/**
	 * The filter function does work on the BufferedImage passed to it and returns a 
	 * 	copy of the newly filtered image 
	 * @param imageToFilter the file that is being modified
	 * @return the modified buffer image
	 */
	//Function that does the work for each implementing filter class
	BufferedImage filter(BufferedImage imageToFilter);
	/**
	 * Returns the unique name that the implementing filter class has. This is used in ImageModifier
	 * 	to create the output name of the final output file if one is not given 
	 * @return the name of the filter
	 */
	//Gets the name of the implementing filter class for ImageModifier
	String getFilterName();
}
