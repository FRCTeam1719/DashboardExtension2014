package CV_Image_Test;
//TODO: Check for Memory Leaks
//TODO: Comments
//TODO: Solve hue frame/win mystery
import static CV_Image_Test.CVImageTest.win;
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
import edu.wpi.first.wpilibj.networktables.NetworkTable;
import java.io.File;
import java.util.ArrayList;

/**
 * @author wjb adapted from jrussell
 */
public class CVImageTest extends WPICameraExtension {

    //Constants that need to be tuned
    private static final int kMinWidth = 30;
    private static final int kMaxWidth = 100;
    private static final int kMinHeight = 30;
    private static final int kMaxHeight = 100;
    
    
//    
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
    //Mask Images
    private static IplImage hue_mask;
    private static IplImage hue_mask2;
    private static IplImage sat_mask;
    private static IplImage val_mask;
    private static WPIPoint linePt1;
    private static WPIPoint linePt2;
    private static WPIPoint hLinePt3;
    private static WPIPoint hLinePt4;
    private static int horizontalOffsetPixels;
    //Canvas Framses for results
    //TODO: Clenan up frames
    static CanvasFrame hue_win;
    static CanvasFrame morph_result;
    static CanvasFrame original;
    CanvasFrame hue_frame;
    CanvasFrame sat_frame;
    CanvasFrame val_frame;
    CanvasFrame hue_mask1_win;
    CanvasFrame hue_mask2_win;
    CanvasFrame bin_frame;
    CanvasFrame hsv_frame;
    static ThresholdSlider win;
    static File file;
    static WPIColorImage rawImage = null;
    static WPIImage resultImage = null;
    static boolean validImage;
    //Grab Netowrk Table
    public static NetworkTable SmartDashboard = NetworkTable.getTable("SmartDashboard");

    boolean circular;
    
    
    public CVImageTest() {
        super();
        //Create thresholdSlider
        win = new ThresholdSlider();
        SmartDashboard.putBoolean("showWin", false);
        //Create canvas frames
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

        //Hide all windows by default
        morph_result.setVisible(false);
        hue_win.setVisible(false);
        hue_frame.setVisible(false);
        sat_frame.setVisible(false);
        val_frame.setVisible(false);
        bin_frame.setVisible(false);
        hue_mask1_win.setVisible(false);
        hue_mask2_win.setVisible(false);
        hsv_frame.setVisible(false);
        win.setVisible(false);

        //Circular or not circular hue slider
        SmartDashboard.putBoolean("circular", true);
        circular = SmartDashboard.getBoolean("circular");


        if (ThresholdSlider.fileSelected()) {
            System.out.println("Thresholder Slider.fileselected");
            validImage = false;

            original.showImage(rawImage.getBufferedImage());
            validImage = true;
            // Process image
            resultImage = processImage(rawImage);
            // Display results
            hue_win.showImage(resultImage.getBufferedImage());
        } //if

        if (validImage && ThresholdSlider.get_imageUpdate()) {
            ThresholdSlider.reset_imageUpdate();
            System.out.println("validImage");
            // Process image
            resultImage = processImage(rawImage);

            // Display results
            hue_win.showImage(resultImage.getBufferedImage());


        } //while  
    }   //main 
//Image processing loop
    boolean lastFrame = false;

