package org.firstteam1719.dashboard.CameraExtension;


import edu.wpi.first.smartdashboard.camera.WPICameraExtension;
import edu.wpi.first.smartdashboard.properties.NumberProperty;
import edu.wpi.first.wpijavacv.WPIBinaryImage;
import edu.wpi.first.wpijavacv.WPIColor;
import edu.wpi.first.wpijavacv.WPIColorImage;
import edu.wpi.first.wpijavacv.WPIContour;
import edu.wpi.first.wpijavacv.WPIImage;
import edu.wpi.first.wpijavacv.WPIPoint;
import edu.wpi.first.wpijavacv.WPIPolygon;

/**
 *
 * @author Aaon Eline
 */
public class VisonExtension extends WPICameraExtension {

    
    /*
    private final NumberProperty redMax;
    private final NumberProperty redMin;
    private final NumberProperty greenMax;
    private final NumberProperty greenMin;
    private final NumberProperty blueMax;
    private final NumberProperty blueMin;
    private final NumberProperty erosion;
    private final NumberProperty dialation;
    private final NumberProperty contours;
    private final NumberProperty polyPercent;
    private final NumberProperty targetLeftside;
    private final NumberProperty targetRightSide;
    private WPIBinaryImage outputImage;
    private WPIContour[] greenContours;
    private WPIPolygon tempPoly;
    private WPIColor color;

    public VisonExtension() {
        super();
        this.redMax = new NumberProperty(this, "red Max", 160);
        this.redMin = new NumberProperty(this, "red Min", 0);
        this.greenMax = new NumberProperty(this, "green Max", 255);
        this.greenMin = new NumberProperty(this, "green Min", 200);
        this.blueMax = new NumberProperty(this, "blue Max", 255);
        this.blueMin = new NumberProperty(this, "blue Min", 0);
        this.erosion = new NumberProperty(this, "Erosion", 0);
        this.dialation = new NumberProperty(this, "dialation", 3);
        this.contours = new NumberProperty(this, "contours", 0);
        this.polyPercent = new NumberProperty(this, "percent accuracy polygons", 0.2);
        this.targetLeftside = new NumberProperty(this, "pixles to left of center", 15);
        this.targetRightSide = new NumberProperty(this, "pixles to right of center", 15);
        this.ipProperty.setDefault("10.17.19.11");

    }

    @Override
    public WPIImage processImage(WPIColorImage rawImage) {
        WPIBinaryImage redMinImage = rawImage.getRedChannel().getThreshold(redMin.getValue().intValue());
        WPIBinaryImage redMaxImage = rawImage.getRedChannel().getThresholdInverted(redMax.getValue().intValue());
        WPIBinaryImage greenMinImage = rawImage.getGreenChannel().getThreshold(greenMin.getValue().intValue());
        WPIBinaryImage greenMaxImage = rawImage.getGreenChannel().getThresholdInverted(greenMax.getValue().intValue());
        WPIBinaryImage blueMinImage = rawImage.getBlueChannel().getThreshold(blueMin.getValue().intValue());
        WPIBinaryImage blueMaxImage = rawImage.getBlueChannel().getThresholdInverted(blueMax.getValue().intValue());

        outputImage = redMinImage.getAnd(redMaxImage).getAnd(greenMinImage).getAnd(greenMaxImage).getAnd(blueMinImage).getAnd(blueMaxImage);

        redMinImage.dispose();
        redMaxImage.dispose();
        greenMinImage.dispose();
        greenMaxImage.dispose();
        blueMinImage.dispose();
        blueMaxImage.dispose();
//
//        try {
//        } catch (Exception e) {
//        }


        outputImage.erode(erosion.getValue().intValue());
        outputImage.dilate(dialation.getValue().intValue());
        greenContours = outputImage.findContours();
        contours.setValue(greenContours.length);
//        if (greenContours.length >= 0){
//            this.contors.setValue(true);
//        }else{
//            this.contors.setValue(false);
//        }       
        for (WPIContour contour : greenContours) {
            if (contour.getHeight() >= 50 || contour.getWidth() >= 50) {


                tempPoly = contour.approxPolygon(polyPercent.getValue().intValue());
                rawImage.drawPolygon(tempPoly, WPIColor.RED, 2);
                int contourX = tempPoly.getX();
                int contourY = tempPoly.getY();
                int halfWidth = tempPoly.getWidth() / 2 + contourX;
                int halfHeight = tempPoly.getHeight() / 2 + contourY;
                if ((halfWidth <= (rawImage.getWidth() / 2 + targetLeftside.getValue().intValue()))
                        && (halfWidth >= (rawImage.getWidth() / 2 - targetRightSide.getValue().intValue()))) {
                    this.color = WPIColor.RED;
                } else {
                    color = WPIColor.CYAN;
                }
                rawImage.drawLine(new WPIPoint(halfWidth, 0), new WPIPoint(halfWidth, rawImage.getHeight()), this.color, 2);
                rawImage.drawLine(new WPIPoint(0, halfHeight), new WPIPoint(rawImage.getWidth(), halfHeight), this.color, 2);
            }
        }

        return rawImage;
    }
    * */
}