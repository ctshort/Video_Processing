import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

import org.bytedeco.ffmpeg.global.avcodec;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.FrameRecorder;
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
		//Java2DFrameConverter converter = new Java2DFrameConverter();
		
		//Storing the grabbed frame
		//Frame vidFrame = new Frame();
		//Frame audioFrame = new Frame(); 
		
		//The Buffered Image that will be written to disk 
		//BufferedImage im = null; 
		
		
		
		//Testing ripping audio only 
		ripAudio(args[0], args[2]);
		ripFrames(args[0], args[1]); 
		
		
		
		/*
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
			while((vidFrame = frameGrabber.grab()) != null
				|| (audioFrame = audioGrabber.grabSamples()) != null)
			{
				im = converter.convert(vidFrame); 
				if(im != null)
					ImageIO.write(im, "png", new File(args[1]+"\\frame"+(frameCounter++)+".png"));
				
				if (rec.getTimestamp() <= audioGrabber.getTimestamp())
				{
						rec.setTimestamp(audioGrabber.getTimestamp());
						rec.recordSamples(audioFrame.samples);
				}
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
		*/
	}
	private static void ripAudio(String ripFile, String outDir)
	{
		Frame f = new Frame(); 
		
		try (FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(new File(ripFile)))
		{
			//Starting the grabber 
			grabber.start();
			
			FFmpegFrameRecorder rec = new FFmpegFrameRecorder(outDir+"\\outputAudio.mp3", grabber.getAudioChannels()); 
			rec.setSampleRate(grabber.getSampleRate());
			rec.setAudioQuality(0);
			rec.setAudioBitrate(grabber.getAudioBitrate());
			rec.setAudioChannels(2);
			rec.setAudioCodec(avcodec.AV_CODEC_ID_MP3);
			rec.start();
			
			while((f = grabber.grabSamples()) != null)
			{
				if (rec.getTimestamp() <= grabber.getTimestamp())
				{
						rec.setTimestamp(grabber.getTimestamp());
						rec.recordSamples(f.samples);
				}
			}
			
			grabber.stop(); grabber.close();
			rec.stop(); rec.close();
		}
		catch (Exception e)
		{
			System.out.println("FAILURE: "+e+"\n"); 
			e.printStackTrace();
		}
	}
	private static void ripFrames(String ripFile, String outDir)
	{ 
		//Structures necessary to convert video frames to images
		Java2DFrameConverter converter = new Java2DFrameConverter();
		Frame f = new Frame();
		BufferedImage im = null; 

		
		try(FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(new File(ripFile)))
		{
			grabber.start();
			
			int frameCounter = 0; 
			while ((f = grabber.grab()) != null)
			{
				im = converter.convert(f); 
				if(im != null)
					ImageIO.write(im, "png", new File(outDir+"\\frame"+(frameCounter++)+".png"));
			}
			
			grabber.stop(); grabber.close();
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