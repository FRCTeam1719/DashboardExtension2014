 /*
 * widget to read camera and perform target tracking for 2013 
 */
/*
 * Change log
 *  adapted from DaisCV 2012 code
 * 1 feb 2013:  removed range table and rpm function
 *              added canvasframes for intermediate images
 *              moved defintion of canvasframes outside widget so frame is 
 *                  only created once then updted inside of widget. 
 * 
 * 2 Feb 2013   updated constants for FoV and target height
 *              change treshold values and tune for red and green LEDs
 *              adjust rectangle ratios
 * 
 * 5 Feb 2013   removed tests for horiz/vert sides and 4 vertices
 *              target recognition is contour with correct aspect ratio
 * 
 * 6 Feb 2013   removed unused code
 *              added crosshairs
 *              moved code to constructor from widget 
 *                  helped get camera imnage up at start 
 *              added network table for passing target info to robot 
 * 
 * 9 Feb 2013   updated calculation for Elevation
 * 
 * 15 Feb 2013  swapped x and y because of camera rotation
 * 17 Feb reverted back to normal orientation
 * */
package edu.phoenix.smartdashboard.phoenixcv;

import com.googlecode.javacv.CanvasFrame;
import com.googlecode.javacv.cpp.opencv_core;
import com.googlecode.javacv.cpp.opencv_core.CvSize;
import com.googlecode.javacv.cpp.opencv_core.IplImage;
import com.googlecode.javacv.cpp.opencv_imgproc;
import com.googlecode.javacv.cpp.opencv_imgproc.IplConvKernel;
import edu.wpi.first.smartdashboard.camera.WPICameraExtension;
import edu.wpi.first.wpijavacv.DaisyExtensions;
import edu.wpi.first.wpijavacv.WPIBinaryImage;
import edu.wpi.first.wpijavacv.WPIColor;
import edu.wpi.first.wpijavacv.WPIColorImage;
import edu.wpi.first.wpijavacv.WPIContour;
import edu.wpi.first.wpijavacv.WPIImage;
import edu.wpi.first.wpijavacv.WPIPoint;
import edu.wpi.first.wpijavacv.WPIPolygon;
import edu.wpi.first.wpilibj.networktables.NetworkTable;
import java.util.ArrayList;

/* HOW TO GET THIS COMPILING IN NETBEANS:
 *  1. Install the SmartDashboard using the installer (if on Windows)
 *      1a. Verify that the OpenCV libraries are in your PATH (on Windows)
 *  2. Add the following libraries to the project:
 *     SmartDashboard.jar
 *     extensions/WPICameraExtension.jar
 *     lib/NetworkTable_Client.jar
 *     extensions/lib/javacpp.jar
 *     extensions/lib/javacv-*your environment*.jar
 *     extensions/lib/javacv.jar
 *     extensions/lib/WPIJavaCV.jar
 */
/*
 * @author wjb, taken heavily from DaisyCV by jrussell
 */
public class PhoenixCV_Widget extends WPICameraExtension {

    public static final String NAME = "PhoenixCV Target Tracker";
    private WPIColor targetColor = new WPIColor(255, 0, 0);
    public static NetworkTable targetTable = NetworkTable.getTable("targetTable");
    // Constants that need to be tuned\
    // TODO update for 2013 targets
    private static final double kNearlyHorizontalSlope = Math.tan(Math.toRadians(20));
    private static final double kNearlyVerticalSlope = Math.tan(Math.toRadians(90 - 20));
    private static final int kMinWidth = 60;
    private static final int kMaxWidth = 300;
    private static final double kRangeOffset = 0.0;
    private static final int kHoleClosingIterations = 3;
    private static final double kShooterOffsetDeg = 0;
    private static final double kHorizontalFOVDeg = 47.0;
    private static final double kVerticalFOVDeg = 480.0 / 640.0 * kHorizontalFOVDeg;
    private static final double kCameraHeightIn = 18.0;
    private static final double kCameraPitchDeg = 0.0;
    private static final double kTopTargetHeightIn = 100.0 + 4.0 + 6.0; // 100 to rim, +4 to bottom of target, +6 to center of target
    private boolean m_debugMode = false;
    // Store JavaCV temporaries as members to reduce memory management during processing
    private CvSize size = null;
    private WPIContour[] contours;
    private ArrayList<WPIPolygon> polygons;
    private IplConvKernel morphKernel;
    private IplImage bin;
    private IplImage hsv;
    private IplImage hue;
    private IplImage sat;
    private IplImage val;
    private WPIPoint linePt1;
    private WPIPoint linePt2;
    private WPIPoint hLinePt3;
    private WPIPoint hLinePt4;
    private int horizontalOffsetPixels=10;
    CanvasFrame result;
    CanvasFrame morph_result;
    CanvasFrame hue_frame;
    CanvasFrame sat_frame;
    CanvasFrame val_frame;
    CanvasFrame hue_thresh;
    CanvasFrame hue_thresh2;
    CanvasFrame sat_thresh;
    CanvasFrame val_thresh;

