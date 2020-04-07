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
		
		ripAudio(args[0], args[2]);
		ripFrames(args[0], args[1]); 
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