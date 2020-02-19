//Cameron Short
//CS451 2019F- Senior Project Image/Video Filter
//This file will be a main that adds filters and runs them using the ImageModifier class

/**
 * MainProgram runs code to show how ImageModifier works using a list of 
 * Filters provided through different instances of Filter Classes
 * @author Cameron Short
 */

public class MainProgram 
{
	public static void main(String[] args) 
	{
		//Initiating single instances of a filter(s) to pass to ImageModifier in order to 
		//	edit an existing image
		BWFilter myBW = new BWFilter();
		ASCIIFilter myASCII = new ASCIIFilter();
		
		//Initiating instance of ImageModifier to modify input images using filters
		ImageModifier myMod = new ImageModifier();
		
		//Adding filters to ImageModifier's list of current filters
		myMod.addFilter(myBW);
		myMod.addFilter(myASCII);
		
		//Execute all filters from ImageModifier's list
		myMod.runFilters("testImages\\pp.png", "outputRGB-BLEND.png");
		
		myMod.clearFilters();
		
		myMod.addFilter(myBW);
		
		myMod.runFilters("testImages\\tj.jpg", "outputTJ.png");
		
		//Easy way to see that the program is done 
		System.out.println("DONE");
	}
}