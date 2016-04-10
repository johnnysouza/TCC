package br.com.furb.testeJavaCV;

import static org.bytedeco.javacpp.helper.opencv_core.CV_RGB;
import static org.bytedeco.javacpp.helper.opencv_imgproc.cvDrawContours;
import static org.bytedeco.javacpp.opencv_core.CV_PI;
import static org.bytedeco.javacpp.opencv_core.cvCreateImage;
import static org.bytedeco.javacpp.opencv_core.cvCreateMemStorage;
import static org.bytedeco.javacpp.opencv_core.cvGetSeqElem;
import static org.bytedeco.javacpp.opencv_core.cvGetSize;
import static org.bytedeco.javacpp.opencv_core.cvPoint;
import static org.bytedeco.javacpp.opencv_imgcodecs.CV_LOAD_IMAGE_COLOR;
import static org.bytedeco.javacpp.opencv_imgcodecs.CV_LOAD_IMAGE_GRAYSCALE;
import static org.bytedeco.javacpp.opencv_imgcodecs.cvLoadImage;
import static org.bytedeco.javacpp.opencv_imgcodecs.cvSaveImage;
import static org.bytedeco.javacpp.opencv_imgproc.CV_AA;
import static org.bytedeco.javacpp.opencv_imgproc.CV_CHAIN_APPROX_NONE;
import static org.bytedeco.javacpp.opencv_imgproc.CV_GRAY2BGR;
import static org.bytedeco.javacpp.opencv_imgproc.CV_HOUGH_STANDARD;
import static org.bytedeco.javacpp.opencv_imgproc.CV_RETR_CCOMP;
import static org.bytedeco.javacpp.opencv_imgproc.THRESH_BINARY_INV;
import static org.bytedeco.javacpp.opencv_imgproc.cvBoundingRect;
import static org.bytedeco.javacpp.opencv_imgproc.cvCanny;
import static org.bytedeco.javacpp.opencv_imgproc.cvContourArea;
import static org.bytedeco.javacpp.opencv_imgproc.cvCvtColor;
import static org.bytedeco.javacpp.opencv_imgproc.cvFindContours;
import static org.bytedeco.javacpp.opencv_imgproc.cvHoughLines2;
import static org.bytedeco.javacpp.opencv_imgproc.cvLine;
import static org.bytedeco.javacpp.opencv_imgproc.cvThreshold;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bytedeco.javacpp.Loader;
import org.bytedeco.javacpp.opencv_core.CvContour;
import org.bytedeco.javacpp.opencv_core.CvMemStorage;
import org.bytedeco.javacpp.opencv_core.CvPoint;
import org.bytedeco.javacpp.opencv_core.CvPoint2D32f;
import org.bytedeco.javacpp.opencv_core.CvRect;
import org.bytedeco.javacpp.opencv_core.CvScalar;
import org.bytedeco.javacpp.opencv_core.CvSeq;
import org.bytedeco.javacpp.opencv_core.CvSize;
import org.bytedeco.javacpp.opencv_core.IplImage;

public class ComponenteConexa {

