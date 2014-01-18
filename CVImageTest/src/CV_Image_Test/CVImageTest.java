package CV_Image_Test;

import com.googlecode.javacv.CanvasFrame;
import com.googlecode.javacv.cpp.opencv_core;
import com.googlecode.javacv.cpp.opencv_core.*;
import com.googlecode.javacv.cpp.opencv_imgproc;
import com.googlecode.javacv.cpp.opencv_imgproc.*;
import edu.wpi.first.smartdashboard.camera.WPICameraExtension;
import edu.wpi.first.wpijavacv.DaisyExtensions;
import edu.wpi.first.wpijavacv.WPIBinaryImage;
import edu.wpi.first.wpijavacv.WPIColor;
import edu.wpi.first.wpijavacv.WPIColorImage;
import edu.wpi.first.wpijavacv.WPIContour;
import edu.wpi.first.wpijavacv.WPIImage;
import edu.wpi.first.wpijavacv.WPIPoint;
import edu.wpi.first.wpijavacv.WPIPolygon;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import javax.imageio.ImageIO;
import javax.swing.Timer;

/**
 * @author wjb adapted from jrussell
 */
public class CVImageTest extends WPICameraExtension {

    //public static final String NAME = "WHS Image Test";
    private static WPIColor targetColor = new WPIColor(0, 255, 0);
    // Constants that need to be tuned
    private static final double kNearlyHorizontalSlope = Math.tan(Math.toRadians(20));
    private static final double kNearlyVerticalSlope = Math.tan(Math.toRadians(90 - 20));
    private static final int kMinWidth = 20;
    private static final int kMaxWidth = 200;
    private static final double kRangeOffset = 0.0;
    private static final int kHoleClosingIterations = 9;
    private static final double kShooterOffsetDeg = -1.55;
    private static final double kHorizontalFOVDeg = 47.0;
    private static final double kVerticalFOVDeg = 480.0 / 640.0 * kHorizontalFOVDeg;
    private static final double kCameraHeightIn = 54.0;
    private static final double kCameraPitchDeg = 21.0;
    private static final double kTopTargetHeightIn = 98.0 + 2.0 + 9.0; // 98 to rim, +2 to bottom of target, +9 to center of targe
    // Store JavaCV temporaries as members to reduce memory management during processing
    private static CvSize size = null;
    private static WPIContour[] contours;
    private static ArrayList<WPIPolygon> polygons;
    private static IplConvKernel morphKernel = IplConvKernel.create(3, 3, 1, 1, opencv_imgproc.CV_SHAPE_RECT, null);
    private static IplImage bin;
    private static IplImage hsv;
    private static IplImage hue;
    private static IplImage sat;
    private static IplImage val;
    private static WPIPoint linePt1;
    private static WPIPoint linePt2;
    private static WPIPoint hLinePt3;
    private static WPIPoint hLinePt4;
    private static int horizontalOffsetPixels;
    //Canvas Framses for results
    static CanvasFrame Result;
    static CanvasFrame morph_result;
    static CanvasFrame original;
    
    CanvasFrame hue_frame;
    CanvasFrame sat_frame;
    CanvasFrame val_frame;
    CanvasFrame hue_thresh;
    CanvasFrame hue_thresh2;
    CanvasFrame sat_thresh;
    CanvasFrame val_thresh;
    static ThresholdSlider win;
    static File file;
    static WPIColorImage rawImage = null;
    static WPIImage resultImage = null;
    static boolean validImage;

    //public CVImageTest() {
        //        result = new CanvasFrame("result");
//        result.setLocation(600, 0);
//        morph_result = new CanvasFrame("morph");
//        morph_result.setLocation(900, 0);
        //comment out the canvas frames below if not displayinmg intermediate results
         
  //  }

