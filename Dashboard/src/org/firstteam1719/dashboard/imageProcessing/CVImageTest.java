package org.firstteam1719.dashboard.imageProcessing;

import com.googlecode.javacv.CanvasFrame;
import com.googlecode.javacv.FFmpegFrameRecorder;
import com.googlecode.javacv.FrameRecorder;
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
import edu.wpi.first.wpijavacv.WPIPolygon;
import edu.wpi.first.wpilibj.networktables.NetworkTable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class CVImageTest extends WPICameraExtension {

    //Constants that need to be tuned
    private final int horzMinWidth = 30;
    private final int horzMinHeight = 100;
    private final int vertMinHeight = 30;
    private final int vertMaxHeight = 100;
    private final int centerPos = 160;
    private final double horzMinRatio=.1;
    private final double horzMaxRatio=.4;
    private final double vertMinRatio=2;
    private final double vertMaxRatio=8.5;

    // Store JavaCV temporaries as members to reduce memory management during processing
    private CvSize size = null;
    private WPIContour[] contours;
    private ArrayList<WPIPolygon> polygons;
    private IplConvKernel morphKernel = IplConvKernel.create(3, 3, 1, 1, opencv_imgproc.CV_SHAPE_RECT, null);
    private IplImage bin;
    private IplImage hsv;
    private IplImage hue;
    private IplImage sat;
    private IplImage val;
    //Mask Images
    private IplImage hue_mask;
    private IplImage hue_mask2;
    private IplImage sat_mask;
    private IplImage val_mask;
    //Canvas Framses for results
    CanvasFrame hue_win;
    CanvasFrame morph_result;
    CanvasFrame original;
    CanvasFrame hue_frame;
    CanvasFrame sat_frame;
    CanvasFrame val_frame;
    CanvasFrame hue_mask1_win;
    CanvasFrame hue_mask2_win;
    CanvasFrame bin_frame;
    CanvasFrame hsv_frame;
    ThresholdSlider win;
    WPIColorImage rawImage = null;
    WPIImage resultImage = null;
    boolean validImage;
    //Grab Netowrk Table
    NetworkTable SmartDashboard = NetworkTable.getTable("SmartDashboard");
    boolean circular = true;
    FrameRecorder robocam;
    boolean windowsVisible = true;

    public CVImageTest() {
        super();
        makeWindows();
        setWindowsVisible(false);
        DaisyExtensions.init();

        //Conceptually, hue is a circular measurement
        //This option toggles if the selected region is the area between the selectors, 
        //or the area outside of the protectors
        SmartDashboard.putBoolean("circular", circular);
        SmartDashboard.putBoolean("showWin", false);
    }

    @Override
    public WPIImage processImage(WPIColorImage rawImage) {
        setWindowsVisible(SmartDashboard.getBoolean("showWin"));

        CvSize newSize = opencv_core.cvSize(rawImage.getWidth(), rawImage.getHeight());
        allocateBuffers(newSize);
        IplImage input = DaisyExtensions.getIplImage(rawImage);
        try {
            robocam.record(input);
        } catch (Exception e) {
            System.err.println(e);
        }
        runMasks(input, circular);
        drawWindows();

        WPIBinaryImage binWpi = DaisyExtensions.makeWPIBinaryImage(bin);
        contours = DaisyExtensions.findConvexContours(binWpi);

        boolean horzfound = false;
        boolean vertfound = false;
        boolean leftHorz = false;
        boolean rightHorz = false;
        boolean leftVert = false;
        boolean rightVert = false;
        for (WPIContour c : contours) {
            rawImage.drawContour(c, WPIColor.WHITE, 1);
            boolean isHorizontal = isHorizontal(c);
            boolean isVertical = isVertical(c);
            boolean left = isLeft(c, centerPos);
            if (isHorizontal) {
                rawImage.drawContour(c, WPIColor.BLUE, 2);
            }
            if (isVertical) {
                rawImage.drawContour(c, WPIColor.GREEN, 2);
            }

            horzfound |= isHorizontal;
            vertfound |= isVertical;
            leftHorz |= left && isHorizontal;
            leftVert |= left && isVertical;
            rightHorz |= !left && isHorizontal;
            rightVert |= !left && isVertical;
        }

        SmartDashboard.putBoolean("found", horzfound && vertfound);
        if (rightVert && rightHorz) {
            SmartDashboard.putString("leftorright", "right");
        } else if (leftVert && leftHorz) {
            SmartDashboard.putString("leftorright", "left");
        } else {
            SmartDashboard.putString("leftorright", "unknown");
        }
        return rawImage;

    }

    /**
     * Performs all of the masking operation on the input image Final result is written to the bin buffer
     *
     * @param input
     * @param hueCircular
     */
    public void runMasks(IplImage input, boolean hueCircular) {
        opencv_imgproc.cvCvtColor(input, hsv, opencv_imgproc.CV_BGR2HSV);
        opencv_core.cvSplit(hsv, hue, sat, val, null);
        if (hueCircular) {
            opencv_imgproc.cvThreshold(hue, hue_mask, ThresholdSlider.hueLowerSlider.getValue(), 255, opencv_imgproc.CV_THRESH_BINARY); //everything above here we want
            opencv_imgproc.cvThreshold(hue, hue_mask2, ThresholdSlider.hueUpperSlider.getValue(), 255, opencv_imgproc.CV_THRESH_BINARY_INV);
            opencv_core.cvOr(hue_mask, hue_mask2, bin, null);
        } else {
            opencv_imgproc.cvThreshold(hue, hue_mask, ThresholdSlider.hueLowerSlider.getValue(), 255, opencv_imgproc.CV_THRESH_BINARY_INV); //everything above here we want
            opencv_imgproc.cvThreshold(hue, hue_mask2, ThresholdSlider.hueUpperSlider.getValue(), 255, opencv_imgproc.CV_THRESH_BINARY);
            opencv_core.cvAnd(hue_mask, hue_mask2, bin, null);
        }
        opencv_imgproc.cvThreshold(sat, sat_mask, ThresholdSlider.satSlider.getValue(), 255, opencv_imgproc.CV_THRESH_BINARY);
        opencv_imgproc.cvThreshold(val, val_mask, ThresholdSlider.valSlider.getValue(), 255, opencv_imgproc.CV_THRESH_BINARY);

        //Initialize bin
//        opencv_core.cvSet(bin, CvScalar.ONE);
        opencv_core.cvAnd(hue_mask, hue_mask, bin, null);
        opencv_core.cvAnd(hue_mask, bin, bin, null);
        opencv_core.cvAnd(bin, sat_mask, bin, null);
        opencv_core.cvAnd(bin, val_mask, bin, null);
    }

    private void allocateBuffers(CvSize newSize) {
        if (size == null || size.width()!=newSize.width() || size.height()!=newSize.height()) {
            if (size != null) {
                try {
                    bin.release();
                    hsv.release();
                    hue.release();
                    sat.release();
                    val.release();
                    hue_mask.release();
                    hue_mask2.release();
                    sat_mask.release();
                    val_mask.release();
                    robocam.release();
                } catch (Exception ex) {
                    System.err.println(ex);
                }
            }
            size = newSize;
            bin = IplImage.create(size, 8, 1);
            hsv = IplImage.create(size, 8, 3);
            hue = IplImage.create(size, 8, 1);
            sat = IplImage.create(size, 8, 1);
            val = IplImage.create(size, 8, 1);
            hue_mask = IplImage.create(size, 8, 1);
            hue_mask2 = IplImage.create(size, 8, 1);
            sat_mask = IplImage.create(size, 8, 1);
            val_mask = IplImage.create(size, 8, 1);
            Date d = new Date();
            DateFormat f = new SimpleDateFormat("yyyy_MM_dd_HH.mm.ss");
            robocam = new FFmpegFrameRecorder("Robocam" + f.format(d) + ".avi", size.width(), size.height());
            try {
                robocam.start();
            } catch (Exception ex) {
                System.err.println(ex);
            }
        }
    }

    private void makeWindows() {
        win = new ThresholdSlider();

        morph_result = new CanvasFrame("morph");
        morph_result.setLocation(900, 0);
        morph_result.setSize(300, 200);

        hue_win = new CanvasFrame("Hue");
        hue_win.setLocation(700, 200);
        hue_win.setSize(300, 200);
        hue_frame = new CanvasFrame("hue");
        hue_frame.setLocation(0, 300);
        sat_frame = new CanvasFrame("Sat");
        sat_frame.setLocation(0, 0);
        val_frame = new CanvasFrame("Val");
        val_frame.setLocation(0, 600);
        bin_frame = new CanvasFrame("Bin");
        bin_frame.setLocation(0, 640);
        hue_mask1_win = new CanvasFrame("Hue Mask 1");
        hue_mask1_win.setLocation(300, 300);
        hue_mask2_win = new CanvasFrame("hue Mask 2");
        hue_mask2_win.setLocation(600, 300);
        hsv_frame = new CanvasFrame("HSV");
        hsv_frame.setLocation(400, 400);

    }

    private void setWindowsVisible(boolean isVisible) {
        if (isVisible == windowsVisible) {
            /**
             * If we do not need to do anything, Bale out early to prevent flickering
             */
            return;
        }
        windowsVisible = isVisible;
        morph_result.setVisible(isVisible);
        hue_win.setVisible(isVisible);
        hue_frame.setVisible(isVisible);
        sat_frame.setVisible(isVisible);
        val_frame.setVisible(isVisible);
        bin_frame.setVisible(isVisible);
        hue_mask1_win.setVisible(isVisible);
        hue_mask2_win.setVisible(isVisible);
        hsv_frame.setVisible(isVisible);
        win.setVisible(isVisible);
    }

    private void drawWindows() {
        if (!windowsVisible) {
            return;
        }
        hue_mask1_win.showImage(hue_mask.getBufferedImage());
        hue_mask2_win.showImage(hue_mask2.getBufferedImage());
        hsv_frame.showImage(hsv.getBufferedImage());
        hue_win.showImage(hue.getBufferedImage());
        bin_frame.showImage(bin.getBufferedImage());
        hue_frame.showImage(hue_mask.getBufferedImage());
        morph_result.showImage(bin.getBufferedImage());
        sat_frame.showImage(sat_mask.getBufferedImage());
        val_frame.showImage(val_mask.getBufferedImage());
    }

    private boolean checkRatio(WPIContour c, double ratioLower, double ratioUpper) {
        boolean works = true;
        double ratio = ((double) c.getHeight()) / ((double) c.getWidth());
        works &= ratio < ratioUpper;
        works &= ratio > ratioLower;
        return works;
    }

    private boolean checkRange(int x, int lower, int upper) {
        boolean works=x > lower && x < upper;
        return x > lower && x < upper;
    }

    private boolean isHorizontal(WPIContour c) {
        boolean isHorizontal = checkRange(c.getWidth(), horzMinWidth, horzMinHeight);
        isHorizontal &= checkRatio(c, horzMinRatio, horzMaxRatio);
        return isHorizontal;
    }

    private boolean isVertical(WPIContour c) {
        boolean isVertical = checkRange(c.getHeight(), vertMinHeight, vertMaxHeight);
        isVertical &= checkRatio(c, vertMinRatio, vertMaxRatio);
        return isVertical;
    }

    private boolean isLeft(WPIContour c, int centerPos) {
        return c.getX() < centerPos;
    }
}
