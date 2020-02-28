import java.io.File;

import org.bytedeco.ffmpeg.global.avcodec;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.FrameRecorder;

public class Stitcher 
{

	public static void main(String[] args) 
	{
		//Ensuring proper usage of command-line args
		//checkProperArgs(args);
				
		//Checking if directory exists
		//File dir = new File(args[1]);
		File dir = new File("VideoStitchedVids");
		if (!dir.exists())					//If the directory doesn't exist 
			dir.mkdirs(); 					//Create it and any other parent directories named in the command-line argument

		//Getting the files in the VideoBrokenVids directory
		//File vids = new File(args[1]); 
		File vids = new File("VideoBrokenVids"); 
		File[] vidDir = vids.listFiles();
		
		try
		{
			//Initializing some objects
			Frame vidFrame = new Frame(); 
			//Frame audioFrame = new Frame(); 
			
			FFmpegFrameGrabber frameGrabber = new FFmpegFrameGrabber(new File("VideoBrokenVids\\subVid0.mp4"));
			//FFmpegFrameGrabber audioGrabber = new FFmpegFrameGrabber(new File("VideoBrokenVids\\subVid0.mp4"));

			//Starting the grabbers
			frameGrabber.start();
			//audioGrabber.start(); 
			
			//Setting up recorder
			//FFmpegFrameRecorder rec = new FFmpegFrameRecorder(args[2]+"stitchedVid.mp4", grabber.getImageWidth(), grabber.getImageHeight());
			FFmpegFrameRecorder rec = new FFmpegFrameRecorder("VideoStitchedVids\\stitchedVid.mp4", frameGrabber.getImageWidth(), frameGrabber.getImageHeight(), 2);
			setRecSettings(rec, frameGrabber);
			rec.start(); 
			
			//Recording into new 
			for(int i = 0; i < vidDir.length; ++i)
			{
				//while((vidFrame = frameGrabber.grab()) != null
				//		|| (audioFrame = audioGrabber.grabSamples()) != null)
				while((vidFrame = frameGrabber.grab()) != null)
				{
					rec.record(vidFrame); 
					//rec.recordSamples(audioFrame.samples);
				}

				frameGrabber.stop(); frameGrabber.close(); 
				//audioGrabber.stop(); audioGrabber.close();
				if (i < vidDir.length-1) 
				{
					frameGrabber = new FFmpegFrameGrabber(new File("VideoBrokenVids\\subVid"+(i+1)+".mp4"));
					//audioGrabber = new FFmpegFrameGrabber(new File("VideoBrokenVids\\subVid"+(i+1)+".mp4"));
				}
				else 
				{
					frameGrabber = new FFmpegFrameGrabber(new File("VideoBrokenVids\\subVid"+(i)+".mp4"));
					//audioGrabber = new FFmpegFrameGrabber(new File("VideoBrokenVids\\subVid"+(i)+".mp4"));
				}
				frameGrabber.start();
				//audioGrabber.start();
			}
			
			//Closing resources
			frameGrabber.stop(); frameGrabber.close(); 
			//audioGrabber.stop(); audioGrabber.close();
			rec.stop(); rec.close(); 
		}
		catch (Exception e)
		{
			System.out.println("FAILURE: "+e+"\n"); 
			e.printStackTrace();
		}
	}
	
	private static void checkProperArgs(String[] args)
	{
		if (args.length != 2)
		{
			System.err.println("Correct usage: Splitter <dir_of_broken_videos> <dir_to_output_video>"); 
			System.exit(1);
		}
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