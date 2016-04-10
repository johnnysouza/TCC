package br.com.furb.testeJavaCV;

import static org.bytedeco.javacpp.helper.opencv_core.CV_RGB;
import static org.bytedeco.javacpp.opencv_core.CV_8UC1;
import static org.bytedeco.javacpp.opencv_core.cvCreateImage;
import static org.bytedeco.javacpp.opencv_core.cvSetImageROI;
import static org.bytedeco.javacpp.opencv_core.cvGetSize;
import static org.bytedeco.javacpp.opencv_core.cvInRangeS;
import static org.bytedeco.javacpp.opencv_imgcodecs.cvLoadImage;
import static org.bytedeco.javacpp.opencv_imgcodecs.cvSaveImage;
import static org.bytedeco.javacpp.opencv_imgcodecs.CV_LOAD_IMAGE_COLOR;
import static org.bytedeco.javacpp.opencv_imgproc.CV_CHAIN_APPROX_NONE;
import static org.bytedeco.javacpp.opencv_imgproc.CV_RETR_CCOMP;
import static org.bytedeco.javacpp.opencv_imgproc.cvBoundingRect;
import static org.bytedeco.javacpp.opencv_imgproc.cvCanny;
import static org.bytedeco.javacpp.opencv_imgproc.cvFindContours;
import static org.bytedeco.javacpp.opencv_imgproc.cvRectangle;
import static org.bytedeco.javacpp.opencv_imgproc.cvPyrMeanShiftFiltering;

import java.util.ArrayList;
import java.util.List;

import org.bytedeco.javacpp.Loader;
import org.bytedeco.javacpp.opencv_core.CvContour;
import org.bytedeco.javacpp.opencv_core.CvMemStorage;
import org.bytedeco.javacpp.opencv_core.CvPoint;
import org.bytedeco.javacpp.opencv_core.CvRect;
import org.bytedeco.javacpp.opencv_core.CvScalar;
import org.bytedeco.javacpp.opencv_core.CvSeq;
import org.bytedeco.javacpp.opencv_core.IplImage;

public class IsolamentoPata {

	public static void main(String[] args) {
			String fileName = "C:\\temp\\Recorte_Imagens\\Parte2\\IFC G1R9 DIA 21 N.jpg";
	        IplImage src = cvLoadImage(fileName, CV_LOAD_IMAGE_COLOR);
	        
	        int sp = 15;
	        int sr = 20;
	        
	        IplImage filtered = cvCreateImage(cvGetSize(src), src.depth(), src.nChannels());
	        cvPyrMeanShiftFiltering(src, filtered, sp, sr, 1, null);
	        
//	        String fileName = "C:\\temp\\Recorte_Imagens\\IFC G1R4 DIA 21 E_meanShift_sp15_sr18.jpg";
//	        IplImage src = cvLoadImage(fileName, CV_LOAD_IMAGE_COLOR);
	        
	        IplImage findFoot = cvCreateImage(cvGetSize(filtered), 8, 1);
	        
//	        String fileName = "C:\\temp\\Recorte_Imagens\\IFC G1R4 DIA 21 E.jpg";
//	        CvScalar lower = new CvScalar(92, 96, 145 , 0);
//	        CvScalar upper = new CvScalar(98, 108, 156, 0);
	        
//	        String fileName = "C:\\temp\\Recorte_Imagens\\IFC G1R9 DIA 21 N.jpg"; Pata traseira
//	        CvScalar lower = new CvScalar(90, 108, 149 , 0);
//	        CvScalar upper = new CvScalar(98, 116, 157, 0);
	        
//	        String fileName = "C:\\temp\\Recorte_Imagens\\IFC G1R9 DIA 21 N.jpg"; Pata dianteira
//	        CvScalar lower = new CvScalar(118, 143, 187 , 0);
//	        CvScalar upper = new CvScalar(125, 154, 199, 0);
	        
	        CvScalar lower = new CvScalar(92, 90, 122 , 0);
	        CvScalar upper = new CvScalar(121, 150, 192, 0);
	        cvInRangeS(filtered, lower, upper, findFoot);
	        cvSaveImage(fileName.replace(".jpg", "_patas.jpg"), findFoot);
			
//			String fileName = "C:\\temp\\Recorte_Imagens\\IFC G1R4 DIA 21 E_teste5.jpg";
//	        IplImage src = cvLoadImage(fileName, CV_8UC1);
	        
	        CvMemStorage storage = CvMemStorage.create();
	        CvSeq contours = new CvContour(null);
	        
	        
//	        IplImage dest = cvCreateImage(cvGetSize(findFoot), findFoot.depth(), CV_8UC1);
//	        cvCanny(findFoot, dest, 100, 200, 3);
//	        fileName = fileName.replace(".jpg", "_edges.jpg");
//	        cvSaveImage(fileName, dest);
	        
	        cvFindContours(findFoot, storage, contours, Loader.sizeof(CvContour.class), CV_RETR_CCOMP, CV_CHAIN_APPROX_NONE, new CvPoint(0,0));
	        CvSeq ptr = new CvSeq();
	        CvPoint p1 = new CvPoint(0,0),p2 = new CvPoint(0,0);
	        List<CvRect> retList = new ArrayList<CvRect>();
	        for (ptr = contours; ptr != null; ptr = ptr.h_next()) {
	        	CvScalar color = CvScalar.RED;
	        	retList.add(cvBoundingRect(ptr, 1));
	        }
	        
	        List<CvRect> interestRetList = new ArrayList<CvRect>();
	        for (CvRect cvRect : retList) {
	        	int size = cvRect.width() * cvRect.height();
	        	if (size > 300) {
	        		interestRetList.add(cvRect);
	        	}
			}	
	        sp = 15;
	        sr = 20;
	        for (int i = 0, length = interestRetList.size(); i < length; i++) {
	        	fileName = "C:\\temp\\Recorte_Imagens\\Parte2\\IFC G1R9 DIA 21 N.jpg";
	        	IplImage original = cvLoadImage(fileName, CV_LOAD_IMAGE_COLOR);
	        	CvRect r = interestRetList.get(i);
	        	r.x(r.x()-2);
	        	r.y(r.y()-2);
	        	r.width(r.width()+4);
	        	r.height(r.height()+4);
	        	cvSetImageROI(original, r);
	        	
	        	IplImage newFiltered = cvCreateImage(cvGetSize(original), original.depth(), original.nChannels());
	        	cvPyrMeanShiftFiltering(original, newFiltered, sp, sr, 1, null);
	        	
	        	cvSaveImage(fileName.replace(".jpg", "_pata_" + (i + 1) + ".jpg"), newFiltered);
			}
		}
}
