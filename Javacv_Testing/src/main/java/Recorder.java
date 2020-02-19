import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

import org.bytedeco.ffmpeg.global.avcodec;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;

public class Recorder 
{
	public static void main(String[] args) 
	{
		Java2DFrameConverter c = new Java2DFrameConverter(); 
		Frame vidFrame = new Frame();
		Frame audioFrame = new Frame(); 
		BufferedImage im = null; 
				
		
		try (FFmpegFrameGrabber audioGrabber = new FFmpegFrameGrabber(new File("VideoBrokenSounds\\outputAudio.mp3")))
		{
			//Starting the grabber
			audioGrabber.start(); 
			
			//Set the first image to record
			int imageCounter = 0; 
			im = ImageIO.read(new File("VideoFilteredFrames\\filteredFrame"+(imageCounter++)+".png"));
			
			//Setting up the recorder 
			FFmpegFrameRecorder rec = new FFmpegFrameRecorder("ZEDoutput.mp4", im.getWidth(), im.getHeight(), 2);
			
			//Setting some stuff for FFmpegFrameRecorder to record properly 
			rec.setFormat("mp4");
			rec.setFrameRate(30);
			rec.setSampleRate(audioGrabber.getSampleRate());
			//rec.setVideoBitrate(frameGrabber.getVideoBitrate());
			rec.setAudioQuality(0);
			rec.setAudioBitrate(audioGrabber.getAudioBitrate());
			rec.setAudioChannels(2);
			rec.setAudioCodec(avcodec.AV_CODEC_ID_AAC);
			
			rec.start(); 
			
			//Record the images and audio into new video file 
			while((audioFrame = audioGrabber.grabFrame()) != null)
			{
				vidFrame = c.convert(im);
				rec.record(vidFrame);
				rec.record(audioFrame); 
				//System.out.println(imageCounter);
				if (new File("VideoFilteredFrames\\filteredFrame"+(imageCounter)+".png").exists())
					im = ImageIO.read(new File("VideoFilteredFrames\\filteredFrame"+(imageCounter++)+".png"));
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

}