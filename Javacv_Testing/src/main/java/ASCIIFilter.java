//Cameron Short
//CS451 Senior Project - Image/Video Filter

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

/**
 * The ASCII Filter is meant to take an image and process the image into a multitude of grid blocks. 
 * 	Each block's level of darkness is calculated and matched to a generated char image of proportional
 * 	darkness. Then each block of the original picture is replaced with the matched char image to create
 * 	an ASCII image. 
 * 
 * The filter method, inherited from ImageFilter, does most of the work for this class. All other functions 
 * are simple helper functions to assist filter to complete the image editing. 
 *  
 * @author Cameron Short
 */
public class ASCIIFilter implements ImageFilter
{
	/**
	 * Default constructor that sets filterName to the default name - "ASCII"
	 */
	public ASCIIFilter()
	{
		filterName = "ASCII";
		generateImageSets(ALPHABET.toCharArray(), SIZE); 
	}
	/**
	 * Constructor that sets the private field filterName 
	 * @param filter custom name for the filter instead of the default 
	 */
	public ASCIIFilter(String filter)
	{
		filterName = filter;
		generateImageSets(ALPHABET.toCharArray(), SIZE); 
	}
	
	private String filterName;
	
	
	//Useful costants in helper functions and filter
	/**
	 * {@value #NUM_COLORS} represents the count of (RGB -> red, green, blue)
	 * {@value #SPACE} the char space -> ' '
	 * {@value #CAPITAL_START} char of whatever the start of the capital portion of the alphabet is for 
	 * 						   the alpha-numeric image set. Used in the char image generation and sorting
	 * {@value #CAPITAL_FIN} char of whatever the end of the capital portion of the alphabet is for the 
	 * 						 alpha-numeric image set. Used in the char image generation and sorting
	 * {@value #LOWERCASE_START} char of whatever the start of the lowercase /portion of the alphabet is 
	 * 							 for the alpha-numeric image set. Used in the char image generation and sorting
	 * {@value #LOWERCASE_FIN} char of whatever the end of the lowercase portion of the alphabet is for the 
	 * 						   alpha-numeric image set. Used in the char image generation and sorting
	 * {@value #FONT_NAME} the name of the font for the alpha-numeric char images
	 * {@value #FONT_STYLE} the type of font, eg. Plain font, Bold font, Italic font etc.
	 * {@value #FONT_SIZE} the starting font size. This value is used in generating the char images as the starting 
	 * 					   point to find the correct font size to generate a blocksize of 50
	 * {@value #DESCENDER_LETTERS} special string of letters containing the letters in the alphabet that descend below the baseline
	 * 							   of the font
	 * {@value #LARGE_LETTERS} special string of letters containing letters that are larger or wider than others and 
	 * 						   subsequently cannot be printed on the same center as other letters
	 * {@value #COLOR_LIMIT} value of the highest R/G/B value, eg. 255 for pure R/G/B
	 * {@value #SIZE} the size of the alpha-numeric image blocks
	 * {@value #ALPHABET} string containing all characters used in creating the alpha-numeric char images
	 */
	//For rgbValueForBlock
	static final int NUM_COLORS = 3;
	//For alphaNumImgGen/Sort
	static final char 	SPACE = ' ', 
						CAPITAL_START = 'A', 
						CAPITAL_FIN = 'Z', 
						LOWERCASE_START = 'a', 
						LOWERCASE_FIN = 'z';
	//For alphaNumImgGen
	static final String FONT_NAME = "Arial"; 
	static final int FONT_STYLE = Font.PLAIN;
	static final int FONT_SIZE = 36;
	//For createSpecialCharsList
	static final String DESCENDER_LETTERS = "gjpqy";
	static final String LARGE_LETTERS = "MQW";
	//For ...
	static final int COLOR_LIMIT = 255;
	static final int SIZE = 6;
	static final String ALPHABET = "@#%&+= 0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
	
