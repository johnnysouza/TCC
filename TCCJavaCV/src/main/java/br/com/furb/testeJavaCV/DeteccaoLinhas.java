package br.com.furb.testeJavaCV;

import static org.bytedeco.javacpp.helper.opencv_core.CV_RGB;
import static org.bytedeco.javacpp.opencv_core.CV_PI;
import static org.bytedeco.javacpp.opencv_core.cvCreateImage;
import static org.bytedeco.javacpp.opencv_core.cvCreateMemStorage;
import static org.bytedeco.javacpp.opencv_core.cvGetSeqElem;
import static org.bytedeco.javacpp.opencv_core.cvGetSize;
import static org.bytedeco.javacpp.opencv_core.cvPoint;
import static org.bytedeco.javacpp.opencv_imgcodecs.*;
import static org.bytedeco.javacpp.opencv_imgproc.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.bytedeco.javacpp.opencv_core.CvLineIterator;
import org.bytedeco.javacpp.opencv_core.CvMemStorage;
import org.bytedeco.javacpp.opencv_core.CvPoint;
import org.bytedeco.javacpp.opencv_core.CvPoint2D32f;
import org.bytedeco.javacpp.opencv_core.CvSeq;
import org.bytedeco.javacpp.opencv_core.IplImage;

public class DeteccaoLinhas {

	public static void main(String[] args) {
		String fileName = "C:\\temp\\Recorte_Imagens\\Componente_Conexa\\IFC G1R9 DIA 21 N_pata_3.jpg";
		IplImage src = cvLoadImage(fileName, CV_LOAD_IMAGE_GRAYSCALE);
		CvMemStorage storage = cvCreateMemStorage(0);
		CvSeq lines = new CvSeq();

		IplImage dst = cvCreateImage(cvGetSize(src), src.depth(), 1);
		IplImage colorDst = cvCreateImage(cvGetSize(src), src.depth(), 3);

		cvCanny(src, dst, 20, 120, 3);
		cvSaveImage(fileName.replace(".jpg", "_canny.jpg"), dst);
		cvCvtColor(dst, colorDst, CV_GRAY2BGR);

		lines = cvHoughLines2(dst, storage, CV_HOUGH_STANDARD, 1,
				Math.PI / 180, 90, 0, 0, 0, CV_PI);
		
		List<Integer> medianXList = new ArrayList<Integer>(); 
		
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
			
			int ptSubX = Math.abs(pt1.x() - pt2.x());
			if (ptSubX < 50) {
				cvLine(colorDst, pt1, pt2, CV_RGB(255, 0, 0), 3, CV_AA, 0);
				int median = Math.abs((pt1.x() + pt2.x()) / 2);
				medianXList.add(median);
			}
		}
		
		Map<Integer, Integer> countValues = new HashMap<Integer, Integer>();
		for (int i = 0, size = medianXList.size(); i < size; i++) {
			int x = medianXList.get(i);
			for (int j = i + 1; j < size; j++) {
				int anotherX = medianXList.get(j);
				if (x != anotherX) {
					int dif = Math.abs(x - anotherX);
					Integer prevVal = countValues.get(dif);
					if (prevVal == null) {
						countValues.put(dif, 1);
					} else {
						countValues.put(dif, ++prevVal);
					}
				}
			}
		}
		
		int maxCount = 0;
		Map<Integer, Integer> biggerValues = new HashMap<Integer, Integer>();
		for (Entry<Integer, Integer> entry : countValues.entrySet()) {
			int value = entry.getValue();
			int key = entry.getKey();
			if (value > maxCount) {
				biggerValues = new HashMap<Integer, Integer>();
				biggerValues.put(key, value);
				maxCount = value;
			} else if (value == maxCount) {
				biggerValues.put(key, value);
			}
		}
		
		int pixelsBySquare = 0;
		if (biggerValues.size() == 1) {
			pixelsBySquare = ((List<Integer>)biggerValues.values()).get(0);
		} else {
			int minKey = Integer.MAX_VALUE;
			for (Integer key : biggerValues.keySet()) {
				if (key < minKey) {
					minKey = key;
				}
			}
			pixelsBySquare = minKey;
		}
		System.out.println(pixelsBySquare);

		cvSaveImage(fileName.replace(".jpg", "_linhas.jpg"), colorDst);
	}

}
