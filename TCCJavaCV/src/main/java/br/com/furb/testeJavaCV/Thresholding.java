package br.com.furb.testeJavaCV;

import static org.bytedeco.javacpp.opencv_imgcodecs.cvLoadImage;
import static org.bytedeco.javacpp.opencv_core.*;
import static org.bytedeco.javacpp.opencv_imgcodecs.*;
import static org.bytedeco.javacpp.opencv_imgproc.*;
import static org.bytedeco.javacpp.opencv_video.*;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.Date;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacpp.opencv_core.CvMat;
import org.bytedeco.javacpp.opencv_core.IplImage;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.bytedeco.javacv.OpenCVFrameConverter;

/**
 * Hello world!
 */
public class Thresholding extends JFrame {
	// color range of red like color
	static CvScalar min = cvScalar(240, 232, 255, 0);// BGR-A
	static CvScalar max = cvScalar(159, 102, 255, 0);// BGR-A

	public static void main(String[] args) {
		String fileName = "C:\\temp\\R7 G3 21 dias - experimental 1.jpg";
		IplImage src = cvLoadImage(fileName, CV_LOAD_IMAGE_COLOR);

		IplImage filtered = cvCreateImage(cvGetSize(src), src.depth(),
				src.nChannels());
		cvPyrMeanShiftFiltering(src, filtered, 10, 12, 1, null);
		IplImage filteredGray = cvCreateImage(cvGetSize(src), src.depth(), CV_LOAD_IMAGE_COLOR);
		cvCvtColor(filtered, filteredGray, CV_RGB2GRAY);

		// create binary image of original size
		IplImage limiarizada = cvCreateImage(cvGetSize(filtered), 8, 1);
		final double thresh = 120;
		final double maxval = 255;
		cvThreshold(filteredGray, limiarizada, thresh, maxval, THRESH_BINARY);
//		// apply thresholding
//		cvInRangeS(filtered, min, max, medianFiltered);
//		// smooth filter- median
//		cvSmooth(medianFiltered, medianFiltered, CV_MEDIAN, 13, 0, 0, 0);

		OpenCVFrameConverter.ToIplImage sourceConverter = new OpenCVFrameConverter.ToIplImage();

		Java2DFrameConverter frameConverter = new Java2DFrameConverter();
		BufferedImage image = frameConverter.getBufferedImage(sourceConverter
				.convert(limiarizada));
		Thresholding app = new Thresholding();
		ImagemPanel panel = new ImagemPanel(image);
		app.add(panel);

		app.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		app.setSize(image.getWidth() / 2 + 16, image.getHeight() / 2 + 37);
		app.setVisible(true);
		app.setTitle(new Date().toString());
	}

	static class ImagemPanel extends JPanel {

		private BufferedImage image;

		public ImagemPanel(BufferedImage image) {
			this.image = image;
		}

		@Override
		public void paint(Graphics g) {
			super.paint(g);
			g.drawImage(image, 0, 0, image.getWidth() / 2,
					image.getHeight() / 2, null);
		}

	}
}