    public CVImageTest() {
        super();
        win = new ThresholdSlider();

        System.out.println("main");
        morph_result = new CanvasFrame("morph");
        morph_result.setLocation(900, 0);
        morph_result.setSize(300, 200);

        original = new CanvasFrame("Original");
        original.setLocation(500, 0);
        original.setSize(300, 200);

        Result = new CanvasFrame("hueWindow");
        Result.setLocation(700, 200);
        Result.setSize(300, 200);
        hue_frame = new CanvasFrame("hue");
        hue_frame.setLocation(0, 300);
        sat_frame = new CanvasFrame("Sat");
        sat_frame.setLocation(0, 0);
        val_frame = new CanvasFrame("Val");
        val_frame.setLocation(0, 600);
//
          hue_thresh = new CanvasFrame("hue Thresh");
          hue_thresh.setLocation(300, 300);
          sat_thresh = new CanvasFrame("Sat Thresh");
          sat_thresh.setLocation(300, 0);
          val_thresh = new CanvasFrame("Val Thresh");
          val_thresh.setLocation(300, 600);
          hue_thresh2 = new CanvasFrame("hue Thresh2");
          hue_thresh2.setLocation(600, 300);
        
        
            if (ThresholdSlider.fileSelected()) {
                System.out.println("Thresholder Slider.fileselected");
                validImage = false;
                
                original.showImage(rawImage.getBufferedImage());
                validImage = true;
               // Process image
                resultImage = processImage(rawImage);
                // Display results
                Result.showImage(resultImage.getBufferedImage());
            } //if

            if (validImage && ThresholdSlider.get_imageUpdate()) {
                ThresholdSlider.reset_imageUpdate();
                System.out.println("validImage");
                // Process image
                resultImage = processImage(rawImage);

                // Display results
                Result.showImage(resultImage.getBufferedImage());
            

        } //while  
    }   //main 

//
    @Override
    public WPIImage processImage(WPIColorImage rawImage) {
        DaisyExtensions.init();
//
         if (ThresholdSlider.fileSelected()) {
                System.out.println("Thresholder Slider.fileselected");
                validImage = false;
                
                original.showImage(rawImage.getBufferedImage());
                validImage = true;
               // Process image
                resultImage = processImage(rawImage);
                // Display results
                //result.showImage(resultImage.getBufferedImage());
            } //if

            if (validImage && ThresholdSlider.get_imageUpdate()) {
                ThresholdSlider.reset_imageUpdate();
                System.out.println("validImage");
                // Process image
                resultImage = processImage(rawImage);

                // Display results
                //result.showImage(resultImage.getBufferedImage());
            

        } //if  
        System.out.println("processsImages");
        if (size == null || size.width() != rawImage.getWidth() || size.height() != rawImage.getHeight()) {
            size = opencv_core.cvSize(rawImage.getWidth(), rawImage.getHeight());
            bin = IplImage.create(size, 8, 1);
            hsv = IplImage.create(size, 8, 3);
            hue = IplImage.create(size, 8, 1);
            sat = IplImage.create(size, 8, 1);
            val = IplImage.create(size, 8, 1);
            horizontalOffsetPixels = (int) Math.round(kShooterOffsetDeg * (size.width() / kHorizontalFOVDeg));
            linePt1 = new WPIPoint(size.width() / 2 + horizontalOffsetPixels, size.height() / 2 + 50);
            linePt2 = new WPIPoint(size.width() / 2 + horizontalOffsetPixels, size.height() / 2 + 100);
            hLinePt3 = new WPIPoint(size.width() / 2 + horizontalOffsetPixels + 25, size.height() / 2 + 75);
            hLinePt4 = new WPIPoint(size.width() / 2 + horizontalOffsetPixels - 25, size.height() / 2 + 75);
        }
//        // Get the raw IplImages for OpenCV
        IplImage input = DaisyExtensions.getIplImage(rawImage);
        WPIColorImage output = new WPIColorImage(rawImage.getBufferedImage());
//        // Convert to HSV color space and split into components
        opencv_imgproc.cvCvtColor(input, hsv, opencv_imgproc.CV_BGR2HSV);
        opencv_core.cvSplit(hsv, hue, sat, val, null);

        // Uncomment the lines below to see intermediate images
//        hue_frame.showImage(hue.getBufferedImage());
//        sat_frame.showImage(sat.getBufferedImage());
//        val_frame.showImage(val.getBufferedImage());
        
        // Threshold each component separately
        // Hue
        // NOTE: colors like green in the middle of the color space, require ANDing together
        // a threshold and inverted threshold in order to get points that are in a narrow range
        // values above threshold are converted to white(255) and values below are converted to black(0)
        //red is 0 to maybe 45.  green 50-75 range
        //green
        opencv_imgproc.cvThreshold(hue, bin, ThresholdSlider.hueUpperSlider.getValue(), 255, opencv_imgproc.CV_THRESH_BINARY_INV); //everything below here we want
//        hue_thresh.showImage(bin.getBufferedImage());
        opencv_imgproc.cvThreshold(hue, hue, ThresholdSlider.hueLowerSlider.getValue(), 255, opencv_imgproc.CV_THRESH_BINARY); //everything above here we want
        //       hue_thresh2.showImage(hue.getBufferedImage());

        // Saturation
        opencv_imgproc.cvThreshold(sat, sat, ThresholdSlider.satSlider.getValue(), 255, opencv_imgproc.CV_THRESH_BINARY); // high color sat is larger #
//        sat_thresh.showImage(sat.getBufferedImage());

        // Value
        opencv_imgproc.cvThreshold(val, val, ThresholdSlider.valSlider.getValue(), 255, opencv_imgproc.CV_THRESH_BINARY); // brightest is larger #
//        val_thresh.showImage(val.getBufferedImage());
//        System.out.println(" valSlider" + ThresholdSlider.valSlider.getValue());

        // Combine the results to obtain our binary image which should for the most
        // part only contain pixels that we care about
        opencv_core.cvAnd(hue, bin, bin, null);
        opencv_core.cvAnd(bin, sat, bin, null);
        opencv_core.cvAnd(bin, val, bin, null);

        // Uncomment the line below to see resultant image after masking
//        result.showImage(bin.getBufferedImage());
//
//        // Fill in any gaps using binary morphology
        opencv_imgproc.cvMorphologyEx(bin, bin, null, morphKernel, opencv_imgproc.CV_MOP_CLOSE, kHoleClosingIterations);
//
//        // Uncomment the next two lines to see the image post-morphology
        morph_result.showImage(bin.getBufferedImage());
        original.showImage(rawImage.getBufferedImage());
        Result.showImage(hue.getBufferedImage());
//      
//        // Find contours
//        WPIBinaryImage binWpi = DaisyExtensions.makeWPIBinaryImage(bin);
//        contours = DaisyExtensions.findConvexContours(binWpi);
//
//        polygons = new ArrayList<WPIPolygon>();
//        // top target ratio = .32, mid target ratio = .47
//        for (WPIContour c : contours) {
//            double ratio = ((double) c.getHeight()) / ((double) c.getWidth());
//
//            //uncomment the following 2 lines to see scoring results
//            rawImage.drawContour(c, WPIColor.WHITE, 1);
//            System.out.println("ratio = " + ratio + " width = " + c.getWidth());
//
//            if (ratio < .6 && ratio > 0.2 && c.getWidth() > kMinWidth && c.getWidth() < kMaxWidth) {
//                polygons.add(c.approxPolygon(20));
//                rawImage.drawContour(c, WPIColor.BLUE, 2);
//            }
//        }
//
////Find highest polygon   
//        WPIPolygon rectangle = null;
//        int highest = Integer.MAX_VALUE;
//
//        for (WPIPolygon p : polygons) {
//            int pCenterX = (p.getX() + (p.getWidth() / 2));
//            int pCenterY = (p.getY() + (p.getHeight() / 2));
//
//            if (pCenterY < highest) // Because coord system is funny
//            {
//                rectangle = p;
//                highest = pCenterY;
//                rawImage.drawPoint(new WPIPoint(pCenterX, pCenterY), targetColor, 3);
//            }
//        }
//
//        if (rectangle != null) {
//            double x = rectangle.getX() + (rectangle.getWidth() / 2);   // x value of center point of rectangle
//            x = 2 * x / size.width() - 1;                           // convert to fraction of screen width off center
//            double y = rectangle.getY() + (rectangle.getHeight() / 2);  // y value of center point of rectangle
//            y = -(2 * y / size.height() - 1);                        // convert to fraction of screen height off center
//
//            double azimuth = x * kHorizontalFOVDeg / 2.0 - kShooterOffsetDeg;
//            double elevation = y * kVerticalFOVDeg / 2.0 - kCameraPitchDeg;
//            //TODO fix range calculation
//            double range = (kTopTargetHeightIn - kCameraHeightIn) / Math.tan((y * kVerticalFOVDeg / 2.0 + kCameraPitchDeg) * Math.PI / 180.0);
//
//            System.out.println("Target found");
//            System.out.println("x: " + x);
//            System.out.println("y: " + elevation);
//            System.out.println("azimuth: " + azimuth);
//            System.out.println("range: " + range);
//        }
//
//        // Draw a crosshair at screen center
        output.drawLine(linePt1, linePt2, targetColor, 2);
        output.drawLine(hLinePt3, hLinePt4, targetColor, 2);
//
        DaisyExtensions.releaseMemory();
//
        //System.out.println("rawImage");
        return rawImage;
    }
}
