import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import javax.imageio.ImageIO;

import org.bytedeco.ffmpeg.global.avcodec;
import org.bytedeco.ffmpeg.global.avutil;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;

public class Recorder 
{
	public static void main(String[] args) 
	{
		//Checking for proper usage
		checkProperArgs(args); 
		
		//Creating any necessary directories
		File dir = new File(args[2]); 
		if (!dir.exists()) dir.mkdirs();
		

		Java2DFrameConverter c = new Java2DFrameConverter(); 
		Frame vidFrame = new Frame();
		Frame audioFrame = new Frame(); 
		BufferedImage im = null; 
				
		
		try (FFmpegFrameGrabber audioGrabber = new FFmpegFrameGrabber(new File(args[1]+"\\outputAudio.mp3")))
		{
			//Starting the grabber
			audioGrabber.start(); 
			
			//Set the first image to record
			int imageCounter = 0; 
			im = ImageIO.read(new File(args[0]+"\\filteredFrame"+(imageCounter++)+".png"));
			
			//Setting up the recorder 
			FFmpegFrameRecorder rec = new FFmpegFrameRecorder(args[2]+"\\"+args[3], im.getWidth(), im.getHeight(), 2);
			
			//Setting some stuff for FFmpegFrameRecorder to record properly 
			rec.setFormat("mp4");
			rec.setFrameRate(30);
			rec.setSampleRate(audioGrabber.getSampleRate());
			rec.setAudioQuality(0);
			rec.setAudioBitrate(audioGrabber.getAudioBitrate());
			rec.setAudioChannels(2);
			rec.setAudioCodec(avcodec.AV_CODEC_ID_AAC);
			
			rec.start(); 
			
			//Record the images and audio into new video file 
			while((audioFrame = audioGrabber.grabFrame()) != null)
			{
				if (im != null)
				{
					vidFrame = c.convert(im);
					rec.record(vidFrame, avutil.AV_PIX_FMT_RGB32_1);
				}
				
				//rec.setTimestamp(audioGrabber.getTimestamp());
				rec.record(audioFrame); 
				if (new File(args[0]+"\\filteredFrame"+(imageCounter)+".png").exists())
					im = ImageIO.read(new File(args[0]+"\\filteredFrame"+(imageCounter++)+".png"));
				else im = null;
			}
			
			audioGrabber.stop(); audioGrabber.close(); 
			rec.stop(); rec.close();
		}
		catch(Exception e) 
		{
			System.out.println("FAILURE: "+e+"\n"); 
			e.printStackTrace();
		}
	}

	private static void checkProperArgs(String[] args)
	{
		if (args.length != 4)
		{
			System.err.println("Correct Usage: Recorder <dir_of_filtered_frames> <dir_of_audio> <dir_to_output_vid> <output_name>"); 
			System.exit(-1);
		}
	}
}