    public PhoenixCV_Widget() {
        this(false);
//        result = new CanvasFrame("result");
//        result.setLocation(600, 0);
        morph_result = new CanvasFrame("morph");
        morph_result.setLocation(900, 0);

        //comment out the canvas frames below if not displayinmg intermediate results
//        hue_frame = new CanvasFrame("hue");
//        hue_frame.setLocation(0, 300);
//        sat_frame = new CanvasFrame("Sat");
//        sat_frame.setLocation(0, 0);
//        val_frame = new CanvasFrame("Val");
//        val_frame.setLocation(0, 600);
//
//        hue_thresh = new CanvasFrame("hue Thresh");
//        hue_thresh.setLocation(300, 300);
//        sat_thresh = new CanvasFrame("Sat Thresh");
//        sat_thresh.setLocation(300, 0);
//        val_thresh = new CanvasFrame("Val Thresh");
//        val_thresh.setLocation(300, 600);
//        hue_thresh2 = new CanvasFrame("hue Thresh2");
//        hue_thresh2.setLocation(600, 300);

        morphKernel = IplConvKernel.create(3, 3, 1, 1, opencv_imgproc.CV_SHAPE_RECT, null);
    }

    public PhoenixCV_Widget(boolean debug) {

        DaisyExtensions.init(); //reserves memory
    }

