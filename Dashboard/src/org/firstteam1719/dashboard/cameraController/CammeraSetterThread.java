/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.firstteam1719.dashboard.cameraController;

import edu.wpi.first.wpilibj.networktables.NetworkTable;
import edu.wpi.first.wpilibj.tables.TableKeyNotDefinedException;
import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import sun.net.www.http.HttpClient;

/**
 *
 * @author Chance
 */
public class CammeraSetterThread implements Runnable {

    public static NetworkTable SmartDashboard = NetworkTable.getTable("SmartDashboard");

    public CammeraSetterThread() {
    }

    @Override
    public void run() {
        boolean b2 = true;
        while (true) {
            boolean b = false;
            try {
                b = SmartDashboard.getBoolean("isAtonomus");
            } catch (TableKeyNotDefinedException e) {
                System.out.println("Caught exception:" + e);
            }
            if (!b) {
                b2 = b;
                HttpWraper.send("http://10.17.19.11/sm/sm.srv?root_ImageSource_I0_Sensor_Contrast=50&action=modify");
                HttpWraper.send("http://10.17.19.11/sm/sm.srv?root_ImageSource_I0_Sensor_ExposureValue=50&action=modify");
            } else if (b) {
                b2 = b;
                HttpWraper.send("http://10.17.19.11/sm/sm.srv?root_ImageSource_I0_Sensor_Contrast=100&action=modify");
                HttpWraper.send("http://10.17.19.11/sm/sm.srv?root_ImageSource_I0_Sensor_ExposureValue=0&action=modify");
            }
            try {
                Thread.sleep(100);
            } catch (Exception e) {
                System.out.println("something went wrong in try catch: " + e);
            }
        }
    }

}
