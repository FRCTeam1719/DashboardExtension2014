/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.firstteam1719.dashboard.flicker;

import com.googlecode.javacv.CanvasFrame;
import com.googlecode.javacv.cpp.opencv_core;
import com.googlecode.javacv.cpp.opencv_core.CvSize;
import com.googlecode.javacv.cpp.opencv_core.IplImage;
import com.googlecode.javacv.cpp.opencv_imgproc;
import edu.wpi.first.smartdashboard.camera.WPICameraExtension;
import edu.wpi.first.wpijavacv.DaisyExtensions;
import edu.wpi.first.wpijavacv.WPIColorImage;
import edu.wpi.first.wpijavacv.WPIImage;
import edu.wpi.first.wpilibj.networktables.NetworkTable;

/**
 *
 * @author aaroneline
 */
public class flicker extends WPICameraExtension {

    public static NetworkTable SmartDashboard = NetworkTable.getTable("SmartDashboard");
    private static CvSize size = null;
    //Create Images
    static IplImage LEDon = null;
    static IplImage LEDoff = null;
    static IplImage dest = null;
    private static IplImage hsvOn;
    private static IplImage satOn;
    private static IplImage hueOn;
    private static IplImage valOn;
    private static IplImage hsvOff;
    private static IplImage satOff;
    private static IplImage hueOff;
    private static IplImage valOff;
    
    CanvasFrame subFrame;

    public flicker() {
        super();

        //SmartDashboard values
        SmartDashboard.putBoolean("Camera LED", false);
        SmartDashboard.putBoolean("Is Camera LED On?", false);

        //Create  Windows
        subFrame = new CanvasFrame("Subtraction");


        //Allocate Images
        //Create IplImages
        
        


    }

    @Override
    public WPIImage processImage(WPIColorImage rawImage) {
        DaisyExtensions.init();
        if (SmartDashboard.getBoolean("Is Camera LED On?")) {

            LEDon = DaisyExtensions.getIplImage(rawImage);
            SmartDashboard.putBoolean("Camera LED", false);
        } else {
            LEDoff = DaisyExtensions.getIplImage(rawImage);
            SmartDashboard.putBoolean("Camera LED", true);
        }
        if(LEDoff == null || LEDon ==null){
            return rawImage;
        }
        
        if (
                size == null || 
                size.width() != rawImage.getWidth() || 
                size.height() != rawImage.getHeight()) {
            size = opencv_core.cvSize(rawImage.getWidth(), rawImage.getHeight());
            hsvOn = IplImage.create(size, 8, 3);
            hueOn = IplImage.create(size, 8, 1);
            satOn = IplImage.create(size, 8, 1);
            valOn = IplImage.create(size, 8, 1);
            dest = IplImage.create(size, 8, 1);
            hsvOff = IplImage.create(size, 8, 3);
            hueOff = IplImage.create(size, 8, 1);
            satOff = IplImage.create(size, 8, 1);
            valOff = IplImage.create(size, 8, 1);
            
        }
        
        
        //Convert to HSV
//        opencv_imgproc.cvCvtColor(LEDon, dest, opencv_imgproc.CV_YUV2BGR);
//        opencv_imgproc.cvCvtColor(dest, hsvOn, opencv_imgproc.CV_BGR2HSV);
        //Getting string to look for null pointer in c code
        
        String failedAddress = "address=0x0,";
        if( LEDon.toString().contains(failedAddress)
                || LEDoff.toString().contains(failedAddress) 
                || hsvOn.toString().contains(failedAddress) 
                || hsvOff.toString().contains(failedAddress)){
            return null;
        }
        System.out.println("A:"+LEDon.toString());
        System.out.println("B:"+hsvOn.toString());
        System.out.println("C:"+LEDoff.toString());
        System.out.println("D:"+hsvOff.toString());
        opencv_imgproc.cvCvtColor(LEDon, hsvOn, opencv_imgproc.CV_BGR2HSV);
        
        
        opencv_core.cvSplit(hsvOn, hueOn, satOn, valOn, null);
        opencv_imgproc.cvCvtColor(LEDoff, hsvOff, opencv_imgproc.CV_BGR2HSV);
        opencv_core.cvSplit(hsvOff, hueOff, satOff, valOff, null);
        
        
        
        opencv_core.cvSub(valOn, valOff, dest, null);

        subFrame.showImage(dest.getBufferedImage());



        return rawImage;
    }
}
