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
		//checkProperArgs(args);
		
		//Getting the first arg as int instead of string 
		//int firstArg = getIntArg(args[0]);			//Assumed arg is num of seconds
		int firstArg = 2; 
		
		//Checking if directory exists
		//File dir = new File(args[1]);
		File dir = new File("VideoBrokenVids");
		if (!dir.exists())					//If the directory doesn't exist 
			dir.mkdirs(); 					//Create it and any other parent directories named in the command-line argument
		
		//Initializing grabbers and recorders 
		Frame vidFrame = new Frame();
		Frame audioFrame = new Frame();
		int videoErr = 0; 


		try (FFmpegFrameGrabber frameGrabber = new FFmpegFrameGrabber(new File("10secTimer.mp4"));
				FFmpegFrameGrabber audioGrabber = new FFmpegFrameGrabber(new File("10secTimer.mp4")))
		{
			//Starting the grabbers
			frameGrabber.start();
			audioGrabber.start(); 
			
			FFmpegFrameRecorder rec = null; 
			
			int vidFrameCounter = 0, totalFrames = frameGrabber.getLengthInVideoFrames();
			int val = (totalFrames/((int)frameGrabber.getFrameRate()))/firstArg;
			for (int i = 0; i < val; videoErr = i++)
			{
				//Setting up the recorder 
				//FFmpegFrameRecorder rec = new FFmpegFrameRecorder(args[1]+"\\subVid"+i+".mp4", frameGrabber.getImageWidth(), frameGrabber.getImageHeight(), 2);
				rec = new FFmpegFrameRecorder("VideoBrokenVids\\subVid"+i+".mp4", frameGrabber.getImageWidth(), frameGrabber.getImageHeight(), 2);
				setRecSettings(rec, audioGrabber);
				rec.start(); 
				
				vidFrameCounter = 0;
				
				//Record the images and audio into new video file 
				while((vidFrame = frameGrabber.grab()) != null && vidFrameCounter < (totalFrames/val))
				{
					rec.record(vidFrame); 
					if(vidFrame.imageHeight != 0 && vidFrame.imageWidth != 0)
						++vidFrameCounter; 
				}
				
				//Closing recorder
				rec.stop(); rec.close(); 
			}

			//Closing resources
			frameGrabber.stop(); frameGrabber.close(); 
			audioGrabber.stop(); audioGrabber.close(); 
		}
		catch (Exception e)
		{
			System.out.println("Failure occured on sub-video: " + videoErr + "\n\n\n");
			System.out.println("FAILURE: "+e+"\n"); 
			e.printStackTrace();
		}
	}
	
	private static void checkProperArgs(String[] args)
	{
		if (args.length != 2)
		{
			System.err.println("Correct usage: Splitter <number_of_broken_videos> <directory_to_generate_videos>"); 
			System.exit(1);
		}
		else
		{
			int value; 
		    try { value = Integer.parseInt(args[1]); }
		    catch (NumberFormatException e) 
		    {
				System.err.println("Correct usage: Splitter <number_of_broken_videos> <directory_to_generate_videos>"); 
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
		rec.setFrameRate(30);
		rec.setSampleRate(grabber.getSampleRate());
		rec.setAudioQuality(0);
		rec.setAudioBitrate(grabber.getAudioBitrate());
		rec.setAudioChannels(2);
		rec.setAudioCodec(avcodec.AV_CODEC_ID_AAC);
	}
}