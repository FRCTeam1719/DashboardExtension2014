package org.firstteam1719.dashboard.CameraExtension;


import com.googlecode.javacv.CanvasFrame;
import com.googlecode.javacv.cpp.opencv_core;
import com.googlecode.javacv.cpp.opencv_imgproc;
import edu.wpi.first.smartdashboard.camera.WPICameraExtension;
import edu.wpi.first.wpijavacv.WPIColorImage;
import edu.wpi.first.wpijavacv.WPIContour;
import edu.wpi.first.wpijavacv.WPIImage;
import edu.wpi.first.wpijavacv.WPIPoint;
import edu.wpi.first.wpijavacv.WPIPolygon;
import java.util.ArrayList;

/**
 *
 * @author Aaon Eline
 */
public class VisonExtension extends WPICameraExtension {

    
    CanvasFrame morph_result;
    CanvasFrame bin_img;
    private opencv_core.CvSize size = null;
    private WPIContour[] contours;
    private ArrayList<WPIPolygon> polygons;
    private opencv_imgproc.IplConvKernel morphKernel;
    private opencv_core.IplImage bin;
    private opencv_core.IplImage hsv;
    private opencv_core.IplImage hue;
    private opencv_core.IplImage sat;
    private opencv_core.IplImage val;
    private WPIPoint linePt1;
    private WPIPoint linePt2;
    private WPIPoint hLinePt3;
    private WPIPoint hLinePt4;
    private int horizontalOffsetPixels=10;
    

    public VisonExtension() {
       
        morph_result = new CanvasFrame("morph");
        morph_result.setLocation(900, 0);
        bin_img = new CanvasFrame("bin");
        bin_img.setLocation(800, 0);
        
        
        
        
    }

    @Override
    public WPIImage processImage(WPIColorImage rawImage) {
         //Set windows size
         if (size == null || size.width() != rawImage.getWidth() || size.height() != rawImage.getHeight()) {
            size = opencv_core.cvSize(rawImage.getWidth(), rawImage.getHeight());
            bin = opencv_core.IplImage.create(size, 8, 1);
            hsv = opencv_core.IplImage.create(size, 8, 3);
            hue = opencv_core.IplImage.create(size, 8, 1);
            sat = opencv_core.IplImage.create(size, 8, 1);
            val = opencv_core.IplImage.create(size, 8, 1);
            
        }
        //Convert WPIImage to IPLImage
        opencv_core.IplImage input = edu.wpi.first.wpijavacv.DaisyExtensions.getIplImage(rawImage);
        //Cast to HSV
        opencv_imgproc.cvCvtColor(input, hsv, opencv_imgproc.CV_BGR2HSV);
        opencv_core.cvSplit(hsv, hue, sat, val, null);
        
        
        opencv_imgproc.cvThreshold(hue, bin, 25, 255, opencv_imgproc.CV_THRESH_BINARY); //everything we don't want for red
        //hue_thresh.showImage(bin.getBufferedImage());
        opencv_imgproc.cvThreshold(hue, hue, 45, 255, opencv_imgproc.CV_THRESH_BINARY_INV); //everything we do want for red
//        hue_thresh2.showImage(hue.getBufferedImage());
        //Applying thresholds
        //pencv_imgproc.cvThreshold(hue, bin,  36, 255, opencv_imgproc.CV_THRESH_BINARY);
        
        
        
        
        //Send image to morph canvas
        morph_result.showImage(val.getBufferedImage());
        bin_img.showImage(bin.getBufferedImage());
        return rawImage;
    }
    
}