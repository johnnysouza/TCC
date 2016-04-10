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
public class KMeans extends JFrame {

	public static void main(String[] args) {
		String fileName = "C:\\Temp\\oc_color_quantization.jpg";

		/* */

		// // IplImage samples = cvCreateImage(cvGetSize(src), src.depth(),
		// CV_32S);
		// int cluster_count = 3; //Quantidade de clusters
		// int attempts = 10;
		// // Mat samples = new Mat(src);
		// // IplImage labels = cvCreateImage(cvGetSize(src), src.depth(),
		// CV_32S);
		// // cvCopy(src, labels);
		// IplImage centers =
		// cvCreateImage(cvGetSize(CvMat.create(cluster_count, src.width())),
		// src.depth(), CV_32S);
		// // cvCvtColor(src, labels, CV_RGB2GRAY);
		// // Mat matLabels = new Mat(labels);
		// CvMat samples = src.asCvMat();
		// CvMat labels = CvMat.create(samples.rows(), 1, opencv_core.CV_32S);
		// CvTermCriteria termCriteria = new CvTermCriteria(TermCriteria.EPS +
		// TermCriteria.MAX_ITER, 10, 1.0);
		// // CvMat clusterCenters = CvMat.create(cluster_count,
		// // samples.cols(),
		// // opencv_core.CV_32FC1);
		// // double[] compactness = new double[attempts];
		// // cvKMeans2(src, cluster_count, labels, termCriteria);
		// cvKMeans2(samples, cluster_count, labels, termCriteria);

		 IplImage src = cvLoadImage(fileName, CV_LOAD_IMAGE_COLOR);
		//
		// int cluster_count = 3;
		// int attempts = 10;
		// CvTermCriteria termCriteria = new CvTermCriteria(TermCriteria.EPS +
		// TermCriteria.MAX_ITER, attempts, 1.0);
		//
		// cvReshape(src, src.asCvMat(), 1, src.height() * src.width());
		// IplImage samples = cvCreateImage(cvGetSize(src), src.depth(), 1);
		// cvConvertImage(src, samples, CV_32F);
		//
		// IplImage labels = cvCreateImage(new CvSize(samples.height()), 1,
		// CV_8U);
		// IplImage centers = cvCreateImage(new CvSize(cluster_count *
		// src.width()), 1, CV_32F);
		// // Mat centroids = new Mat(cluster_count, src.width(), CV_32F);
		// // IplImage centers = new IplImage(centroids);
		// // centers.arrayData().fill(0);
		//
		// cvKMeans2(samples, cluster_count, labels, termCriteria, 1, new
		// long[]{0}, KMEANS_RANDOM_CENTERS, centers, new double[]{0});

		CvTermCriteria terminationCriteria = new CvTermCriteria(
				opencv_core.CV_TERMCRIT_EPS,
				// + opencv_core.CV_TERMCRIT_ITER,
				10, 1.0);

		CvMat clusterLabels = CvMat
				.create(src.asCvMat().rows(), 1, opencv_core.CV_32SC1);
		CvMat clusterCenters = CvMat.create(3, src.asCvMat().cols(),
				opencv_core.CV_32FC1);
		double[] compactness = new double[10];
		// Run a clusterer on the eigenimages
		opencv_core.cvKMeans2(src.asCvMat(), 3, clusterLabels,
				terminationCriteria, 10, new long[]{0}, 0,
				clusterCenters, compactness);

		/* */

		OpenCVFrameConverter.ToIplImage sourceConverter = new OpenCVFrameConverter.ToIplImage();

		Java2DFrameConverter frameConverter = new Java2DFrameConverter();
		BufferedImage image = frameConverter.getBufferedImage(sourceConverter
				.convert(src));
		KMeans app = new KMeans();
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
