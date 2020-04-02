import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

public class Sleep
{
	public static void main(String args[]) throws IOException, InterruptedException
	{
		checkProperArgs(args); 
		
		File inputFile = new File (args[0]);
		File outputFile = new File(args[1]); createFileIfNotExists(outputFile); 
		
		
		try (   FileReader fileReader = new FileReader (inputFile);
			    BufferedReader reader = new BufferedReader (fileReader); 
				PrintWriter writer = new PrintWriter(outputFile))
		{
			String x = reader.readLine();
			for(int i = Integer.parseInt(x); i > 0; --i)
			{
				writer.write(i+"...\n");
				Thread.sleep(1000);
			}
		}
	}
	private static void createFileIfNotExists(File f)
	{
		if(!f.exists()) 
		{
			try { f.createNewFile(); }
			catch (IOException e)
			{
				System.err.println("Failed to generate output file"); 
				System.exit(-1);
			}
		}
	}
	private static void checkProperArgs(String args[])
	{
		if (args.length != 2)
		{
			System.err.println("Correct Usage: Sleep <input_file> <output_file>");
			System.exit(-1);
		}
	}
	
}