    @Override
    public WPIImage processImage(WPIColorImage rawImage) {
        //Get start time
        long startTime = System.nanoTime();
        //Initialize Daisy Extnesion
        DaisyExtensions.init();

        //Check if windows should be displayed
        boolean thisFrame = SmartDashboard.getBoolean("showWin");



        if (thisFrame != lastFrame && thisFrame) {

            //Show windows
            morph_result.setVisible(true);
            hue_win.setVisible(true);
            hue_frame.setVisible(true);
            sat_frame.setVisible(true);
            val_frame.setVisible(true);
            bin_frame.setVisible(true);
            hue_mask1_win.setVisible(true);
            hue_mask2_win.setVisible(true);
            hsv_frame.setVisible(true);
            win.setVisible(true);

        }//if
        if (thisFrame != lastFrame && !thisFrame) {
            //Hide windows
            morph_result.setVisible(false);
            hue_win.setVisible(false);
            hue_frame.setVisible(false);
            sat_frame.setVisible(false);
            val_frame.setVisible(false);
            bin_frame.setVisible(false);
            hue_mask1_win.setVisible(false);
            hue_mask2_win.setVisible(false);
            hsv_frame.setVisible(false);
            win.setVisible(false);
        }
        lastFrame = thisFrame;




//Read values from sliders
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
        //Alocate Images
        if (size == null || size.width() != rawImage.getWidth() || size.height() != rawImage.getHeight()) {
            size = opencv_core.cvSize(rawImage.getWidth(), rawImage.getHeight());
            bin = IplImage.create(size, 8, 1);
            hsv = IplImage.create(size, 8, 3);
            hue = IplImage.create(size, 8, 1);
            sat = IplImage.create(size, 8, 1);
            val = IplImage.create(size, 8, 1);
            hue_mask = IplImage.create(size, 8, 1);
            hue_mask2 = IplImage.create(size, 8, 1);
            sat_mask = IplImage.create(size, 8, 1);
            val_mask = IplImage.create(size, 8, 1);
        } //if
        // Get the raw IplImages for OpenCV
        IplImage input = DaisyExtensions.getIplImage(rawImage);
        WPIColorImage output = new WPIColorImage(rawImage.getBufferedImage());
        // Convert to HSV color space and split into components
        opencv_imgproc.cvCvtColor(input, hsv, opencv_imgproc.CV_BGR2HSV);
        opencv_core.cvSplit(hsv, hue, sat, val, null);

        //Show windows if necessary
        if (SmartDashboard.getBoolean("showWin")) {
            hsv_frame.showImage(hsv.getBufferedImage());
            hue_win.showImage(hue.getBufferedImage());
        };
        
        // NOTE: colors like green in the middle of the color space, require ANDing together
        // a threshold and inverted threshold in order to get points that are in a narrow range
        // values above threshold are converted to white(255) and values below are converted to black(0)
        //red is 0 to maybe 45.  green 50-75 range
        
        //Hue
        hueSlider(circular);
        // Saturation
        opencv_imgproc.cvThreshold(sat, sat_mask, ThresholdSlider.satSlider.getValue(), 255, opencv_imgproc.CV_THRESH_BINARY); // high color sat is larger #


        // Value
        opencv_imgproc.cvThreshold(val, val_mask, ThresholdSlider.valSlider.getValue(), 255, opencv_imgproc.CV_THRESH_BINARY); // brightest is larger #

        if (SmartDashboard.getBoolean("showWin")) {
            //Display hue masks before anding
            hue_mask1_win.showImage(hue_mask.getBufferedImage());
            hue_mask2_win.showImage(hue_mask2.getBufferedImage());
        }//if
        // Combine the results to obtain our binary image which should for the most
        // part only contain pixels that we care about
        
        combineHue(circular);
        //Initialize bin
        opencv_core.cvAnd(hue_mask, hue_mask, bin, null);
        //Combine images
        opencv_core.cvAnd(hue_mask, bin, bin, null);
        opencv_core.cvAnd(bin, sat_mask, bin, null);
        opencv_core.cvAnd(bin, val_mask, bin, null);


        // Uncomment the line below to see resultant image after masking
//        result.showImage(bin.getBufferedImage());



        if (SmartDashboard.getBoolean("showWin")) {
            //Show bin and hue before morphology is applied
            bin_frame.showImage(bin.getBufferedImage());
            hue_frame.showImage(hue_mask.getBufferedImage());
        }
       
        //Show windows if necessary
        if (SmartDashboard.getBoolean("showWin")) {
            //show morphology 
            morph_result.showImage(bin.getBufferedImage());
            //Display saturation and value masks
            sat_frame.showImage(sat_mask.getBufferedImage());
            val_frame.showImage(val_mask.getBufferedImage());
        }//if
        //Find Contours
        WPIBinaryImage binWpi = DaisyExtensions.makeWPIBinaryImage(bin);
        contours = DaisyExtensions.findConvexContours(binWpi);

        //Array for storying contours that match our ratios
        polygons = new ArrayList<WPIPolygon>();
        
        
        //Array for storing X/Y values
        int positions[] = new int[2];
        //Boolean for the Xc center of the image
        int centerPos = 160;
        //Boolean for finding objects
        boolean horzfound = false;
        boolean vertfound = false;
        boolean leftHorz = false;
        boolean rightHorz = false;
        boolean leftVert = false;
        boolean rightVert = false;
        //Check if any of our contrours match
        for (WPIContour c : contours) {
            //Ratios
            double ratio = ((double) c.getHeight()) / ((double) c.getWidth());
            //Draw all contours whte
            rawImage.drawContour(c, WPIColor.WHITE, 1);
            
            //Check if it matches the ratio and width meet the horizontal bar
            if (ratio < .4 && ratio > .2 && c.getWidth() > kMinWidth && c.getWidth() < kMaxWidth) {
                //Check if it's on the left or the right
                if(c.getX() >= centerPos){
                    rightHorz = true;
                } else {
                    leftHorz = true;
                }
                
                System.out.println("(horz) ratio = " + ratio + " width = " + c.getWidth() + "   height = " + c.getHeight() + " Position  = " + c.getX());
                //Add this contour to the polygon array
                polygons.add(c.approxPolygon(20));
                //Draw this contour blue
                rawImage.drawContour(c, WPIColor.BLUE, 2);
                horzfound = true; 
                
                
            } 
            //Check if contour matches the ratioj and height for the verticle bar
            if(ratio!=1){
            System.out.println("(vert) ratio = " + ratio + " width = " + c.getWidth() + "   height = " + c.getHeight() + "Position = " + c.getX());
            }
            if (ratio < 8 && ratio > 2 && c.getHeight() > kMinHeight && c.getWidth() < kMaxHeight ){
                //Check if it's on the left or the right
                if(c.getX() >= centerPos){
                    rightVert = true;
                }else {
                    leftVert = true;
                }
                //Add to the polygon array
                polygons.add(c.approxPolygon(20));
                //Draw it green
                rawImage.drawContour(c, WPIColor.GREEN, 2);
                vertfound = true;
                
            }

            //Line break in the printstream
            System.out.println("\n");
        }//for
        
        
        //If it finds both bars
        if (horzfound && vertfound) {
                              
            SmartDashboard.putBoolean("found", true);
            //Left or right logic
            if(rightVert&&rightHorz){
                SmartDashboard.putString("leftorright", "right");
            }else if(leftVert&&leftHorz){
                SmartDashboard.putString("leftorright", "left");
            }else{
                SmartDashboard.putString("leftorright", "unknown");
            }
            //Tell us which bar we are not fiding
        } else if(horzfound && !vertfound) {
            SmartDashboard.putBoolean("found", false);
            System.out.println("vert not found \n");
        } else if(!horzfound && vertfound) {
            System.out.println("horz not found");
            SmartDashboard.putBoolean("found", false);
        }
        
        
        else {
            SmartDashboard.putBoolean("found", false);
            
            System.out.println("Target(s) not founds");
        } //else


        //Release memory
        DaisyExtensions.releaseMemory();

        //Read end time
        System.out.println("Elapsed Loop Time: " + (System.nanoTime() - startTime));
        
        return rawImage;
        
    }
    
    //Circular hue slider methods
    public void hueSlider(boolean circular){
        
        if(circular){
        opencv_imgproc.cvThreshold(hue, hue_mask, ThresholdSlider.hueLowerSlider.getValue(), 255, opencv_imgproc.CV_THRESH_BINARY); //everything above here we want

        opencv_imgproc.cvThreshold(hue, hue_mask2, ThresholdSlider.hueUpperSlider.getValue(), 255, opencv_imgproc.CV_THRESH_BINARY_INV);
        }else if(!circular){
            opencv_imgproc.cvThreshold(hue, hue_mask, ThresholdSlider.hueLowerSlider.getValue(), 255, opencv_imgproc.CV_THRESH_BINARY_INV); //everything above here we want

            opencv_imgproc.cvThreshold(hue, hue_mask2, ThresholdSlider.hueUpperSlider.getValue(), 255, opencv_imgproc.CV_THRESH_BINARY);
        
        }
        
        
        
    }
    
    public void combineHue(boolean circular){
        if(circular){
            opencv_core.cvOr(hue_mask, hue_mask2, bin, null);
        }else if (!circular){
            opencv_core.cvAnd(hue_mask, hue_mask2, bin, null);
        }
        
        
        
        
        
    }
    
    
    
}