	public static void main(String[] args) {
		String fileName = "C:\\temp\\Recorte_Imagens\\Componente_Conexa\\IFC G1R4 DIA 21 E_pata_1.jpg";
		IplImage paw = cvLoadImage(fileName, CV_LOAD_IMAGE_COLOR);

		// int sp = 15;
		// int sr = 20;
		//
		// IplImage filtered = cvCreateImage(cvGetSize(paw), paw.depth(),
		// paw.nChannels());
		// cvPyrMeanShiftFiltering(paw, filtered, sp, sr, 1, null);
		// cvSaveImage(fileName.replace(".jpg", "meanShift.jpg"), filtered);
		//
		// IplImage grayPaw = cvCreateImage(cvGetSize(filtered), 8, 1);
		// cvCvtColor(filtered, grayPaw, CV_BGR2GRAY);

		IplImage grayPaw = cvLoadImage(fileName, CV_LOAD_IMAGE_GRAYSCALE);
		cvSaveImage(fileName.replace(".jpg", "gray.jpg"), grayPaw);

		// create binary image of original size
		IplImage limiarizada = cvCreateImage(cvGetSize(grayPaw), 8, 1);
		
		//IFC G1R9 DIA 21 N_pata_ori
//		final double thresh = 172;
//		final double maxval = 255;
		
		//IFC G1R4 DIA 21 E_pata_1
		final double thresh = 120;
		final double maxval = 255;
		cvThreshold(grayPaw, limiarizada, thresh, maxval, THRESH_BINARY_INV);
		cvSaveImage(fileName.replace(".jpg", "_segmentada.jpg"), limiarizada);

		// IplImage erodePaw = cvCreateImage(cvGetSize(limiarizada),
		// limiarizada.depth(), limiarizada.nChannels());
		// cvErode(limiarizada, erodePaw);
		// cvSaveImage(fileName.replace(".jpg", "erode.jpg"), erodePaw);

		CvMemStorage storage = CvMemStorage.create();
		CvSeq contours = new CvContour(null);
		cvFindContours(limiarizada, storage, contours,
				Loader.sizeof(CvContour.class), CV_RETR_CCOMP,
				CV_CHAIN_APPROX_NONE, new CvPoint(0, 0));

		CvSeq ptr = new CvSeq();
		double bigArea = 0;
		CvSeq bigContour = new CvSeq();
		List<CvRect> retList = new ArrayList<CvRect>();
		for (ptr = contours; ptr != null; ptr = ptr.h_next()) {
			double size = cvContourArea(ptr);
			System.out.println(size);
			if (size > bigArea) {
				bigArea = size;
				bigContour = ptr;
			}
		}

		float pixelsByQuadrant = calcPixelsByQuadrant("C:\\temp\\Recorte_Imagens\\Componente_Conexa\\IFC G1R4 DIA 21 E.jpg");

		CvRect boundingPaw = cvBoundingRect(bigContour);
		int size = boundingPaw.width() * boundingPaw.height();
		CvSize cvSize = new CvSize(size);
		IplImage pawContour = cvCreateImage(cvGetSize(limiarizada), 8, 1);
		cvDrawContours(pawContour, bigContour, CvScalar.WHITE,
				CV_RGB(248, 18, 18), 1, -1, 8);

		float pawWidth = ((float) boundingPaw.width()) / pixelsByQuadrant;
		float pawHeight = ((float) boundingPaw.height()) / pixelsByQuadrant;
		cvSaveImage(
				fileName.replace(".jpg", "_componente_height_" + pawHeight
						+ "_width" + pawWidth + ".jpg"), pawContour);

	}

