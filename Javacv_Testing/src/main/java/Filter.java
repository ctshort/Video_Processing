import java.io.File;

public class Filter 
{
	public static void main(String[] args) 
	{
		//Get list of all the frames broken into VideoBrokenFrames Directory
		File dir = new File("VideoBrokenFrames");
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
            myMod.runFilters("VideoBrokenFrames\\frame"+imagesFiltered+".png", "VideoFilteredFrames\\filteredFrame"+(imagesFiltered++)+".png");
        }
        
        System.out.println("DONE"); 
	}

}
