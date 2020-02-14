//Cameron Short
//CS451 2019F Senior Project - Image/Video Filter
//Class handling the adding and execution of filters

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.imageio.ImageIO;

/**
 * ImageModifier keeps track of all filters added to a list and executes them, in order, on given input files. 
 * A copy of the image is then modified and written to output, whether given or automatically generated
 * based on the input file. 
 * @author Cameron Short
 */
public class ImageModifier 
{
	/**
	 * Default Constructor for the ImageModifier class
	 */
	//Default Ctor
	public ImageModifier()
	{
		filterList = new LinkedList<ImageFilter>();
	}
		
	
	//Methods
	//
	/**
	 * Adds the parameter to the class' list of filters 
	 * @param f the filter to be added to the list of filters
	 */
	//Add f to the ordered list of filters user wants to run
	public void addFilter(ImageFilter f)
	{
		filterList.add(f);
	}
	/**
	 * Executes each filter on the given input image
	 * @param input the name of the input file
	 */
	//Running each filter on the input image (without altering input)
	public void runFilters(String input)
	{
		//If output is undefined -> create a name and location for it
		File out = new File(createOutputName(input));
			
		try 
		{ 	//Attempt to run filters on copy of input and write output to file
			BufferedImage imgInput = ImageIO.read(new File(input));		//Initialize input
			BufferedImage imgOutput = imgInput;							//Output is copy of input that is to be modified 
			
			for(ImageFilter filter : filterList)						//Running each filter in the list
				imgOutput = filter.filter(imgOutput);					//	that saves changes to the output copy
			
			ImageIO.write(imgOutput, "png", out);			//Writing the changes to output file 
		}
		catch (IOException e) 
		{ 	//Tell user something went wrong and die
			System.out.println("Failure to run filters on image"); 
			System.exit(-1);
		}
	}
	/**
	 * Executes each filter on the given input image
	 * @param input the name of the input file
	 * @param output the name of the output file
	 */
	//Running each filter on the input image (without altering input)
	public void runFilters(String input, String output)
	{
		try 
		{ 	//Attempt to run filters on copy of input and write output to file
			BufferedImage imgInput = ImageIO.read(new File(input));		//Initialize input
			BufferedImage imgOutput = imgInput;							//Output is copy of input that is to be modified 
			
			for(ImageFilter filter : filterList)						//Running each filter in the list
				imgOutput = filter.filter(imgOutput);					//	that saves changes to the output copy
			
			ImageIO.write(imgOutput, "png", new File(output));			//Writing the changes to output file 
		}
		catch (IOException e) 
		{ 	//Tell user something went wrong and die
			System.out.println("Failure to run filters on image"); 
			System.exit(-1);
		}
	}
	/**
	 * Empties the list of filters for new filters to be added in different order, 
	 */
	//Creates a new empty list erasing any old saved filters 
	public void clearFilters()
	{
		filterList.clear();
	}
	/**
	 * Creates a name for the output file based on the filters used to create the output image
	 * @param in the input file that used to generate a name for the output file
	 */
	//Generates a name for the output file based on the original + any filters' names
	private String createOutputName(String in)
	{
		//Initializing vars
		String outputName = "";
		
		//Copying the first part of the input's name
		for(char s : in.toCharArray())
		{
			//If the period (designating the beginning of the file type) hasn't been reached
			if (s != '.') outputName += s;		//Add the next character of the input's name
			else 
			{	//If the period has been reached (we don't want to rename the output the same as the input
				outputName += "Filtered"; break;	//Distinguish the outputName from the inputName
			}
		}
		
		//Make the new outputName
		for(ImageFilter filter : filterList)
			outputName += "-" + filter.getFilterName(); 	//Add each used filter's name to the output's name (i.e. - "...Filtered-BW-ASCII...")
		
		//Append the desired file type suffix (.png)
		outputName += ".png";		
		
		//Returning the generated name 
		return outputName;
	}
	
	
	//Properties
	private List<ImageFilter> filterList;		//List containing each filter that user desires to run on input
}
