import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

public class TestExecute 
{
	public static void main(String[] args) throws IOException
	{
		//Setting up command args for the three programs
		/*
		String[] ripper = {"10minAlice.mp4", "RipperTest\\AliceFrames", "RipperTest\\AliceAudio"};
		String[] filter = {ripper[1], "FilterTest\\AliceFilteredFrames"};
		String[] recorder = {filter[1], ripper[2], "RecorderTest\\Alice"}; 
		*/
		String[] splitter = {"alice.mp4", "60", "SplitterTest\\Alice"}; 
		
		//Creating a new file to record the start/end time
		File time = new File ("AliceSplitTime.txt"); 
		if (!time.exists()) time.createNewFile();
		else { time.delete(); time.createNewFile(); }
		
		//Print out start time
		PrintWriter pw = new PrintWriter(time); 
		pw.write(java.time.LocalDateTime.now().toString()+"\n");
		

		//Processing and Filtering the video
		/*
		Ripper.main(ripper);
		Filter.main(filter);
		Recorder.main(recorder);
		*/
		Splitter.main(splitter);
		
		
		
		//Print out end time
		pw.write(java.time.LocalDateTime.now().toString());
		pw.close();
	}
}
