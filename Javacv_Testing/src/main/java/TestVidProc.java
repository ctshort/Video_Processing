import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

import org.bytedeco.ffmpeg.global.avcodec;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;

public class TestVidProc 
{
	public static void main(String[] args) 
	{
		//Instantiating the structure that converts the grabbed frames to images that can be stored 
		Java2DFrameConverter converter = new Java2DFrameConverter();
		
		//Storing the grabbed frame
		Frame f = new Frame(); 
		
		//The Buffered Image that will be written to disk 
		BufferedImage im = null; 
		
		//FFmpegFrameGrabber breaks the video into frames that can be converted and then stored
		try (FFmpegFrameGrabber frameGrabber = new FFmpegFrameGrabber(new File("30secTimer.mp4"));)
		{		
			//Starting the grabber
			frameGrabber.start();
			
			//FFmpegFrameRecorder will record the images back into video 
			FFmpegFrameRecorder rec = new FFmpegFrameRecorder("output.mp4", frameGrabber.getImageWidth(), 
					frameGrabber.getImageHeight(), 2);
			
			//Setting some stuff for FFmpegFrameRecorder to record properly 
			rec.setFormat("mp4");
			rec.setFrameRate(frameGrabber.getFrameRate());
			rec.setSampleRate(frameGrabber.getSampleRate());
			rec.setVideoBitrate(frameGrabber.getVideoBitrate());
			rec.setAudioQuality(0);
			rec.setAudioBitrate(frameGrabber.getAudioBitrate());
			rec.setAudioChannels(2);
			rec.setAudioCodec(avcodec.AV_CODEC_ID_AAC);
		
			
			//Starting the recorder
			rec.start();
			
			
			//Counting the frames  
			int frameImage = 0;
			//Going through each frame in video and converting to BufferedImages
			//	and subsequently writing those images to disk
			while((f = frameGrabber.grabFrame()) != null)
			{
				//Saving the frame to disk in order to do processing 
				im = converter.convert(f); 
				if (im != null) 
				{
					ImageIO.write(im, "png", new File("VideoBrokenFrames//frame"+(frameImage++)+".png"));
				}
				
				//Recording each frame into the output video 
				rec.setTimestamp(frameGrabber.getTimestamp());
				rec.record(f);
			}
			
			//Closing rec
			rec.stop();
			rec.close();
		}
		catch (Exception e)
		{
			System.out.println("FAILURE: "+e+"\n"); 
			e.printStackTrace();
		}

	}

}