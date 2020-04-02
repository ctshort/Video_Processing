import java.io.File;

import javax.imageio.ImageIO;

import org.bytedeco.ffmpeg.global.avcodec;
import org.bytedeco.ffmpeg.global.avutil;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.FrameRecorder;

public class Splitter 
{
	public static void main(String[] args) 
	{
		//Ensuring proper usage of command-line args
		checkProperArgs(args);
		
		//Getting the first arg as int instead of string 
		int firstArg = getIntArg(args[1]);			//Assumed arg is num of seconds
		
		//Checking if directory exists
		File dir = new File(args[2]);
		if (!dir.exists())					//If the directory doesn't exist 
			dir.mkdirs(); 					//Create it and any other parent directories named in the command-line argument
		
		//Initializing grabbers and recorders 
		Frame vidFrame = new Frame();


		try (FFmpegFrameGrabber frameGrabber = new FFmpegFrameGrabber(new File(args[0])))
		{
			//(Re)Setting the grabber and recorder
			frameGrabber.start();
			FFmpegFrameRecorder rec = null; 
			
			boolean isFinished = false;
			int i = 0, vidFrameCounter = 0, totalFrames = frameGrabber.getLengthInVideoFrames();
			int rate = (int)frameGrabber.getFrameRate();
			while (!isFinished)
			{
				//Setting up the recorder 
				rec = new FFmpegFrameRecorder(args[2]+"\\subVid"+i+".mp4", frameGrabber.getImageWidth(), frameGrabber.getImageHeight(), 2);
				setRecSettings(rec, frameGrabber);
				rec.start(); 
				
				//Record the images and audio into new video file 
				//While the video hasn't recorded the command line arg length (in secs)
				while(vidFrameCounter < (firstArg*((i+1)*rate)) || 
						(vidFrameCounter > (totalFrames-rate) && vidFrameCounter < totalFrames))	//OR if there aren't enough frames to get to the next second
				{
					if ((vidFrame = frameGrabber.grab()) != null)
					{
						rec.record(vidFrame);
						if(vidFrame.imageHeight != 0 && vidFrame.imageWidth != 0)
							++vidFrameCounter;
					}
					else 
					{
						isFinished = true; break;					 
					}
				}
				
				//Closing recorder
				rec.stop(); rec.close(); 
				++i;
			}

			//Closing resources
			frameGrabber.stop(); frameGrabber.close(); 
		}
		catch (Exception e)
		{
			System.out.println("FAILURE: "+e+"\n"); 
			e.printStackTrace();
		}
	}
	
	private static void checkProperArgs(String[] args)
	{
		if (args.length != 3)
		{
			System.err.println("Correct usage: Splitter <file_to_split> <length_of_broken_videos> <directory_to_generate_videos>"); 
			System.exit(1);
		}
		else
		{
		    try { int value = Integer.parseInt(args[1]); }
		    catch (NumberFormatException e) 
		    {
		    	System.out.println(args[0]);
				System.err.println("-->Error: First argument must be an integer"); 
				System.exit(1);
		    }
		}
	}
	private static int getIntArg(String arg)
	{
		int value = -1; 
	    try { value = Integer.parseInt(arg); }
	    catch (NumberFormatException e) 
	    {
	        System.err.println("Argument" + arg + " must be an integer.");
	        System.exit(1);
	    }
		
		return value; 
	}
	private static void setRecSettings(FrameRecorder rec, FrameGrabber grabber)
	{
		//Setting some stuff for FFmpegFrameRecorder to record properly 
		rec.setFormat("mp4");
		rec.setVideoQuality(0);
		rec.setFrameRate(30);
		rec.setSampleRate(grabber.getSampleRate());
		rec.setAudioQuality(0);
		rec.setAudioBitrate(grabber.getAudioBitrate());
		rec.setAudioChannels(2);
		rec.setAudioCodec(avcodec.AV_CODEC_ID_AAC);
	}
}