	private static int calcPixelsByQuadrant(String fileName) {
		IplImage src = cvLoadImage(fileName, CV_LOAD_IMAGE_GRAYSCALE);
		CvMemStorage storage = cvCreateMemStorage(0);
		CvSeq lines = new CvSeq();

		IplImage dst = cvCreateImage(cvGetSize(src), src.depth(), 1);
		IplImage colorDst = cvCreateImage(cvGetSize(src), src.depth(), 3);

		cvCanny(src, dst, 20, 140, 3);
		cvSaveImage(fileName.replace(".jpg", "_canny.jpg"), dst);
		cvCvtColor(dst, colorDst, CV_GRAY2BGR);

		lines = cvHoughLines2(dst, storage, CV_HOUGH_STANDARD, 1,
				Math.PI / 180, 90, 0, 0, 0, CV_PI);

		List<Integer> medianPointList = new ArrayList<Integer>();

		for (int i = 0; i < lines.total(); i++) {
			CvPoint2D32f point = new CvPoint2D32f(cvGetSeqElem(lines, i));

			float rho = point.x();
			float theta = point.y();

			double a = Math.cos((double) theta), b = Math.sin((double) theta);
			double x0 = a * rho, y0 = b * rho;
			CvPoint pt1 = cvPoint((int) Math.round(x0 + 1000 * (-b)),
					(int) Math.round(y0 + 1000 * (a)));
			CvPoint pt2 = cvPoint((int) Math.round(x0 - 1000 * (-b)),
					(int) Math.round(y0 - 1000 * (a)));
			System.out.print("Line spotted: ");
			System.out.print("\t rho= " + rho);
			System.out.print("\t theta= " + theta);
			System.out.print("\t p1= " + pt1);
			System.out.println("\t p2= " + pt2);

			if (src.width() > src.height()) {
				int ptSubX = Math.abs(pt1.x() - pt2.x());
				if (ptSubX < 50) {
					cvLine(colorDst, pt1, pt2, CV_RGB(255, 0, 0), 3, CV_AA, 0);
					int median = Math.abs((pt1.x() + pt2.x()) / 2);
					medianPointList.add(median);
				}
			} else {
				int ptSubY = Math.abs(pt1.y() - pt2.y());
				if (ptSubY < 50) {
					cvLine(colorDst, pt1, pt2, CV_RGB(255, 0, 0), 3, CV_AA, 0);
					int median = Math.abs((pt1.y() + pt2.y()) / 2);
					medianPointList.add(median);
				}
			}
		}
		cvSaveImage(fileName.replace(".jpg", "_linhas.jpg"), colorDst);

//		Map<Integer, Integer> countValues = new HashMap<Integer, Integer>();
//		for (int i = 0, size = medianPointList.size(); i < size; i++) {
//			int point = medianPointList.get(i);
//			for (int j = i + 1; j < size; j++) {
//				int anotherPoint = medianPointList.get(j);
//				if (point != anotherPoint) {
//					int dif = Math.abs(point - anotherPoint);
//					Integer prevVal = countValues.get(dif);
//					if (prevVal == null) {
//						countValues.put(dif, 1);
//					} else {
//						countValues.put(dif, ++prevVal);
//					}
//				}
//			}
//		}
		
//		int maxCount = 0;
//		Map<Integer, Integer> biggerValues = new HashMap<Integer, Integer>();
//		for (Entry<Integer, Integer> entry : countValues.entrySet()) {
//			int value = entry.getValue();
//			int key = entry.getKey();
//			if (value > maxCount) {
//				biggerValues = new HashMap<Integer, Integer>();
//				biggerValues.put(key, value);
//				maxCount = value;
//			} else if (value == maxCount) {
//				biggerValues.put(key, value);
//			}
//		}
//
//		int pixelsBySquare = 0;
//		int minKey = Integer.MAX_VALUE;
//		for (Integer key : biggerValues.keySet()) {
//			if (key < minKey) {
//				minKey = key;
//			}
//		}
//		pixelsBySquare = minKey;
		
		List<HoughLinesVal> countValues = new ArrayList<HoughLinesVal>();
		for (int i = 0, size = medianPointList.size(); i < size; i++) {
			int point = medianPointList.get(i);
			for (int j = i + 1; j < size; j++) {
				int anotherPoint = medianPointList.get(j);
				if (point != anotherPoint) {
					int dif = Math.abs(point - anotherPoint);
					HoughLinesVal countVal = new HoughLinesVal(dif);
					int index = countValues.indexOf(countVal);
					if (index == -1) {
						countValues.add(countVal);
					} else {
						HoughLinesVal prevVal = countValues.get(index);
						prevVal.setVal(dif);
					}
				}
			}
		}

		int maxCount = 0;
		Map<Integer, Integer> biggerValues = new HashMap<Integer, Integer>();
		for (HoughLinesVal val : countValues) {
			int value = val.getCount();
			int key = val.getVal();
			if (value > maxCount) {
				biggerValues = new HashMap<Integer, Integer>();
				biggerValues.put(key, value);
				maxCount = value;
			} else if (value == maxCount) {
				biggerValues.put(key, value);
			}
		}

		int pixelsBySquare = 0;
		int minKey = Integer.MAX_VALUE;
		for (Integer key : biggerValues.keySet()) {
			if (key < minKey && key > 10) { //Redução de linhas muito próximas que são ruidos da saída da transformação de hough
				minKey = key;
			}
		}
		pixelsBySquare = minKey;

		System.out.println(pixelsBySquare);
		return pixelsBySquare;
	}

}