	//Functions
	//
	//
	//
	/**
	 * Sorts the hashmap from greatest to least and returns an ordered map
	 * @param hm the hash map to be sorted
	 * @return returns a linked hash map that can be iterated through 
	 */
	public static Map<String, Integer> sortByValue(Map<String, Integer> hm) 
    { 
        // Create a list from elements of HashMap 
        List<Map.Entry<String, Integer> > list = new LinkedList<Map.Entry<String, Integer> >(hm.entrySet()); 
  
        // Sort the list 
        Collections.sort(list, new Comparator<Map.Entry<String, Integer> >() 
        { 
        	//Writing comparator for Collections.sort
            public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) 
            { 
            	//Comparing greatest to least 
                return (o2.getValue()).compareTo(o1.getValue()); 
            } 
        }); 
          
        // put data from sorted list to hashmap  
        HashMap<String, Integer> temp = new LinkedHashMap<String, Integer>(); 
        for (Map.Entry<String, Integer> aa : list) 
            temp.put(aa.getKey(), aa.getValue()); 
        return temp; 
    }
    /**
     * Calculates the sum of red, green, and blue values for the pixel (w, h) in img
     * @param img the image from which the pixel is taken
     * @param w the X coordinate of the pixel
     * @param h the Y coordinate of the pixel
     * @return returns INT value of R+G+B
     */
	private static int getRGBSum(BufferedImage img, int w, int h)
    {
    	Color color = null;
    	color = new Color(img.getRGB(w, h));		//Get the color of the pixel
		int r = color.getRed();							//	Get just the red
		int g = color.getGreen();						//	Get just the green
		int b = color.getBlue();						//	Get just the blue
		
		return (r+g+b);
    }
	/**
     * Calculates an average rgb dScore based on the bounds passed in representing the gridcell's boundaries
     * @param img the image from which the grid is taken
     * @param wStart the X coordinate of the starting coordinate or the leftmost X
     * @param hStart the Y coordinate of the starting coordinate or the uppermost Y
     * @param wStop the X coordinate that the gridcell bounds should end at or the rightmost X
     * @param hStop the Y coordinate that the gridcell bounds should end at or the lowermost Y
     * @return the average dScore for the gridcell that tells how dark that block of the image is
     */
	private static int rgbValueForBlock(BufferedImage img, int wStart, int hStart, int wStop, int hStop) 
    {
		//Initializing some useful vars
		int rgbSum = 0, rgbTotal = 0;
    	
		for(int h = hStart; h < hStop; ++h)
		{
			for (int w = wStart; w < wStop; ++w)
			{
				rgbSum = getRGBSum(img, w, h);
				rgbTotal += rgbSum;
			}
		}
		int wSize = wStop - wStart;							//Calculate the length of X traversed right 
		int hSize = hStop - hStart;							//Calculate the length of Y traversed down 
		return (rgbTotal / (wSize*hSize)) / NUM_COLORS;		//Return the dScore -> wSize*hSize gets the total number of pixels 
    }
	/**
	 * Sorts all the char images used to replace blocks of color with chars from *alphabet*. Sorts the char images based on 
	 * 	dScore of char least to greatest (ie darkest to lightest)
	 * @param alphabet contains all of the characters the images represent
	 * @param size the height of the square char images
	 * @return an array implementation list of the char images, greatest to least, based on the dScores of each image
	 */
	public static List<Pair<String, Integer>> alphaNumImgSort(char[] alphabet, int size)
    {
		//Data Structure to conveniently store Score Key, file name Value pairs
		List<Pair<String, Integer>> listImgSet = new ArrayList<Pair<String,Integer>>(alphabet.length); 
		String fileName = null;	int score = 0;
		
		//For every char image in alphabet -> should be: 1:1
		for (char x : alphabet)
		{
			try
			{
				//If x is blank (space)
				if (x == SPACE) 										fileName = "resources\\BlankSpace.png"; 
				//If x is in the upper case
				else if (x >= CAPITAL_START && x <= CAPITAL_FIN) 		fileName = "resources\\Uppercase-" + x + ".png";
				//If x is in the lower case
				else if (x >= LOWERCASE_START && x <= LOWERCASE_FIN) 	fileName = "resources\\Lowercase-" + x + ".png";
				//Else x is a number
				else 													fileName = "resources\\" + x + ".png";
				
				//
				BufferedImage img = ImageIO.read(new File(fileName));
				
				//Getting the score and adding it to the list
				score = rgbValueForBlock(img, 0, 0, size, size);		//Getting the score
				listImgSet.add(new Pair(fileName, score));	//Adding score to list
				score = 0;												//Resetting score to 0 for next time 
			}
			catch (IOException e) 
			{
				//If something went wrong, tell user and die
				System.out.println("Failure to sort char images");
				System.err.print(e);
				System.exit(-1);
			}
		}
		
		//Sorting least to greatest 
        Collections.sort(listImgSet, new Comparator<Map.Entry<String, Integer> >() 
        { 	//Writing comparator for Collections.sort
            public int compare(Map.Entry<String, Integer> o1,  
                               Map.Entry<String, Integer> o2) 
            { 
                return (o1.getValue()).compareTo(o2.getValue()); 
            } 
        }); 

        //Returning list of sorted char images 
		return listImgSet;
    }
	private static List<Pair<String, Integer>> sortCharImages(char[] alphabet, int size, String[] prefixes)
	{
		//Data Structure to conveniently store Score Key, file name Value pairs
				List<Pair<String, Integer>> listImgSet = new ArrayList<Pair<String,Integer>>(alphabet.length); 
				String fileName = null;	int score = 0;
				
				//For every char image in alphabet -> should be: 1:1
				for (String s : prefixes)
				{
					for (char x : alphabet)
					{
						try
						{
							//If x is blank (space)
							if (x == SPACE) 										fileName = "resources\\BlankSpace.png"; 
							//If x is in the upper case
							else if (x >= CAPITAL_START && x <= CAPITAL_FIN) 		fileName = "resources\\Uppercase-" + s + x + ".png";
							//If x is in the lower case
							else if (x >= LOWERCASE_START && x <= LOWERCASE_FIN) 	fileName = "resources\\Lowercase-" + s + x + ".png";
							//Else x is a number
							else 													fileName = "resources\\" + s + x + ".png";
							
							//
							BufferedImage img = ImageIO.read(new File(fileName));
							
							//Getting the score and adding it to the list
							score = rgbValueForBlock(img, 0, 0, size, size);		//Getting the score
							listImgSet.add(new Pair(fileName, score));	//Adding score to list
							score = 0;												//Resetting score to 0 for next time 
						}
						catch (IOException e) 
						{
							//If something went wrong, tell user and die
							System.out.println("Failure to sort char images");
							System.err.print(e);
							System.exit(-1);
						}
					}
				}
				
				//Adding BLANKBLOCK to the list
				/*try 
				{
					fileName = "resources\\BlankBlock.png";
					BufferedImage i = ImageIO.read(new File(fileName)); 
					score = rgbValueForBlock(i, 0, 0, size, size);
					listImgSet.add(new Pair(fileName, score));	//Adding score to list
				}
				catch (IOException e) 
				{
					//If something went wrong, tell user and die
					System.out.println("Failure to sort char images");
					System.err.print(e);
					System.exit(-1);
				}*/
				 
				
				
				//Sorting least to greatest 
		        Collections.sort(listImgSet, new Comparator<Map.Entry<String, Integer> >() 
		        { 	//Writing comparator for Collections.sort
		            public int compare(Map.Entry<String, Integer> o1,  
		                               Map.Entry<String, Integer> o2) 
		            { 
		                return (o1.getValue()).compareTo(o2.getValue()); 
		            } 
		        }); 
		        
		        
		        
		        
		        //Printing the list to external file for viewing 
		     // print the sorted list to external text file
				File output = new File("Sort_Log.txt");
				try
				{
					output.delete();
					if (output.createNewFile())
					{
						PrintWriter writer = new PrintWriter(output);
						for (int i = 0; i < listImgSet.size(); ++i)
						{
							writer.print(i + "-- " + listImgSet.get(i).getKey() + ": " + listImgSet.get(i).getValue() + "\n");
						}
				        writer.close();
					}
				}
				catch (IOException e) 
				{	//If something went wrong, tell user and die
					System.out.println("Failure to sort char images");
					System.err.print(e);
					System.exit(-1);					
				}
				
		        
		        //Returning list of sorted char images 
				return listImgSet;
	}
	
	/**
	 * Scales the given image to square *scale* dimensions
	 * @param scaleImage the image to be scaled down or up
	 * @param scale the dimensions of the new square, scaled image
	 * @return the scaled image
	 */
	public static BufferedImage scale(BufferedImage scaleImage, int scale) 
	{
		//Initializing blank BufferedImage with dimensions scaleWidth/scaleHeight
        BufferedImage newImage = new BufferedImage(scale, scale, BufferedImage.TYPE_INT_ARGB);
        
    	Graphics2D g = newImage.createGraphics();												//Creating graphics of new image to perform drawing operation
    	g.drawImage(scaleImage,  0,  0,  newImage.getWidth(),  newImage.getHeight(), null);		//Drawing old image to smaller newImage
    	g.dispose();																			//Cleanup

    	//Returning the new scaled image
    	return newImage;
    }
    /**
     * Simple function that generates a list of certain chars in *letters*
     * @param letters String containing all the letters desired to be inserted into the list
     * @return the list of all the characters
     */
	private static List<Character> createSpecialCharsList(String letters)
    {
		//Initializing the list to contain all chars from *letters*
    	List<Character> returnList = new ArrayList<Character>();
    	
    	for (char c : letters.toCharArray()) 
    	{	//For each char in *letters*
    		returnList.add(c);	//add it to the list 
    	}
    	
    	//Return the list of chars
    	return returnList;
    }
	
	
	
	/**
	 * Generates all the char images with *size* dimensions for each char in *alphabet*  
	 * @param alphabet contains all chars that will be used to generate the char images
	 * @param size the height of the square char image
	 */
	public static void alphaNumImgGen(char[] alphabet, int size) 
	{
		//Initializing some vars
		BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);		//Initializing img to create graphics instance
		Graphics2D g = img.createGraphics();											//Will use g to do some operations on the font and later, the image
		FontMetrics fMetrics = null; Font f = null;
		int blockSize = 0; int z = FONT_SIZE;
		int fontWidth=0, fontHeight=0, currCharWidth=0;
		
		//Automatically find correct font size to get 50x50 blocks
		while(blockSize<50)
		{	//Cylces through a bunch of different sizes until it finds a size of roughly 50
			f = new Font(FONT_NAME,FONT_STYLE, z++);					//Dynamically updating the font size until reaching block size of 50
			g.setFont(f);												//		updating the font
			fMetrics = g.getFontMetrics();								//		saving information to graphics in case this is final Font
			
			//Getting the max height of any char
			fontHeight = fMetrics.getAscent();//-fMetrics.getDescent();
			//Getting the Width
			for (int b = 0; b < alphabet.length; ++b)
			{	//Checks to see which char has the widest width
				//		Java doesn't provide helpful fonts like they do with height 
				currCharWidth = fMetrics.charsWidth(alphabet, b, 1);
				if (currCharWidth > fontWidth) fontWidth = currCharWidth;
			}			
			
			//Get the biggest value from width and height above 
			if (fontWidth > fontHeight) blockSize = fontWidth; 
			else blockSize = fontHeight;
		}
		
		//Drawing our AlphaNumeric Images
		for (char x : alphabet)
		{
			char[] c = {x};		//Char array only to print the char onto image using g.drawChars()
			img = new BufferedImage(blockSize, blockSize, BufferedImage.TYPE_INT_ARGB);	
			g = img.createGraphics();
			g.setColor(Color.BLACK);										//Image is created with black background
			g.fillRect(0,  0,  img.getWidth(),  img.getHeight());			//	so painting background white
			g.setFont(f);													//Setting the right font to get 50 block size
			fMetrics = g.getFontMetrics();
			g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
			g.setColor(Color.WHITE);										//Setting font color to white
			
			//Lists containing special or weird characters
			List<Character> descenderLetters = createSpecialCharsList(DESCENDER_LETTERS);
			List<Character> largeLetters = createSpecialCharsList(LARGE_LETTERS);
			
			//Drawing char images
			//
			//Adding some conditional statements to correctly draw some troublesome chars
			//		that look out of place if drawn by the default
			//
			//For letters that extend below baseline
			if (descenderLetters.contains(x))
					g.drawChars(c,  0,  1,  (img.getWidth()/8),  img.getHeight()-fMetrics.getDescent());
			//For letters that are too big to be printed in middle
			else if (largeLetters.contains(x))
					g.drawChars(c,  0,  1,  0,  img.getHeight()-(fMetrics.getDescent()/2));
			else 	g.drawChars(c,  0,  1,  (img.getWidth()/5),  img.getHeight()-(fMetrics.getDescent()/2));
			
			g.dispose();													//Garbage cleanup
			
			//Scaling down to desired size 
			img = scale(img, size);
			
			//Writing the written char to a tangible file 
			try
			{
				//If x is blank (space)
				if (x == SPACE) ImageIO.write(img, "png", new File("resources\\BlankSpace.png"));
				//If x is in the upper case
				else if (x >= CAPITAL_START && x <= CAPITAL_FIN) ImageIO.write(img, "png", new File("resources\\Uppercase-" + x + ".png"));
				//If x is in the lower case
				else if (x >= LOWERCASE_START && x <= LOWERCASE_FIN) ImageIO.write(img, "png", new File("resources\\Lowercase-" + x + ".png"));
				//Else X is one of the numbers
				else ImageIO.write(img, "png", new File("resources\\" + x + ".png"));
			}
			catch (IOException e) 
			{	//Tell the user there was a problem and die 
				System.out.println("Failure to create " + x + " char image");
				System.err.print(e);
				System.exit(-1);
			}
		}
	}
	private static void genCharImages(char[] alphabet, int size, Color bkgd, Color font, String prefix)
	{
		//Initializing some vars
		BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);		//Initializing img to create graphics instance
		Graphics2D g = img.createGraphics();											//Will use g to do some operations on the font and later, the image
		FontMetrics fMetrics = null; Font f = null;
		int blockSize = 0; int z = FONT_SIZE;
		int fontWidth=0, fontHeight=0, currCharWidth=0;
		
		//Automatically find correct font size to get 50x50 blocks
		while(blockSize<50)
		{	//Cylces through a bunch of different sizes until it finds a size of roughly 50
			f = new Font(FONT_NAME,FONT_STYLE, z++);					//Dynamically updating the font size until reaching block size of 50
			g.setFont(f);												//		updating the font
			fMetrics = g.getFontMetrics();								//		saving information to graphics in case this is final Font
			
			//Getting the max height of any char
			fontHeight = fMetrics.getAscent();//-fMetrics.getDescent();
			//Getting the Width
			for (int b = 0; b < alphabet.length; ++b)
			{	//Checks to see which char has the widest width
				//		Java doesn't provide helpful fonts like they do with height 
				currCharWidth = fMetrics.charsWidth(alphabet, b, 1);
				if (currCharWidth > fontWidth) fontWidth = currCharWidth;
			}			
			
			//Get the biggest value from width and height above 
			if (fontWidth > fontHeight) blockSize = fontWidth; 
			else blockSize = fontHeight;
		}
		
		//Creating pure white block now that bkgd color is black instead of white
		//Pre-setting some options for printing char on img
		/*char[] blank = {' '};		//Char array only to print the char onto image using g.drawChars()
		img = new BufferedImage(blockSize, blockSize, BufferedImage.TYPE_INT_ARGB);	
		g = img.createGraphics();
		g.setColor(Color.WHITE);										//Image is created with black background
		g.fillRect(0,  0,  img.getWidth(),  img.getHeight());			//	so painting background white
		g.setFont(f);													//Setting the right font to get 50 block size
		fMetrics = g.getFontMetrics();
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g.setColor(Color.WHITE);										//Setting font color to white
		
		//Drawing the char on img
		g.drawChars(blank,  0,  1,  (img.getWidth()/5),  img.getHeight()-(fMetrics.getDescent()/2));
		g.dispose();
		//Scaling down to desired size 
		img = scale(img, size);
		try { ImageIO.write(img, "png", new File("resources\\BlankBlock.png")); } 
		catch (IOException e)
		{ 	//Tell the user there was a problem and die 
			System.out.println("Failure to create BLANKBLOCK char image");
			System.err.print(e);
			System.exit(-1);			
		}*/
		
	
		//Drawing our AlphaNumeric Images
		for (char x : alphabet)
		{
			char[] c = {x};		//Char array only to print the char onto image using g.drawChars()
			img = new BufferedImage(blockSize, blockSize, BufferedImage.TYPE_INT_ARGB);	
			g = img.createGraphics();
			g.setColor(bkgd);										//Image is created with black background
			g.fillRect(0,  0,  img.getWidth(),  img.getHeight());			//	so painting background white
			g.setFont(f);													//Setting the right font to get 50 block size
			fMetrics = g.getFontMetrics();
			g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
			g.setColor(font);										//Setting font color to white
			
			//Lists containing special or weird characters
			List<Character> descenderLetters = createSpecialCharsList(DESCENDER_LETTERS);
			List<Character> largeLetters = createSpecialCharsList(LARGE_LETTERS);
			
			//Drawing char images
			//
			//Adding some conditional statements to correctly draw some troublesome chars
			//		that look out of place if drawn by the default
			//
			//For letters that extend below baseline
			if (descenderLetters.contains(x))
					g.drawChars(c,  0,  1,  (img.getWidth()/8),  img.getHeight()-fMetrics.getDescent());
			//For letters that are too big to be printed in middle
			else if (largeLetters.contains(x))
					g.drawChars(c,  0,  1,  0,  img.getHeight()-(fMetrics.getDescent()/2));
			else 	g.drawChars(c,  0,  1,  (img.getWidth()/5),  img.getHeight()-(fMetrics.getDescent()/2));
			
			g.dispose();													//Garbage cleanup
			
			//Scaling down to desired size 
			img = scale(img, size);
			
			//Writing the written char to a tangible file 
			try
			{
				//If x is blank (space)
				if (x == SPACE) ImageIO.write(img, "png", new File("resources\\BlankSpace.png"));
				//If x is in the upper case
				else if (x >= CAPITAL_START && x <= CAPITAL_FIN) ImageIO.write(img, "png", new File("resources\\Uppercase-" + prefix + x + ".png"));
				//If x is in the lower case
				else if (x >= LOWERCASE_START && x <= LOWERCASE_FIN) ImageIO.write(img, "png", new File("resources\\Lowercase-" + prefix + x + ".png"));
				//Else X is one of the numbers
				else ImageIO.write(img, "png", new File("resources\\" + prefix + x + ".png"));
			}
			catch (IOException e) 
			{	//Tell the user there was a problem and die 
				System.out.println("Failure to create " + prefix + x + " char image");
				System.err.print(e);
				System.exit(-1);
			}
		}
	}
	private static void generateImageSets(char[] alphabet, int size)
	{ 
		genCharImages(alphabet, size, Color.BLACK, Color.GRAY, "G");
		genCharImages(alphabet, size, Color.BLACK, Color.LIGHT_GRAY, "LG"); 
		genCharImages(alphabet, size, Color.BLACK, Color.WHITE, "W"); 
		genCharImages(alphabet, size, Color.GRAY, Color.BLACK, "B");
		genCharImages(alphabet, size, Color.DARK_GRAY, Color.BLACK, "BDG");
		genCharImages(alphabet, size, Color.LIGHT_GRAY, Color.BLACK, "BLG");
		genCharImages(alphabet, size, Color.GRAY, Color.BLACK, "BG");
		genCharImages(alphabet, size, Color.BLACK, Color.DARK_GRAY, "DG");
		genCharImages(alphabet, size, Color.LIGHT_GRAY, Color.WHITE, "WLG");
	}
	
	
	
	
	
	
	
	//Filter Code
	
	@Override
	public String getFilterName()
	{
		return filterName;
	}
	@Override
	public BufferedImage filter(BufferedImage imageToFilter) 
	{
		//Getting the grid from test picture
		int dScore = 0;
		int W_LIM = imageToFilter.getWidth(), H_LIM = imageToFilter.getHeight();
		List<GridCell> grid = new ArrayList<GridCell>();
		
		
		int wCellStart = 0, hCellStart = 0;
		int wCellLim = SIZE, hCellLim = SIZE;
		int gridLim = ((W_LIM / SIZE) * (H_LIM / SIZE));			//gridLim = total number of different *SIZE* big blocks
		for (int i = 0; i < gridLim; ++i)							//For each cell
		{
			//Getting the score for the current grid cell and adding it to the list 
			dScore = rgbValueForBlock(imageToFilter, wCellStart, hCellStart, wCellLim, hCellLim);
			grid.add(new GridCell(dScore, wCellStart, hCellStart));
			dScore = 0;												//Resetting score to 0 before next calculation of dScore
			
			//Move to next gridCell in row
			wCellStart += SIZE; 
			wCellLim += SIZE;
			if (wCellStart >= W_LIM)								//If reached the end of the row
			{
				wCellStart = 0; 									//	Go back to first column
				wCellLim = SIZE;									//	
				hCellStart += SIZE; 								//	Go down to next row
				hCellLim += SIZE;
			}
		}		


		//Creating alpha-numeric char images and sorting them 
		//alphaNumImgGen(ALPHABET.toCharArray(), SIZE);
		//generateImageSets(ALPHABET.toCharArray(), SIZE); 
				
		//List<Pair<String,Integer>> charImgSet = alphaNumImgSort(ALPHABET.toCharArray(), SIZE);
		String[] prefixes = new String[] {"DG", "G", "LG", "W", "B", "BDG", "BLG", "BG", "WLG"} ; 
		List<Pair<String,Integer>> charImgSet = sortCharImages(ALPHABET.toCharArray(), SIZE, prefixes);
		
	
		
		//Bucket code 
		//Normalize the dScores of letter images
		int minScore=charImgSet.get(0).getValue(), normalizedIndex=-1;		//Saves the value of the lowest dScore
		int maxScore = charImgSet.get(charImgSet.size()-1).getValue();		//Save the value of the highest dScore	-> both minScore and maxScore used in math 
																			//	for normalizedIndex calculation
		myImage[] buckets = new myImage[256];								//Data structure holding all the image buckets
		//Stretching the char images across 255 spaces
		for (Pair<String,Integer> pair : charImgSet)
		{ 
			//Getting the appropriate index to store images into buckets
			normalizedIndex = COLOR_LIMIT*(pair.getValue() - minScore) / (maxScore - minScore);
			
			//Store the image (and it's char image's filename) in the buckets
			try { buckets[normalizedIndex] = new myImage(ImageIO.read(new File(pair.getKey())), pair.getKey()); }
			catch (IOException e)
			{ 	//Tell user something went wrong and die
				System.out.println("Failure to fill image buckets"); 
				System.exit(-1);
			}
		}
		
		//Filling all the gaps in the buckets
		int bucketIndex = 0, nullIndex=-1, nullCount = 0; 
		int holeL = -1, holeR = -1;
		myImage Left=null, Right=null;
		while (bucketIndex < buckets.length)								//While you havent checked the entire array
		{
			if (buckets[bucketIndex] != null) Left = buckets[bucketIndex++];	//Save each new element as it could be the left of a hole
			else if (buckets[bucketIndex] == null)							//If you reach a hole
			{
				nullIndex = bucketIndex;										//Save the start of the hole
				while (buckets[nullIndex] == null)								//While you traverse the hole
				{
					++nullCount;														//Increment count
					++nullIndex;														//Increment index
				}
				Right = buckets[nullIndex];										//Save the image just past the hole
				if (nullCount % 2 != 0) buckets[bucketIndex++] = Left;				//If the count is odd go ahead and fill the current location
				holeL = nullCount / 2; holeR = nullCount / 2;					//Divide the hole in two 
				
				//Filling the hole with right and left
				for (int l = 0; l < holeL; ++l)									//Filling left half of hole
					buckets[bucketIndex++] = Left;  
				for (int r = 0; r < holeR; ++r)									//Filling right half of hole
					buckets[bucketIndex++] = Right;
				nullCount = 0;													//Resetting the nullCount
			}
		}
		
		//Drawing the new image
		BufferedImage imgToDraw = new BufferedImage(imageToFilter.getWidth(), imageToFilter.getHeight(), BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = imgToDraw.createGraphics();
		
		Map<String, Integer> imgNumLog = new HashMap<String, Integer>();
		String mapKey = null;
		//Attempting to draw new image one grid cell at a time
		int x, y, oldCharCount;
		int size = buckets[grid.get(0).GetScore()].getImage().getHeight(null);				//can use height because both height and width are same
		for(GridCell cell : grid)
		{
			//Drawing char to new image
			x = cell.GetX();
			y = cell.GetY();
			g2.drawImage(buckets[cell.GetScore()].getImage(), x, y, x+size, y+size, 0, 0, size, size, null);
			
			//Recording the count
			mapKey = buckets[cell.GetScore()].getFileName();
			if (imgNumLog.containsKey(mapKey))									//If it already exists in log
			{
				oldCharCount = imgNumLog.get(mapKey);
				imgNumLog.replace(mapKey, oldCharCount, ++oldCharCount);			//Update the information 
			}
			else imgNumLog.put(mapKey, 1);										//Otherwise store a count of 1 at least
		}
		
		
		//Writing a log for each char used 
		imgNumLog = sortByValue(imgNumLog); 	//Sorted by most used to least used
		int n = 1;													//Marks the line in the text file -> denotes which char is the 1st char or nth char listed
		
        // print the sorted TreeMap to external text file
		File output = new File("imgNumCount_Log_.txt");
		try
		{
			if(output.exists()) output.delete();					//Rewriting the file -> don't want any old stuff 
			if (output.createNewFile())								//Check to make sure successful creation of new file 
			{
				PrintWriter writer = new PrintWriter(output);
		        for (Map.Entry<String, Integer> hm : imgNumLog.entrySet()) 					//Write the counts to the log file 
		        { 
		        	writer.print(n + "- " + hm.getKey() + ": " + hm.getValue() + "\n");		//	Something like -- 1- "Char image Uppercase-G": 24500
		        	++n;
		        }
		        writer.close();
			}
			else System.out.println("File creation failed. Could not write to imgNumCount_Log file");
		}
		catch (IOException e) 
		{	//Tell user what went wrong and die 
			System.out.println(e.getMessage());
			System.exit(-1);
		}
		
		//Return the filtered image
		return imgToDraw;
	}
}