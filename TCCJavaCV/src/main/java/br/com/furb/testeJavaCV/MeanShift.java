package br.com.furb.testeJavaCV;

import static org.bytedeco.javacpp.opencv_core.cvCreateImage;
import static org.bytedeco.javacpp.opencv_core.cvGetSize;
import static org.bytedeco.javacpp.opencv_imgcodecs.CV_LOAD_IMAGE_COLOR;
import static org.bytedeco.javacpp.opencv_imgcodecs.cvLoadImage;
import static org.bytedeco.javacpp.opencv_imgcodecs.cvSaveImage;
import static org.bytedeco.javacpp.opencv_imgproc.cvPyrMeanShiftFiltering;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.bytedeco.javacpp.opencv_core.IplImage;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.bytedeco.javacv.OpenCVFrameConverter;

/**
 * Hello world!
 */
public class MeanShift extends JFrame {

	public static void main(String[] args) {
		String fileName = "C:\\temp\\Recorte_Imagens\\Componente_Conexa\\IFC G1R9 DIA 21 N_pata_ori.jpg";
        IplImage src = cvLoadImage(fileName, CV_LOAD_IMAGE_COLOR);
        
        int sp = 15;
        int sr = 20;
        
        IplImage filtered = cvCreateImage(cvGetSize(src), src.depth(), src.nChannels());
        cvPyrMeanShiftFiltering(src, filtered, sp, sr, 1, null);
        
        OpenCVFrameConverter.ToIplImage sourceConverter = new OpenCVFrameConverter.ToIplImage();

        Java2DFrameConverter frameConverter = new Java2DFrameConverter();
        BufferedImage image = frameConverter.getBufferedImage(sourceConverter.convert(filtered));
        MeanShift app = new MeanShift();
        ImagemPanel panel = new ImagemPanel(image);
        app.add(panel);
        
        cvSaveImage(fileName.replace(".jpg", "") + ("_meanShift_sp" + sp + "_sr" + sr + ".jpg"), filtered);
        
//        app.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        app.setSize(image.getWidth() / 2 + 16, image.getHeight() / 2 + 37);
//        app.setVisible(true);
//        app.setTitle(new Date().toString());
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
