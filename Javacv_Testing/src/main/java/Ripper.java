import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

import org.bytedeco.ffmpeg.global.avcodec;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;

public class Ripper 
{
	public static void main(String[] args) 
	{
		//Checking to ensure proper usage
		checkProperArgs(args);
		
		//Creating any necessary directories
		File dir = new File(args[1]);
		if (!dir.exists()) dir.mkdirs(); 	
		
		dir = new File(args[2]); 
		if (!dir.exists()) dir.mkdirs();
		
		
		//Instantiating the structure that converts the grabbed frames to images that can be stored 
		Java2DFrameConverter converter = new Java2DFrameConverter();
		
		//Storing the grabbed frame
		Frame vidFrame = new Frame();
		Frame audioFrame = new Frame(); 
		
		//The Buffered Image that will be written to disk 
		BufferedImage im = null; 
		
		try (FFmpegFrameGrabber frameGrabber = new FFmpegFrameGrabber(new File(args[0]));
				FFmpegFrameGrabber audioGrabber = new FFmpegFrameGrabber(new File(args[0])))
		{
			//Starting the grabber
			frameGrabber.start();
			audioGrabber.start(); 
			
			//FFmpegFrameRecorder will record the images back into video 
			FFmpegFrameRecorder rec = new FFmpegFrameRecorder(args[2]+"\\outputAudio.mp3", audioGrabber.getAudioChannels()); 
			rec.setFormat("mp3");
			rec.setAudioCodec(avcodec.AV_CODEC_ID_MP3);
			rec.start();
			
			//Saving the video frames
			int frameCounter = 0;
			//frameGrabber.setTimestamp(10100000L);
			while((vidFrame = frameGrabber.grab()) != null
				|| (audioFrame = audioGrabber.grabSamples()) != null)
			{
				im = converter.convert(vidFrame); 
				if(im != null)
					ImageIO.write(im, "png", new File(args[1]+"\\frame"+(frameCounter++)+".png"));
				
				rec.recordSamples(audioFrame.samples);
			}
						
			frameGrabber.stop(); frameGrabber.close(); 
			audioGrabber.stop(); audioGrabber.close(); 
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
		if (args.length != 3)
		{
			System.err.println("Correct Usage: Ripper <file_to_be_ripped> <dir_to_output_frames> <dir_to_output_audio>"); 
			System.exit(-1);
		}
	}
}