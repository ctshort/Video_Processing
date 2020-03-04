import java.io.File;

public class Filter 
{
	public static void main(String[] args) 
	{
		//Checking for proper usage
		checkProperArgs(args); 
		
		//Creating any necessary dirs
		File out = new File(args[1]); 
		if (!out.exists()) out.mkdirs();
		
		//Get list of all the frames broken into chosen Directory
		File dir = new File(args[0]);
        File[] files = dir.listFiles();
 
        //Set filters 
        BWFilter bw = new BWFilter(); 
        ASCIIFilter ascii = new ASCIIFilter(); 
        
		//Initiating instance of ImageModifier to modify input images using filters
		ImageModifier myMod = new ImageModifier();
		
		//Adding filters to ImageModifier's list of current filters
		myMod.addFilter(bw);
		myMod.addFilter(ascii);
		
		//Counter to keep track of the frames converted
		int imagesFiltered = 0; 
        
        for (File file : files)
        {
            myMod.runFilters(args[0]+"\\frame"+imagesFiltered+".png", args[1]+"\\filteredFrame"+(imagesFiltered++)+".png");
        }
        
        System.out.println("DONE"); 
	}

	private static void checkProperArgs(String[] args)
	{
		if (args.length != 2)
		{
			System.err.println("Correct Usage: Filter <dir_of_frames_to_filter> <dir_to_output_filtered_frames>"); 
			System.exit(-1); 
		}
	}
}
