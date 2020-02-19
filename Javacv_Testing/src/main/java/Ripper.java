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
		//Instantiating the structure that converts the grabbed frames to images that can be stored 
		Java2DFrameConverter converter = new Java2DFrameConverter();
		
		//Storing the grabbed frame
		Frame vidFrame = new Frame();
		Frame audioFrame = new Frame(); 
		
		//The Buffered Image that will be written to disk 
		BufferedImage im = null; 
		
		try (FFmpegFrameGrabber frameGrabber = new FFmpegFrameGrabber(new File("10secTimer.mp4"));
				FFmpegFrameGrabber audioGrabber = new FFmpegFrameGrabber(new File("10secTimer.mp4")))
		{
			//Starting the grabber
			frameGrabber.start();
			audioGrabber.start(); 
			
			//FFmpegFrameRecorder will record the images back into video 
			//FFmpegFrameRecorder rec = new FFmpegFrameRecorder("VideoBrokenSounds\\outputAudio.mp3", frameGrabber.getImageWidth(),	frameGrabber.getImageHeight(), 2);
			FFmpegFrameRecorder rec = new FFmpegFrameRecorder("VideoBrokenSounds\\outputAudio.mp3", audioGrabber.getAudioChannels()); 
			rec.setFormat("mp3");
			rec.setAudioCodec(avcodec.AV_CODEC_ID_MP3);
			//rec.setSampleFormat(audioGrabber.getSampleFormat());
			//rec.setSampleRate(audioGrabber.getSampleRate());
			rec.start();
			
			//Saving the video frames
			int frameCounter = 0; 
			while((vidFrame = frameGrabber.grab()) != null
				|| (audioFrame = audioGrabber.grabSamples()) != null)
			{
				im = converter.convert(vidFrame); 
				if(im != null)
					ImageIO.write(im, "png", new File("VideoBrokenFrames\\frame"+(frameCounter++)+".png"));
				
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

}