    @Override
    public WPIImage processImage(WPIColorImage rawImage) {

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
        // Get the raw IplImages for OpenCV
        IplImage input = DaisyExtensions.getIplImage(rawImage);
        // Convert to HSV color space
        opencv_imgproc.cvCvtColor(input, hsv, opencv_imgproc.CV_BGR2HSV);
        opencv_core.cvSplit(hsv, hue, sat, val, null);

        // Uncomment the lines below to see intermediate images
//        hue_frame.showImage(hue.getBufferedImage());
//        sat_frame.showImage(sat.getBufferedImage());
//        val_frame.showImage(val.getBufferedImage());

        // Threshold each component separately
        // Hue
        // NOTE: Green is in the middle of the color space, so you need to AND together
        // a thresh and inverted thresh in order to get points that are green
        // values above threshold are converted to white(255) and values below are converted to black(0)

        //red is 0 to maybe 45.  
//        opencv_imgproc.cvThreshold(hue, bin, 0, 255, opencv_imgproc.CV_THRESH_BINARY); //everything we don't want for red
//        hue_thresh.showImage(bin.getBufferedImage());
//        opencv_imgproc.cvThreshold(hue, hue, 45, 255, opencv_imgproc.CV_THRESH_BINARY_INV); //everything we do want for red
//        hue_thresh2.showImage(hue.getBufferedImage());

        //green
        opencv_imgproc.cvThreshold(hue, bin, 60 - 15, 255, opencv_imgproc.CV_THRESH_BINARY); //everything we don't want for green
//        hue_thresh.showImage(bin.getBufferedImage());
        opencv_imgproc.cvThreshold(hue, hue, 60 - 25, 255, opencv_imgproc.CV_THRESH_BINARY); //everything we do want for green
//        hue_thresh2.showImage(hue.getBufferedImage());

        // Saturation
        opencv_imgproc.cvThreshold(sat, sat, 180, 255, opencv_imgproc.CV_THRESH_BINARY); // hih color sat is larger #
//        sat_thresh.showImage(sat.getBufferedImage());

        // Value
        opencv_imgproc.cvThreshold(val, val, 155, 255, opencv_imgproc.CV_THRESH_BINARY); // brightest is larger #
//        val_thresh.showImage(val.getBufferedImage());

        // Combine the results to obtain our binary image which should for the most
        // part only contain pixels that we care about
        opencv_core.cvAnd(hue, bin, bin, null);
        opencv_core.cvAnd(bin, sat, bin, null);
        opencv_core.cvAnd(bin, val, bin, null);

        // Uncomment the line below to see resultant image after masking
//        result.showImage(bin.getBufferedImage());

        // Fill in any gaps using binary morphology
        opencv_imgproc.cvMorphologyEx(bin, bin, null, morphKernel, opencv_imgproc.CV_MOP_CLOSE, kHoleClosingIterations);

        // Uncomment the next two lines to see the image post-morphology
        morph_result.showImage(bin.getBufferedImage());

        // Find contours
        WPIBinaryImage binWpi = DaisyExtensions.makeWPIBinaryImage(bin);
        contours = DaisyExtensions.findConvexContours(binWpi);

        polygons = new ArrayList<WPIPolygon>();
        // top target ratio = .32, mid target ratio = .47
        for (WPIContour c : contours) {
            double ratio = ((double) c.getHeight()) / ((double) c.getWidth());

            //uncomment the following 2 lines to see scoring results
            rawImage.drawContour(c, WPIColor.WHITE, 1);
            System.out.println("ratio = " + ratio + " width = " + c.getWidth());

            if (ratio < .6 && ratio > 0.2 && c.getWidth() > kMinWidth && c.getWidth() < kMaxWidth) {
                polygons.add(c.approxPolygon(20));
                rawImage.drawContour(c, WPIColor.BLUE, 2);
            }
        }

//Find highest polygon   
        WPIPolygon rectangle = null;
        int highest = Integer.MAX_VALUE;

        for (WPIPolygon p : polygons) {
            int pCenterX = (p.getX() + (p.getWidth() / 2));
            int pCenterY = (p.getY() + (p.getHeight() / 2));

            if (pCenterY < highest) // Because coord system is funny
            {
                rectangle = p;
                highest = pCenterY;
                rawImage.drawPoint(new WPIPoint(pCenterX, pCenterY), targetColor, 3);
            }
        }

        if (rectangle != null) {
            double x = rectangle.getX() + (rectangle.getWidth() / 2);   // x value of center point of rectangle
            x = 2 * x / size.width() - 1;                           // convert to fraction of screen width off center
            double y = rectangle.getY() + (rectangle.getHeight() / 2);  // y value of center point of rectangle
            y = -(2 * y / size.height() - 1);                        // convert to fraction of screen height off center

            double azimuth = x * kHorizontalFOVDeg / 2.0 - kShooterOffsetDeg;
             double elevation = y * kVerticalFOVDeg / 2.0 - kCameraPitchDeg;
             //TODO fix range calculation
            double range = (kTopTargetHeightIn - kCameraHeightIn) / Math.tan((y * kVerticalFOVDeg / 2.0 + kCameraPitchDeg) * Math.PI / 180.0);

            targetTable.putBoolean("found", true);
            targetTable.putNumber("Az", azimuth);
            targetTable.putNumber("El", elevation);

            System.out.println("Target found");
            System.out.println("x: " + x);
            System.out.println("y: " + elevation);
            System.out.println("azimuth: " + azimuth);
            System.out.println("range: " + range);

        } else {
            targetTable.putBoolean("found", false);
            targetTable.putNumber("Az", 0);
            targetTable.putNumber("El", 0);
            System.out.println("Target not found");
        }

        // Draw a crosshair at screen center
        rawImage.drawLine(linePt1, linePt2, targetColor, 2);
        rawImage.drawLine(hLinePt3, hLinePt4, targetColor, 2);

        DaisyExtensions.releaseMemory();

        return rawImage;
    }
}
